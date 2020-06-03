package com.ahujadevansh.mbuddy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import com.ahujadevansh.mbuddy.databinding.ActivityChatLogBinding
import com.google.android.gms.common.util.DataUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*
import kotlinx.android.synthetic.main.user_row_new_chat.view.*

class ChatLogActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatLogBinding

    val adapter = GroupAdapter<GroupieViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat_log)

        binding.recyclerviewChatLog.adapter = adapter

        val user = intent.getParcelableExtra<User>(NewChatActivity.USER_KEY)

        supportActionBar?.title = user.username

        listenForMessages()

        binding.sendButtonChatLog.setOnClickListener {
            sendMessage()
        }


    }

    private fun listenForMessages() {

        val ref = FirebaseDatabase.getInstance().getReference("/messages")
        ref.addChildEventListener(object: ChildEventListener {
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java)
                if(chatMessage != null) {
                    if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {
                        adapter.add(ChatFromItem(chatMessage.text))
                    }
                    else {
                        adapter.add(ChatToItem(chatMessage.text))
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildRemoved(p0: DataSnapshot) {
                TODO("Not yet implemented")
            }
        })

    }

    private fun sendMessage() {
        val text = binding.messageEditTextChatLog.text.toString()
        val fromId = FirebaseAuth.getInstance().uid
        val user = intent.getParcelableExtra<User>(NewChatActivity.USER_KEY)
        val toId = user.uid
        val reference = FirebaseDatabase.getInstance().getReference("/messages").push()

        if(fromId == null) return

        val chatMessage = ChatMessage(reference.key!!, text, fromId, toId, System.currentTimeMillis())
        reference.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d("CHATLOG", "Saved")
            }


    }
}

class ChatFromItem(val text:String) : Item<GroupieViewHolder>() {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.textView_from_row.text = text
    }

    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }
}


class ChatToItem(val text:String) : Item<GroupieViewHolder>() {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.textView_to_row.text = text
    }

    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }
}