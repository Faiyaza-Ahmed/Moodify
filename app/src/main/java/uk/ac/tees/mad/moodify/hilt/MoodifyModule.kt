package uk.ac.tees.mad.moodify.hilt

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
object MoodifyModule {

    @Provides
    fun providesFirestore () : FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    fun provideFirebaseAuth () : FirebaseAuth = FirebaseAuth.getInstance()

}