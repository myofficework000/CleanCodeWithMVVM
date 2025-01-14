package com.example.imageflickrapp.presentation.view.navigation

/**
 * Represents the navigation routes within the application using a sealed class structure.
 * Each object in the sealed class corresponds to a specific screen in the app.
 *
 * @property route The unique route associated with each screen.
 */
sealed class AppScreen(val route: String) {
    /**
     * Represents the Home screen route.
     */
    data object Home : AppScreen("home")

    /**
     * Represents the Detailed screen route.
     */
    data object Detail : AppScreen("detail")
}
