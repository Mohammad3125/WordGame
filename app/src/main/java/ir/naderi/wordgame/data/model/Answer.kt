package ir.naderi.wordgame.data.model

/**
 * @author MohammadHosseinNaderi
 * Data class that holds answers that user gave.
 * @author MohammadHosseinNaderi
 */
data class Answer(val question: Question, val isAnswerCorrect: Boolean, val totalTime: Long)
