package uk.ac.tees.mad.moodify.hilt

import android.content.Context
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import uk.ac.tees.mad.moodify.data.local.AppDatabase
import uk.ac.tees.mad.moodify.data.remote.HuggingFaceApi
import uk.ac.tees.mad.moodify.data.remote.SpotifyApi
import javax.inject.Qualifier
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object MoodifyModule {


    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "moodify_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideMoodDao(appDatabase: AppDatabase) = appDatabase.moodDao()


    @Provides
    @Singleton
    @HuggingApi
    fun provideRetrofit() : Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api-inference.huggingface.co/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    @SpotifyQualifierApi
    fun provideSpotifyRetrofit() : Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.spotify.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideSpotifyApi(@SpotifyQualifierApi retrofit: Retrofit): SpotifyApi {
        return retrofit.create(SpotifyApi::class.java)
    }

    @Provides
    @Singleton
    fun provideHuggingFaceApi(@HuggingApi retrofit: Retrofit): HuggingFaceApi {
        return retrofit.create(HuggingFaceApi::class.java)
    }

    @Provides
    fun providesFirestore () : FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    fun provideFirebaseAuth () : FirebaseAuth = FirebaseAuth.getInstance()

}