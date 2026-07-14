package pl.kul.espmobileapp.view.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import pl.kul.espmobileapp.navigation.AppNavigation
import pl.kul.espmobileapp.navigation.Screen
import pl.kul.espmobileapp.view.cardView.components.BottomBar

@Composable
fun MainLayout() {
    val navController = rememberNavController()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val currentScreen: Screen = remember(currentRoute) {
        Screen.allScreen.find { it.route == currentRoute } ?: Screen.MainScreen
    }
    Scaffold(
        bottomBar = {
            BottomBar(
                currentScreen = currentScreen,
                navController = navController
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            AppNavigation(
                navController = navController
            )
        }
    }
}