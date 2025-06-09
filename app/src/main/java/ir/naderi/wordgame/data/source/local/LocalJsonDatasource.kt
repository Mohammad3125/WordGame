package ir.naderi.wordgame.data.source.local

import android.content.Context
import ir.naderi.wordgame.data.model.TranslatedWord
import ir.naderi.wordgame.data.source.WordDatasource
import jakarta.inject.Inject
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream

/**
 * Implementation of a [WordDatasource] class that's responsible for fetching words from a json file.
 * @throws java.io.IOException
 * @author MohammadHosseinNaderi
 */
class LocalJsonDatasource @Inject constructor(private val context: Context) : WordDatasource {

    @OptIn(ExperimentalSerializationApi::class)
    override fun getWords(): List<TranslatedWord> {
        return Json.decodeFromStream(context.assets.open("words.json"))
    }
}