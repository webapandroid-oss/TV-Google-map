package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.LocationPoint
import com.example.model.MapLayerType
import com.example.model.MapOverlay
import com.example.model.NavigationRoute
import com.example.model.TvInteractionMode
import com.example.ui.theme.GoogleBlue
import com.example.ui.theme.GoogleGreen
import com.example.ui.theme.GoogleRed
import com.example.ui.theme.GoogleYellow
import com.example.ui.theme.TvAccentFocus
import com.example.ui.theme.TvBackground
import com.example.ui.theme.TvBorderFocus
import com.example.ui.theme.TvSurface
import com.example.ui.theme.TvSurfaceElevated
import com.example.ui.theme.TvSurfaceVariant
import com.example.ui.theme.TvTextPrimary
import com.example.ui.theme.TvTextSecondary

@Composable
fun TvFocusButton(
    text: String,
    icon: ImageVector? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    accentColor: Color = GoogleBlue,
    testTag: String = "tv_focus_button"
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val scale by animateFloatAsState(targetValue = if (isFocused) 1.08f else 1.0f, label = "buttonScale")
    val borderColor by animateColorAsState(
        targetValue = when {
            isFocused -> TvBorderFocus
            isSelected -> accentColor
            else -> Color.Transparent
        },
        label = "buttonBorder"
    )
    val bgColor by animateColorAsState(
        targetValue = when {
            isFocused -> TvSurfaceElevated
            isSelected -> accentColor.copy(alpha = 0.25f)
            else -> TvSurface
        },
        label = "buttonBg"
    )

    Surface(
        onClick = onClick,
        interactionSource = interactionSource,
        shape = RoundedCornerShape(12.dp),
        color = bgColor,
        border = BorderStroke(if (isFocused) 2.5.dp else if (isSelected) 1.5.dp else 0.dp, borderColor),
        modifier = modifier
            .testTag(testTag)
            .scale(scale)
            .focusable(interactionSource = interactionSource)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isFocused || isSelected) accentColor else TvTextPrimary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                color = if (isFocused) TvTextPrimary else if (isSelected) accentColor else TvTextPrimary,
                fontSize = 14.sp,
                fontWeight = if (isFocused || isSelected) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}

@Composable
fun TvLocationCard(
    location: LocationPoint,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val scale by animateFloatAsState(targetValue = if (isFocused) 1.06f else 1.0f, label = "cardScale")

    Card(
        onClick = onClick,
        interactionSource = interactionSource,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isFocused) TvSurfaceElevated else TvSurface
        ),
        border = BorderStroke(
            width = if (isFocused) 2.5.dp else if (isSelected) 1.5.dp else 1.dp,
            color = if (isFocused) TvBorderFocus else if (isSelected) GoogleBlue else TvSurfaceVariant
        ),
        modifier = modifier
            .testTag("location_card_${location.id}")
            .scale(scale)
            .focusable(interactionSource = interactionSource)
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = location.imageUrl,
                contentDescription = location.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(10.dp))
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = location.name,
                    color = TvTextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${location.city}, ${location.country}",
                    color = TvTextSecondary,
                    fontSize = 12.sp,
                    maxLines = 1
                )
            }

            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(GoogleGreen)
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
        }
    }
}

@Composable
fun TvRemoteControlGuide(
    currentMode: TvInteractionMode,
    onToggleMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onToggleMode,
        shape = RoundedCornerShape(24.dp),
        color = TvSurface.copy(alpha = 0.95f),
        border = BorderStroke(
            1.5.dp,
            if (currentMode == TvInteractionMode.MAP_PAN) GoogleYellow else TvBorderFocus
        ),
        modifier = modifier
            .testTag("tv_remote_guide")
            .shadow(12.dp, RoundedCornerShape(24.dp))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(if (currentMode == TvInteractionMode.MAP_PAN) GoogleYellow else GoogleBlue)
            )

            Text(
                text = currentMode.badge,
                color = TvTextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )

            Box(
                modifier = Modifier
                    .background(TvSurfaceVariant, RoundedCornerShape(6.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "Press OK to toggle",
                    color = TvTextSecondary,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
fun OnScreenDpad(
    onPan: (Float, Float) -> Unit,
    onCenterClick: () -> Unit,
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = TvSurface.copy(alpha = 0.92f),
        border = BorderStroke(1.dp, TvSurfaceVariant),
        modifier = modifier
            .testTag("on_screen_dpad")
            .padding(8.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(8.dp)
        ) {
            // Top: Up arrow
            IconButton(
                onClick = { onPan(0f, -1f) },
                modifier = Modifier.size(38.dp).testTag("dpad_up")
            ) {
                Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Pan Up", tint = TvTextPrimary)
            }

            // Middle row: Left, Center (OK), Right
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                IconButton(
                    onClick = { onPan(-1f, 0f) },
                    modifier = Modifier.size(38.dp).testTag("dpad_left")
                ) {
                    Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Pan Left", tint = TvTextPrimary)
                }

                Surface(
                    onClick = onCenterClick,
                    shape = CircleShape,
                    color = GoogleBlue,
                    modifier = Modifier.size(34.dp).testTag("dpad_center")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("OK", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Black)
                    }
                }

                IconButton(
                    onClick = { onPan(1f, 0f) },
                    modifier = Modifier.size(38.dp).testTag("dpad_right")
                ) {
                    Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Pan Right", tint = TvTextPrimary)
                }
            }

            // Bottom: Down arrow
            IconButton(
                onClick = { onPan(0f, 1f) },
                modifier = Modifier.size(38.dp).testTag("dpad_down")
            ) {
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Pan Down", tint = TvTextPrimary)
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Zoom in / out quick buttons
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Surface(
                    onClick = onZoomIn,
                    shape = RoundedCornerShape(8.dp),
                    color = TvSurfaceElevated,
                    modifier = Modifier.size(32.dp).testTag("dpad_zoom_in")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Add, contentDescription = "Zoom In", tint = TvTextPrimary, modifier = Modifier.size(18.dp))
                    }
                }
                Surface(
                    onClick = onZoomOut,
                    shape = RoundedCornerShape(8.dp),
                    color = TvSurfaceElevated,
                    modifier = Modifier.size(32.dp).testTag("dpad_zoom_out")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Remove, contentDescription = "Zoom Out", tint = TvTextPrimary, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun TvLocationDetailDialog(
    location: LocationPoint,
    onClose: () -> Unit,
    onFlyTo: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black.copy(alpha = 0.65f))
            .clickable(onClick = onClose),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = TvSurface,
            border = BorderStroke(2.dp, GoogleBlue),
            modifier = Modifier
                .width(520.dp)
                .padding(20.dp)
                .clickable(enabled = false) {}
                .testTag("tv_location_detail_modal")
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = location.name,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = TvTextPrimary
                        )
                        Text(
                            text = "${location.city}, ${location.country}",
                            fontSize = 14.sp,
                            color = GoogleBlue
                        )
                    }

                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TvTextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                AsyncImage(
                    model = location.imageUrl,
                    contentDescription = location.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(14.dp))
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = location.description,
                    color = TvTextPrimary,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(TvSurfaceVariant, RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Text(
                            text = "TRIVIA & FACTS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoogleYellow
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = location.trivia,
                            fontSize = 13.sp,
                            color = TvTextPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Coordinates: ${String.format("%.4f", location.latitude)}°N, ${String.format("%.4f", location.longitude)}°E | Elevation: ${location.elevationMeters}m",
                            fontSize = 11.sp,
                            color = TvTextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TvFocusButton(
                        text = "Close",
                        onClick = onClose,
                        modifier = Modifier.padding(end = 12.dp)
                    )
                    TvFocusButton(
                        text = "Fly To Place",
                        icon = Icons.Default.NearMe,
                        onClick = onFlyTo,
                        isSelected = true,
                        accentColor = GoogleGreen
                    )
                }
            }
        }
    }
}

@Composable
fun TvLayersDialog(
    currentLayer: MapLayerType,
    activeOverlays: Set<MapOverlay>,
    onSelectLayer: (MapLayerType) -> Unit,
    onToggleOverlay: (MapOverlay) -> Unit,
    onClose: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black.copy(alpha = 0.65f))
            .clickable(onClick = onClose),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = TvSurface,
            border = BorderStroke(2.dp, GoogleBlue),
            modifier = Modifier
                .width(460.dp)
                .padding(20.dp)
                .clickable(enabled = false) {}
                .testTag("tv_layers_dialog")
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Map Types & Layers",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TvTextPrimary
                    )
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TvTextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "SELECT MAP STYLE",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoogleBlue
                )

                Spacer(modifier = Modifier.height(8.dp))

                MapLayerType.values().forEach { layer ->
                    TvFocusButton(
                        text = "${layer.label} — ${layer.description}",
                        onClick = { onSelectLayer(layer) },
                        isSelected = (currentLayer == layer),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "MAP OVERLAYS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoogleGreen
                )

                Spacer(modifier = Modifier.height(8.dp))

                MapOverlay.values().forEach { overlay ->
                    val isChecked = activeOverlays.contains(overlay)
                    TvFocusButton(
                        text = overlay.label,
                        icon = if (isChecked) Icons.Default.Check else null,
                        onClick = { onToggleOverlay(overlay) },
                        isSelected = isChecked,
                        accentColor = GoogleGreen,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TvFocusButton(
                        text = "Apply & Done",
                        onClick = onClose,
                        isSelected = true,
                        accentColor = GoogleBlue
                    )
                }
            }
        }
    }
}

@Composable
fun TvRoutePlannerDialog(
    origin: LocationPoint?,
    destination: LocationPoint?,
    allLocations: List<LocationPoint>,
    onSelectOrigin: (LocationPoint) -> Unit,
    onSelectDestination: (LocationPoint) -> Unit,
    onCalculate: () -> Unit,
    onClose: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black.copy(alpha = 0.65f))
            .clickable(onClick = onClose),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = TvSurface,
            border = BorderStroke(2.dp, GoogleYellow),
            modifier = Modifier
                .width(500.dp)
                .padding(20.dp)
                .clickable(enabled = false) {}
                .testTag("tv_route_planner_dialog")
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TV Route Navigation",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TvTextPrimary
                    )
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TvTextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(text = "Origin Point: ${origin?.name ?: "Select Location"}", color = GoogleBlue, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(vertical = 6.dp)
                ) {
                    allLocations.take(3).forEach { loc ->
                        TvFocusButton(
                            text = loc.city,
                            onClick = { onSelectOrigin(loc) },
                            isSelected = (origin?.id == loc.id),
                            accentColor = GoogleBlue
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(text = "Destination: ${destination?.name ?: "Select Location"}", color = GoogleRed, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(vertical = 6.dp)
                ) {
                    allLocations.drop(3).take(3).forEach { loc ->
                        TvFocusButton(
                            text = loc.city,
                            onClick = { onSelectDestination(loc) },
                            isSelected = (destination?.id == loc.id),
                            accentColor = GoogleRed
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TvFocusButton(
                        text = "Cancel",
                        onClick = onClose,
                        modifier = Modifier.padding(end = 12.dp)
                    )
                    TvFocusButton(
                        text = "Navigate Route",
                        icon = Icons.Default.Directions,
                        onClick = onCalculate,
                        isSelected = true,
                        accentColor = GoogleYellow
                    )
                }
            }
        }
    }
}

@Composable
fun TvAutoKeyboardSearchBox(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearchSubmit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var isFocused by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = {
            Text(
                text = "Search on TV Google Map",
                color = TvTextSecondary,
                fontSize = 13.sp
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = if (isFocused) GoogleBlue else TvTextSecondary,
                modifier = Modifier.size(20.dp)
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear",
                        tint = TvTextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(
            onSearch = {
                keyboardController?.hide()
                onSearchSubmit()
            }
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = TvSurfaceElevated,
            unfocusedContainerColor = TvSurface,
            focusedTextColor = TvTextPrimary,
            unfocusedTextColor = TvTextPrimary,
            focusedBorderColor = TvBorderFocus,
            unfocusedBorderColor = TvSurfaceVariant
        ),
        shape = RoundedCornerShape(14.dp),
        modifier = modifier
            .testTag("tv_search_edit_box")
            .onFocusChanged { focusState ->
                isFocused = focusState.isFocused
                if (focusState.isFocused) {
                    // Automatically opens on-screen keyboard when TV remote cursor lands on edit box
                    keyboardController?.show()
                }
            }
            .scale(if (isFocused) 1.04f else 1.0f)
    )
}

@Composable
fun TvQuickSearchOptionPill(
    label: String,
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = "quick_search_pill"
) {
    TvFocusButton(
        text = label,
        icon = icon,
        onClick = onClick,
        accentColor = accentColor,
        testTag = testTag,
        modifier = modifier
    )
}

@Composable
fun TvCurrentLocationBottomBar(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val scale by animateFloatAsState(targetValue = if (isFocused) 1.05f else 1.0f, label = "locationBtnScale")

    Surface(
        onClick = onClick,
        interactionSource = interactionSource,
        shape = RoundedCornerShape(20.dp),
        color = if (isFocused) GoogleBlue else TvSurface.copy(alpha = 0.95f),
        border = BorderStroke(
            width = if (isFocused) 2.5.dp else 1.5.dp,
            color = if (isFocused) Color.White else GoogleBlue
        ),
        shadowElevation = 8.dp,
        modifier = modifier
            .testTag("show_current_location_btn")
            .scale(scale)
            .focusable(interactionSource = interactionSource)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.MyLocation,
                contentDescription = "Current Location",
                tint = if (isFocused) Color.White else GoogleBlue,
                modifier = Modifier.size(22.dp)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = "Show Your Current Location",
                color = if (isFocused) Color.White else TvTextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
        }
    }
}

