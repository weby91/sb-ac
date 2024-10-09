package jp.speakbuddy.edisonandroidexercise.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jp.speakbuddy.edisonandroidexercise.repository.repository.FactRepository
import jp.speakbuddy.edisonandroidexercise.repository.repository.QuizRepository
import jp.speakbuddy.edisonandroidexercise.repository.repository.TranslationRepository
import jp.speakbuddy.edisonandroidexercise.use_case.GetFactUseCase
import jp.speakbuddy.edisonandroidexercise.use_case.GetLatestFactUseCase
import jp.speakbuddy.edisonandroidexercise.use_case.GetQuizUseCase
import jp.speakbuddy.edisonandroidexercise.use_case.GetSavedFactsUseCase
import jp.speakbuddy.edisonandroidexercise.use_case.SaveFactUseCase
import jp.speakbuddy.edisonandroidexercise.use_case.SearchSavedFactsUseCase
import jp.speakbuddy.edisonandroidexercise.use_case.TranslateUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DomainModule {

    @Provides
    @Singleton
    fun provideGetFactUseCase(repository: FactRepository): GetFactUseCase {
        return GetFactUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideSaveFactUseCase(repository: FactRepository): SaveFactUseCase {
        return SaveFactUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetLatestFactUseCase(repository: FactRepository): GetLatestFactUseCase {
        return GetLatestFactUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideTranslateUseCase(repository: TranslationRepository): TranslateUseCase {
        return TranslateUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetSavedFactsUseCase(repository: FactRepository): GetSavedFactsUseCase {
        return GetSavedFactsUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetQuizUseCase(repository: QuizRepository): GetQuizUseCase {
        return GetQuizUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideSearchSavedFactsUseCase(repository: FactRepository): SearchSavedFactsUseCase {
        return SearchSavedFactsUseCase(repository)
    }
}