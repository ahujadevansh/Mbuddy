package com.ahujadevansh.mbuddy

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.ahujadevansh.mbuddy.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var selectedPhotoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)


        checkLogin()
        binding.signUpButtonRegister.setOnClickListener {

            registerUser()


        }

        binding.alreadyHaveAnAccountTextViewRegister.setOnClickListener {

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.selectPhotoButton.setOnClickListener {
            selectPhoto()
        }
    }

    private fun checkLogin() {

        val uid = FirebaseAuth.getInstance().uid
        if (uid != null) {
            val intent = Intent(this, MessagesActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

    }

    private fun selectPhoto() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {

            selectedPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
            binding.selectPhotoImageview.setImageBitmap(bitmap)
            binding.selectPhotoButton.alpha = 0f
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

                uploadImageToFirebaseStorage()
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

    private fun uploadImageToFirebaseStorage() {
        if (selectedPhotoUri == null) return

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {

                ref.downloadUrl.addOnSuccessListener {
                    saveUserToFirebaseDatabase(it.toString())
                }
            }
            .addOnFailureListener {
                TODO("Not yet Implemented")
            }

    }

    private fun saveUserToFirebaseDatabase(profileImageUri: String) {

        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        val username = binding.usernameEditTextRegister.text.toString()
        val user = User(uid, username, profileImageUri)
        Log.d("MainActivity1", "$uid $username $profileImageUri")
        ref.setValue(user)
            .addOnSuccessListener {

                val intent = Intent(this, MessagesActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)

            }
            .addOnFailureListener {
                Log.d("MainActivity1", "Failed pls try again")
            }
    }
}
