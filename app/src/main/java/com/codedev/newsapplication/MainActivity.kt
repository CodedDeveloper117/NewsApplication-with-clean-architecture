package com.codedev.newsapplication

import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.Animatable
import androidx.compose.material.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.annotation.ExperimentalCoilApi
import com.codedev.newsapplication.domain.entities.EntityArticle
import com.codedev.newsapplication.presentation.favourites.FavouriteScreen
import com.codedev.newsapplication.presentation.home.HomeScreen
import com.codedev.newsapplication.presentation.search.SearchScreen
import com.codedev.newsapplication.presentation.settings.SettingsScreen
import com.codedev.newsapplication.presentation.ui.navigation.CustomBottomNavigation
import com.codedev.newsapplication.presentation.ui.navigation.CustomBottomNavigationScreens
import com.codedev.newsapplication.presentation.ui.theme.*
import com.codedev.newsapplication.presentation.weather.WeatherScreen
import com.codedev.newsapplication.presentation.web_page.WebPageScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

private const val TAG = "MainActivity"

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @ExperimentalMaterialApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NewsApplicationTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination?.route
                val scope = rememberCoroutineScope()

                val systemUiVisibility = remember {
                    when (currentDestination) {
                        "weather" -> {
                            false
                        }
                        else -> {
                            true
                        }
                    }
                }

                val opacity = remember { 1 }
                val color = remember {
                    when (currentDestination) {
                        "home" -> {
                            Animatable(DarkGrayTone)
                        }
                        "search" -> {
                            Animatable(MidBlue)
                        }
                        "favourite" -> {
                            Animatable(Color.Transparent)
                        }
                        "weather" -> {
                            Animatable(ColorPurple)
                        }
                        "settings" -> {
                            Animatable(ColorPink)
                        }
                        else -> {
                            Animatable(DarkGrayTone)
                        }
                    }
                }

                LaunchedEffect(key1 = color.value) {
                    window.apply {
                        addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                        clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                        statusBarColor = color.value.toArgb()
                    }
                }

                LaunchedEffect(key1 = systemUiVisibility) {
                    if (systemUiVisibility) {
                        showSystemUi()
                    } else hideSystemUi()
                }


                Scaffold(
                    bottomBar = {
                        CustomBottomNavigation(
                            currentDestination = currentDestination,
                        ) {
                            scope.launch {
                                navController.navigate(it.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                                color.animateTo(
                                    targetValue = Color(
                                        if (it.route == "home" || it.route == "search") {
                                            DarkGrayTone.toArgb()
                                        } else it.color.toArgb()
                                    ),
                                )
                            }
                        }
                    }
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = CustomBottomNavigationScreens.Home.route
                    ) {
                        composable(CustomBottomNavigationScreens.Home.route) {
                            HomeScreen(
                                CustomBottomNavigationScreens.Home.color,
                                scope = scope,
                                navController = navController
                            )
                        }
                        composable(CustomBottomNavigationScreens.Favourite.route) {
                            FavouriteScreen(
                                CustomBottomNavigationScreens.Favourite.color
                            )
                        }
                        composable(CustomBottomNavigationScreens.Search.route) {
                            SearchScreen(CustomBottomNavigationScreens.Search.color)
                        }
                        composable(CustomBottomNavigationScreens.Weather.route) {
                            WeatherScreen(CustomBottomNavigationScreens.Weather.color)
                        }
                        composable(CustomBottomNavigationScreens.Settings.route) {
                            SettingsScreen(CustomBottomNavigationScreens.Settings.color)
                        }
                        composable(
                            "webpage"
                        ) { backStackEntry ->
                            val article =
                                navController.previousBackStackEntry?.savedStateHandle?.get<EntityArticle>(
                                    "article"
                                )
                            article?.let {
                                WebPageScreen(article = it)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun hideSystemUi() {

    }

    private fun showSystemUi() {

    }
}