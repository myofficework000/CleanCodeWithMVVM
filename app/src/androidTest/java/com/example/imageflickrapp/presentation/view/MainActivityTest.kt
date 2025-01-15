package com.example.imageflickrapp.presentation.view

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltAndroidRule

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun searchBarIsVisible() {
        composeTestRule.onNode(hasSetTextAction()).assertExists()
        composeTestRule.onNode(hasSetTextAction()).assertIsEnabled()
    }

    @Test
    fun photoGridIsInitiallyEmpty() {
        composeTestRule.onAllNodesWithTag("photoGrid")
            .assertCountEquals(0)
    }

    @Test
    fun emptyStateMessageNotVisibleInitially() {
        composeTestRule.onNodeWithText("No Results Found")
            .assertDoesNotExist()
    }
}