package ir.naderi.wordgame.di

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ir.naderi.wordgame.data.repositories.DefaultWordRepository
import ir.naderi.wordgame.data.repositories.WordRepository
import ir.naderi.wordgame.data.source.WordDatasource
import ir.naderi.wordgame.data.source.local.LocalJsonDatasource
import ir.naderi.wordgame.utils.DefaultQuestionMaker
import ir.naderi.wordgame.utils.QuestionMaker

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {
    @Binds
    abstract fun provideWordRepository(defaultWordRepository: DefaultWordRepository): WordRepository

    @Binds
    abstract fun provideDatasource(localJsonDatasource: LocalJsonDatasource): WordDatasource

    @Binds
    abstract fun provideQuestionMaker(defaultQuestionMaker: DefaultQuestionMaker): QuestionMaker
}

@Module
@InstallIn(SingletonComponent::class)
object DataSourceModule {

    @Provides
    fun createDatasource(@ApplicationContext context: Context) = LocalJsonDatasource(context)

    @Provides
    fun createQuestionMaker() = DefaultQuestionMaker()
}

