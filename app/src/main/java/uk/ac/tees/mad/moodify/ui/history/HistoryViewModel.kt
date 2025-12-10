package uk.ac.tees.mad.moodify.ui.history

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import uk.ac.tees.mad.moodify.data.local.MoodDao
import uk.ac.tees.mad.moodify.data.local.MoodEntries
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val auth : FirebaseAuth,
    private val db : FirebaseFirestore,
    private val dao : MoodDao
) : ViewModel() {

    init {
        if (auth.currentUser!= null){
            fetchFromFirestore(auth.currentUser!!.uid)
        }
    }

    val user = auth.currentUser
    val userId = user?.uid ?: ""

    val moodsFromRoom : StateFlow<List<MoodEntries>> = dao.getMoodEntries()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())


    fun fetchFromFirestore(uid: String) {
        viewModelScope.launch {
            try {
                db.collection("users")
                    .document(uid)
                    .collection("moodEntries")
                    .get()
                    .addOnSuccessListener {
                        val moodEntries = it.toObjects(MoodEntries::class.java)
                        Log.d("Firestore", "Fetched ${moodEntries.size} entries from Firestore")
                        viewModelScope.launch {
                            dao.deleteAllMoodEntries()
                            dao.insertMoodEntry(moodEntries)
                            Log.d("Firestore", "Inserted ${moodEntries.size} entries into Room")
                        }
                    }
                    .addOnFailureListener {
                        it.printStackTrace()
                    }
            } catch (e : Exception){
                e.printStackTrace()
            }
        }
    }



}