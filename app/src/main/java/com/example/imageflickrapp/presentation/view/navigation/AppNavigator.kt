package com.example.imageflickrapp.presentation.view.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.imageflickrapp.presentation.view.detail.DetailedScreen
import com.example.imageflickrapp.presentation.view.home.HomeScreen
import com.example.imageflickrapp.presentation.view.home.SplashScreen
import com.example.imageflickrapp.presentation.viewmodel.PhotoListViewModel

@Composable
fun AppNavHost(navController: NavHostController, photoListViewModel: PhotoListViewModel) {

    NavHost(navController=navController, startDestination = AppScreen.Splash.route){
        composable("splash") {
            SplashScreen(navController = navController)
        }
        composable(AppScreen.Home.route){
            HomeScreen(navController,photoListViewModel)
        }
        composable(AppScreen.Detail.route){
            DetailedScreen(photoListViewModel)
        }
    }
}