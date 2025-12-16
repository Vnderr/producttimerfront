package com.example.producttimer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.producttimer.data.local.UserPreferencesDataStore
import com.example.producttimer.data.remote.AuthClient
import com.example.producttimer.data.remote.ApiClient
import com.example.producttimer.data.remote.repository.AuthRepository
import com.example.producttimer.data.remote.repository.ApiRepository
import com.example.producttimer.navigation.BottomBar
import com.example.producttimer.navigation.BottomNavItem
import com.example.producttimer.navigation.Routes
import com.example.producttimer.ui.screen.*
import com.example.producttimer.ui.viewmodel.AuthViewModel
import com.example.producttimer.ui.viewmodel.AuthViewModelFactory
import com.example.producttimer.ui.viewmodel.MainViewModel
import com.example.producttimer.ui.viewmodel.MainViewModelFactory
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        FirebaseApp.initializeApp(this)
        setContent {
            App()
        }
    }
}

@Composable
fun App() {
    val context = LocalContext.current

    val userPrefs = remember { UserPreferencesDataStore(context) }
    val apiService = ApiClient.service
    val authClient = remember { AuthClient() }
    val authRepository = remember { AuthRepository(apiService, authClient, userPrefs) }
    val apiRepository = remember { ApiRepository(apiService) }

    val authVmFactory = remember { AuthViewModelFactory(authRepository) }
    val authVm: AuthViewModel = viewModel(factory = authVmFactory)

    val mainVmFactory = remember { MainViewModelFactory(apiRepository, userPrefs) }
    val mainVm: MainViewModel = viewModel(factory = mainVmFactory)

    val neonUserId by mainVm.currentNeonUserIdFlow.collectAsState(initial = null)
    val startDestination = if (neonUserId != null) Routes.HOME else Routes.LOGIN
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            if (currentRoute != Routes.LOGIN && currentRoute != Routes.REGISTER) {
                BottomBar(
                    navController,
                    listOf(
                        BottomNavItem.Home,
                        BottomNavItem.AgregarProducto,
                        BottomNavItem.Profile
                    )
                )
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(padding)
        ) {
            composable(Routes.LOGIN) {
                LoginScreen(
                    viewModel = authVm,
                    onLoginSuccess = {
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    },
                    onNavigateToRegister = { navController.navigate(Routes.REGISTER) }
                )
            }

            composable(Routes.REGISTER) {
                RegisterScreen(
                    viewModel = authVm,
                    onRegisterSuccess = {
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(Routes.REGISTER) { inclusive = true }
                        }
                    }
                )
            }

            composable(Routes.HOME) {
                HomeScreen(
                    viewModel = mainVm,
                    onItemClick = { id -> navController.navigate(Routes.detailRoute(id.toString())) }
                )
            }

            composable(Routes.PROFILE) {
                ProfileScreen(
                    viewModel = authVm,
                    onLogout = {
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(Routes.HOME) { inclusive = true }
                        }
                    }
                )
            }

            composable(Routes.PRODUCTOS) {
                AgregarProductoScreen(mainVm, navController)
            }

            composable(
                route = Routes.DETAIL,
                arguments = listOf(navArgument("remoteId") { type = NavType.StringType })
            ) { backStackEntry ->
                DetailScreen(
                    productoId = backStackEntry.arguments?.getString("remoteId") ?: "",
                    viewModel = mainVm,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
