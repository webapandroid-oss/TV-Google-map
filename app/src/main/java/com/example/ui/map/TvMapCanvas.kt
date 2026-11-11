package com.example.ui.map

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.LocationPoint
import com.example.model.MapDataRepository
import com.example.model.MapLayerType
import com.example.model.MapOverlay
import com.example.model.NavigationRoute
import com.example.ui.theme.GoogleBlue
import com.example.ui.theme.GoogleGreen
import com.example.ui.theme.GoogleRed
import com.example.ui.theme.GoogleYellow
import com.example.ui.theme.MapContourTerrain
import com.example.ui.theme.MapLandDay
import com.example.ui.theme.MapLandNight
import com.example.ui.theme.MapLandSatellite
import com.example.ui.theme.MapLandTerrain
import com.example.ui.theme.MapOceanDay
import com.example.ui.theme.MapOceanNight
import com.example.ui.theme.MapOceanSatellite
import com.example.ui.theme.MapOceanTerrain
import com.example.ui.theme.MapPark
import com.example.ui.theme.MapRoadNight
import com.example.ui.theme.TvAccentFocus
import com.example.ui.theme.TvSurface
import com.example.ui.theme.TvTextPrimary
import com.example.ui.theme.TvTextSecondary
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.ln
import kotlin.math.sin
import kotlin.math.tan

// Simplied continental landmass polygon coordinates for smooth vector rendering on TV
private val continentalOutlines = listOf(
    // North America
    listOf(-165.0 to 65.0, -140.0 to 70.0, -100.0 to 75.0, -60.0 to 60.0, -65.0 to 45.0, -75.0 to 35.0, -80.0 to 25.0, -90.0 to 30.0, -97.0 to 22.0, -105.0 to 20.0, -117.0 to 32.0, -125.0 to 48.0, -140.0 to 58.0, -165.0 to 65.0),
    // South America
    listOf(-75.0 to 10.0, -60.0 to 5.0, -35.0 to -5.0, -40.0 to -22.0, -55.0 to -35.0, -65.0 to -55.0, -75.0 to -50.0, -72.0 to -30.0, -80.0 to -5.0, -75.0 to 10.0),
    // Europe
    listOf(-10.0 to 36.0, 0.0 to 43.0, -5.0 to 48.0, 2.0 to 51.0, 5.0 to 58.0, 15.0 to 55.0, 25.0 to 70.0, 32.0 to 70.0, 30.0 to 60.0, 25.0 to 40.0, 15.0 to 38.0, -5.0 to 36.0, -10.0 to 36.0),
    // Africa
    listOf(-17.0 to 15.0, -17.0 to 30.0, 10.0 to 37.0, 32.0 to 32.0, 42.0 to 15.0, 51.0 to 10.0, 40.0 to -10.0, 32.0 to -30.0, 20.0 to -34.0, 12.0 to -18.0, 9.0 to 4.0, -17.0 to 15.0),
    // Asia
    listOf(35.0 to 35.0, 40.0 to 45.0, 60.0 to 60.0, 80.0 to 75.0, 130.0 to 72.0, 170.0 to 65.0, 140.0 to 40.0, 120.0 to 30.0, 105.0 to 20.0, 100.0 to 5.0, 80.0 to 10.0, 70.0 to 25.0, 50.0 to 25.0, 35.0 to 35.0),
    // Australia
    listOf(115.0 to -22.0, 130.0 to -12.0, 142.0 to -11.0, 153.0 to -28.0, 150.0 to -38.0, 138.0 to -35.0, 115.0 to -34.0, 115.0 to -22.0)
)

private val MapRoadDay = Color(0xFFFFFFFF)

@Composable
fun TvMapCanvas(
    latitude: Double,
    longitude: Double,
    zoom: Float,
    layer: MapLayerType,
    overlays: Set<MapOverlay>,
    selectedLocation: LocationPoint?,
    activeRoute: NavigationRoute?,
    onPan: (Float, Float) -> Unit,
    onSelectLocation: (LocationPoint) -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAnim by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseRadius"
    )
    val dashPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 40f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "dashPhase"
    )

    val textMeasurer = rememberTextMeasurer()

    Box(modifier = modifier.fillMaxSize().testTag("tv_map_canvas")) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        // Drag gesture on canvas for touch/mouse testing
                        onPan(-dragAmount.x / 400f, -dragAmount.y / 400f)
                    }
                }
        ) {
            val width = size.width
            val height = size.height
            val center = Offset(width / 2f, height / 2f)

            // Zoom scale factor
            val pixelsPerDegree = (256.0 * Math.pow(2.0, zoom.toDouble()) / 360.0).toFloat()

            // 1. Draw Background Ocean based on Layer
            val oceanColor = when (layer) {
                MapLayerType.STANDARD -> MapOceanDay
                MapLayerType.SATELLITE -> MapOceanSatellite
                MapLayerType.TERRAIN -> MapOceanTerrain
                MapLayerType.NIGHT -> MapOceanNight
            }
            drawRect(color = oceanColor)

            // Subtle gradient overlay for satellite/night depth
            if (layer == MapLayerType.SATELLITE || layer == MapLayerType.NIGHT) {
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.5f)),
                        center = center,
                        radius = width * 0.7f
                    )
                )
            }

            // 2. Draw Vector Landmasses
            val landColor = when (layer) {
                MapLayerType.STANDARD -> MapLandDay
                MapLayerType.SATELLITE -> MapLandSatellite
                MapLayerType.TERRAIN -> MapLandTerrain
                MapLayerType.NIGHT -> MapLandNight
            }

            continentalOutlines.forEach { continent ->
                val path = Path()
                var first = true
                continent.forEach { (cLng, cLat) ->
                    val screenX = center.x + ((cLng - longitude).toFloat() * pixelsPerDegree)
                    val screenY = center.y - ((cLat - latitude).toFloat() * pixelsPerDegree)
                    if (first) {
                        path.moveTo(screenX, screenY)
                        first = false
                    } else {
                        path.lineTo(screenX, screenY)
                    }
                }
                path.close()
                drawPath(path = path, color = landColor)

                // Coastline border
                drawPath(
                    path = path,
                    color = when (layer) {
                        MapLayerType.NIGHT -> GoogleBlue.copy(alpha = 0.4f)
                        MapLayerType.TERRAIN -> MapContourTerrain
                        else -> landColor.copy(alpha = 0.8f)
                    },
                    style = Stroke(width = 1.5f)
                )
            }

            // 3. Draw Local Street & Terrain Grid when zoomed in
            if (zoom >= 8f) {
                drawLocalGrid(
                    center = center,
                    width = width,
                    height = height,
                    zoom = zoom,
                    layer = layer
                )
            }

            // 4. Coordinates Graticule Overlay
            if (overlays.contains(MapOverlay.GRID)) {
                drawGraticule(
                    center = center,
                    width = width,
                    height = height,
                    longitude = longitude,
                    latitude = latitude,
                    pixelsPerDegree = pixelsPerDegree,
                    layer = layer,
                    textMeasurer = textMeasurer
                )
            }

            // 5. Traffic Overlay
            if (overlays.contains(MapOverlay.TRAFFIC) && zoom >= 7f) {
                drawTrafficLines(center = center, width = width, height = height, zoom = zoom)
            }

            // 6. Draw Active Navigation Route
            if (activeRoute != null) {
                drawNavigationRoute(
                    route = activeRoute,
                    center = center,
                    longitude = longitude,
                    latitude = latitude,
                    pixelsPerDegree = pixelsPerDegree,
                    dashPhase = dashPhase,
                    textMeasurer = textMeasurer
                )
            }

            // 7. Draw Landmark Pins
            if (overlays.contains(MapOverlay.LANDMARKS)) {
                MapDataRepository.curatedLocations.forEach { loc ->
                    val screenX = center.x + ((loc.longitude - longitude).toFloat() * pixelsPerDegree)
                    val screenY = center.y - ((loc.latitude - latitude).toFloat() * pixelsPerDegree)

                    // Only draw if visible inside canvas bounds
                    if (screenX in -60f..(width + 60f) && screenY in -60f..(height + 60f)) {
                        val isSelected = selectedLocation?.id == loc.id
                        drawLandmarkMarker(
                            loc = loc,
                            x = screenX,
                            y = screenY,
                            isSelected = isSelected,
                            pulseAnim = pulseAnim,
                            textMeasurer = textMeasurer,
                            zoom = zoom
                        )
                    }
                }
            }

            // 8. Draw Center Reticle / Crosshair HUD
            drawCenterReticle(center = center, layer = layer)

            // 9. Compass Rose in top-right
            drawCompass(width = width, textMeasurer = textMeasurer)
        }

        // Live Coordinate & Zoom Badge (Bottom-Right overlay)
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = TvSurface.copy(alpha = 0.90f),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Text(
                text = "${String.format("%.4f", latitude)}°N, ${String.format("%.4f", longitude)}°E  |  Zoom: ${String.format("%.1f", zoom)}x",
                color = TvTextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            )
        }
    }
}

private fun DrawScope.drawLocalGrid(
    center: Offset,
    width: Float,
    height: Float,
    zoom: Float,
    layer: MapLayerType
) {
    val roadColor = if (layer == MapLayerType.NIGHT) MapRoadNight else MapRoadDay
    val spacing = (30f + (zoom - 8f) * 15f).coerceIn(20f, 120f)

    // Minor local street grid
    var x = center.x % spacing
    while (x < width) {
        drawLine(
            color = roadColor.copy(alpha = 0.35f),
            start = Offset(x, 0f),
            end = Offset(x, height),
            strokeWidth = 2f
        )
        x += spacing
    }

    var y = center.y % spacing
    while (y < height) {
        drawLine(
            color = roadColor.copy(alpha = 0.35f),
            start = Offset(0f, y),
            end = Offset(width, y),
            strokeWidth = 2f
        )
        y += spacing
    }

    // Major avenue diagonal lines
    val avenueColor = if (layer == MapLayerType.NIGHT) GoogleBlue.copy(alpha = 0.5f) else GoogleYellow.copy(alpha = 0.6f)
    drawLine(
        color = avenueColor,
        start = Offset(center.x - 200f, center.y - 150f),
        end = Offset(center.x + 300f, center.y + 250f),
        strokeWidth = 5f
    )
    drawLine(
        color = avenueColor,
        start = Offset(center.x - 300f, center.y + 100f),
        end = Offset(center.x + 250f, center.y - 200f),
        strokeWidth = 4f
    )
}

private fun DrawScope.drawGraticule(
    center: Offset,
    width: Float,
    height: Float,
    longitude: Double,
    latitude: Double,
    pixelsPerDegree: Float,
    layer: MapLayerType,
    textMeasurer: TextMeasurer
) {
    val gridColor = when (layer) {
        MapLayerType.NIGHT -> GoogleBlue.copy(alpha = 0.25f)
        MapLayerType.SATELLITE -> Color.White.copy(alpha = 0.2f)
        else -> Color.Gray.copy(alpha = 0.35f)
    }

    // Latitude horizontal lines every 15 degrees
    for (latDeg in -75..75 step 15) {
        val y = center.y - ((latDeg - latitude).toFloat() * pixelsPerDegree)
        if (y in 0f..height) {
            drawLine(
                color = gridColor,
                start = Offset(0f, y),
                end = Offset(width, y),
                strokeWidth = if (latDeg == 0) 2f else 1f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
            )
            val label = if (latDeg == 0) "Equator 0°" else "${Math.abs(latDeg)}°${if (latDeg > 0) "N" else "S"}"
            drawText(
                textMeasurer = textMeasurer,
                text = label,
                style = TextStyle(color = gridColor, fontSize = 10.sp),
                topLeft = Offset(12f, y - 16f)
            )
        }
    }

    // Longitude vertical lines every 15 degrees
    for (lngDeg in -180..180 step 15) {
        val x = center.x + ((lngDeg - longitude).toFloat() * pixelsPerDegree)
        if (x in 0f..width) {
            drawLine(
                color = gridColor,
                start = Offset(x, 0f),
                end = Offset(x, height),
                strokeWidth = if (lngDeg == 0) 2f else 1f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
            )
            val label = if (lngDeg == 0) "Prime Meridian 0°" else "${Math.abs(lngDeg)}°${if (lngDeg > 0) "E" else "W"}"
            drawText(
                textMeasurer = textMeasurer,
                text = label,
                style = TextStyle(color = gridColor, fontSize = 10.sp),
                topLeft = Offset(x + 4f, 8f)
            )
        }
    }
}

private fun DrawScope.drawTrafficLines(center: Offset, width: Float, height: Float, zoom: Float) {
    // Simulated live traffic flow with green, orange, and red congested segments
    val trafficGreen = Color(0xFF00E676)
    val trafficOrange = Color(0xFFFF9100)
    val trafficRed = Color(0xFFFF1744)

    drawLine(
        color = trafficGreen,
        start = Offset(center.x - 300f, center.y + 40f),
        end = Offset(center.x - 50f, center.y + 40f),
        strokeWidth = 4f
    )
    drawLine(
        color = trafficOrange,
        start = Offset(center.x - 50f, center.y + 40f),
        end = Offset(center.x + 80f, center.y + 40f),
        strokeWidth = 4f
    )
    drawLine(
        color = trafficRed,
        start = Offset(center.x + 80f, center.y + 40f),
        end = Offset(center.x + 220f, center.y + 40f),
        strokeWidth = 5f
    )
}

private fun DrawScope.drawNavigationRoute(
    route: NavigationRoute,
    center: Offset,
    longitude: Double,
    latitude: Double,
    pixelsPerDegree: Float,
    dashPhase: Float,
    textMeasurer: TextMeasurer
) {
    val startX = center.x + ((route.origin.longitude - longitude).toFloat() * pixelsPerDegree)
    val startY = center.y - ((route.origin.latitude - latitude).toFloat() * pixelsPerDegree)
    val endX = center.x + ((route.destination.longitude - longitude).toFloat() * pixelsPerDegree)
    val endY = center.y - ((route.destination.latitude - latitude).toFloat() * pixelsPerDegree)

    // Great circle curved path
    val routePath = Path()
    routePath.moveTo(startX, startY)
    val midX = (startX + endX) / 2f
    val midY = (startY + endY) / 2f - 60f // Arched geodesic curve
    routePath.quadraticTo(midX, midY, endX, endY)

    // Glowing underlay
    drawPath(
        path = routePath,
        color = GoogleBlue.copy(alpha = 0.35f),
        style = Stroke(width = 12f)
    )

    // Animated dashed line
    drawPath(
        path = routePath,
        color = GoogleYellow,
        style = Stroke(
            width = 4f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 10f), dashPhase)
        )
    )

    // Distance Label along path
    val routeText = "${route.title}: ${String.format("%.0f", route.distanceKm)} km (${String.format("%.1f", route.flightHours)}h flight)"
    drawText(
        textMeasurer = textMeasurer,
        text = routeText,
        style = TextStyle(
            color = Color.White,
            background = TvSurface.copy(alpha = 0.9f),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        ),
        topLeft = Offset(midX - 100f, midY - 24f)
    )
}

private fun DrawScope.drawLandmarkMarker(
    loc: LocationPoint,
    x: Float,
    y: Float,
    isSelected: Boolean,
    pulseAnim: Float,
    textMeasurer: TextMeasurer,
    zoom: Float
) {
    val isCurrentLocation = loc.id == "my_current_location"

    val pinColor = when {
        isCurrentLocation -> GoogleBlue
        loc.category == com.example.model.LocationCategory.RESTAURANTS -> GoogleRed
        loc.category == com.example.model.LocationCategory.COFFEE -> GoogleYellow
        loc.category == com.example.model.LocationCategory.SHOPS -> GoogleGreen
        loc.category == com.example.model.LocationCategory.SUPERMARKETS -> GoogleBlue
        loc.category == com.example.model.LocationCategory.SHOWROOMS -> TvAccentFocus
        loc.category == com.example.model.LocationCategory.WONDERS -> GoogleRed
        loc.category == com.example.model.LocationCategory.CITIES -> GoogleBlue
        loc.category == com.example.model.LocationCategory.NATURE -> GoogleGreen
        else -> GoogleBlue
    }

    // Special GPS radar ripple for Current Device Location
    if (isCurrentLocation) {
        drawCircle(
            color = GoogleBlue.copy(alpha = 0.25f),
            radius = 36f * pulseAnim,
            center = Offset(x, y)
        )
        drawCircle(
            color = Color.White,
            radius = 14f,
            center = Offset(x, y)
        )
        drawCircle(
            color = GoogleBlue,
            radius = 10f,
            center = Offset(x, y)
        )
        val youAreHereText = "YOU ARE HERE (${loc.city})"
        drawText(
            textMeasurer = textMeasurer,
            text = youAreHereText,
            style = TextStyle(
                color = Color.White,
                background = GoogleBlue,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            ),
            topLeft = Offset(x - 30f, y + 16f)
        )
        return
    }

    // If selected, draw glowing pulse circle
    if (isSelected) {
        drawCircle(
            color = pinColor.copy(alpha = 0.35f),
            radius = 28f * pulseAnim,
            center = Offset(x, y)
        )
        drawCircle(
            color = pinColor,
            radius = 8f,
            center = Offset(x, y)
        )
    }

    // Pin marker shadow
    drawCircle(
        color = Color.Black.copy(alpha = 0.4f),
        radius = 11f,
        center = Offset(x + 2f, y + 2f)
    )

    // Pin marker body
    drawCircle(
        color = pinColor,
        radius = 10f,
        center = Offset(x, y)
    )
    drawCircle(
        color = Color.White,
        radius = 4f,
        center = Offset(x, y)
    )

    // Label if zoomed in or selected
    if (zoom >= 5f || isSelected) {
        val label = loc.name
        val style = TextStyle(
            color = Color.White,
            background = Color.Black.copy(alpha = 0.75f),
            fontSize = if (isSelected) 12.sp else 10.sp,
            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium
        )
        drawText(
            textMeasurer = textMeasurer,
            text = label,
            style = style,
            topLeft = Offset(x - 20f, y + 14f)
        )
    }
}

private fun DrawScope.drawCenterReticle(center: Offset, layer: MapLayerType) {
    val reticleColor = if (layer == MapLayerType.NIGHT) GoogleBlue else GoogleRed

    // Center crosshair lines
    drawLine(
        color = reticleColor,
        start = Offset(center.x - 16f, center.y),
        end = Offset(center.x - 4f, center.y),
        strokeWidth = 2.5f
    )
    drawLine(
        color = reticleColor,
        start = Offset(center.x + 4f, center.y),
        end = Offset(center.x + 16f, center.y),
        strokeWidth = 2.5f
    )
    drawLine(
        color = reticleColor,
        start = Offset(center.x, center.y - 16f),
        end = Offset(center.x, center.y - 4f),
        strokeWidth = 2.5f
    )
    drawLine(
        color = reticleColor,
        start = Offset(center.x, center.y + 4f),
        end = Offset(center.x, center.y + 16f),
        strokeWidth = 2.5f
    )

    // Outer reticle circle
    drawCircle(
        color = reticleColor.copy(alpha = 0.65f),
        radius = 18f,
        center = center,
        style = Stroke(width = 1.5f)
    )
}

private fun DrawScope.drawCompass(width: Float, textMeasurer: TextMeasurer) {
    val compassX = width - 40f
    val compassY = 40f

    // North Pointer Arrow
    val northPath = Path()
    northPath.moveTo(compassX, compassY - 18f)
    northPath.lineTo(compassX - 6f, compassY + 4f)
    northPath.lineTo(compassX, compassY)
    northPath.close()
    drawPath(path = northPath, color = GoogleRed)

    // South Pointer Arrow
    val southPath = Path()
    southPath.moveTo(compassX, compassY + 18f)
    southPath.lineTo(compassX + 6f, compassY - 4f)
    southPath.lineTo(compassX, compassY)
    southPath.close()
    drawPath(path = southPath, color = Color.White.copy(alpha = 0.8f))

    drawText(
        textMeasurer = textMeasurer,
        text = "N",
        style = TextStyle(color = GoogleRed, fontSize = 10.sp, fontWeight = FontWeight.Bold),
        topLeft = Offset(compassX - 4f, compassY - 32f)
    )
}
