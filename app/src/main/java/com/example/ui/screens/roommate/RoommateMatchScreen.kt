package com.example.ui.screens.roommate

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.data.model.RoommateProfile
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
    RoommateMatchingScreen(viewModel = viewModel, onNavigateToRoute = onNavigateToRoute)
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RoommateMatchingScreen(
    viewModel: SafeNestViewModel,
    onNavigateToRoute: (String) -> Unit
) {
    val matches by viewModel.roommateMatches.collectAsState()
    val isMatching by viewModel.isMatchingRoommates.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()

    var activeTab by remember { mutableStateOf(0) } // 0: Suggested Matches, 1: Profile & Lifestyle Tags

    // Search and Filters
    var searchQuery by remember { mutableStateOf("") }
    var selectedCityFilter by remember { mutableStateOf("All") }
    var minScoreFilter by remember { mutableStateOf(0) }

    // User Profile Form State
    var userName by remember { mutableStateOf(userProfile?.name ?: "Ananya Sharma") }
    var userOccupation by remember { mutableStateOf(userProfile?.role ?: "Student / Professional") }
    var userCity by remember { mutableStateOf(userProfile?.cityPreference ?: "Noida") }
    var userMaxBudget by remember { mutableStateOf(userProfile?.maxBudget?.toFloat() ?: 15000f) }
    var sleepSchedule by remember { mutableStateOf(userProfile?.sleepSchedule ?: "Early Bird") }
    var cleanliness by remember { mutableStateOf(userProfile?.cleanliness ?: "Strict") }
    var foodPreference by remember { mutableStateOf(userProfile?.foodPreference ?: "Vegetarian") }
    var smokingPreference by remember { mutableStateOf(userProfile?.smokingPreference ?: "Non-Smoker") }

    // Lifestyle Tags State
    val initialTags = remember(userProfile) {
        userProfile?.lifestyleTags?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() }
            ?: listOf("Early Bird", "Strict Cleanliness", "Vegetarian", "Non-Smoker", "Quiet Study Zone", "WFH Friendly")
    }
    var selectedTags by remember { mutableStateOf(initialTags) }
    var customTagInput by remember { mutableStateOf("") }

    // Connect / Invite Dialog State
    var selectedCandidateForInvite by remember { mutableStateOf<RoommateMatchResult?>(null) }
    var inviteSentSuccess by remember { mutableStateOf<String?>(null) }
    var profileSaveNotice by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (matches.isEmpty()) {
            viewModel.runRoommateMatching()
        }
    }

    LaunchedEffect(userProfile) {
        userProfile?.let { prof ->
            userName = prof.name
            userOccupation = prof.role
            userCity = prof.cityPreference
            userMaxBudget = prof.maxBudget.toFloat()
            sleepSchedule = prof.sleepSchedule
            cleanliness = prof.cleanliness
            foodPreference = prof.foodPreference
            smokingPreference = prof.smokingPreference
            val tagsFromProf = prof.lifestyleTags.split(",").map { it.trim() }.filter { it.isNotEmpty() }
            if (tagsFromProf.isNotEmpty()) {
                selectedTags = tagsFromProf
            }
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
        ) {
            // Header Banner
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .border(1.dp, BorderSubtle)
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(TealPrimary, shape = RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.People,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Roommate Lifestyle Matcher",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = NavyDark
                            )
                            Text(
                                text = "AI Harmony Engine • ${matches.size} Candidate Profiles",
                                fontSize = 11.sp,
                                color = TextSecondary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .background(TealContainer, RoundedCornerShape(20.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("100% Verified", fontSize = 11.sp, color = TealPrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Tab Bar
            TabRow(
                selectedTabIndex = activeTab,
                containerColor = Color.White,
                contentColor = TealPrimary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[activeTab]),
                        color = TealPrimary
                    )
                }
            ) {
                Tab(
                    selected = activeTab == 0,
                    onClick = { activeTab = 0 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.People, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Suggested Roommates (${matches.size})", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    },
                    modifier = Modifier.testTag("tab_suggested_roommates")
                )
                Tab(
                    selected = activeTab == 1,
                    onClick = { activeTab = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Tag, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("My Lifestyle & Tags", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    },
                    modifier = Modifier.testTag("tab_lifestyle_profile")
                )
            }

            // Profile saved notification banner
            AnimatedVisibility(visible = profileSaveNotice) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SafeGreen)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Profile & lifestyle tags updated! Recalculated roommates.",
                                fontSize = 12.sp,
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        IconButton(onClick = { profileSaveNotice = false }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                        }
                    }
                }
            }

            // Tab Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                if (activeTab == 0) {
                    // TAB 0: SUGGESTED ROOMMATES LIST
                    // Search & Filter Box
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, BorderSubtle, RoundedCornerShape(14.dp))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = { Text("Search by name, occupation, or tag...", fontSize = 12.sp) },
                                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary) },
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = TealPrimary,
                                    unfocusedBorderColor = BorderSubtle
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .testTag("roommate_search_input")
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // City Filter Chips
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("City:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                                listOf("All", "Noida", "Delhi", "Gurugram").forEach { city ->
                                    val isSelected = selectedCityFilter == city
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                if (isSelected) TealPrimary else BackgroundLight,
                                                RoundedCornerShape(16.dp)
                                            )
                                            .border(
                                                1.dp,
                                                if (isSelected) TealPrimary else BorderSubtle,
                                                RoundedCornerShape(16.dp)
                                            )
                                            .clickable { selectedCityFilter = city }
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = city,
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) Color.White else TextPrimary
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Score Filter Chips
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Match:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                                listOf(0 to "All Scores", 75 to "75%+ High", 85 to "85%+ Top").forEach { (score, label) ->
                                    val isSelected = minScoreFilter == score
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                if (isSelected) NavyDark else BackgroundLight,
                                                RoundedCornerShape(16.dp)
                                            )
                                            .border(
                                                1.dp,
                                                if (isSelected) NavyDark else BorderSubtle,
                                                RoundedCornerShape(16.dp)
                                            )
                                            .clickable { minScoreFilter = score }
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = label,
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) Color.White else TextPrimary
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Recalculate AI Button
                    Button(
                        onClick = {
                            val activeProfile = UserProfile(
                                name = userName,
                                role = userOccupation,
                                cityPreference = userCity,
                                maxBudget = userMaxBudget.toInt(),
                                sleepSchedule = sleepSchedule,
                                cleanliness = cleanliness,
                                foodPreference = foodPreference,
                                smokingPreference = smokingPreference,
                                lifestyleTags = selectedTags.joinToString(",")
                            )
                            viewModel.runRoommateMatching(activeProfile)
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
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Refresh AI Compatibility", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Filtered List
                    val userTagsSet = selectedTags.toSet()
                    val filteredMatches = matches.filter { match ->
                        val rm = match.roommate
                        val matchesCity = selectedCityFilter == "All" || rm.city.equals(selectedCityFilter, ignoreCase = true)
                        val matchesScore = match.compatibilityScore >= minScoreFilter
                        val rmTags = rm.lifestyleTags.split(",").map { it.trim() }
                        val matchesQuery = searchQuery.isBlank() ||
                                rm.name.contains(searchQuery, ignoreCase = true) ||
                                rm.occupation.contains(searchQuery, ignoreCase = true) ||
                                rm.city.contains(searchQuery, ignoreCase = true) ||
                                rmTags.any { it.contains(searchQuery, ignoreCase = true) }
                        matchesCity && matchesScore && matchesQuery
                    }

                    Text(
                        text = "RECOMMENDED ROOMMATES (${filteredMatches.size})",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    if (filteredMatches.isEmpty()) {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
                                .padding(24.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(Icons.Default.People, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(40.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("No matching roommates found", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = NavyDark)
                                Text("Try adjusting your filters or search query.", fontSize = 12.sp, color = TextSecondary)
                            }
                        }
                    } else {
                        filteredMatches.forEach { match ->
                            RoommateCandidateCard(
                                match = match,
                                userTags = userTagsSet,
                                onConnectClick = { selectedCandidateForInvite = match }
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                        }
                    }
                } else {
                    // TAB 1: CREATE / EDIT LIFESTYLE PROFILE & TAGS
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, BorderSubtle, RoundedCornerShape(16.dp))
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(TealContainer, shape = CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Person, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(18.dp))
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("Create & Customize Profile", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = NavyDark)
                                    Text("Set your living habits & select lifestyle tags", fontSize = 11.sp, color = TextSecondary)
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Name
                            OutlinedTextField(
                                value = userName,
                                onValueChange = { userName = it },
                                label = { Text("Your Full Name") },
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("profile_name_input")
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Occupation / Role
                            OutlinedTextField(
                                value = userOccupation,
                                onValueChange = { userOccupation = it },
                                label = { Text("Occupation / College / Role") },
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("profile_occupation_input")
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // City & Budget Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Preferred City", fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    CityDropdown(
                                        currentCity = userCity,
                                        onCitySelected = { userCity = it }
                                    )
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Max Budget: ₹${userMaxBudget.toInt()}/mo", fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Slider(
                                        value = userMaxBudget,
                                        onValueChange = { userMaxBudget = it },
                                        valueRange = 5000f..30000f,
                                        steps = 25,
                                        colors = SliderDefaults.colors(
                                            thumbColor = TealPrimary,
                                            activeTrackColor = TealPrimary
                                        ),
                                        modifier = Modifier.fillMaxWidth().testTag("profile_budget_slider")
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text("Core Habit Preferences", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = NavyDark)

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                PreferenceSelectDropdown(
                                    label = "Sleep Routine",
                                    current = sleepSchedule,
                                    options = listOf("Early Bird", "Night Owl", "Flexible"),
                                    onSelected = { sleepSchedule = it },
                                    modifier = Modifier.weight(1f)
                                )
                                PreferenceSelectDropdown(
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
                                PreferenceSelectDropdown(
                                    label = "Dietary Habit",
                                    current = foodPreference,
                                    options = listOf("Vegetarian", "Non-Veg", "Eggetarian", "Any"),
                                    onSelected = { foodPreference = it },
                                    modifier = Modifier.weight(1f)
                                )
                                PreferenceSelectDropdown(
                                    label = "Smoking",
                                    current = smokingPreference,
                                    options = listOf("Non-Smoker", "Outside Only", "Social"),
                                    onSelected = { smokingPreference = it },
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            // Lifestyle Tags Section
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Tag, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Lifestyle Tags", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = NavyDark)
                            }

                            Text(
                                text = "Select tags that describe your personality, routine, and living preferences.",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Selected Tags active box
                            if (selectedTags.isNotEmpty()) {
                                Text("YOUR ACTIVE TAGS (${selectedTags.size})", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TealPrimary)
                                Spacer(modifier = Modifier.height(6.dp))
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    selectedTags.forEach { tag ->
                                        Box(
                                            modifier = Modifier
                                                .background(TealContainer, RoundedCornerShape(16.dp))
                                                .border(1.dp, TealPrimary.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                                                .clickable {
                                                    selectedTags = selectedTags.filter { it != tag }
                                                }
                                                .padding(horizontal = 10.dp, vertical = 5.dp)
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(tag, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TealPrimary)
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Icon(Icons.Default.Close, contentDescription = "Remove tag", tint = TealPrimary, modifier = Modifier.size(12.dp))
                                            }
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(14.dp))
                            }

                            // Preset Categories
                            LifestyleTagCategory(
                                title = "🌅 Routine & Sleep",
                                tags = listOf("Early Bird", "Night Owl", "Flexible Hours"),
                                selectedTags = selectedTags,
                                onToggleTag = { tag ->
                                    selectedTags = if (selectedTags.contains(tag)) {
                                        selectedTags.filter { it != tag }
                                    } else {
                                        selectedTags + tag
                                    }
                                }
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            LifestyleTagCategory(
                                title = "🧹 Cleanliness & Order",
                                tags = listOf("Strict Cleanliness", "Moderate Cleanliness", "Deep Cleaner", "Casual"),
                                selectedTags = selectedTags,
                                onToggleTag = { tag ->
                                    selectedTags = if (selectedTags.contains(tag)) {
                                        selectedTags.filter { it != tag }
                                    } else {
                                        selectedTags + tag
                                    }
                                }
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            LifestyleTagCategory(
                                title = "🥗 Diet & Kitchen",
                                tags = listOf("Vegetarian", "Non-Veg", "Eggetarian", "Teetotaler"),
                                selectedTags = selectedTags,
                                onToggleTag = { tag ->
                                    selectedTags = if (selectedTags.contains(tag)) {
                                        selectedTags.filter { it != tag }
                                    } else {
                                        selectedTags + tag
                                    }
                                }
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            LifestyleTagCategory(
                                title = "⚡ Vibe & Work Environment",
                                tags = listOf("Quiet Study Zone", "WFH Friendly", "Fitness Enthusiast", "Music Lover", "Pet Friendly", "No Pets", "Social Butterfly", "Introverted / Peaceful"),
                                selectedTags = selectedTags,
                                onToggleTag = { tag ->
                                    selectedTags = if (selectedTags.contains(tag)) {
                                        selectedTags.filter { it != tag }
                                    } else {
                                        selectedTags + tag
                                    }
                                }
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Custom Tag Input Field
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = customTagInput,
                                    onValueChange = { customTagInput = it },
                                    placeholder = { Text("Add custom lifestyle tag (e.g. Yoga, Gamer)...", fontSize = 11.sp) },
                                    singleLine = true,
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .testTag("custom_tag_input")
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = {
                                        val trimmed = customTagInput.trim()
                                        if (trimmed.isNotEmpty() && !selectedTags.contains(trimmed)) {
                                            selectedTags = selectedTags + trimmed
                                            customTagInput = ""
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = NavyDark),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .height(48.dp)
                                        .testTag("add_custom_tag_btn")
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Add", fontSize = 12.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            // Save Profile Action Button
                            Button(
                                onClick = {
                                    val updatedProfile = UserProfile(
                                        name = userName.ifBlank { "Ananya Sharma" },
                                        role = userOccupation.ifBlank { "Student / Professional" },
                                        cityPreference = userCity,
                                        maxBudget = userMaxBudget.toInt(),
                                        sleepSchedule = sleepSchedule,
                                        cleanliness = cleanliness,
                                        foodPreference = foodPreference,
                                        smokingPreference = smokingPreference,
                                        lifestyleTags = selectedTags.joinToString(",")
                                    )
                                    viewModel.updateUserProfile(updatedProfile)
                                    profileSaveNotice = true
                                    activeTab = 0 // Switch to suggested matches tab
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("save_lifestyle_profile_btn")
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Save Lifestyle Profile & Match", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }

    // CONNECT / INVITE DIALOG
    selectedCandidateForInvite?.let { match ->
        val rm = match.roommate
        AlertDialog(
            onDismissRequest = { selectedCandidateForInvite = null },
            icon = {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(TealContainer, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Send, contentDescription = null, tint = TealPrimary)
                }
            },
            title = {
                Text(
                    text = "Connect with ${rm.name}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyDark
                )
            },
            text = {
                Column {
                    Text(
                        text = "${rm.occupation} • ${rm.city}",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFDCFCE7), RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "AI Compatibility: ${match.compatibilityScore}% Match",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF15803D)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Send a verified co-living invitation message to discuss apartment sharing in ${rm.city}.",
                        fontSize = 12.sp,
                        color = TextPrimary
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        inviteSentSuccess = "Invitation sent to ${rm.name}! Check your messages."
                        selectedCandidateForInvite = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                ) {
                    Text("Send Co-Living Request")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedCandidateForInvite = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Success Toast/Dialog
    inviteSentSuccess?.let { msg ->
        AlertDialog(
            onDismissRequest = { inviteSentSuccess = null },
            icon = { Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SafeGreen, modifier = Modifier.size(36.dp)) },
            title = { Text("Request Sent!") },
            text = { Text(msg) },
            confirmButton = {
                Button(onClick = { inviteSentSuccess = null }) {
                    Text("OK")
                }
            }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun RoommateCandidateCard(
    match: RoommateMatchResult,
    userTags: Set<String>,
    onConnectClick: () -> Unit
) {
    val rm = match.roommate
    val rmTagsList = rm.lifestyleTags.split(",").map { it.trim() }.filter { it.isNotEmpty() }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, BorderSubtle, RoundedCornerShape(14.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header row with score badge
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
                        Text(text = "${rm.occupation} • ${rm.city}", fontSize = 12.sp, color = TextSecondary)
                    }
                }

                // Compatibility Score Badge
                val scoreColor = when {
                    match.compatibilityScore >= 85 -> Color(0xFF15803D)
                    match.compatibilityScore >= 70 -> TealPrimary
                    else -> Color(0xFFD97706)
                }
                val scoreBg = when {
                    match.compatibilityScore >= 85 -> Color(0xFFDCFCE7)
                    match.compatibilityScore >= 70 -> TealContainer
                    else -> Color(0xFFFEF3C7)
                }

                Box(
                    modifier = Modifier
                        .background(scoreBg, shape = RoundedCornerShape(12.dp))
                        .border(1.dp, scoreColor.copy(alpha = 0.3f), shape = RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "${match.compatibilityScore}% Match",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = scoreColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(text = rm.bio, fontSize = 12.sp, color = TextPrimary, lineHeight = 17.sp)

            Spacer(modifier = Modifier.height(12.dp))

            // Lifestyle Tags Display
            Text("LIFESTYLE TAGS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
            Spacer(modifier = Modifier.height(6.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                rmTagsList.forEach { tag ->
                    val isShared = userTags.contains(tag)
                    Box(
                        modifier = Modifier
                            .background(
                                if (isShared) Color(0xFFDCFCE7) else BackgroundLight,
                                RoundedCornerShape(6.dp)
                            )
                            .border(
                                1.dp,
                                if (isShared) Color(0xFF16A34A).copy(alpha = 0.4f) else BorderSubtle,
                                RoundedCornerShape(6.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (isShared) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF15803D), modifier = Modifier.size(10.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                            }
                            Text(
                                text = tag,
                                fontSize = 10.sp,
                                fontWeight = if (isShared) FontWeight.Bold else FontWeight.Medium,
                                color = if (isShared) Color(0xFF15803D) else TextSecondary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // AI Harmony Advice Box
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

            Spacer(modifier = Modifier.height(12.dp))

            // Connect Button
            Button(
                onClick = onConnectClick,
                colors = ButtonDefaults.buttonColors(containerColor = NavyDark),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp)
            ) {
                Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Connect & Invite Roommate", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun LifestyleTagCategory(
    title: String,
    tags: List<String>,
    selectedTags: List<String>,
    onToggleTag: (String) -> Unit
) {
    Column {
        Text(title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
        Spacer(modifier = Modifier.height(4.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            tags.forEach { tag ->
                val isSelected = selectedTags.contains(tag)
                Box(
                    modifier = Modifier
                        .background(
                            if (isSelected) TealPrimary else BackgroundLight,
                            RoundedCornerShape(14.dp)
                        )
                        .border(
                            1.dp,
                            if (isSelected) TealPrimary else BorderSubtle,
                            RoundedCornerShape(14.dp)
                        )
                        .clickable { onToggleTag(tag) }
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (isSelected) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        Text(
                            text = tag,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else TextPrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PreferenceSelectDropdown(
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
private fun CityDropdown(
    currentCity: String,
    onCitySelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val cities = listOf("Noida", "Delhi", "Gurugram")

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(BackgroundLight, RoundedCornerShape(8.dp))
            .border(1.dp, BorderSubtle, RoundedCornerShape(8.dp))
            .clickable { expanded = true }
            .padding(horizontal = 10.dp, vertical = 10.dp)
    ) {
        Text(currentCity, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = NavyDark)

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            cities.forEach { city ->
                DropdownMenuItem(
                    text = { Text(city, fontSize = 12.sp) },
                    onClick = {
                        onCitySelected(city)
                        expanded = false
                    }
                )
            }
        }
    }
}
