package ir.naderi.wordgame.data.model

/**
 * Data class that has two [TranslatedWord] in it. It'll be used by any [ir.naderi.wordgame.utils.QuestionMaker] to create
 * a list of [ir.naderi.wordgame.data.model.Question] object that later will be used to show questions to user.
 * @author MohammadHosseinNaderi
 */
data class Question(val original: TranslatedWord, val translation: TranslatedWord) {
    companion object {
        val DEFAULT_QUESTION = Question(TranslatedWord("", ""), TranslatedWord("", ""))
    }
}
