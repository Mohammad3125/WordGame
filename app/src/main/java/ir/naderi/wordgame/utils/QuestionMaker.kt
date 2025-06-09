package ir.naderi.wordgame.utils

import ir.naderi.wordgame.data.model.Question
import ir.naderi.wordgame.data.model.TranslatedWord

/**
 * Interface defining how a class should make list of [Question] of out [TranslatedWord]s.
 */
interface QuestionMaker {
    /**
     * Takes list of [TranslatedWord] and [questionCount] and makes a list of [Question] from it.
     */
    suspend fun createQuestions(questions: List<TranslatedWord>, questionCount: Int): List<Question>
}