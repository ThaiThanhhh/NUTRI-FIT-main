package com.example.nutrifit.ui.navigation

import android.content.Context
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.edit
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.nutrifit.data.repository.UserRepository
import com.example.nutrifit.ui.components.BottomNavBar
import com.example.nutrifit.ui.screens.ScanScreen.ScanScreen
import com.example.nutrifit.ui.screens.dailylog.DailyLogScreen
import com.example.nutrifit.ui.screens.emailverification.EmailVerificationScreen
import com.example.nutrifit.ui.screens.forgotpw.ForgotPasswordScreen
import com.example.nutrifit.ui.screens.home.HomeScreen
import com.example.nutrifit.ui.screens.login.LoginScreen
import com.example.nutrifit.ui.screens.login.LoginScreen2
import com.example.nutrifit.ui.screens.map.MapScreen
import com.example.nutrifit.ui.screens.meal.MealDetailScreen
import com.example.nutrifit.ui.screens.meal.MealScreen
import com.example.nutrifit.ui.screens.onboarding.OnboardingScreen
import com.example.nutrifit.ui.screens.profile.ProfileScreen
import com.example.nutrifit.ui.screens.register.RegisterScreen
import com.example.nutrifit.ui.screens.schedule.ScheduleScreen
import com.example.nutrifit.ui.screens.setting.SettingScreen
import com.example.nutrifit.ui.screens.target.TargetScreen
import com.example.nutrifit.ui.screens.terms.TermsOfServiceScreen
import com.example.nutrifit.ui.screens.workout.WorkoutDetailScreen
import com.example.nutrifit.ui.screens.workout.WorkoutScreen
import com.example.nutrifit.viewmodel.ForgotPasswordViewModel
import com.example.nutrifit.viewmodel.LoginViewModel
import com.example.nutrifit.viewmodel.SettingViewModel
import com.example.nutrifit.viewmodel.WorkoutViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch


@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination: NavDestination? = backStackEntry?.destination
    val context = LocalContext.current

    val sharedPref = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val hasSeenOnboarding = sharedPref.getBoolean("has_seen_onboarding", false)

    val currentUser = FirebaseAuth.getInstance().currentUser
    var startDestination by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(currentUser, hasSeenOnboarding) {
        startDestination = when {
            currentUser?.isEmailVerified == true -> {
                try {
                    val userRepository = UserRepository()
                    val userData = userRepository.getUser(currentUser.uid)
                    if (userData.isSuccess) {
                        val userInfo = userData.getOrNull()
                        when {
                            userInfo?.name.isNullOrBlank() -> NavRoutes.Profile
                            userInfo?.goal.isNullOrBlank() -> NavRoutes.Target
                            else -> NavRoutes.Home
                        }
                    } else {
                        NavRoutes.Profile
                    }
                } catch (e: Exception) {
                    NavRoutes.Profile
                }
            }
            hasSeenOnboarding -> NavRoutes.Login
            else -> NavRoutes.Onboarding
        }
        isLoading = false
    }

    val bottomBarRoutes = setOf(
        NavRoutes.Home,
        NavRoutes.Meal,
        NavRoutes.DailyLog,
        NavRoutes.Workout,
        NavRoutes.Setting,
    )
    val showBottomBar = currentDestination?.route in bottomBarRoutes

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavBar(
                    currentRoute = currentDestination?.route,
                    onNavigate = { route ->
                        if (route != currentDestination?.route) {
                            navController.navigate(route) {
                                val startRoute = navController.graph.findStartDestination().route ?: NavRoutes.Home
                                popUpTo(startRoute) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        if (isLoading) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
            }
        } else {
            NavHost(
                navController = navController,
                startDestination = startDestination ?: NavRoutes.Login,
                modifier = Modifier.padding(paddingValues),
                enterTransition = { fadeIn(animationSpec = tween(300)) },
                exitTransition = { fadeOut(animationSpec = tween(300)) },
                popEnterTransition = { fadeIn(animationSpec = tween(300)) },
                popExitTransition = { fadeOut(animationSpec = tween(300)) }
            ) {
                composable(NavRoutes.Onboarding) {
                    OnboardingScreen(onStart = {
                        sharedPref.edit { putBoolean("has_seen_onboarding", true) }
                        navController.navigate(NavRoutes.Login) {
                            popUpTo(NavRoutes.Onboarding) { inclusive = true }
                        }
                    })
                }

                composable(NavRoutes.Login) {
                    LoginScreen(
                        onLogin = { nextScreen ->
                            val route = when (nextScreen) {
                                LoginViewModel.NextScreen.Profile -> NavRoutes.Profile
                                LoginViewModel.NextScreen.Target -> NavRoutes.Target
                                LoginViewModel.NextScreen.Home -> NavRoutes.Home
                            }
                            navController.navigate(route) {
                                popUpTo(NavRoutes.Login) { inclusive = true }
                            }
                        },
                        onFirstLogin = {
                            navController.navigate(NavRoutes.Profile) {
                                popUpTo(NavRoutes.Login) { inclusive = true }
                            }
                        },
                        onGoRegister = { navController.navigate(NavRoutes.Register) },
                        onForgotPw = { navController.navigate(NavRoutes.ForgotPw) },
                        onEmailLogin = { navController.navigate(NavRoutes.Login2) }
                    )
                }

                composable(NavRoutes.Login2) {
                    LoginScreen2(
                        onLogin = { nextScreen ->
                            val route = when (nextScreen) {
                                LoginViewModel.NextScreen.Profile -> NavRoutes.Profile
                                LoginViewModel.NextScreen.Target -> NavRoutes.Target
                                LoginViewModel.NextScreen.Home -> NavRoutes.Home
                            }
                            navController.navigate(route) {
                                popUpTo(NavRoutes.Login2) { inclusive = true }
                            }
                        },
                        onFirstLogin = {
                            navController.navigate(NavRoutes.Profile) {
                                popUpTo(NavRoutes.Login2) { inclusive = true }
                            }
                        },
                        onGoRegister = { navController.navigate(NavRoutes.Register) },
                        onGoBack = { navController.popBackStack() },
                        onForgotPw = { navController.navigate(NavRoutes.ForgotPw) },
                        onEmailNotVerified = { email ->
                            navController.navigate("${NavRoutes.EmailVerification}/$email/login")
                        }
                    )
                }

                composable(NavRoutes.Register) {
                    RegisterScreen(
                        onRegister = { },
                        onBackToLogin = {
                            navController.navigate(NavRoutes.Login2) {
                                popUpTo(NavRoutes.Login2) { inclusive = true }
                            }
                        },
                        onEmailVerification = { email, source ->
                            navController.navigate("${NavRoutes.EmailVerification}/$email/$source")
                        }
                    )
                }

                composable(
                    route = "${NavRoutes.EmailVerification}/{email}/{source}",
                    arguments = listOf(
                        navArgument("email") { type = NavType.StringType },
                        navArgument("source") {
                            type = NavType.StringType
                            defaultValue = "register"
                        }
                    )
                ) { backStackEntry ->
                    val email = backStackEntry.arguments?.getString("email") ?: ""
                    val source = backStackEntry.arguments?.getString("source") ?: "register"
                    val scope = rememberCoroutineScope()
                    EmailVerificationScreen(
                        email = email,
                        source = source,
                        onVerificationSuccess = {
                            val currentUser = FirebaseAuth.getInstance().currentUser
                            if (currentUser != null) {
                                scope.launch {
                                    try {
                                        val userRepository = UserRepository()
                                        val userData = userRepository.getUser(currentUser.uid)
                                        val nextRoute = if (userData.isSuccess) {
                                            val userInfo = userData.getOrNull()
                                            when {
                                                userInfo?.name.isNullOrBlank() -> NavRoutes.Profile
                                                userInfo?.goal.isNullOrBlank() -> NavRoutes.Target
                                                else -> NavRoutes.Home
                                            }
                                        } else {
                                            NavRoutes.Profile
                                        }
                                        navController.navigate(nextRoute) {
                                            popUpTo(NavRoutes.EmailVerification) { inclusive = true }
                                        }
                                    } catch (_: Exception) {
                                        navController.navigate(NavRoutes.Profile) {
                                            popUpTo(NavRoutes.EmailVerification) { inclusive = true }
                                        }
                                    }
                                }
                            } else {
                                navController.navigate(NavRoutes.Profile) {
                                    popUpTo(NavRoutes.EmailVerification) { inclusive = true }
                                }
                            }
                        },
                        onBackToLogin = {
                            navController.navigate(NavRoutes.Login2) {
                                popUpTo(NavRoutes.EmailVerification) { inclusive = true }
                            }
                        }
                    )
                }

                composable(NavRoutes.ForgotPw) {
                    val forgotPasswordViewModel: ForgotPasswordViewModel = viewModel()
                    ForgotPasswordScreen(
                        onBackToLogin = {
                            navController.navigate(NavRoutes.Login) {
                                popUpTo(NavRoutes.ForgotPw) { inclusive = true }
                            }
                        },
                        viewModel = forgotPasswordViewModel
                    )
                }

                composable(NavRoutes.ForgotPw2) {
                    navController.navigate(NavRoutes.Login) {
                        popUpTo(NavRoutes.ForgotPw2) { inclusive = true }
                    }
                }

                composable(NavRoutes.Profile) {
                    ProfileScreen(
                        onNextClicked = {
                            navController.navigate(NavRoutes.Target) {
                                popUpTo(NavRoutes.Profile) { inclusive = true }
                            }
                        }
                    )
                }

                composable(NavRoutes.Target) {
                    TargetScreen(
                        onBack = {
                            navController.popBackStack()
                        },
                        onNextClicked = {
                            navController.navigate(NavRoutes.Home) {
                                popUpTo(NavRoutes.Target) { inclusive = true }
                            }
                        }
                    )
                }

                composable(NavRoutes.Home) {
                    HomeScreen(navController)
                }
                composable(NavRoutes.Meal) {
                    MealScreen(navController)
                }
                composable(NavRoutes.DailyLog) {
                    DailyLogScreen(navController)
                }
                composable(NavRoutes.Workout) {
                    WorkoutScreen(navController)
                }
                composable(NavRoutes.Map) {
                    MapScreen()
                }

                composable(NavRoutes.Terms) {
                    TermsOfServiceScreen(navController = navController)
                }

                composable("${NavRoutes.MealDetail}/{mealId}") { backStackEntry ->
                    val mealId = backStackEntry.arguments?.getString("mealId")?.toIntOrNull() ?: 0
                    MealDetailScreen(mealId = mealId, navController = navController)
                }

                composable(
                    route = "${NavRoutes.WORKOUT_DETAIL}/{workoutId}",
                    arguments = listOf(navArgument("workoutId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val workoutId = backStackEntry.arguments?.getString("workoutId") ?: ""
                    val workoutViewModel: WorkoutViewModel = viewModel()
                    WorkoutDetailScreen(
                        workoutId = workoutId,
                        navController = navController,
                        workoutViewModel = workoutViewModel
                    )
                }

                composable(NavRoutes.Schedule) {
                    ScheduleScreen(navController = navController, onBackClick = { navController.popBackStack() })
                }
                composable(NavRoutes.Scan) {
                    ScanScreen(navController)
                }
                composable(NavRoutes.Setting) {
                    val settingViewModel: SettingViewModel = viewModel()
                    SettingScreen(
                        onBackClick = { navController.popBackStack() },
                        navController = navController,
                        viewModel = settingViewModel
                    )
                }
            }
        }
    }
}
