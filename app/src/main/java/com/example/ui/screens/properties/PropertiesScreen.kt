package com.example.ui.screens.properties

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import com.example.ui.components.SafeNestBottomNavBar
import com.example.ui.navigation.Screen
import com.example.ui.theme.BackgroundLight
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.NavyDark
import com.example.ui.theme.TealContainer
import com.example.ui.theme.TealPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.SafeNestViewModel

@Composable
fun PropertiesScreen(
    viewModel: SafeNestViewModel,
    onNavigateToRoute: (String) -> Unit,
    onSelectProperty: (String) -> Unit
) {
    val properties by viewModel.properties.collectAsState()
    val filterState by viewModel.filterState.collectAsState()

    var showSortMenu by remember { mutableStateOf(false) }

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
            // Search & Sort Header
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
                        placeholder = { Text("Search city, locality, address...", fontSize = 13.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary) },
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
                            listOf("Trust Score", "Rent: Low to High", "Safety Score").forEach { option ->
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

                Spacer(modifier = Modifier.height(12.dp))

                // City Filter Chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val cities = listOf("All", "Delhi", "Noida", "Gurugram")
                    items(cities) { city ->
                        val isSelected = filterState.selectedCity == city
                        FilterChipItem(
                            label = if (city == "All") "All Cities" else city,
                            isSelected = isSelected,
                            onClick = { viewModel.updateFilters(filterState.copy(selectedCity = city)) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Type & Verified Filter Chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val types = listOf("All", "PG/Hostel", "1BHK", "2BHK", "Studio")
                    items(types) { type ->
                        val isSelected = filterState.selectedType == type
                        FilterChipItem(
                            label = if (type == "All") "All Types" else type,
                            isSelected = isSelected,
                            onClick = { viewModel.updateFilters(filterState.copy(selectedType = type)) }
                        )
                    }

                    item {
                        val isVerifiedOnly = filterState.verifiedOnly
                        Box(
                            modifier = Modifier
                                .background(if (isVerifiedOnly) Color(0xFFDCFCE7) else BackgroundLight, shape = RoundedCornerShape(20.dp))
                                .border(1.dp, if (isVerifiedOnly) Color(0xFF16A34A) else BorderSubtle, shape = RoundedCornerShape(20.dp))
                                .clickable { viewModel.updateFilters(filterState.copy(verifiedOnly = !isVerifiedOnly)) }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "100% Verified Only",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isVerifiedOnly) Color(0xFF15803D) else TextSecondary
                                )
                            }
                        }
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
                    Text("Try relaxing city or budget filters to view more options.", fontSize = 12.sp, color = TextSecondary)
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
}

@Composable
private fun FilterChipItem(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .background(if (isSelected) TealPrimary else Color.White, shape = RoundedCornerShape(20.dp))
            .border(1.dp, if (isSelected) TealPrimary else Color(0xFFCBD5E1), shape = RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 7.dp)
    ) {
        Text(
            text = label,
            fontSize = 12.5.sp,
            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
            color = if (isSelected) Color.White else NavyDark
        )
    }
}
