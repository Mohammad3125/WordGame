package ir.naderi.wordgame.data.repositories

import ir.naderi.wordgame.data.model.Question
import ir.naderi.wordgame.data.model.TranslatedWord

/**
 * An interface defining how any repository should fetch and make questions.
 * @author MohammadHosseinNaderi
 */
interface WordRepository {
    /**
     * Create [TranslatedWord]s from DataSources.
     */
    suspend fun getWords(): List<TranslatedWord>

    /**
     * Create questions with given [questionCount].
     * @param questionCount Number of questions to make.
     */
    suspend fun createQuestion(questionCount: Int): List<Question>
}