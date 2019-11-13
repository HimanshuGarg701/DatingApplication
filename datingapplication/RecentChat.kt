package com.example.datingapplication

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_history.view.*
import kotlinx.android.synthetic.main.activity_recent_chat.*

class RecentChat : AppCompatActivity() {

    val adapter = GroupAdapter<ViewHolder>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recent_chat)
        RecyclerRecentChat.layoutManager = LinearLayoutManager(this)
        RecyclerRecentChat.adapter = adapter

        RecyclerRecentChat.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        adapter.setOnItemClickListener { item, view ->
            val intent = Intent(this, ChatLog::class.java)
            val row = item as DisplayRows
            intent.putExtra(FeedActivity.USER_KEY, row.datePartnerUser)
            startActivity(intent)
        }

       fetchMessagesFromFirebase()
    }



    val mappingRecentMessage = HashMap<String, ChatMessage>()

    private fun updatingChatList(){
        adapter.clear()
        mappingRecentMessage.values.forEach {
            Log.d("DisplayMessage","Adding Message to the Row List")
            adapter.add(DisplayRows(it))
            Log.d("IsSent","Adding Message to the Row List")
        }
    }
    private fun fetchMessagesFromFirebase(){
        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/recent-messages/$fromId")
        ref.addChildEventListener(object:ChildEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java)?:return
                mappingRecentMessage[p0.key!!] = chatMessage
                updatingChatList()
            }
            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java)?:return
                mappingRecentMessage[p0.key!!] = chatMessage
                updatingChatList()
            }


            override fun onChildRemoved(p0: DataSnapshot) {

            }

        })
    }
}

class DisplayRows(val chatMessage: ChatMessage):Item<ViewHolder>(){

    var datePartnerUser: User? = null
    override fun getLayout(): Int {
        return R.layout.activity_chat_history
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        Log.d("InsideAdapter", "Heyyyy I am inside adapter but your message is not")
        viewHolder.itemView.MessageRecentChat.text = chatMessage.text

        val datePartnerId:String
        if(chatMessage.fromId == FirebaseAuth.getInstance().uid){
            datePartnerId = chatMessage.toId
        }
        else{
            datePartnerId = chatMessage.fromId
        }
        val ref = FirebaseDatabase.getInstance().getReference("/users/$datePartnerId")
        ref.addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                datePartnerUser = p0.getValue(User::class.java)
                viewHolder.itemView.UserNameRecentChat.text = datePartnerUser?.userName
                Picasso.get().load(datePartnerUser?.imageUrl).into(viewHolder.itemView.PicRecentChat)
            }

        })
        }
    }



