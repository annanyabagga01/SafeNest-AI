package com.example.ui.screens.dashboard

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalPolice
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.example.ui.theme.SafeGreen
import com.example.ui.theme.TealContainer
import com.example.ui.theme.TealPrimary
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.SafeNestViewModel

@Composable
fun DashboardScreen(
    viewModel: SafeNestViewModel,
    onNavigateToRoute: (String) -> Unit,
    onSelectProperty: (String) -> Unit
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val properties by viewModel.properties.collectAsState()
    val bookings by viewModel.allBookings.collectAsState()
    val savedProperties by viewModel.savedProperties.collectAsState()

    val userName = userProfile?.name ?: "Ananya Sharma"

    Scaffold(
        topBar = { HackathonDemoBadge() },
        bottomBar = {
            SafeNestBottomNavBar(
                currentRoute = Screen.Dashboard.route,
                onNavigate = onNavigateToRoute
            )
        },
        containerColor = BackgroundLight
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(TealPrimary, shape = RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "SafeNest",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Namaste, $userName 👋",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = NavyDark
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SafeGreen, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "Verified Tenant Identity",
                                fontSize = 11.sp,
                                color = SafeGreen,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Row {
                    IconButton(
                        onClick = { onNavigateToRoute(Screen.Admin.route) },
                        modifier = Modifier.testTag("admin_demo_btn")
                    ) {
                        Icon(Icons.Default.AdminPanelSettings, contentDescription = "Admin Demo", tint = NavyDark)
                    }
                    IconButton(
                        onClick = { onNavigateToRoute(Screen.Profile.route) },
                        modifier = Modifier.testTag("dashboard_profile_btn")
                    ) {
                        Icon(Icons.Default.Person, contentDescription = "Profile", tint = NavyDark)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Search Bar Shortcut
            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    readOnly = true,
                    placeholder = { Text("Search localities in Delhi, Noida, Gurugram...", fontSize = 13.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary) },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = TealPrimary,
                        unfocusedBorderColor = BorderSubtle
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToRoute(Screen.Properties.route) }
                        .testTag("dashboard_search_bar")
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // AI Trust Insights Card (Matching Professional Polish HTML styling)
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .border(1.dp, BorderSubtle, RoundedCornerShape(16.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "LOCALITY TRUST INSIGHTS",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TealPrimary,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Noida Sector 62 Hub",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = NavyDark
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Verified student zone • 24/7 Police Patrol • 0.5km Metro access",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            lineHeight = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .border(3.dp, TealPrimary, CircleShape)
                                .background(TealContainer, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "94%",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = TealPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "High Trust", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TealPrimary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Quick Actions Grid (4 Primary Pillars)
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "QUICK SAFETY ACTIONS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickActionCard(
                        title = "Scam Check",
                        subtitle = "AI Fraud Detection",
                        icon = Icons.Default.Security,
                        bgColor = Color(0xFFECFDF5),
                        borderColor = Color(0xFFA7F3D0),
                        iconTint = Color(0xFF059669),
                        onClick = { onNavigateToRoute(Screen.ScamCheck.route) },
                        modifier = Modifier.weight(1f).testTag("quick_action_scam_check")
                    )

                    QuickActionCard(
                        title = "Roommates",
                        subtitle = "Compatibility AI",
                        icon = Icons.Default.People,
                        bgColor = Color(0xFFEEF2FF),
                        borderColor = Color(0xFFC7D2FE),
                        iconTint = Color(0xFF4F46E5),
                        onClick = { onNavigateToRoute(Screen.RoommateMatch.route) },
                        modifier = Modifier.weight(1f).testTag("quick_action_roommate")
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickActionCard(
                        title = "Find Homes",
                        subtitle = "Verified Rentals",
                        icon = Icons.Default.Search,
                        bgColor = Color(0xFFF0F9FF),
                        borderColor = Color(0xFFBAE6FD),
                        iconTint = Color(0xFF0284C7),
                        onClick = { onNavigateToRoute(Screen.Properties.route) },
                        modifier = Modifier.weight(1f).testTag("quick_action_properties")
                    )

                    QuickActionCard(
                        title = "Safety Hub",
                        subtitle = "Police & Hospitals",
                        icon = Icons.Default.LocalPolice,
                        bgColor = Color(0xFFFEF2F2),
                        borderColor = Color(0xFFFECACA),
                        iconTint = Color(0xFFDC2626),
                        onClick = { onNavigateToRoute(Screen.Safety.route) },
                        modifier = Modifier.weight(1f).testTag("quick_action_safety")
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Recent Bookings Banner (If any)
            if (bookings.isNotEmpty()) {
                val latest = bookings.first()
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        text = "ACTIVE BOOKING REQUEST",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(TealContainer, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Shield, contentDescription = null, tint = TealPrimary)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = latest.propertyTitle, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = NavyDark)
                                Text(
                                    text = "Status: ${latest.status.name} • Move-in: ${latest.moveInDate}",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFFFEF3C7), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(text = "Escrow Logged", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD97706))
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Recommended Verified Properties Section
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "RECOMMENDED VERIFIED HOMES",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "View All (${properties.size})",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TealPrimary,
                        modifier = Modifier.clickable { onNavigateToRoute(Screen.Properties.route) }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp)
                ) {
                    items(properties.take(5)) { prop ->
                        PropertyCard(
                            property = prop,
                            onCardClick = { onSelectProperty(prop.id) },
                            onSaveToggle = { viewModel.toggleSaveProperty(prop.id, prop.isSaved) },
                            modifier = Modifier.width(280.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Saved Properties Section (if any saved)
            if (savedProperties.isNotEmpty()) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Bookmark, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "SAVED HOMES (${savedProperties.size})",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary,
                            letterSpacing = 0.5.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp)
                    ) {
                        items(savedProperties) { prop ->
                            PropertyCard(
                                property = prop,
                                onCardClick = { onSelectProperty(prop.id) },
                                onSaveToggle = { viewModel.toggleSaveProperty(prop.id, prop.isSaved) },
                                modifier = Modifier.width(280.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun QuickActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    bgColor: Color,
    borderColor: Color,
    iconTint: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        modifier = modifier
            .border(1.dp, borderColor, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(iconTint, shape = RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = NavyDark)
            Text(text = subtitle, fontSize = 11.sp, color = TextSecondary)
        }
    }
}
