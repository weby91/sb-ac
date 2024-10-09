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
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideFactApi(okHttpClient: OkHttpClient): FactApi {
        return Retrofit.Builder()
            .baseUrl("https://catfact.ninja/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FactApi::class.java)
    }

    @Provides
    @Singleton
    fun provideTranslationApi(okHttpClient: OkHttpClient): TranslationApi {
        return Retrofit.Builder()
            .baseUrl("https://api.jar-vis.com/api/v1/cron/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TranslationApi::class.java)
    }

    @Provides
    @Singleton
    fun provideQuizApi(okHttpClient: OkHttpClient): QuizApi {
        return Retrofit.Builder()
            .baseUrl("https://api-jarvis.jar-vis.com/")
            .client(okHttpClient)
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
}