package com.example.datingapplication

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Take the user to the register page if they don't have account
        NoAccount.setOnClickListener{
            val intent = Intent(this, RegisterActivity::class.java)
            Toast.makeText(this, "please register your account",Toast.LENGTH_SHORT).show()
            startActivity(intent)

        }
        LoginButton.setOnClickListener {
            AllowLogin()
        }
    }

    private fun AllowLogin(){
        val email = EmailLogin.text.toString()
        val password = PasswordLogin.text.toString()

        if(email.isEmpty() || password.isEmpty()){
            Toast.makeText(this,"please enter the email and pass correctly",Toast.LENGTH_SHORT).show()
            return
        }
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if(!it.isSuccessful)return@addOnCompleteListener
                // else logged in successfully
                Toast.makeText(this,"Logged in Successfully",Toast.LENGTH_SHORT).show()
                val intent = Intent(this, FeedActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            .addOnFailureListener {
                Toast.makeText(this,"please enter the email and pass correctly",Toast.LENGTH_SHORT).show()
            }
    }
}
