package com.travelgo.ui.theme.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.travelgo.R
import com.travelgo.ui.theme.home.HomeActivity
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    // 1. Configurar el lanzador para el resultado de Google
    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            account?.idToken?.let { idToken ->
                firebaseAuthWithGoogle(idToken)
            }
        } catch (e: ApiException) {
            Log.w("LoginActivity", "Google sign in failed", e)
            Toast.makeText(this, "Error al conectar con Google: ${e.statusCode}", Toast.LENGTH_LONG).show()
        }
    }

    // CREA PERFIL EN FIRESTORE SI NO EXISTE
    private fun crearPerfilSiNoExiste() {
        val user = auth.currentUser ?: return
        val uid = user.uid
        val email = user.email ?: ""

        val userDoc = FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)

        userDoc.get().addOnSuccessListener { snap ->
            if (!snap.exists()) {
                val data = mapOf(
                    "uid" to uid,
                    "email" to email,
                    "nombre" to (user.displayName ?: ""),
                    "foto" to (user.photoUrl?.toString() ?: ""),
                    "createdAt" to System.currentTimeMillis()
                )
                userDoc.set(data)
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        // 2. Configurar Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Referencias UI
        val etUser = findViewById<EditText>(R.id.etUser)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnGoogle = findViewById<SignInButton>(R.id.btnGoogleSignIn)

        // --- LOGIN CON EMAIL ---
        btnLogin.setOnClickListener {
            val email = etUser.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        crearPerfilSiNoExiste()   // <--- ***IMPORTANTE***
                        irAHome()
                    } else {
                        Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        }

        // --- LOGIN CON GOOGLE ---
        btnGoogle.setOnClickListener {
            signInGoogle()
        }
    }

    private fun signInGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    crearPerfilSiNoExiste()   // <--- ***IMPORTANTE***
                    Toast.makeText(this, "¡Bienvenido!", Toast.LENGTH_SHORT).show()
                    irAHome()
                } else {
                    Toast.makeText(this, "Error en Firebase: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun irAHome() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun onStart() {
        super.onStart()
        if (auth.currentUser != null) {
            irAHome()
        }
    }
}   
