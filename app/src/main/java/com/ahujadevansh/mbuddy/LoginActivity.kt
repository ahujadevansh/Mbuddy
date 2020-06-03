package com.ahujadevansh.mbuddy

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.ahujadevansh.mbuddy.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        binding.buttonSignIn.setOnClickListener {
            signIn()
        }
    }

    private fun signIn() {

        val email = binding.emailEditTextSignIn.text.toString()
        val password = binding.passwordEditTextSignIn.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill email and password fields", Toast.LENGTH_LONG).show()
            return
        }

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (!it.isComplete) return@addOnCompleteListener
                Toast.makeText(this, "hello ${it.result?.user?.email}", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MessagesActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)

            }
            .addOnFailureListener {

            Toast.makeText(
                    this,
                    "Please make sure your email and password is correct",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}