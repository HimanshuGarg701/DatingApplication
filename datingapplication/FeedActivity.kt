package com.example.datingapplication

import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.*
import com.example.datingapplication.FeedActivity.Companion.USER_KEY
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_feed.*
import kotlinx.android.synthetic.main.display_feed.*
import kotlinx.android.synthetic.main.display_feed.view.*

class FeedActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed)
        RecyclerFeed.layoutManager = LinearLayoutManager(this)
        RecyclerFeed.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        fetchUsers()
        CheckUserLogin()

    }
    companion object {
        val USER_KEY = "USER_KEY"
    }

    private fun CheckUserLogin(){
        val uid = FirebaseAuth.getInstance().uid
        if(uid==null){
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.navigation_bar,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.SignOut ->{
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            R.id.Chat ->{
                val intent = Intent(this, RecentChat::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun fetchUsers(){
        val userList : MutableList<User> = mutableListOf()
        val ref = FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                p0.children.forEach {
                    val user = it.getValue(User::class.java)
                    userList.add(user!!)
                }
                RecyclerFeed.adapter = MainAdapter(userList)
            }

        })
    }
}

class MainAdapter(val user: MutableList<User>) : RecyclerView.Adapter<CustomViewHolder>(){
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): CustomViewHolder {
        val layoutInflater = LayoutInflater.from(p0.context)
        val cellForEachRow = layoutInflater.inflate(R.layout.display_feed, p0, false)
        return CustomViewHolder(cellForEachRow)

    }

    override fun getItemCount(): Int {
        return user.count()
    }

    override fun onBindViewHolder(p0: CustomViewHolder, p1: Int) {
        val userNum = user[p1]
        p0.view.UserNameFeed.text = userNum.userName
        Picasso.get().load(userNum.imageUrl).into(p0.view.UserPic)
        // passing the user to the custom view holder and view holder passes the whole user info to chatlog
        p0.user = userNum


    }

}

class CustomViewHolder(val view: View, var user:User?=null): RecyclerView.ViewHolder(view){
    var allowChat : Boolean = false

    init {
        view.LikeButton.setOnClickListener {
            view.LikeButton.setBackgroundColor(Color.BLUE)
            view.DislikeButton.setBackgroundColor(Color.GRAY)
            allowChat = true
        }

        view.DislikeButton.setOnClickListener {
            view.LikeButton.setBackgroundColor(Color.GRAY)
            view.DislikeButton.setBackgroundColor(Color.BLUE)
            allowChat = false
        }
        view.UserPic.setOnClickListener {
            if(allowChat) {
                Log.d("NextPage", "Moving to ChatLog")
                val intent = Intent(view.context, ChatLog::class.java)
                intent.putExtra(USER_KEY, user)
                view.context.startActivity(intent)
            }
        }
    }
}
