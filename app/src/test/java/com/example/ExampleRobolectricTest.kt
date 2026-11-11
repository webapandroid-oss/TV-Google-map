package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.LocationCategory
import com.example.model.MapDataRepository
import com.example.model.TvInteractionMode
import com.example.viewmodel.TvMapViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("TV Google Map", appName)
  }

  @Test
  fun `test tv map view model panning and zoom`() {
    val viewModel = TvMapViewModel()
    val initialZoom = viewModel.uiState.value.zoom
    viewModel.zoomIn()
    assertEquals(initialZoom + 1f, viewModel.uiState.value.zoom)

    viewModel.zoomOut()
    assertEquals(initialZoom, viewModel.uiState.value.zoom)

    val initialLat = viewModel.uiState.value.latitude
    viewModel.panBy(0f, 1f)
    assertTrue(viewModel.uiState.value.latitude != initialLat)
  }

  @Test
  fun `test tv interaction mode toggle`() {
    val viewModel = TvMapViewModel()
    assertEquals(TvInteractionMode.MENU_FOCUS, viewModel.uiState.value.interactionMode)

    viewModel.toggleInteractionMode()
    assertEquals(TvInteractionMode.MAP_PAN, viewModel.uiState.value.interactionMode)

    viewModel.toggleInteractionMode()
    assertEquals(TvInteractionMode.MENU_FOCUS, viewModel.uiState.value.interactionMode)
  }

  @Test
  fun `test show current location message and coordinates`() {
    val viewModel = TvMapViewModel()
    val toastMessage = viewModel.showCurrentLocation()

    assertEquals("Your current location is PBW+QJJG, Dhaka 1318, Bangladesh", toastMessage)
    assertEquals("my_current_location", viewModel.uiState.value.selectedLocation?.id)
    assertEquals("Dhaka", viewModel.uiState.value.selectedLocation?.city)
    assertEquals("Bangladesh", viewModel.uiState.value.selectedLocation?.country)
  }

  @Test
  fun `test quick search categories for restaurants coffee shops supermarkets and showrooms`() {
    val viewModel = TvMapViewModel()

    viewModel.quickSearchCategory(LocationCategory.RESTAURANTS, "Restaurant")
    assertTrue(viewModel.uiState.value.filteredLocations.any { it.category == LocationCategory.RESTAURANTS })

    viewModel.quickSearchCategory(LocationCategory.COFFEE, "Coffee")
    assertTrue(viewModel.uiState.value.filteredLocations.any { it.category == LocationCategory.COFFEE })

    viewModel.quickSearchCategory(LocationCategory.SHOPS, "Shop")
    assertTrue(viewModel.uiState.value.filteredLocations.any { it.category == LocationCategory.SHOPS })

    viewModel.quickSearchCategory(LocationCategory.SUPERMARKETS, "Supermarket")
    assertTrue(viewModel.uiState.value.filteredLocations.any { it.category == LocationCategory.SUPERMARKETS })

    viewModel.quickSearchCategory(LocationCategory.SHOWROOMS, "Showroom")
    assertTrue(viewModel.uiState.value.filteredLocations.any { it.category == LocationCategory.SHOWROOMS })
  }

  @Test
  fun `test route calculation`() {
    val paris = MapDataRepository.curatedLocations.first { it.id == "eiffel_tower" }
    val tokyo = MapDataRepository.curatedLocations.first { it.id == "tokyo_shibuya" }
    val route = MapDataRepository.calculateSampleRoute(paris, tokyo)

    assertNotNull(route)
    assertTrue(route.distanceKm > 9000.0)
    assertTrue(route.flightHours > 10.0)
  }
}
