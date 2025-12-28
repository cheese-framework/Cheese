package net.codeocean.cheese.bottomnav

import android.content.Context
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable

import net.codeocean.cheese.screen.DebugScreen

import net.codeocean.cheese.screen.HomeScreen

import net.codeocean.cheese.screen.SettingsScreen


@ExperimentalMaterial3Api
@Composable
fun BottomNavGraph(navController: NavHostController) {

    NavHost(navController = navController, startDestination = BottomBarScreen.Home.route) {
        composable(route = BottomBarScreen.Home.route)
        {
            HomeScreen()
        }
        composable(route = BottomBarScreen.Debug.route)
        {
            DebugScreen()
        }
        composable(route = BottomBarScreen.Settings.route)
        {
            SettingsScreen()
        }



    }


}