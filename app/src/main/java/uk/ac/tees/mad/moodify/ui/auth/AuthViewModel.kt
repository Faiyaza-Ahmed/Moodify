package uk.ac.tees.mad.moodify.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    // Example fake loading to simulate API call
    fun login(email: String, password: String, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            // TODO: Replace with Firebase Auth login
            // FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            //     .addOnCompleteListener { task ->
            //         if (task.isSuccessful) onResult(Result.success(Unit))
            //         else onResult(Result.failure(task.exception ?: Exception("Login failed")))
            //     }

            // temporary mock delay to simulate network
            delay(1000)
            if (email == "test@gmail.com" && password == "123456") {
                onResult(Result.success(Unit))
            } else {
                onResult(Result.failure(Exception("Invalid credentials")))
            }
        }
    }

    fun signup(firstName: String, lastName: String, email: String, password: String, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            // TODO: Replace with Firebase Auth signup
            // FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            //     .addOnCompleteListener { task ->
            //         if (task.isSuccessful) {
            //             // Optionally save name in Firestore
            //             onResult(Result.success(Unit))
            //         } else {
            //             onResult(Result.failure(task.exception ?: Exception("Signup failed")))
            //         }
            //     }

            delay(1000)
            onResult(Result.success(Unit)) // fake success for now
        }
    }
}
