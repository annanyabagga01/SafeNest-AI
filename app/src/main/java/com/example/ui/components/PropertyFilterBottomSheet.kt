package com.example.ui.components

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.HomeWork
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.SheetState
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
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
import com.example.data.model.Property
import com.example.ui.theme.BackgroundLight
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.NavyDark
import com.example.ui.theme.SafeGreen
import com.example.ui.theme.TealContainer
import com.example.ui.theme.TealPrimary
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.PropertyFilterState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PropertyFilterBottomSheet(
    filterState: PropertyFilterState,
    allProperties: List<Property>,
    onApplyFilters: (PropertyFilterState) -> Unit,
    onResetFilters: () -> Unit,
    onDismissRequest: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var pendingFilter by remember(filterState) { mutableStateOf(filterState) }

    // Calculate matching count for pending filters in real-time
    val matchingCount = remember(pendingFilter, allProperties) {
        allProperties.count { prop ->
            val matchesQuery = pendingFilter.searchQuery.isEmpty() ||
                    prop.title.contains(pendingFilter.searchQuery, ignoreCase = true) ||
                    prop.locality.contains(pendingFilter.searchQuery, ignoreCase = true) ||
                    prop.city.contains(pendingFilter.searchQuery, ignoreCase = true) ||
                    prop.type.contains(pendingFilter.searchQuery, ignoreCase = true)
            val matchesCity = pendingFilter.selectedCity == "All" || prop.city.equals(pendingFilter.selectedCity, ignoreCase = true)
            val matchesLocality = pendingFilter.selectedLocality == "All" || prop.locality.contains(pendingFilter.selectedLocality, ignoreCase = true)
            val matchesType = pendingFilter.selectedType == "All" || prop.type.equals(pendingFilter.selectedType, ignoreCase = true)
            val matchesGender = pendingFilter.selectedGender == "All" || prop.genderPreference.equals(pendingFilter.selectedGender, ignoreCase = true)
            val matchesRent = prop.monthlyRent in pendingFilter.minRent..pendingFilter.maxRent
            val matchesVerified = !pendingFilter.verifiedOnly || (prop.propertyVerified && prop.ownerVerified)
            val matchesSafety = prop.safetyScore >= pendingFilter.minSafetyScore

            matchesQuery && matchesCity && matchesLocality && matchesType && matchesGender && matchesRent && matchesVerified && matchesSafety
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .width(42.dp)
                    .height(5.dp)
                    .background(Color(0xFFCBD5E1), CircleShape)
            )
        },
        modifier = Modifier.testTag("property_filter_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            // Sheet Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(TealContainer, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = null,
                            tint = TealPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Filter Rental Properties",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = NavyDark
                            )
                            if (pendingFilter.activeFilterCount > 0) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .background(TealPrimary, RoundedCornerShape(12.dp))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "${pendingFilter.activeFilterCount} Active",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                        Text(
                            text = "Narrow down search by price, city, type & safety",
                            fontSize = 11.5.sp,
                            color = TextSecondary
                        )
                    }
                }

                IconButton(
                    onClick = onDismissRequest,
                    modifier = Modifier.testTag("close_filter_sheet_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Filters",
                        tint = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Scrollable Content Block
            Column(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .verticalScroll(rememberScrollState())
            ) {
                // 1. PRICE RANGE FILTER
                FilterSectionHeader(
                    icon = Icons.Default.Payments,
                    title = "Monthly Price Range",
                    subtitle = "₹${pendingFilter.minRent} - ₹${pendingFilter.maxRent}/month"
                )

                Spacer(modifier = Modifier.height(6.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BackgroundLight, RoundedCornerShape(14.dp))
                        .border(1.dp, BorderSubtle, RoundedCornerShape(14.dp))
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Min: ₹${pendingFilter.minRent}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TealPrimary
                        )
                        Text(
                            text = "Max: ₹${pendingFilter.maxRent}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TealPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    RangeSlider(
                        value = pendingFilter.minRent.toFloat()..pendingFilter.maxRent.toFloat(),
                        onValueChange = { range ->
                            pendingFilter = pendingFilter.copy(
                                minRent = (range.start / 1000).toInt() * 1000,
                                maxRent = (range.endInclusive / 1000).toInt() * 1000
                            )
                        },
                        valueRange = 0f..40000f,
                        steps = 39,
                        colors = SliderDefaults.colors(
                            thumbColor = TealPrimary,
                            activeTrackColor = TealPrimary,
                            inactiveTrackColor = Color(0xFFE2E8F0)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("price_range_slider")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Quick Price Preset Chips
                    Text("Quick Presets:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                    Spacer(modifier = Modifier.height(6.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val presets = listOf(
                            "Under ₹10k" to (0 to 10000),
                            "₹10k - ₹20k" to (10000 to 20000),
                            "₹20k - ₹30k" to (20000 to 30000),
                            "₹30k+" to (30000 to 40000),
                            "All Prices" to (0 to 35000)
                        )
                        presets.forEach { (label, range) ->
                            val isSelected = pendingFilter.minRent == range.first && pendingFilter.maxRent == range.second
                            Box(
                                modifier = Modifier
                                    .background(
                                        if (isSelected) TealPrimary else Color.White,
                                        RoundedCornerShape(16.dp)
                                    )
                                    .border(
                                        1.dp,
                                        if (isSelected) TealPrimary else BorderSubtle,
                                        RoundedCornerShape(16.dp)
                                    )
                                    .clickable {
                                        pendingFilter = pendingFilter.copy(
                                            minRent = range.first,
                                            maxRent = range.second
                                        )
                                    }
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else NavyDark
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // 2. LOCATION & LOCALITY FILTER
                FilterSectionHeader(
                    icon = Icons.Default.LocationOn,
                    title = "Location & City",
                    subtitle = if (pendingFilter.selectedCity == "All") "All Cities" else "${pendingFilter.selectedCity} • ${pendingFilter.selectedLocality}"
                )

                Spacer(modifier = Modifier.height(6.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BackgroundLight, RoundedCornerShape(14.dp))
                        .border(1.dp, BorderSubtle, RoundedCornerShape(14.dp))
                        .padding(14.dp)
                ) {
                    Text("Select City:", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                    Spacer(modifier = Modifier.height(6.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("All", "Delhi", "Noida", "Gurugram").forEach { city ->
                            val isSelected = pendingFilter.selectedCity == city
                            FilterChoiceChip(
                                label = if (city == "All") "All Cities" else city,
                                isSelected = isSelected,
                                onClick = { pendingFilter = pendingFilter.copy(selectedCity = city, selectedLocality = "All") },
                                testTag = "filter_city_${city.lowercase()}"
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Popular Locality / Neighborhood:", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                    Spacer(modifier = Modifier.height(6.dp))
                    val localities = listOf(
                        "All",
                        "Hauz Khas",
                        "Saket",
                        "Sector 62",
                        "Noida Expressway",
                        "DLF Phase 3",
                        "Cyber City",
                        "Greater Noida"
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        localities.forEach { loc ->
                            val isSelected = pendingFilter.selectedLocality == loc
                            FilterChoiceChip(
                                label = if (loc == "All") "All Neighborhoods" else loc,
                                isSelected = isSelected,
                                onClick = { pendingFilter = pendingFilter.copy(selectedLocality = loc) },
                                testTag = "filter_locality_${loc.lowercase().replace(" ", "_")}"
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // 3. PROPERTY TYPE FILTER
                FilterSectionHeader(
                    icon = Icons.Default.HomeWork,
                    title = "Property Type",
                    subtitle = if (pendingFilter.selectedType == "All") "All Types" else pendingFilter.selectedType
                )

                Spacer(modifier = Modifier.height(6.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BackgroundLight, RoundedCornerShape(14.dp))
                        .border(1.dp, BorderSubtle, RoundedCornerShape(14.dp))
                        .padding(14.dp)
                ) {
                    val propertyTypes = listOf("All", "PG/Hostel", "1BHK", "2BHK", "Studio")
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        propertyTypes.forEach { type ->
                            val isSelected = pendingFilter.selectedType == type
                            FilterChoiceChip(
                                label = if (type == "All") "All Property Types" else type,
                                isSelected = isSelected,
                                onClick = { pendingFilter = pendingFilter.copy(selectedType = type) },
                                testTag = "filter_type_${type.lowercase().replace("/", "_")}"
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // 4. TENANT PREFERENCE FILTER
                FilterSectionHeader(
                    icon = Icons.Default.People,
                    title = "Tenant Preference",
                    subtitle = if (pendingFilter.selectedGender == "All") "Any Tenant" else pendingFilter.selectedGender
                )

                Spacer(modifier = Modifier.height(6.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BackgroundLight, RoundedCornerShape(14.dp))
                        .border(1.dp, BorderSubtle, RoundedCornerShape(14.dp))
                        .padding(14.dp)
                ) {
                    val genders = listOf("All", "Girls Only", "Boys Only", "Unisex")
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        genders.forEach { gender ->
                            val isSelected = pendingFilter.selectedGender == gender
                            FilterChoiceChip(
                                label = if (gender == "All") "All Preferences" else gender,
                                isSelected = isSelected,
                                onClick = { pendingFilter = pendingFilter.copy(selectedGender = gender) },
                                testTag = "filter_gender_${gender.lowercase().replace(" ", "_")}"
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // 5. SAFETY & VERIFICATION
                FilterSectionHeader(
                    icon = Icons.Default.Security,
                    title = "Trust & Verification",
                    subtitle = "SafeNest Verified Standards"
                )

                Spacer(modifier = Modifier.height(6.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BackgroundLight, RoundedCornerShape(14.dp))
                        .border(1.dp, BorderSubtle, RoundedCornerShape(14.dp))
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = SafeGreen, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "100% Verified Only",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NavyDark
                                )
                                Text(
                                    text = "Verified property & owner background badge",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                        Switch(
                            checked = pendingFilter.verifiedOnly,
                            onCheckedChange = { pendingFilter = pendingFilter.copy(verifiedOnly = it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = TealPrimary
                            ),
                            modifier = Modifier.testTag("filter_verified_switch")
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Min Safety Rating:", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                    Spacer(modifier = Modifier.height(6.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(0 to "Any Safety Score", 80 to "80%+ High Safety", 90 to "90%+ Top Safety").forEach { (score, label) ->
                            val isSelected = pendingFilter.minSafetyScore == score
                            FilterChoiceChip(
                                label = label,
                                isSelected = isSelected,
                                onClick = { pendingFilter = pendingFilter.copy(minSafetyScore = score) },
                                testTag = "filter_safety_$score"
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // 6. SORT BY
                FilterSectionHeader(
                    icon = Icons.Default.Sort,
                    title = "Sort Results By",
                    subtitle = pendingFilter.sortBy
                )

                Spacer(modifier = Modifier.height(6.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BackgroundLight, RoundedCornerShape(14.dp))
                        .border(1.dp, BorderSubtle, RoundedCornerShape(14.dp))
                        .padding(14.dp)
                ) {
                    val sortOptions = listOf("Trust Score", "Rent: Low to High", "Rent: High to Low", "Safety Score")
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        sortOptions.forEach { option ->
                            val isSelected = pendingFilter.sortBy == option
                            FilterChoiceChip(
                                label = option,
                                isSelected = isSelected,
                                onClick = { pendingFilter = pendingFilter.copy(sortBy = option) },
                                testTag = "filter_sort_${option.lowercase().replace(":", "").replace(" ", "_")}"
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            // Sheet Footer Action Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = {
                        val reset = PropertyFilterState(searchQuery = filterState.searchQuery)
                        pendingFilter = reset
                        onResetFilters()
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .height(48.dp)
                        .weight(0.4f)
                        .testTag("reset_filters_btn")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Refresh, contentDescription = "Reset", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Reset All", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Button(
                    onClick = {
                        onApplyFilters(pendingFilter)
                        onDismissRequest()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .height(48.dp)
                        .weight(0.6f)
                        .testTag("apply_filters_btn")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Show $matchingCount Properties",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterSectionHeader(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = NavyDark)
        }
        Text(text = subtitle, fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold, color = TealPrimary)
    }
}

@Composable
private fun FilterChoiceChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Box(
        modifier = Modifier
            .background(
                if (isSelected) TealPrimary else Color.White,
                RoundedCornerShape(18.dp)
            )
            .border(
                1.dp,
                if (isSelected) TealPrimary else BorderSubtle,
                RoundedCornerShape(18.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .testTag(testTag)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (isSelected) {
                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color.White else TextPrimary
            )
        }
    }
}
