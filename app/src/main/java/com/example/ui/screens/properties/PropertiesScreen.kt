package com.example.ui.screens.properties

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.HackathonDemoBadge
import com.example.ui.components.PropertyCard
import com.example.ui.components.PropertyFilterBottomSheet
import com.example.ui.components.SafeNestBottomNavBar
import com.example.ui.navigation.Screen
import com.example.ui.theme.BackgroundLight
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.NavyDark
import com.example.ui.theme.SafeGreen
import com.example.ui.theme.TealContainer
import com.example.ui.theme.TealPrimary
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.SafeNestViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PropertiesScreen(
    viewModel: SafeNestViewModel,
    onNavigateToRoute: (String) -> Unit,
    onSelectProperty: (String) -> Unit
) {
    val properties by viewModel.properties.collectAsState()
    val allRawProperties by viewModel.rawProperties.collectAsState()
    val filterState by viewModel.filterState.collectAsState()

    var showSortMenu by remember { mutableStateOf(false) }
    var showFilterBottomSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { HackathonDemoBadge() },
        bottomBar = {
            SafeNestBottomNavBar(
                currentRoute = Screen.Properties.route,
                onNavigate = onNavigateToRoute
            )
        },
        containerColor = BackgroundLight
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search & Filter Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .border(1.dp, BorderSubtle)
                    .padding(16.dp)
            ) {
                Text(
                    text = "Find Verified Safe Homes",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyDark
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = filterState.searchQuery,
                        onValueChange = { viewModel.updateFilters(filterState.copy(searchQuery = it)) },
                        placeholder = { Text("Search city, locality, property...", fontSize = 13.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary) },
                        trailingIcon = {
                            if (filterState.searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.updateFilters(filterState.copy(searchQuery = "")) }) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear search", tint = TextSecondary, modifier = Modifier.size(16.dp))
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = BackgroundLight,
                            unfocusedContainerColor = BackgroundLight,
                            focusedBorderColor = TealPrimary,
                            unfocusedBorderColor = BorderSubtle
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("property_search_input")
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // Dedicated Bottom Sheet Filter Button
                    Box {
                        IconButton(
                            onClick = { showFilterBottomSheet = true },
                            modifier = Modifier
                                .background(
                                    if (filterState.activeFilterCount > 0) TealPrimary else TealContainer,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .testTag("open_filter_sheet_btn")
                        ) {
                            Box(contentAlignment = Alignment.TopEnd) {
                                Icon(
                                    imageVector = Icons.Default.Tune,
                                    contentDescription = "Filter Properties",
                                    tint = if (filterState.activeFilterCount > 0) Color.White else TealPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Sort Button Dropdown
                    Box {
                        IconButton(
                            onClick = { showSortMenu = true },
                            modifier = Modifier
                                .background(TealContainer, shape = RoundedCornerShape(10.dp))
                                .testTag("property_sort_btn")
                        ) {
                            Icon(Icons.Default.Sort, contentDescription = "Sort", tint = TealPrimary)
                        }

                        DropdownMenu(
                            expanded = showSortMenu,
                            onDismissRequest = { showSortMenu = false }
                        ) {
                            listOf("Trust Score", "Rent: Low to High", "Rent: High to Low", "Safety Score").forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option, fontSize = 13.sp) },
                                    onClick = {
                                        viewModel.updateFilters(filterState.copy(sortBy = option))
                                        showSortMenu = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Active Filter Chips Summary (with option to open bottom sheet or clear filters)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "FILTERS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary,
                            letterSpacing = 0.5.sp
                        )
                        if (filterState.activeFilterCount > 0) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .background(TealPrimary, CircleShape)
                                    .padding(horizontal = 7.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "${filterState.activeFilterCount}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (filterState.activeFilterCount > 0) {
                            TextButton(
                                onClick = { viewModel.resetFilters() },
                                modifier = Modifier.height(28.dp)
                            ) {
                                Text("Clear All", fontSize = 11.sp, color = TealPrimary, fontWeight = FontWeight.Bold)
                            }
                        }

                        TextButton(
                            onClick = { showFilterBottomSheet = true },
                            modifier = Modifier.height(28.dp).testTag("open_filters_text_btn")
                        ) {
                            Text("Adjust Filters...", fontSize = 11.5.sp, color = TealPrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Active Filter Removable Tags
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (filterState.selectedCity != "All") {
                        ActiveRemovableChip(
                            label = "City: ${filterState.selectedCity}",
                            onRemove = { viewModel.updateFilters(filterState.copy(selectedCity = "All")) }
                        )
                    }
                    if (filterState.selectedLocality != "All") {
                        ActiveRemovableChip(
                            label = "Area: ${filterState.selectedLocality}",
                            onRemove = { viewModel.updateFilters(filterState.copy(selectedLocality = "All")) }
                        )
                    }
                    if (filterState.selectedType != "All") {
                        ActiveRemovableChip(
                            label = "Type: ${filterState.selectedType}",
                            onRemove = { viewModel.updateFilters(filterState.copy(selectedType = "All")) }
                        )
                    }
                    if (filterState.minRent > 0 || filterState.maxRent < 35000) {
                        ActiveRemovableChip(
                            label = "Rent: ₹${filterState.minRent}-₹${filterState.maxRent}",
                            onRemove = { viewModel.updateFilters(filterState.copy(minRent = 0, maxRent = 35000)) }
                        )
                    }
                    if (filterState.selectedGender != "All") {
                        ActiveRemovableChip(
                            label = "Preference: ${filterState.selectedGender}",
                            onRemove = { viewModel.updateFilters(filterState.copy(selectedGender = "All")) }
                        )
                    }
                    if (filterState.verifiedOnly) {
                        ActiveRemovableChip(
                            label = "Verified Only",
                            onRemove = { viewModel.updateFilters(filterState.copy(verifiedOnly = false)) }
                        )
                    }
                    if (filterState.minSafetyScore > 0) {
                        ActiveRemovableChip(
                            label = "Safety: ${filterState.minSafetyScore}%+",
                            onRemove = { viewModel.updateFilters(filterState.copy(minSafetyScore = 0)) }
                        )
                    }
                }
            }

            // Results count + active sorting text
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${properties.size} Safe Properties Found",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )
                Text(
                    text = "Sorted by: ${filterState.sortBy}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = TealPrimary
                )
            }

            // Property List
            if (properties.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Default.FilterList, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("No properties match your current filters.", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = NavyDark)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Try adjusting price range, city, or property type.", fontSize = 12.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { viewModel.resetFilters() },
                        colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                    ) {
                        Text("Reset All Filters")
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 20.dp)
                ) {
                    items(properties, key = { it.id }) { property ->
                        PropertyCard(
                            property = property,
                            onCardClick = { onSelectProperty(property.id) },
                            onSaveToggle = { viewModel.toggleSaveProperty(property.id, property.isSaved) }
                        )
                    }
                }
            }
        }
    }

    // Property Filter Bottom Sheet Dialog Component
    if (showFilterBottomSheet) {
        PropertyFilterBottomSheet(
            filterState = filterState,
            allProperties = allRawProperties,
            onApplyFilters = { updatedFilter ->
                viewModel.updateFilters(updatedFilter)
            },
            onResetFilters = {
                viewModel.resetFilters()
            },
            onDismissRequest = {
                showFilterBottomSheet = false
            }
        )
    }
}

@Composable
private fun ActiveRemovableChip(
    label: String,
    onRemove: () -> Unit
) {
    Box(
        modifier = Modifier
            .background(TealContainer, RoundedCornerShape(16.dp))
            .border(1.dp, TealPrimary.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            .clickable(onClick = onRemove)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = label, fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = TealPrimary)
            Spacer(modifier = Modifier.width(4.dp))
            Icon(Icons.Default.Close, contentDescription = "Remove filter", tint = TealPrimary, modifier = Modifier.size(12.dp))
        }
    }
}

