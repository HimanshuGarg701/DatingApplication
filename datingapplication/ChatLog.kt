package com.example.datingapplication


import android.widget.Toast
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.chat_from_user.*
import kotlinx.android.synthetic.main.chat_from_user.view.*
import kotlinx.android.synthetic.main.chat_to_user.view.*
import kotlinx.android.synthetic.main.chat_to_user.view.*
import java.sql.Timestamp

class ChatLog : AppCompatActivity() {

    companion object {
        var currentUser: User? = null
    }

    val adapter = GroupAdapter<ViewHolder>()
    var toUser:User? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        RecyclerChatLog.adapter = adapter
        toUser = intent.getParcelableExtra<User>(FeedActivity.USER_KEY)
        supportActionBar?.title = toUser?.userName


        SendButton.setOnClickListener {
            performSendMessage()
        }
        fetchCurrentUser()
        fetchMessagesFromFirebase()

    }

    private fun fetchMessagesFromFirebase(){
        val fromId = FirebaseAuth.getInstance().uid
        val toId = toUser?.uid
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")

        ref.addChildEventListener(object : ChildEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java)
                val current_User = currentUser

                    if (chatMessage?.fromId == FirebaseAuth.getInstance().uid) {
                        adapter.add(ChatFromUserAdapter(chatMessage!!.text, current_User!!))
                    } else {

                        adapter.add(ChatToUserAdapter(chatMessage!!.text, toUser!!))
                    }
                RecyclerChatLog.scrollToPosition(adapter.itemCount-1)      //Message will remain above keyboard
            }



            override fun onChildRemoved(p0: DataSnapshot) {

            }
        })
    }

    private fun performSendMessage(){

        val text = EnterMessage.text.toString()
        val fromId = FirebaseAuth.getInstance().uid
        // To get the To-id we need to use help of Parcelize
        val user = intent.getParcelableExtra<User>(FeedActivity.USER_KEY)
        val toId = user.uid

        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()

        val receiveRef = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()

        val chatMessage = ChatMessage(ref.key!!, text, fromId!!, toId)
        ref.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d("MessageData", "Sending message to firebase")
                EnterMessage.text.clear()
            }
            .addOnFailureListener {
                Toast.makeText(this,"failed to add message",Toast.LENGTH_SHORT).show()
            }
        receiveRef.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d("MessageData", "sending message to firebase}")
            }
            .addOnFailureListener {
                Toast.makeText(this, "failed to add message", Toast.LENGTH_SHORT).show()
            }

        // The reference below will help to fetch the users and messages in the recent message activity
        val recentMessageFromRef = FirebaseDatabase.getInstance().getReference("/recent-messages/$fromId/$toId")
        recentMessageFromRef.setValue(chatMessage)
        val recentMessageToRef = FirebaseDatabase.getInstance().getReference("/recent-messages/$toId/$fromId")
        recentMessageToRef.setValue(chatMessage)
    }

    private fun fetchCurrentUser(){
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                currentUser = p0.getValue(User::class.java)
            }

        })
    }
}

class ChatFromUserAdapter(val text: String, val user:User): Item<ViewHolder>(){
    override fun getLayout(): Int {
        return R.layout.chat_from_user
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        Log.d("MessageReceived","Added message to recycler view")
        viewHolder.itemView.MessageFromUser.text = text

        Picasso.get().load(user.imageUrl).into(viewHolder.itemView.PicFromUser)
    }

}

class ChatToUserAdapter(val text: String, val user:User):Item<ViewHolder>(){
    override fun getLayout(): Int {
        return R.layout.chat_to_user
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        Log.d("BindMessage","Message display successful")
        viewHolder.itemView.MessageToUser.text = text

        Picasso.get().load(user.imageUrl).into(viewHolder.itemView.ToUserPic)
    }

}

// This is the data class to store info for the messages
class ChatMessage(val id: String, val text: String, val fromId: String, val toId: String){
    constructor(): this("","","","")
}