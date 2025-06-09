package ir.naderi.wordgame.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Class representing a word and it's translation.
 * @param textEnglish English text.
 * @param textSpanish Spanish text (translation) .
 * @author MohammadHosseinNaderi
 */
@Serializable
data class TranslatedWord(
    @SerialName("text_eng")
    val textEnglish: String,
    @SerialName("text_spa")
    val textSpanish: String
)