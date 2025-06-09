package ir.naderi.wordgame.ui.screens.question

import android.os.CountDownTimer
import androidx.annotation.FloatRange
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.naderi.wordgame.data.model.Answer
import ir.naderi.wordgame.data.model.Question
import ir.naderi.wordgame.data.repositories.WordRepository
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


/**
 * Data class that represents the state of [QuestionScreen].
 * @author MohammadHosseinNaderi
 */
data class QuestionScreenState(
    val question: Question = Question.DEFAULT_QUESTION,
    @FloatRange(0.0, 1.0) val progress: Float = 0f,
    val questionNumber: Int = 0,
    val totalQuestions: Int = 0,
    val questionTime: Int = 5000,
    val isLoading: Boolean = true,
    val isFinished: Boolean = false,
    val isInErrorState: Boolean = false,
    val totalCorrectAnswers: Int = 0,
    val averageTimeForAnswer: Long = 0,
    val answers: List<Answer> = emptyList(),
    val errorMessage: String = ""
)

/**
 * Viewmodel that manages [Question] and [Answer] and state of [QuestionScreen].
 * @param wordRepository Any implementation of a word repository to fetch words.
 * @param savedStateHandle To fetch arguments passed from [StartScreen]
 */
@HiltViewModel
class QuestionScreenViewModel @Inject constructor(
    private val wordRepository: WordRepository,
    savedStateHandle: SavedStateHandle
) :
    ViewModel() {

    private lateinit var questionList: List<Question>
    private val answerHolder = mutableListOf<Answer>()
    private var questionIndex = 0

    private val questionCount = savedStateHandle.get<Int>("totalQuestions")!!
    private val questionTime: Long = savedStateHandle.get<Int>("time")!! * 1000L

    private val _uiState = MutableStateFlow(
        QuestionScreenState(questionTime = questionTime.toInt())
    )

    /**
     * Current time holder for the [Question] in [QuestionScreenState].
     */
    private var currentTimeMillis = 0L

    /**
     * implementation of a [CountDownTimer] that is responsible to measure the time it took user to answer the question.
     */
    private val countDownTimer = object : CountDownTimer(questionTime, COUNTDOWN_INTERVAL) {
        override fun onTick(millisUntilFinished: Long) {
            currentTimeMillis = questionTime - millisUntilFinished
        }

        override fun onFinish() {
            answerHolder.add(Answer(_uiState.value.question, false, questionTime))
            currentTimeMillis = 0
            loadNextQuestion()
        }
    }

    val uiState: StateFlow<QuestionScreenState>
        get() = _uiState

    init {
        loadNewQuestions(questionCount)
    }

    private fun loadNewQuestions(count: Int = 5) {
        runWithExceptionHandling {
            questionIndex = 0

            questionList = wordRepository.createQuestion(count)

            countDownTimer.cancelTimer()

            _uiState.update {
                QuestionScreenState(
                    question = questionList.first(),
                    questionNumber = 1,
                    questionTime = questionTime.toInt(),
                    totalQuestions = questionList.size,
                    isLoading = false,
                )
            }

            countDownTimer.start()
        }
    }

    /**
     * Function for when user selects the correct button. This internally calculates the correct answer.
     */
    fun onCorrectClicked() {
        saveAnswerAndLoadNextQuestion(isCorrectClicked = true)
    }

    /**
     * Function for when user selects the correct button. This internally calculates the correct answer.
     */
    fun onIncorrectClicked() {
        saveAnswerAndLoadNextQuestion(isCorrectClicked = false)
    }

    /**
     * Function for when user want's to try again.
     */
    fun onTryAgain() {
        runWithExceptionHandling {
            answerHolder.clear()

            _uiState.update {
                it.copy(isLoading = true, isFinished = false)
            }

            loadNewQuestions(questionCount)
        }
    }

    /**
     * Saves the answer in answer data holder and loads the next question and updates the ui on it.
     */
    private fun saveAnswerAndLoadNextQuestion(isCorrectClicked: Boolean) {
        if (isTotalAnswerReached()) {
            return
        }
        runWithExceptionHandling {
            _uiState.value.handleAnswer(isCorrectClicked)
            loadNextQuestion()
        }
    }

    private fun isTotalAnswerReached(): Boolean = answerHolder.lastIndex == questionIndex

    /**
     * Loads next question from the question data holder and updates the state of app.
     */
    private fun loadNextQuestion() {
        countDownTimer.cancelTimer()

        _uiState.update {
            if (isThereMoreQuestions()) {
                countDownTimer.start()

                it.copy(
                    progress = (questionIndex + 1).toFloat() / questionList.size,
                    totalQuestions = questionList.size,
                    question = questionList[++questionIndex],
                    questionNumber = questionIndex + 1,
                )
            } else {
                // If there isn't any more questions, complete the progress that indicates the questions are finished.
                it.copy(
                    progress = 1f,
                    isFinished = true,
                    totalCorrectAnswers = answerHolder.count { answer -> answer.isAnswerCorrect },
                    averageTimeForAnswer = answerHolder.map { answer -> answer.totalTime }.average()
                        .toLong(),
                    answers = answerHolder.toList()
                )
            }

        }
    }

    /**
     * Adds answers to a data holder that later will be used to represent all the answers that user gave.
     * This extention function is also responsible for checking the correct answer and put in a [Answer] object.
     */
    private fun QuestionScreenState.handleAnswer(isCorrectClicked: Boolean) {
        answerHolder.add(
            Answer(
                _uiState.value.question, isCorrectAnswer(isCorrectClicked), currentTimeMillis
            )
        )
    }

    /**
     * Function responsible for executing a suspend block in a ViewModelScope and handling UI update on errors.
     * @param block high-order function to be run inside a ViewModelScope.
     */
    private fun runWithExceptionHandling(block: suspend CoroutineScope.() -> Unit) {
        viewModelScope.launch {
            try {
                block()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isInErrorState = true, errorMessage = e.message ?: "Unknown Error")
                }
            }
        }
    }

    private fun CountDownTimer.cancelTimer() {
        currentTimeMillis = 0
        cancel()
    }

    private fun isThereMoreQuestions(): Boolean = questionIndex < questionList.lastIndex

    private fun QuestionScreenState.isAnswerAndQuestionSame(): Boolean =
        this.question.original == this.question.translation

    private fun QuestionScreenState.isCorrectAnswer(isCorrectClicked: Boolean): Boolean {
        return ((isAnswerAndQuestionSame() && isCorrectClicked) || (!isAnswerAndQuestionSame() && !isCorrectClicked))
    }

    companion object {
        private const val COUNTDOWN_INTERVAL = 1L
    }
}