package com.example.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalPolice
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.navigation.Screen
import com.example.ui.theme.NavyDark
import com.example.ui.theme.TealPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun SafeNestBottomNavBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 6.dp
    ) {
        val items = listOf(
            Triple("Dashboard", Screen.Dashboard.route, Icons.Default.Home),
            Triple("Find Homes", Screen.Properties.route, Icons.Default.Search),
            Triple("Scam Check", Screen.ScamCheck.route, Icons.Default.Shield),
            Triple("Roommates", Screen.RoommateMatch.route, Icons.Default.People),
            Triple("Safety", Screen.Safety.route, Icons.Default.LocalPolice)
        )

        items.forEach { (label, route, icon) ->
            val isSelected = currentRoute == route
            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(route) },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        modifier = Modifier.size(22.dp)
                    )
                },
                label = {
                    Text(
                        text = label,
                        fontSize = 11.5.sp,
                        fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
                        color = if (isSelected) TealPrimary else NavyDark,
                        maxLines = 1
                    )
                },
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = TealPrimary,
                    selectedTextColor = TealPrimary,
                    indicatorColor = TealPrimary.copy(alpha = 0.18f),
                    unselectedIconColor = NavyDark,
                    unselectedTextColor = NavyDark
                ),
                modifier = Modifier.testTag("nav_tab_${label.lowercase().replace(" ", "_")}")
            )
        }
    }
}
