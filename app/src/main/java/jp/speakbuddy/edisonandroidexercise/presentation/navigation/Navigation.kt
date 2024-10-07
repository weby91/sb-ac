package jp.speakbuddy.edisonandroidexercise.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import jp.speakbuddy.edisonandroidexercise.presentation.fact.FactScreen
import jp.speakbuddy.edisonandroidexercise.presentation.fact.FactViewModel
import jp.speakbuddy.edisonandroidexercise.presentation.fact.SavedFactsScreen

@Composable
fun Navigation() {
    val navController = rememberNavController()
    val viewModel: FactViewModel = hiltViewModel()

    NavHost(navController = navController, startDestination = "factScreen") {
        composable("factScreen") {
            FactScreen(viewModel = viewModel, navController = navController)
        }
        composable("savedFacts") {
            SavedFactsScreen(viewModel = viewModel) {
                navController.popBackStack()
            }
        }
    }
}