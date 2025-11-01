package uk.ac.tees.mad.moodify.ui.auth

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    val isUserLoggedIn = mutableStateOf(auth.currentUser != null)

    fun login(email: String, password: String, onSuccess : () -> Unit, onFailure : (Exception) -> Unit) {
        viewModelScope.launch {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        isUserLoggedIn.value = true
                        onSuccess()
                    } else {
                        val exception = task.exception ?: Exception("Login failed")
                        onFailure(exception)
                    }
                }
        }
    }

    fun signup(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        viewModelScope.launch {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userId = auth.currentUser?.uid
                        val userData = hashMapOf(
                            "firstName" to firstName,
                            "lastName" to lastName,
                            "email" to email,
                            "createdAt" to System.currentTimeMillis()
                        )

                        if (userId != null) {
                            firestore.collection("users")
                                .document(userId)
                                .set(userData)
                                .addOnSuccessListener {
                                    isUserLoggedIn.value = true
                                    onSuccess()
                                }
                                .addOnFailureListener { e ->
                                    onFailure(e)
                                }
                        } else {
                            onFailure(Exception("User ID is null"))
                        }
                    } else {
                        val exception = task.exception ?: Exception("Signup failed")
                        onFailure(exception)
                    }
                }
        }
    }

    fun logout() {
        auth.signOut()
        isUserLoggedIn.value = false
    }
}
