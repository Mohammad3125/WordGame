package ir.naderi.wordgame.ui.screens.question

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ir.naderi.wordgame.R
import ir.naderi.wordgame.data.model.Answer
import ir.naderi.wordgame.data.model.Question
import ir.naderi.wordgame.data.model.TranslatedWord
import kotlinx.coroutines.launch


/**
 * Jetpack Compose screen that renders [Question]s and shows error sate and finished state (when user finishes the questions) that report
 * how did user perform on the questions. It works with [QuestionScreenViewModel] that manages it's state.
 * @param onGetBack callback for when user taps on 'getBack' button.
 * @param questionScreenViewModel Viewmodel that creates state for this screen.
 */
@Composable
fun QuestionScreen(
    onGetBack: () -> Unit = {},
    questionScreenViewModel: QuestionScreenViewModel = hiltViewModel()
) {
    val uiState = questionScreenViewModel.uiState.collectAsStateWithLifecycle().value

    QuestionScreenContent(
        uiState,
        onGetBack,
        questionScreenViewModel::onTryAgain,
        questionScreenViewModel::onCorrectClicked,
        questionScreenViewModel::onIncorrectClicked
    )
}

@Composable
fun QuestionScreenContent(
    uiState: QuestionScreenState,
    onGetBack: () -> Unit,
    onTryAgain: () -> Unit,
    onCorrectClicked: () -> Unit,
    onIncorrectClicked: () -> Unit,
) {
    Scaffold(
        Modifier.fillMaxSize(),
        topBar = {
            QuestionScreenTopAppBar(
                uiState.questionNumber,
                uiState.totalQuestions,
                uiState.isLoading,
                uiState.isFinished,
                onGetBack
            )
        }) { innerPadding ->
        Crossfade(
            targetState = uiState.isLoading || uiState.isFinished || uiState.isInErrorState,
            animationSpec = tween(durationMillis = 2000)
        ) { targetState ->
            when {
                targetState && uiState.isInErrorState -> {
                    ErrorStateContent(Modifier.padding(innerPadding), uiState.errorMessage)
                }

                targetState && uiState.isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                targetState && uiState.isFinished -> {
                    ResultContent(
                        Modifier.padding(innerPadding),
                        averageTime = uiState.averageTimeForAnswer,
                        correctAnswers = uiState.totalCorrectAnswers,
                        totalQuestions = uiState.totalQuestions,
                        answers = uiState.answers,
                        onTryAgain = onTryAgain,
                    )
                }

                else -> {
                    MainQuestionScreenContent(
                        uiState,
                        Modifier.padding(innerPadding),
                        onCorrectClicked,
                        onIncorrectClicked
                    )
                }
            }
        }
    }
}

@Composable
@Preview
fun ErrorStateContent(modifier: Modifier = Modifier, message: String = "Couldn't load") {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Filled.Clear,
            contentDescription = message,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .aspectRatio(1f)
        )

        Text(message, style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center)
    }
}

@Composable
fun MainQuestionScreenContent(
    uiState: QuestionScreenState,
    modifier: Modifier = Modifier,
    onCorrectClicked: () -> Unit,
    onIncorrectClicked: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        val animatedProgress = remember { Animatable(0f) }

        LaunchedEffect(uiState.progress) {
            animatedProgress.animateTo(
                targetValue = uiState.progress,
                animationSpec = tween(durationMillis = 300, easing = LinearEasing)
            )
        }

        LinearProgressIndicator(
            progress = { animatedProgress.value },
            modifier = Modifier.fillMaxWidth()
        )

        AnimatingText(
            uiState.question,
            uiState.questionTime,
            Modifier.weight(1f)
        )

        QuestionBar(
            questionName = uiState.question.original.textEnglish,
            onCorrectClicked = onCorrectClicked,
            onIncorrectClicked = onIncorrectClicked
        )
    }
}

@Composable
fun AnimatingText(question: Question, questionTime: Int, modifier: Modifier = Modifier) {
    var boxHeightPx by remember { mutableFloatStateOf(0f) }
    var textHeightPx by remember { mutableFloatStateOf(0f) }
    var animationStarted by remember { mutableStateOf(false) }

    val translationY = remember { Animatable(0f) }
    val textAlpha = remember { Animatable(0f) }

    LaunchedEffect(question, boxHeightPx, textHeightPx) {
        animationStarted = true

        textAlpha.snapTo(0f)
        translationY.snapTo(0f)

        if (animationStarted && boxHeightPx > 0f && textHeightPx > 0f) {
            launch {
                translationY.animateTo(boxHeightPx - textHeightPx, tween(questionTime), 0f)
            }
            launch {
                textAlpha.animateTo(1f, tween(questionTime), 0f)
            }
        }

    }

    Box(
        modifier
            .fillMaxWidth()
            .onGloballyPositioned {
                boxHeightPx = it.size.height.toFloat()
            },
        contentAlignment = Alignment.TopCenter
    ) {
        Text(
            modifier = Modifier
                .onGloballyPositioned {
                    textHeightPx = it.size.height.toFloat()
                }
                .graphicsLayer {
                    // Only changes the draw phase in the whole recomposition.
                    this.translationY = translationY.value
                    this.alpha = textAlpha.value
                },
            maxLines = 1,
            text = question.translation.textSpanish,
            style = MaterialTheme.typography.displayMedium
        )
    }
}

@Composable
fun QuestionBar(
    modifier: Modifier = Modifier,
    questionName: String,
    onCorrectClicked: () -> Unit,
    onIncorrectClicked: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        FilledIconButton(
            onClick = onIncorrectClicked,
            shape = MaterialTheme.shapes.small
        ) {
            Icon(
                Icons.Filled.Clear,
                modifier = Modifier.size(24.dp),
                contentDescription = stringResource(R.string.get_back_from_question_page)
            )
        }

        Column(modifier = modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                stringResource(R.string.is_the_translation_correct),
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                questionName,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.displayMedium
            )
        }

        FilledIconButton(
            onClick = onCorrectClicked,
            shape = MaterialTheme.shapes.small
        ) {
            Icon(
                Icons.Filled.Done,
                modifier = Modifier.size(24.dp),
                contentDescription = stringResource(R.string.get_back_from_question_page)
            )
        }


    }
}

@Composable
fun ResultContent(
    modifier: Modifier = Modifier,
    averageTime: Long,
    correctAnswers: Int,
    totalQuestions: Int,
    answers: List<Answer>,
    onTryAgain: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Row {
            Text(
                text = stringResource(
                    R.string.correct_answers_out_of_total,
                    correctAnswers,
                    totalQuestions
                ),
                style = MaterialTheme.typography.bodyLarge,
            )

            Spacer(Modifier.size(16.dp))

            Text(
                text = stringResource(R.string.average_time, averageTime),
                style = MaterialTheme.typography.bodyLarge,
            )
        }

        Spacer(Modifier.size(16.dp))

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            itemsIndexed(answers) { index, answer ->
                AnswerItem(index + 1, answer)
            }
        }

        Button(onClick = onTryAgain) {
            Text(text = stringResource(R.string.try_again))
        }
    }
}

@Composable
fun AnswerItem(index: Int, answer: Answer, modifier: Modifier = Modifier) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (answer.isAnswerCorrect) Icons.Filled.Done else Icons.Filled.Clear,
            contentDescription = if (answer.isAnswerCorrect) stringResource(R.string.correct_answer) else stringResource(
                R.string.incorrect_answer
            ),
            tint = if (answer.isAnswerCorrect) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
            modifier = Modifier.size(24.dp)
        )
        Spacer(Modifier.size(8.dp))

        Text(text = index.toString())

        Spacer(Modifier.size(8.dp))

        Text(
            text = "${answer.question.original.textEnglish} -> ${answer.question.translation.textSpanish} : ${answer.totalTime}ms",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionScreenTopAppBar(
    currentQuestionNumber: Int,
    totalQuestions: Int,
    isLoading: Boolean,
    isFinished: Boolean,
    onGetBack: () -> Unit = {}
) {
    TopAppBar(
        {
            when {
                isFinished -> {
                    Text(stringResource(R.string.finished))
                }

                isLoading -> {
                    Text(stringResource(R.string.textLoading))
                }

                else -> {
                    Text(stringResource(R.string.question, currentQuestionNumber, totalQuestions))
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = onGetBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.get_back_from_question_page)
                )
            }
        })
}

@Composable
@Preview
fun QuestionBarPreview() {
    MaterialTheme {
        QuestionBar(questionName = "Por Favor", onCorrectClicked = {}, onIncorrectClicked = {})
    }
}

@Composable
@Preview(showSystemUi = true)
fun QuestionScreenPreview() {
    MaterialTheme {
        QuestionScreenContent(
            QuestionScreenState(
                Question(
                    TranslatedWord("Test", "Translated"),
                    TranslatedWord("Test", "Translated"),
                ), isLoading = false
            ), {}, {}, {}, {})
    }
}

@Composable
@Preview
fun ResultContentPreview() {
    MaterialTheme {
        ResultContent(
            averageTime = 3234,
            correctAnswers = 2,
            totalQuestions = 10,
            answers = listOf(
                Answer(
                    Question(TranslatedWord("Test", "Test"), TranslatedWord("Test", "Test")),
                    false,
                    3420
                ),
                Answer(
                    Question(TranslatedWord("Test", "Test"), TranslatedWord("Test", "Test")),
                    true,
                    3420
                ),
                Answer(
                    Question(TranslatedWord("Test", "Test"), TranslatedWord("Test", "Test")),
                    true,
                    3420
                ),
                Answer(
                    Question(TranslatedWord("Test", "Test"), TranslatedWord("Test", "Test")),
                    true,
                    3420
                ),
                Answer(
                    Question(TranslatedWord("Test", "Test"), TranslatedWord("Test", "Test")),
                    true,
                    3420
                ),
                Answer(
                    Question(TranslatedWord("Test", "Test"), TranslatedWord("Test", "Test")),
                    true,
                    3420
                )
            )
        )
    }
}
