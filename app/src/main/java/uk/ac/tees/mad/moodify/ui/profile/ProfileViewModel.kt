package uk.ac.tees.mad.moodify.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import uk.ac.tees.mad.moodify.data.local.MoodDao
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val dao: MoodDao
) : ViewModel() {

    private val _userName = MutableStateFlow("")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        if (auth.currentUser != null) {
            fetchUserData()
        }
    }

    fun fetchUserData() {
        viewModelScope.launch {
            try {
                val snapshot = db.collection("users")
                    .document(auth.currentUser!!.uid)
                    .get()
                    .await()
                val firstName = snapshot.getString("firstName") ?: ""
                val lastName = snapshot.getString("lastName") ?: ""
                _userName.value = "$firstName $lastName".trim()
            } catch (e: Exception) {
                _error.value = "Failed to fetch user data: ${e.message}"
            }
        }
    }

    fun updateUserData(fullName: String) {
        viewModelScope.launch {
            try {
                if (fullName.isBlank()) {
                    _error.value = "Name cannot be empty"
                    return@launch
                }
                val parts = fullName.trim().split("\\s+".toRegex(), limit = 2)
                val firstName = parts[0]
                val lastName = parts.getOrElse(1) { "" }
                db.collection("users")
                    .document(auth.currentUser!!.uid)
                    .update(mapOf("firstName" to firstName, "lastName" to lastName))
                    .await()
                _userName.value = fullName.trim()
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Failed to update name: ${e.message}"
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun logOut() {
        auth.signOut()
    }
}