package com.ahujadevansh.mbuddy

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.ahujadevansh.mbuddy.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.signUpButtonRegister.setOnClickListener {

            registerUser()


        }

        binding.alreadyHaveAnAccountTextViewRegister.setOnClickListener {

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun registerUser() {
        val email = binding.emailEditTextRegister.text.toString()
        val password = binding.passwordEditTextRegister.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill email and password fields", Toast.LENGTH_LONG).show()
            return
        }

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (!it.isComplete) return@addOnCompleteListener
                Toast.makeText(this, "hello ${it.result?.user?.email}", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Log.d("MAIN", "something went wrong")
                Toast.makeText(
                    this,
                    "Please make sure your email and password is correct",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}