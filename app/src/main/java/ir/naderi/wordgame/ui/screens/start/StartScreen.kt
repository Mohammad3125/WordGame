package ir.naderi.wordgame.ui.screens.start

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ir.naderi.wordgame.R

/**
 * Jetpack Compose screen that is shown to user when he starts the game that let user
 * Customize the question game on amount of total questions and each question time.
 * @author MohammadHosseinNaderi
 */
@Composable
@Preview
fun StartScreen(
    onStartGameClicked: (questionCount: Int, questionTime: Int) -> Unit = { questionCount, questionTime -> },
) {
    val questionTimes = listOf(2, 4, 6)
    val totalQuestionsOptions = listOf(5, 10, 15)

    Column(
        modifier = Modifier
            .systemBarsPadding()
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        var selectedTime by rememberSaveable { mutableIntStateOf(2) }
        var selectedQuestionCount by rememberSaveable { mutableIntStateOf(5) }

        QuestionTimeContent(
            selectedTime,
            questionTimes
        )
        { time -> selectedTime = time }


        Spacer(modifier = Modifier.height(16.dp))

        TotalQuestionsContent(
            selectedQuestionCount,
            totalQuestionsOptions
        ) { count -> selectedQuestionCount = count }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onStartGameClicked(selectedQuestionCount, selectedTime) },
            modifier = Modifier.fillMaxWidth(0.6f)
        ) {
            Text(text = stringResource(R.string.start_game))
        }
    }
}

@Composable
private fun QuestionTimeContent(
    selectedTime: Int,
    questionTimes: List<Int>,
    onTimeClicked: (Int) -> Unit
) {
    Text(
        text = stringResource(R.string.select_question_time),
        style = MaterialTheme.typography.headlineSmall
    )
    Spacer(modifier = Modifier.height(16.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        questionTimes.forEach { time ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                RadioButton(
                    selected = (time == selectedTime),
                    onClick = { onTimeClicked(time) }
                )
                Text(
                    text = stringResource(R.string.time, time),
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun TotalQuestionsContent(
    selectedQuestionCount: Int,
    totalQuestionsOptions: List<Int>,
    onQuestionCountClicked: (Int) -> Unit
) {

    Text(
        text = stringResource(R.string.select_total_questions),
        style = MaterialTheme.typography.headlineSmall
    )
    Spacer(modifier = Modifier.height(16.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        totalQuestionsOptions.forEach { count ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                RadioButton(
                    selected = (count == selectedQuestionCount),
                    onClick = { onQuestionCountClicked(count) }
                )
                Text(
                    text = count.toString(),
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }
    }
}