package com.example.ui.screens.roommate

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.data.model.RoommateMatchResult
import com.example.data.model.UserProfile
import com.example.ui.components.HackathonDemoBadge
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
fun RoommateMatchScreen(
    viewModel: SafeNestViewModel,
    onNavigateToRoute: (String) -> Unit
) {
    val matches by viewModel.roommateMatches.collectAsState()
    val isMatching by viewModel.isMatchingRoommates.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()

    var sleepSchedule by remember { mutableStateOf("Early Bird") }
    var cleanliness by remember { mutableStateOf("Strict") }
    var foodPreference by remember { mutableStateOf("Vegetarian") }
    var smokingPreference by remember { mutableStateOf("Non-Smoker") }

    LaunchedEffect(Unit) {
        if (matches.isEmpty()) {
            viewModel.runRoommateMatching()
        }
    }

    Scaffold(
        topBar = { HackathonDemoBadge() },
        bottomBar = {
            SafeNestBottomNavBar(
                currentRoute = Screen.RoommateMatch.route,
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
            // Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .border(1.dp, BorderSubtle)
                    .padding(20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(TealPrimary, shape = RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.People, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("AI Roommate Compatibility", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = NavyDark)
                        Text("Verified Profiles • Lifestyle Matching", fontSize = 11.sp, color = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Find flatmates who share your sleep hours, cleanliness standards, and dietary habits in Delhi NCR.",
                    fontSize = 13.sp,
                    color = TextSecondary,
                    lineHeight = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // User Preference Selector Box
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .border(1.dp, BorderSubtle, RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Your Living Preferences", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = NavyDark)

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        PreferenceDropdown(
                            label = "Sleep Schedule",
                            current = sleepSchedule,
                            options = listOf("Early Bird", "Night Owl", "Flexible"),
                            onSelected = { sleepSchedule = it },
                            modifier = Modifier.weight(1f)
                        )
                        PreferenceDropdown(
                            label = "Cleanliness",
                            current = cleanliness,
                            options = listOf("Strict", "Moderate", "Casual"),
                            onSelected = { cleanliness = it },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        PreferenceDropdown(
                            label = "Food Preference",
                            current = foodPreference,
                            options = listOf("Vegetarian", "Non-Veg", "Eggetarian", "Any"),
                            onSelected = { foodPreference = it },
                            modifier = Modifier.weight(1f)
                        )
                        PreferenceDropdown(
                            label = "Smoking Habit",
                            current = smokingPreference,
                            options = listOf("Non-Smoker", "Outside Only", "Social"),
                            onSelected = { smokingPreference = it },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            val profileOverride = (userProfile ?: UserProfile()).copy(
                                sleepSchedule = sleepSchedule,
                                cleanliness = cleanliness,
                                foodPreference = foodPreference,
                                smokingPreference = smokingPreference
                            )
                            viewModel.runRoommateMatching(profileOverride)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("run_roommate_match_btn")
                    ) {
                        if (isMatching) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp))
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Recalculate AI Compatibility", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Results Section
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "COMPATIBLE MATCHES (${matches.size})",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                matches.forEach { match ->
                    RoommateMatchCard(match = match)
                    Spacer(modifier = Modifier.height(14.dp))
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
private fun PreferenceDropdown(
    label: String,
    current: String,
    options: List<String>,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        Text(label, fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(BackgroundLight, RoundedCornerShape(8.dp))
                .border(1.dp, BorderSubtle, RoundedCornerShape(8.dp))
                .clickable { expanded = true }
                .padding(horizontal = 10.dp, vertical = 8.dp)
        ) {
            Text(current, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = NavyDark)

            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEach { opt ->
                    DropdownMenuItem(
                        text = { Text(opt, fontSize = 12.sp) },
                        onClick = {
                            onSelected(opt)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun RoommateMatchCard(match: RoommateMatchResult) {
    val rm = match.roommate

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, BorderSubtle, RoundedCornerShape(14.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .background(TealContainer, shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = rm.name.take(2).uppercase(),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TealPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "${rm.name}, ${rm.age}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = NavyDark)
                            if (rm.verifiedBadge) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.Default.VerifiedUser, contentDescription = "Verified", tint = SafeGreen, modifier = Modifier.size(16.dp))
                            }
                        }
                        Text(text = rm.occupation, fontSize = 12.sp, color = TextSecondary)
                    }
                }

                // Compatibility Score Badge
                Box(
                    modifier = Modifier
                        .background(Color(0xFFDCFCE7), shape = RoundedCornerShape(12.dp))
                        .border(1.dp, Color(0xFF16A34A).copy(alpha = 0.3f), shape = RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "${match.compatibilityScore}% Match",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF15803D)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(text = rm.bio, fontSize = 12.sp, color = TextPrimary, lineHeight = 17.sp)

            Spacer(modifier = Modifier.height(12.dp))

            // Key habit tags
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                HabitChip(rm.sleepSchedule)
                HabitChip("${rm.cleanliness} Clean")
                HabitChip(rm.foodPreference)
                HabitChip(rm.city)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // AI Harmony Advice
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BackgroundLight, RoundedCornerShape(10.dp))
                    .border(1.dp, BorderSubtle, RoundedCornerShape(10.dp))
                    .padding(10.dp)
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    Icon(Icons.Default.Lightbulb, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text("AI HARMONY ADVICE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TealPrimary)
                        Text(text = match.aiHarmonyAdvice, fontSize = 11.sp, color = TextSecondary, lineHeight = 16.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun HabitChip(label: String) {
    Box(
        modifier = Modifier
            .background(BackgroundLight, RoundedCornerShape(6.dp))
            .border(1.dp, BorderSubtle, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(text = label, fontSize = 10.sp, fontWeight = FontWeight.Medium, color = TextSecondary)
    }
}
