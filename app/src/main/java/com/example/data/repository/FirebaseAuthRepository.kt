package com.example.data.repository

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/**
 * Repository for managing Firebase Authentication with Email/Password and
 * Google Sign-In using the Android Credential Manager API.
 */
class FirebaseAuthRepository(private val context: Context) {

    private val auth: FirebaseAuth? by lazy {
        try {
            if (FirebaseApp.getApps(context).isEmpty()) {
                val options = try {
                    FirebaseOptions.fromResource(context)
                } catch (e: Exception) {
                    null
                }
                if (options != null) {
                    FirebaseApp.initializeApp(context, options)
                } else {
                    val fallbackOptions = FirebaseOptions.Builder()
                        .setApplicationId("1:1234567890:android:safenestapp")
                        .setApiKey("AIzaSySafeNestDemoApiKeyForFirebase")
                        .setProjectId("safenest-demo-project")
                        .build()
                    FirebaseApp.initializeApp(context, fallbackOptions)
                }
            }
            FirebaseAuth.getInstance()
        } catch (e: Exception) {
            Log.e("FirebaseAuthRepo", "Firebase Auth initialization error: ${e.message}")
            null
        }
    }

    private val _currentUserState = MutableStateFlow<FirebaseUser?>(null)
    val currentUserState: StateFlow<FirebaseUser?> = _currentUserState.asStateFlow()

    init {
        try {
            auth?.let { firebaseAuth ->
                _currentUserState.value = firebaseAuth.currentUser
                firebaseAuth.addAuthStateListener { listenerAuth ->
                    _currentUserState.value = listenerAuth.currentUser
                }
            }
        } catch (e: Exception) {
            Log.e("FirebaseAuthRepo", "Error setting up AuthStateListener: ${e.message}")
        }
    }

    val isUserLoggedIn: Boolean
        get() = try {
            auth?.currentUser != null
        } catch (e: Exception) {
            false
        }

    suspend fun signInWithEmail(email: String, password: String): Result<FirebaseUser> = withContext(Dispatchers.IO) {
        val firebaseAuth = auth ?: return@withContext Result.failure(
            Exception("Firebase Authentication is unavailable.")
        )
        try {
            val result = firebaseAuth.signInWithEmailAndPassword(email.trim(), password).await()
            val user = result.user ?: throw Exception("Authentication returned empty user details.")
            _currentUserState.value = user
            Result.success(user)
        } catch (e: Exception) {
            Log.e("FirebaseAuthRepo", "Email sign in error", e)
            Result.failure(e)
        }
    }

    suspend fun signUpWithEmail(email: String, password: String, name: String? = null): Result<FirebaseUser> = withContext(Dispatchers.IO) {
        val firebaseAuth = auth ?: return@withContext Result.failure(
            Exception("Firebase Authentication is unavailable.")
        )
        try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email.trim(), password).await()
            val user = result.user ?: throw Exception("User creation failed.")
            
            if (!name.isNullOrBlank()) {
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(name.trim())
                    .build()
                user.updateProfile(profileUpdates).await()
            }
            _currentUserState.value = user
            Result.success(user)
        } catch (e: Exception) {
            Log.e("FirebaseAuthRepo", "Email sign up error", e)
            Result.failure(e)
        }
    }

    /**
     * Google Sign-In via Android Credential Manager & Google ID Token.
     */
    suspend fun signInWithGoogle(activityContext: Context, webClientId: String? = null): Result<FirebaseUser> = withContext(Dispatchers.IO) {
        val firebaseAuth = auth ?: return@withContext Result.failure(
            Exception("Firebase Authentication is unavailable.")
        )
        try {
            val credentialManager = CredentialManager.create(activityContext)
            
            val clientId = webClientId?.takeIf { it.isNotBlank() }
                ?: "1000000000000-dummyclient.apps.googleusercontent.com"

            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(clientId)
                .setAutoSelectEnabled(false)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(
                context = activityContext,
                request = request
            )

            val credential = result.credential
            if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val idToken = googleIdTokenCredential.idToken
                val authCredential = GoogleAuthProvider.getCredential(idToken, null)
                val authResult = firebaseAuth.signInWithCredential(authCredential).await()
                val user = authResult.user ?: throw Exception("User is null after Google authentication.")
                _currentUserState.value = user
                Result.success(user)
            } else {
                Result.failure(Exception("Unsupported credential response from Google Credential Manager."))
            }
        } catch (e: GetCredentialCancellationException) {
            Log.d("FirebaseAuthRepo", "Google Sign-In prompt dismissed by user.")
            Result.failure(Exception("Google Sign-In was cancelled."))
        } catch (e: GetCredentialException) {
            Log.e("FirebaseAuthRepo", "Credential Manager failure: ${e.message}")
            Result.failure(Exception("Google Sign-In failed: ${e.localizedMessage ?: "Credential error"}"))
        } catch (e: Exception) {
            Log.e("FirebaseAuthRepo", "Google Sign-In exception", e)
            Result.failure(e)
        }
    }

    fun signOut() {
        try {
            auth?.signOut()
            _currentUserState.value = null
        } catch (e: Exception) {
            Log.e("FirebaseAuthRepo", "Sign out error", e)
        }
    }
}
