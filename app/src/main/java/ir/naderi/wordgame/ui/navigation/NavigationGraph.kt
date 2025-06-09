package ir.naderi.wordgame.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ir.naderi.wordgame.ui.screens.question.QuestionScreen
import ir.naderi.wordgame.ui.screens.start.StartScreen
import kotlinx.serialization.Serializable


@Serializable
data class QuestionsPage(val time: Int, val totalQuestions: Int)

@Serializable
object Start

/**
 * This composable represents navigation graph of the app which contains two screens, [QuestionScreen] and [StartScreen]
 * @author MohammadHosseinNaderi
 */
@Composable
fun GameNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Start) {
        composable<QuestionsPage> {
            QuestionScreen({ navController.navigate(Start) })
        }
        composable<Start> {
            StartScreen() { questionCount, questionTime ->
                navController.navigate(QuestionsPage(questionTime, questionCount))
            }
        }
    }
}