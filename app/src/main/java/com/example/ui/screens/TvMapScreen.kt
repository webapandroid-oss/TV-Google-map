package com.example.ui.screens

import android.view.KeyEvent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.LocationCategory
import com.example.model.MapDataRepository
import com.example.model.TvInteractionMode
import com.example.ui.components.OnScreenDpad
import com.example.ui.components.TvAutoKeyboardSearchBox
import com.example.ui.components.TvCurrentLocationBottomBar
import com.example.ui.components.TvFocusButton
import com.example.ui.components.TvLayersDialog
import com.example.ui.components.TvLocationCard
import com.example.ui.components.TvLocationDetailDialog
import com.example.ui.components.TvQuickSearchOptionPill
import com.example.ui.components.TvRemoteControlGuide
import com.example.ui.components.TvRoutePlannerDialog
import com.example.ui.map.TvMapCanvas
import com.example.ui.theme.GoogleBlue
import com.example.ui.theme.GoogleGreen
import com.example.ui.theme.GoogleRed
import com.example.ui.theme.GoogleYellow
import com.example.ui.theme.TvAccentFocus
import com.example.ui.theme.TvBackground
import com.example.ui.theme.TvSurface
import com.example.ui.theme.TvSurfaceVariant
import com.example.ui.theme.TvTextPrimary
import com.example.ui.theme.TvTextSecondary
import com.example.viewmodel.TvMapViewModel

@Composable
fun TvMapScreen(
    viewModel: TvMapViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val mapFocusRequester = remember { FocusRequester() }
    var isSidebarVisible by remember { mutableStateOf(true) }

    // Toast listener for device location messages
    LaunchedEffect(Unit) {
        viewModel.toastEvents.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }

    // Remote D-Pad Navigation and Key Event Handling
    BackHandler {
        if (uiState.isDetailsOpen) {
            viewModel.setDetailsOpen(false)
        } else if (uiState.isLayersMenuOpen) {
            viewModel.setLayersMenuOpen(false)
        } else if (uiState.isRoutePlannerOpen) {
            viewModel.setRoutePlannerOpen(false)
        } else if (uiState.activeRoute != null) {
            viewModel.clearActiveRoute()
        } else if (uiState.interactionMode == TvInteractionMode.MAP_PAN) {
            viewModel.setInteractionMode(TvInteractionMode.MENU_FOCUS)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(TvBackground)
            .testTag("tv_map_screen")
            .focusRequester(mapFocusRequester)
            .focusable()
            .onKeyEvent { event ->
                if (event.nativeKeyEvent.action == KeyEvent.ACTION_DOWN) {
                    val keyCode = event.nativeKeyEvent.keyCode

                    // Zoom shortcuts via TV Remote Channel / Page / Media keys
                    if (keyCode == KeyEvent.KEYCODE_PAGE_UP || keyCode == KeyEvent.KEYCODE_MEDIA_FAST_FORWARD) {
                        viewModel.zoomIn()
                        return@onKeyEvent true
                    }
                    if (keyCode == KeyEvent.KEYCODE_PAGE_DOWN || keyCode == KeyEvent.KEYCODE_MEDIA_REWIND) {
                        viewModel.zoomOut()
                        return@onKeyEvent true
                    }

                    // In MAP_PAN mode, D-Pad directly pans the map smoothly
                    if (uiState.interactionMode == TvInteractionMode.MAP_PAN) {
                        when (keyCode) {
                            KeyEvent.KEYCODE_DPAD_UP -> {
                                viewModel.panBy(0f, -0.6f)
                                return@onKeyEvent true
                            }
                            KeyEvent.KEYCODE_DPAD_DOWN -> {
                                viewModel.panBy(0f, 0.6f)
                                return@onKeyEvent true
                            }
                            KeyEvent.KEYCODE_DPAD_LEFT -> {
                                viewModel.panBy(-0.6f, 0f)
                                return@onKeyEvent true
                            }
                            KeyEvent.KEYCODE_DPAD_RIGHT -> {
                                viewModel.panBy(0.6f, 0f)
                                return@onKeyEvent true
                            }
                            KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER -> {
                                if (uiState.selectedLocation != null) {
                                    viewModel.setDetailsOpen(true)
                                } else {
                                    viewModel.toggleInteractionMode()
                                }
                                return@onKeyEvent true
                            }
                            KeyEvent.KEYCODE_BACK -> {
                                viewModel.setInteractionMode(TvInteractionMode.MENU_FOCUS)
                                return@onKeyEvent true
                            }
                        }
                    }
                }
                false
            }
    ) {
        // 1. Map Canvas Viewport (Background)
        TvMapCanvas(
            latitude = uiState.latitude,
            longitude = uiState.longitude,
            zoom = uiState.zoom,
            layer = uiState.activeLayer,
            overlays = uiState.activeOverlays,
            selectedLocation = uiState.selectedLocation,
            activeRoute = uiState.activeRoute,
            onPan = { dx, dy -> viewModel.panBy(dx, dy) },
            onSelectLocation = { loc -> viewModel.flyToLocation(loc) }
        )

        // 2. Top TV Navigation & Command Bar
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            TvBackground.copy(alpha = 0.98f),
                            TvBackground.copy(alpha = 0.88f),
                            Color.Transparent
                        )
                    )
                )
                .padding(horizontal = 24.dp, vertical = 10.dp)
        ) {
            // Row 1: Brand & Top Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // TV Google Map Title & Brand
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = GoogleBlue,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.Tv,
                                contentDescription = "TV",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = "TV Google Map",
                            color = TvTextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "Android TV Navigation & World Explorer",
                            color = TvTextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                // Action Buttons for TV Remote
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TvFocusButton(
                        text = if (uiState.interactionMode == TvInteractionMode.MAP_PAN) "Exit Pan Mode" else "Pan Map Mode",
                        icon = Icons.Default.Public,
                        onClick = { viewModel.toggleInteractionMode() },
                        isSelected = (uiState.interactionMode == TvInteractionMode.MAP_PAN),
                        accentColor = GoogleYellow,
                        testTag = "toggle_pan_mode_btn"
                    )

                    TvFocusButton(
                        text = "Layers",
                        icon = Icons.Default.Layers,
                        onClick = { viewModel.setLayersMenuOpen(true) },
                        isSelected = uiState.isLayersMenuOpen,
                        accentColor = GoogleBlue,
                        testTag = "layers_menu_btn"
                    )

                    TvFocusButton(
                        text = "Route Navigation",
                        icon = Icons.Default.Directions,
                        onClick = { viewModel.setRoutePlannerOpen(true) },
                        isSelected = uiState.isRoutePlannerOpen || uiState.activeRoute != null,
                        accentColor = GoogleGreen,
                        testTag = "route_planner_btn"
                    )

                    TvFocusButton(
                        text = "World View",
                        onClick = { viewModel.resetToWorldView() },
                        testTag = "world_view_btn"
                    )

                    TvFocusButton(
                        text = if (isSidebarVisible) "Hide List" else "Show List",
                        onClick = { isSidebarVisible = !isSidebarVisible },
                        testTag = "toggle_sidebar_btn"
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Row 2: Search on TV Google Map Edit Box + Narrow Quick Options Strip
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Search on TV Google Map (Opens virtual keyboard automatically when remote cursor lands on it)
                TvAutoKeyboardSearchBox(
                    query = uiState.searchQuery,
                    onQueryChange = { viewModel.setSearchQuery(it) },
                    onSearchSubmit = { /* Handled via filter */ },
                    modifier = Modifier.width(310.dp)
                )

                Spacer(modifier = Modifier.width(14.dp))

                // Alongside the Edit Box: Narrow space with requested quick search options
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 2.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    item {
                        TvQuickSearchOptionPill(
                            label = "Search for Restaurants",
                            icon = Icons.Default.Restaurant,
                            accentColor = GoogleRed,
                            onClick = { viewModel.quickSearchCategory(LocationCategory.RESTAURANTS, "Restaurant") },
                            testTag = "quick_search_restaurants"
                        )
                    }
                    item {
                        TvQuickSearchOptionPill(
                            label = "Search for Coffee",
                            icon = Icons.Default.LocalCafe,
                            accentColor = GoogleYellow,
                            onClick = { viewModel.quickSearchCategory(LocationCategory.COFFEE, "Coffee") },
                            testTag = "quick_search_coffee"
                        )
                    }
                    item {
                        TvQuickSearchOptionPill(
                            label = "Search for Shops",
                            icon = Icons.Default.ShoppingBag,
                            accentColor = GoogleGreen,
                            onClick = { viewModel.quickSearchCategory(LocationCategory.SHOPS, "Shop") },
                            testTag = "quick_search_shops"
                        )
                    }
                    item {
                        TvQuickSearchOptionPill(
                            label = "Search for Supermarkets",
                            icon = Icons.Default.ShoppingCart,
                            accentColor = GoogleBlue,
                            onClick = { viewModel.quickSearchCategory(LocationCategory.SUPERMARKETS, "Supermarket") },
                            testTag = "quick_search_supermarkets"
                        )
                    }
                    item {
                        TvQuickSearchOptionPill(
                            label = "Search for Showrooms",
                            icon = Icons.Default.DirectionsCar,
                            accentColor = TvAccentFocus,
                            onClick = { viewModel.quickSearchCategory(LocationCategory.SHOWROOMS, "Showroom") },
                            testTag = "quick_search_showrooms"
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Row 3: Comprehensive Google Maps Category Classification Tools
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 2.dp)
            ) {
                items(LocationCategory.values()) { category ->
                    TvFocusButton(
                        text = category.label,
                        onClick = { viewModel.selectCategory(category) },
                        isSelected = (uiState.selectedCategory == category && uiState.searchQuery.isEmpty()),
                        accentColor = GoogleBlue,
                        testTag = "category_chip_${category.name}"
                    )
                }
            }
        }

        // 3. Collapsible Left Destination Sidebar (Filtered POIs & Places)
        AnimatedVisibility(
            visible = isSidebarVisible,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 24.dp, top = 175.dp, bottom = 80.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = TvSurface.copy(alpha = 0.94f),
                border = androidx.compose.foundation.BorderStroke(1.dp, TvSurfaceVariant),
                modifier = Modifier
                    .width(320.dp)
                    .fillMaxHeight()
                    .testTag("tv_destination_sidebar")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (uiState.searchQuery.isNotEmpty()) "SEARCH RESULTS (${uiState.filteredLocations.size})" else "EXPLORE PLACES (${uiState.filteredLocations.size})",
                            color = GoogleBlue,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(uiState.filteredLocations) { location ->
                            TvLocationCard(
                                location = location,
                                isSelected = (uiState.selectedLocation?.id == location.id),
                                onClick = { viewModel.flyToLocation(location) }
                            )
                        }
                    }

                    if (uiState.selectedLocation != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        TvFocusButton(
                            text = "Inspect Details",
                            icon = Icons.Default.Info,
                            onClick = { viewModel.setDetailsOpen(true) },
                            isSelected = true,
                            accentColor = GoogleYellow,
                            modifier = Modifier.fillMaxWidth(),
                            testTag = "inspect_place_btn"
                        )
                    }
                }
            }
        }

        // 4. On-Screen D-Pad for Touch/Mouse emulator testing (Bottom Left)
        OnScreenDpad(
            onPan = { dx, dy -> viewModel.panBy(dx * 0.5f, dy * 0.5f) },
            onCenterClick = {
                if (uiState.selectedLocation != null) {
                    viewModel.setDetailsOpen(true)
                } else {
                    viewModel.toggleInteractionMode()
                }
            },
            onZoomIn = { viewModel.zoomIn() },
            onZoomOut = { viewModel.zoomOut() },
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 24.dp, bottom = 16.dp)
        )

        // 5. At the very bottom: "Show Your Current Location" Button & TV Remote Guide (Bottom Center)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        ) {
            // Requested Button at the bottom
            TvCurrentLocationBottomBar(
                onClick = {
                    val msg = viewModel.showCurrentLocation()
                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // TV Mode Badge (Toggles between Pan & Menu)
            TvRemoteControlGuide(
                currentMode = uiState.interactionMode,
                onToggleMode = { viewModel.toggleInteractionMode() }
            )
        }

        // 6. Floating Zoom In/Out Buttons (Right Center)
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = TvSurface.copy(alpha = 0.92f),
            border = androidx.compose.foundation.BorderStroke(1.dp, TvSurfaceVariant),
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 24.dp)
        ) {
            Column(modifier = Modifier.padding(6.dp)) {
                TvFocusButton(
                    text = "+",
                    onClick = { viewModel.zoomIn() },
                    accentColor = GoogleBlue,
                    testTag = "zoom_in_btn",
                    modifier = Modifier.size(44.dp)
                )
                Spacer(modifier = Modifier.height(6.dp))
                TvFocusButton(
                    text = "-",
                    onClick = { viewModel.zoomOut() },
                    accentColor = GoogleBlue,
                    testTag = "zoom_out_btn",
                    modifier = Modifier.size(44.dp)
                )
            }
        }

        // 7. Modals
        if (uiState.isDetailsOpen && uiState.selectedLocation != null) {
            TvLocationDetailDialog(
                location = uiState.selectedLocation!!,
                onClose = { viewModel.setDetailsOpen(false) },
                onFlyTo = {
                    viewModel.flyToLocation(uiState.selectedLocation!!)
                    viewModel.setDetailsOpen(false)
                }
            )
        }

        if (uiState.isLayersMenuOpen) {
            TvLayersDialog(
                currentLayer = uiState.activeLayer,
                activeOverlays = uiState.activeOverlays,
                onSelectLayer = { viewModel.setLayer(it) },
                onToggleOverlay = { viewModel.toggleOverlay(it) },
                onClose = { viewModel.setLayersMenuOpen(false) }
            )
        }

        if (uiState.isRoutePlannerOpen) {
            TvRoutePlannerDialog(
                origin = uiState.routeOrigin,
                destination = uiState.routeDestination,
                allLocations = MapDataRepository.curatedLocations,
                onSelectOrigin = { viewModel.setRouteOrigin(it) },
                onSelectDestination = { viewModel.setRouteDestination(it) },
                onCalculate = { viewModel.calculateRouteBetweenPoints() },
                onClose = { viewModel.setRoutePlannerOpen(false) }
            )
        }
    }
}
