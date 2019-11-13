package com.example.datingapplication

import android.app.Activity
import android.content.Intent
import android.media.Image
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.preference.PreferenceManager
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        RegisterButton.setOnClickListener {
            RegisterUser()
        }

        PhotoButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }
    }

    var photoUri: Uri? = null
    // Function to add the photo on the registration page
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==0 && resultCode== Activity.RESULT_OK && data!=null){
            photoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, photoUri)
            ImageDisplay.setImageBitmap(bitmap)
            PhotoButton.alpha = 0f
        }
    }


    private fun RegisterUser(){
        val email = EmailRegister.text.toString()
        val password = PasswordRegister.text.toString()

        if(email.isEmpty() || password.isEmpty()){
            Toast.makeText(this, "Please enter the email/pass correctly", Toast.LENGTH_SHORT).show()
            return
        }

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if(!it.isSuccessful) return@addOnCompleteListener

                // else the user will be created successfully
                Log.d("RegisterStatus", "Successfully Registered the user")
                Toast.makeText(this,"You are registered successfully",Toast.LENGTH_SHORT).show()

                // When user is registered successfully, we can add the photo on firebase storage
                UploadPhoto()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Please enter the email/pass correctly", Toast.LENGTH_SHORT).show()
                Log.d("RegisterStatus","Failed to create the user")
            }


    }
    private fun UploadPhoto(){
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/image/$filename")
        ref.putFile(photoUri!!)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener {
                    SavingUserInfoToDatabase(it.toString())
                }
            }.addOnFailureListener {
                Log.d("SavingInfo", "Failed to put the image file on database")
            }
    }

    private fun SavingUserInfoToDatabase(ImageUrl: String){
        val uid = FirebaseAuth.getInstance().uid?:""        // instead of null, initializing to empty string
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        val user = User(uid, UserNameRegister.text.toString(), PhoneRegister.text.toString(), ImageUrl)

        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("SavingInfo", "Successfully added the info of user to database")

                val intent = Intent(this, FeedActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            .addOnFailureListener {
                Log.d("SavingInfo","Failed to add the info to database")
            }
    }

}
@Parcelize
class User(val uid: String, val userName: String, val phone: String, val imageUrl: String ):Parcelable{
    constructor() : this("","","","")
}