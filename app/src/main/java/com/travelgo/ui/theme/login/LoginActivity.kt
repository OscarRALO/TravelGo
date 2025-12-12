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
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.travelgo.R
import com.travelgo.ui.theme.home.HomeActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var etUser: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnGoogle: Button

    companion object {
        private const val TAG = "LoginActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        Log.d(TAG, "onCreate")

        auth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        initViews()
        setupListeners()
    }

    private fun initViews() {
        etUser = findViewById(R.id.etUser)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        btnGoogle = findViewById(R.id.btnGoogle)
    }

    private fun setupListeners() {
        btnLogin.setOnClickListener {
            val email = etUser.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            loginWithEmail(email, password)
        }

        btnGoogle.setOnClickListener {
            signInGoogle()
        }
    }

    private fun loginWithEmail(email: String, password: String) {
        Log.d(TAG, "loginWithEmail: $email")

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "loginWithEmail: EXITO")
                    val user = auth.currentUser
                    Log.d(TAG, "Usuario: ${user?.email} (uid=${user?.uid})")

                    crearPerfilSiNoExiste()
                    irAHome()
                } else {
                    Log.e(TAG, "loginWithEmail: ERROR - ${task.exception?.message}")
                    Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            Log.d(TAG, "Google Sign-In exitoso: ${account.email}")
            account?.idToken?.let { idToken ->
                firebaseAuthWithGoogle(idToken)
            }
        } catch (e: ApiException) {
            Log.w(TAG, "Google sign in failed: ${e.statusCode}", e)
            Toast.makeText(this, "Error al conectar con Google: ${e.statusCode}", Toast.LENGTH_LONG).show()
        }
    }

    private fun signInGoogle() {
        Log.d(TAG, "signInGoogle: Iniciando flujo de Google Sign-In")
        val signInIntent = googleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        Log.d(TAG, "firebaseAuthWithGoogle")
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "firebaseAuthWithGoogle: EXITO")
                    val user = auth.currentUser
                    Log.d(TAG, "Usuario: ${user?.email} (uid=${user?.uid})")

                    crearPerfilSiNoExiste()
                    Toast.makeText(this, "Bienvenido ${user?.displayName}!", Toast.LENGTH_SHORT).show()
                    irAHome()
                } else {
                    Log.e(TAG, "firebaseAuthWithGoogle: ERROR - ${task.exception?.message}")
                    Toast.makeText(this, "Error en Firebase: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun crearPerfilSiNoExiste() {
        val user = auth.currentUser ?: return
        val uid = user.uid
        val email = user.email ?: ""

        Log.d(TAG, "crearPerfilSiNoExiste: uid=$uid, email=$email")

        val userDoc = FirebaseFirestore.getInstance().collection("users").document(uid)

        userDoc.get().addOnSuccessListener { snap ->
            if (!snap.exists()) {
                Log.d(TAG, "Perfil no existe, creando nuevo perfil")

                val data = mapOf(
                    "uid" to uid,
                    "email" to email,
                    "nombre" to (user.displayName ?: ""),
                    "foto" to (user.photoUrl?.toString() ?: ""),
                    "createdAt" to System.currentTimeMillis()
                )

                userDoc.set(data)
                    .addOnSuccessListener {
                        Log.d(TAG, "Perfil creado exitosamente")
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Error creando perfil: ${e.message}", e)
                    }
            } else {
                Log.d(TAG, "Perfil ya existe")
            }
        }.addOnFailureListener { e ->
            Log.e(TAG, "Error verificando perfil: ${e.message}", e)
        }
    }

    private fun irAHome() {
        Log.d(TAG, "irAHome: Navegando a HomeActivity")
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            Log.d(TAG, "onStart: Usuario ya autenticado - ${currentUser.email}")
            irAHome()
        }
    }
}