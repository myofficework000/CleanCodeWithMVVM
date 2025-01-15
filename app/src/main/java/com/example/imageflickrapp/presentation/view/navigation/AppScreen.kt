package com.example.imageflickrapp.presentation.view.navigation


sealed class AppScreen(val route: String) {

    data object Splash : AppScreen("splash")
    data object Home : AppScreen("home")
    data object Detail : AppScreen("detail")
}
