package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.LocationCategory
import com.example.model.LocationPoint
import com.example.model.MapDataRepository
import com.example.model.MapLayerType
import com.example.model.MapOverlay
import com.example.model.NavigationRoute
import com.example.model.TvInteractionMode
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min

data class TvMapUiState(
    val latitude: Double = 48.8584,
    val longitude: Double = 2.2945,
    val zoom: Float = 14f,
    val activeLayer: MapLayerType = MapLayerType.STANDARD,
    val activeOverlays: Set<MapOverlay> = setOf(MapOverlay.LANDMARKS, MapOverlay.GRID),
    val interactionMode: TvInteractionMode = TvInteractionMode.MENU_FOCUS,
    val selectedLocation: LocationPoint? = MapDataRepository.curatedLocations[1],
    val selectedCategory: LocationCategory = LocationCategory.ALL,
    val searchQuery: String = "",
    val activeRoute: NavigationRoute? = null,
    val isDetailsOpen: Boolean = false,
    val isLayersMenuOpen: Boolean = false,
    val isRoutePlannerOpen: Boolean = false,
    val routeOrigin: LocationPoint? = MapDataRepository.curatedLocations[1],
    val routeDestination: LocationPoint? = MapDataRepository.curatedLocations[2],
    val filteredLocations: List<LocationPoint> = MapDataRepository.curatedLocations
)

class TvMapViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(TvMapUiState())
    val uiState: StateFlow<TvMapUiState> = _uiState.asStateFlow()

    private val _toastEvents = MutableSharedFlow<String>()
    val toastEvents: SharedFlow<String> = _toastEvents.asSharedFlow()

    private var cameraAnimationJob: Job? = null

    init {
        updateFilteredLocations()
    }

    fun toggleInteractionMode() {
        _uiState.update { current ->
            val nextMode = if (current.interactionMode == TvInteractionMode.MAP_PAN) {
                TvInteractionMode.MENU_FOCUS
            } else {
                TvInteractionMode.MAP_PAN
            }
            current.copy(interactionMode = nextMode)
        }
    }

    fun setInteractionMode(mode: TvInteractionMode) {
        _uiState.update { it.copy(interactionMode = mode) }
    }

    fun panBy(dxPercent: Float, dyPercent: Float) {
        val currentZoom = _uiState.value.zoom
        val scaleFactor = 180.0 / Math.pow(2.0, currentZoom.toDouble())
        val deltaLng = dxPercent * scaleFactor * 0.4
        val deltaLat = -dyPercent * scaleFactor * 0.4

        _uiState.update { current ->
            val newLat = (current.latitude + deltaLat).coerceIn(-85.0, 85.0)
            var newLng = current.longitude + deltaLng
            if (newLng > 180.0) newLng -= 360.0
            if (newLng < -180.0) newLng += 360.0
            current.copy(latitude = newLat, longitude = newLng)
        }
    }

    fun zoomIn() {
        _uiState.update { current ->
            val newZoom = min(19f, current.zoom + 1f)
            current.copy(zoom = newZoom)
        }
    }

    fun zoomOut() {
        _uiState.update { current ->
            val newZoom = max(2f, current.zoom - 1f)
            current.copy(zoom = newZoom)
        }
    }

    fun setLayer(layer: MapLayerType) {
        _uiState.update { it.copy(activeLayer = layer) }
    }

    fun toggleOverlay(overlay: MapOverlay) {
        _uiState.update { current ->
            val newOverlays = if (current.activeOverlays.contains(overlay)) {
                current.activeOverlays - overlay
            } else {
                current.activeOverlays + overlay
            }
            current.copy(activeOverlays = newOverlays)
        }
    }

    fun selectCategory(category: LocationCategory) {
        _uiState.update { it.copy(selectedCategory = category, searchQuery = "") }
        updateFilteredLocations()
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        updateFilteredLocations()
    }

    fun quickSearchCategory(category: LocationCategory, keyword: String) {
        _uiState.update {
            it.copy(
                selectedCategory = category,
                searchQuery = keyword
            )
        }
        updateFilteredLocations()

        // Fly to the first match if available
        val matches = _uiState.value.filteredLocations
        if (matches.isNotEmpty()) {
            flyToLocation(matches.first())
        }
    }

    private fun updateFilteredLocations() {
        val query = _uiState.value.searchQuery.trim().lowercase()
        val cat = _uiState.value.selectedCategory

        val filtered = MapDataRepository.curatedLocations.filter { loc ->
            val matchesCategory = (cat == LocationCategory.ALL || loc.category == cat)
            val matchesQuery = query.isEmpty() ||
                    loc.name.lowercase().contains(query) ||
                    loc.city.lowercase().contains(query) ||
                    loc.country.lowercase().contains(query) ||
                    loc.category.label.lowercase().contains(query) ||
                    loc.description.lowercase().contains(query)
            matchesCategory && matchesQuery
        }

        _uiState.update { it.copy(filteredLocations = filtered) }
    }

    fun showCurrentLocation(): String {
        val location = MapDataRepository.myDeviceLocation
        flyToLocation(location)
        _uiState.update { it.copy(selectedLocation = location) }

        val toastMessage = "Your current location is ${location.plusCode}"
        viewModelScope.launch {
            _toastEvents.emit(toastMessage)
        }
        return toastMessage
    }

    fun flyToLocation(location: LocationPoint) {
        cameraAnimationJob?.cancel()
        _uiState.update { it.copy(selectedLocation = location, isDetailsOpen = false) }

        cameraAnimationJob = viewModelScope.launch {
            val startLat = _uiState.value.latitude
            val startLng = _uiState.value.longitude
            val startZoom = _uiState.value.zoom
            val targetLat = location.latitude
            val targetLng = location.longitude
            val targetZoom = location.defaultZoom

            val steps = 20
            for (i in 1..steps) {
                val fraction = i.toFloat() / steps
                val eased = if (fraction < 0.5f) {
                    2 * fraction * fraction
                } else {
                    -1 + (4 - 2 * fraction) * fraction
                }

                val currentLat = startLat + (targetLat - startLat) * eased
                val currentLng = startLng + (targetLng - startLng) * eased
                val currentZ = startZoom + (targetZoom - startZoom) * eased

                _uiState.update {
                    it.copy(
                        latitude = currentLat,
                        longitude = currentLng,
                        zoom = currentZ
                    )
                }
                delay(16)
            }
        }
    }

    fun setDetailsOpen(isOpen: Boolean) {
        _uiState.update { it.copy(isDetailsOpen = isOpen) }
    }

    fun setLayersMenuOpen(isOpen: Boolean) {
        _uiState.update { it.copy(isLayersMenuOpen = isOpen) }
    }

    fun setRoutePlannerOpen(isOpen: Boolean) {
        _uiState.update { it.copy(isRoutePlannerOpen = isOpen) }
    }

    fun setRouteOrigin(location: LocationPoint) {
        _uiState.update { it.copy(routeOrigin = location) }
        recalculateRoute()
    }

    fun setRouteDestination(location: LocationPoint) {
        _uiState.update { it.copy(routeDestination = location) }
        recalculateRoute()
    }

    private fun recalculateRoute() {
        if (_uiState.value.activeRoute != null) {
            calculateRouteBetweenPoints()
        }
    }

    fun calculateRouteBetweenPoints() {
        val origin = _uiState.value.routeOrigin ?: MapDataRepository.curatedLocations[1]
        val dest = _uiState.value.routeDestination ?: MapDataRepository.curatedLocations[2]
        val route = MapDataRepository.calculateSampleRoute(origin, dest)
        _uiState.update { it.copy(activeRoute = route, isRoutePlannerOpen = false) }

        val midLat = (origin.latitude + dest.latitude) / 2
        val midLng = (origin.longitude + dest.longitude) / 2
        _uiState.update {
            it.copy(
                latitude = midLat,
                longitude = midLng,
                zoom = 4f
            )
        }
    }

    fun clearActiveRoute() {
        _uiState.update { it.copy(activeRoute = null) }
    }

    fun resetToWorldView() {
        cameraAnimationJob?.cancel()
        _uiState.update {
            it.copy(
                latitude = 20.0,
                longitude = 0.0,
                zoom = 3f,
                selectedLocation = null
            )
        }
    }
}
