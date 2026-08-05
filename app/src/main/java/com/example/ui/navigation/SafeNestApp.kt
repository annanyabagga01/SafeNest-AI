package com.example.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ui.screens.admin.AdminScreen
import com.example.ui.screens.auth.LoginScreen
import com.example.ui.screens.auth.RegisterScreen
import com.example.ui.screens.booking.BookingScreen
import com.example.ui.screens.dashboard.DashboardScreen
import com.example.ui.screens.landing.LandingScreen
import com.example.ui.screens.profile.ProfileScreen
import com.example.ui.screens.properties.PropertiesScreen
import com.example.ui.screens.properties.PropertyDetailScreen
import com.example.ui.screens.roommate.RoommateMatchScreen
import com.example.ui.screens.safety.SafetyScreen
import com.example.ui.screens.scam.ScamCheckScreen
import com.example.ui.viewmodel.SafeNestViewModel

@Composable
fun SafeNestApp(
    viewModel: SafeNestViewModel,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Landing.route,
        modifier = Modifier
    ) {
        composable(Screen.Landing.route) {
            LandingScreen(
                onNavigateToLogin = { navController.navigate(Screen.Login.route) },
                onNavigateToDashboard = { navController.navigate(Screen.Dashboard.route) },
                onNavigateToProperties = { navController.navigate(Screen.Properties.route) },
                onNavigateToScamCheck = { navController.navigate(Screen.ScamCheck.route) }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Landing.route) { inclusive = false }
                    }
                },
                onNavigateToRegister = { navController.navigate(Screen.Register.route) }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Landing.route) { inclusive = false }
                    }
                },
                onNavigateToLogin = { navController.navigate(Screen.Login.route) }
            )
        }

        composable(Screen.Dashboard.route) {
            DashboardScreen(
                viewModel = viewModel,
                onNavigateToRoute = { route -> navController.navigate(route) },
                onSelectProperty = { propertyId ->
                    navController.navigate(Screen.PropertyDetail.createRoute(propertyId))
                }
            )
        }

        composable(Screen.Properties.route) {
            PropertiesScreen(
                viewModel = viewModel,
                onNavigateToRoute = { route -> navController.navigate(route) },
                onSelectProperty = { propertyId ->
                    navController.navigate(Screen.PropertyDetail.createRoute(propertyId))
                }
            )
        }

        composable(
            route = Screen.PropertyDetail.route,
            arguments = listOf(navArgument("propertyId") { type = NavType.StringType })
        ) { backStackEntry ->
            val propertyId = backStackEntry.arguments?.getString("propertyId") ?: ""
            PropertyDetailScreen(
                propertyId = propertyId,
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onNavigateToScamCheckPrefill = { desc, rent, deposit ->
                    viewModel.updateScamInputs(desc = desc, rent = rent.toString(), deposit = deposit.toString())
                    navController.navigate(Screen.ScamCheck.route)
                },
                onNavigateToBooking = { id ->
                    navController.navigate(Screen.Booking.createRoute(id))
                }
            )
        }

        composable(Screen.ScamCheck.route) {
            ScamCheckScreen(
                viewModel = viewModel,
                onNavigateToRoute = { route -> navController.navigate(route) }
            )
        }

        composable(Screen.RoommateMatch.route) {
            RoommateMatchScreen(
                viewModel = viewModel,
                onNavigateToRoute = { route -> navController.navigate(route) }
            )
        }

        composable(
            route = Screen.Booking.route,
            arguments = listOf(navArgument("propertyId") { type = NavType.StringType })
        ) { backStackEntry ->
            val propertyId = backStackEntry.arguments?.getString("propertyId") ?: ""
            BookingScreen(
                propertyId = propertyId,
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onBookingSubmitted = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Dashboard.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Safety.route) {
            SafetyScreen(
                viewModel = viewModel,
                onNavigateToRoute = { route -> navController.navigate(route) }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                viewModel = viewModel,
                onNavigateToRoute = { route -> navController.navigate(route) },
                onLogout = {
                    navController.navigate(Screen.Landing.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Admin.route) {
            AdminScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
