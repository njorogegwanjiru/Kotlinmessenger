package com.example.kotlinmessanger

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_user_registration.*
import java.util.*

class UserRegistration : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_registration)

        register_button_register
            .setOnClickListener {
                performRegistration()
            }

        already_have_account_text_view
            .setOnClickListener {
                val intent = Intent(this, Login::class.java)
                startActivity(intent)
            }

        image_button_register
            .setOnClickListener {
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/"
                startActivityForResult(intent, 0)
            }
    }
    var selectedPhotoUri: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode== Activity.RESULT_OK && data!=null){
            selectedPhotoUri = data.data
            //bitmap of selected photo
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
            imageciv.setImageBitmap(bitmap)
            image_button_register.alpha = 0f
        }
    }
    private fun performRegistration()
    {
        val email = email_edittext_login.text.toString()
        val password = password_edittext_login.text.toString()

        if (email.isEmpty() || password.isEmpty()){
            Toast.makeText(this, "Enter Text", Toast.LENGTH_LONG).show()
        }
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (!it.isSuccessful)return@addOnCompleteListener
                uploadImageToFirebase()
            }.addOnFailureListener {
                Toast.makeText(this, "Registration Failed", Toast.LENGTH_LONG).show()
            }
    }

    private fun uploadImageToFirebase() {
        if (selectedPhotoUri == null) return
        val filename = UUID.randomUUID().toString() //Assign unique random ID
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")
        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d("Register", "Successfully uploaded image")

                ref.downloadUrl.addOnSuccessListener {
                    Log.d("Register", "File Location: $it")
                    saveUserToDb(it.toString())
                }
            }
            .addOnFailureListener {
                Log.d("Register", "Upload failed")
            }
    }

    private fun saveUserToDb(profileImageUrl: String) {
        val uid = FirebaseAuth.getInstance().uid?:""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        val user = User(uid, username_edittext_register.text.toString(), profileImageUrl)
        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("Register", "User saved")
                val intent = Intent(this, LatestMessages::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                //clears back stack so back returns you to desktop
                startActivity(intent)
            }
    }
}
class User(val uid: String, val userame: String, val profileImageUrl: String)