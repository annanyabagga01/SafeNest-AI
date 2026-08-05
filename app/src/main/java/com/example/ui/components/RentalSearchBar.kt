package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BackgroundLight
import com.example.ui.theme.BorderStrong
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.NavyDark
import com.example.ui.theme.TealPrimary
import com.example.ui.theme.TextSecondary

/**
 * Reusable rental search bar component featuring query input, clear button,
 * and horizontal city/type filter chips.
 */
@Composable
fun RentalSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    selectedCity: String,
    onCitySelect: (String) -> Unit,
    selectedType: String,
    onTypeSelect: (String) -> Unit,
    onClearClick: () -> Unit,
    modifier: Modifier = Modifier,
    cities: List<String> = listOf("All", "Delhi", "Noida", "Gurugram"),
    types: List<String> = listOf("All", "PG/Hostel", "1BHK", "2BHK", "Studio")
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .border(1.dp, BorderSubtle)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                placeholder = {
                    Text(
                        text = "Search locality, city, or PG...",
                        fontSize = 13.5.sp,
                        color = TextSecondary
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = TealPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(
                            onClick = onClearClick,
                            modifier = Modifier.testTag("rental_search_clear_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear Search",
                                tint = TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = BackgroundLight,
                    unfocusedContainerColor = BackgroundLight,
                    focusedBorderColor = TealPrimary,
                    unfocusedBorderColor = BorderStrong
                ),
                modifier = Modifier
                    .weight(1f)
                    .testTag("rental_search_input")
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // City Selector Chips
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.FilterList,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "City:",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.width(8.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(cities) { city ->
                    val isSelected = selectedCity.equals(city, ignoreCase = true)
                    SearchFilterChip(
                        label = if (city == "All") "All Cities" else city,
                        isSelected = isSelected,
                        onClick = { onCitySelect(city) },
                        testTag = "city_chip_${city.lowercase()}"
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Property Type Chips
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Type:",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                modifier = Modifier.padding(start = 22.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(types) { type ->
                    val isSelected = selectedType.equals(type, ignoreCase = true)
                    SearchFilterChip(
                        label = if (type == "All") "All Types" else type,
                        isSelected = isSelected,
                        onClick = { onTypeSelect(type) },
                        testTag = "type_chip_${type.lowercase().replace("/", "_")}"
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchFilterChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Box(
        modifier = Modifier
            .background(
                color = if (isSelected) TealPrimary else Color.White,
                shape = RoundedCornerShape(20.dp)
            )
            .border(
                width = 1.dp,
                color = if (isSelected) TealPrimary else BorderStrong,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 5.dp)
            .testTag(testTag)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
            color = if (isSelected) Color.White else NavyDark
        )
    }
}
