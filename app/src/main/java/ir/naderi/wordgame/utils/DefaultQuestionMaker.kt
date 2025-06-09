package ir.naderi.wordgame.utils

import ir.naderi.wordgame.data.model.Question
import ir.naderi.wordgame.data.model.TranslatedWord
import kotlin.random.Random

/**
 * Implementation of a [QuestionMaker] that is responsible for making questions.
 * @author MohammadHosseinNaderi
 */
class DefaultQuestionMaker : QuestionMaker {
    override suspend fun createQuestions(
        questions: List<TranslatedWord>,
        questionCount: Int
    ): List<Question> {
        val totalAvailableQuestions = questions.size / 2

        if (questionCount > totalAvailableQuestions) {
            throw IllegalStateException("Question count is greater than available questions")
        }

        if (totalAvailableQuestions < 2) {
            throw IllegalStateException("Not enough words to create questions")
        }

        return mutableListOf<Question>().also { questionList ->
            // Shuffle the questions to prevent predictable questions.
            questions.shuffled().let {
                repeat(questionCount) { index ->
                    val finalIndex = index + 1

                    // Create a question from current translated word and a answer from next word in a 50/50 chance.
                    // So it might be the same word and answer in a question (which makes the question a correct question).
                    questionList.add(
                        Question(
                            it[finalIndex],
                            it[Random.nextInt(finalIndex, finalIndex * 2)]
                        )
                    )
                }
            }
        }
    }

}
