package com.ahujadevansh.mbuddy

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.ahujadevansh.mbuddy.databinding.ActivityNewChatBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.user_row_new_chat.view.*

class NewChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewChatBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_new_chat)

        supportActionBar?.title = "Select User"

//        val adapter = GroupAdapter<ViewHolder>()
//
//        adapter.add(??)
//
//        binding.recyclerviewNewchat.adapter = adapter

        fetchUsers()

    }

    private fun fetchUsers() {
        val ref = FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val adapter = GroupAdapter<GroupieViewHolder>()

                p0.children.forEach {
                    val user = it.getValue(User::class.java)
                    if (user != null) {
                        adapter.add(UserItem(user))
                    }
                }

                adapter.setOnItemClickListener { item, view ->
                    val intent = Intent(view.context, MessagesActivity::class.java)
                    startActivity(intent)
                    finish()
                }

                binding.recyclerviewNewchat.adapter = adapter
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

}

class UserItem(val user: User) : Item<GroupieViewHolder>() {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.username_text_new_chat.text = user.username
        Picasso.get().load(user.profileImageUrl)
            .into(viewHolder.itemView.profile_image_view_new_chat)
    }

    override fun getLayout(): Int {
        return R.layout.user_row_new_chat
    }
}