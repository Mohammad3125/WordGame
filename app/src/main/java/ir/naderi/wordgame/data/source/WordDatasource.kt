package ir.naderi.wordgame.data.source

import ir.naderi.wordgame.data.model.TranslatedWord

/**
 * An interface defining how any datasource should fetch words.
 * @author MohammadHosseinNaderi
 */
interface WordDatasource {
    fun getWords(): List<TranslatedWord>
}