package com.ahujadevansh.mbuddy

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.ahujadevansh.mbuddy.databinding.ActivityMessagesBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MessagesActivity : AppCompatActivity() {

    companion object {
        var currentUser:User? = null
    }

    private lateinit var binding: ActivityMessagesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_messages)
        loginRequired()
        fetchCurrectUser()
    }

    private fun loginRequired() {

        val uid = FirebaseAuth.getInstance().uid
        if (uid == null) {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    private fun fetchCurrectUser() {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                currentUser = p0.getValue(User::class.java)
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item?.itemId) {

            R.id.sign_out_menu -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }

            R.id.new_chat_menu -> {
                val intent = Intent(this, NewChatActivity::class.java)
                startActivity(intent)
            }

        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.navbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
}