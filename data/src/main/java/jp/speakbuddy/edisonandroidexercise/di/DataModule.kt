package jp.speakbuddy.edisonandroidexercise.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jp.speakbuddy.edisonandroidexercise.data.FactStorage
import jp.speakbuddy.edisonandroidexercise.data.local.FactDao
import jp.speakbuddy.edisonandroidexercise.data.local.FactDatabase
import jp.speakbuddy.edisonandroidexercise.data.remote.FactApi
import jp.speakbuddy.edisonandroidexercise.data.remote.QuizApi
import jp.speakbuddy.edisonandroidexercise.data.remote.TranslationApi
import jp.speakbuddy.edisonandroidexercise.data.repository.FactRepositoryImpl
import jp.speakbuddy.edisonandroidexercise.data.repository.QuizRepositoryImpl
import jp.speakbuddy.edisonandroidexercise.data.repository.TranslationRepositoryImpl
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
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideFactApi(): FactApi {
        return Retrofit.Builder()
            .baseUrl("https://catfact.ninja/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FactApi::class.java)
    }

    @Provides
    @Singleton
    fun provideTranslationApi(): TranslationApi {
        return Retrofit.Builder()
            .baseUrl("https://api.jar-vis.com/api/v1/cron/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TranslationApi::class.java)
    }

    @Provides
    @Singleton
    fun provideQuizApi(): QuizApi {
        return Retrofit.Builder()
            .baseUrl("https://api-jarvis.jar-vis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(QuizApi::class.java)
    }

    @Provides
    @Singleton
    fun provideFactDatabase(@ApplicationContext context: Context): FactDatabase {
        return Room.databaseBuilder(
            context,
            FactDatabase::class.java,
            "fact_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideFactDao(database: FactDatabase): FactDao = database.factDao()

    @Provides
    @Singleton
    fun provideFactStorage(factDao: FactDao): FactStorage = FactStorage(factDao)

    @Provides
    @Singleton
    fun provideFactRepository(api: FactApi, factStorage: FactStorage): FactRepository {
        return FactRepositoryImpl(api, factStorage)
    }

    @Provides
    @Singleton
    fun provideTranslationRepository(api: TranslationApi): TranslationRepository {
        return TranslationRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideQuizRepository(api: QuizApi): QuizRepository {
        return QuizRepositoryImpl(api)
    }

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
    fun provideCoroutineDispatcher(): CoroutineDispatcher {
        return Dispatchers.Default
    }

    @Provides
    @Singleton
    fun provideSearchSavedFactsUseCase(repository: FactRepository): SearchSavedFactsUseCase {
        return SearchSavedFactsUseCase(repository)
    }
}