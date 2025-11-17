package uk.ac.tees.mad.moodify.hilt

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import uk.ac.tees.mad.moodify.data.remote.HuggingFaceApi
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object MoodifyModule {

    @Provides
    @Singleton
    fun provideRetrofit() : Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api-inference.huggingface.co/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideHuggingFaceApi(retrofit: Retrofit): HuggingFaceApi {
        return retrofit.create(HuggingFaceApi::class.java)
    }

    @Provides
    fun providesFirestore () : FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    fun provideFirebaseAuth () : FirebaseAuth = FirebaseAuth.getInstance()

}