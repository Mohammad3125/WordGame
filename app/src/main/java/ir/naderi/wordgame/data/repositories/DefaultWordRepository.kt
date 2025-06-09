package ir.naderi.wordgame.data.repositories

import ir.naderi.wordgame.data.model.Question
import ir.naderi.wordgame.data.model.TranslatedWord
import ir.naderi.wordgame.data.source.WordDatasource
import ir.naderi.wordgame.di.IoDispatcher
import ir.naderi.wordgame.utils.QuestionMaker
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.IOException

/**
 * Implementation of [WordRepository] that handles word fetching from any [WordDatasource].
 * It is also responsible for creating questions by using any [QuestionMaker].
 * @param wordDatasource any [WordDatasource] to fetch words from it.
 * @param questionMaker any [QuestionMaker] to create questions based on words.
 * @author MohammadHosseinNaderi
 */
class DefaultWordRepository @Inject constructor(
    private val wordDatasource: WordDatasource,
    private val questionMaker: QuestionMaker,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) :
    WordRepository {
    override suspend fun getWords(): List<TranslatedWord> {
        return withContext(dispatcher) {
            try {
                wordDatasource.getWords()
            } catch (_: IOException) {
                throw IllegalStateException("Couldn't read the questions file")
            } catch (_: Exception) {
                throw IllegalStateException("There was an error fetching words")
            }
        }
    }

    override suspend fun createQuestion(questionCount: Int): List<Question> {
        return withContext(dispatcher) {
            // Simulate a heavy workload.
            delay(3000)

            getWords().takeIf { it.isNotEmpty() }?.let { questions ->
                questionMaker.createQuestions(questions, questionCount)
            } ?: throw IllegalStateException("No words found")
        }
    }
}