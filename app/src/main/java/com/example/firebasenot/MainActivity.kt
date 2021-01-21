package com.example.firebasenot

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import java.io.File
import java.util.*


class MainActivity : AppCompatActivity() {

    private val auth by lazy {Firebase.auth}
    private val messaging by lazy {Firebase.messaging}
    private val analytics by lazy {Firebase.analytics}
    private val storage by lazy {Firebase.storage.reference}

    private val btn1 by lazy {findViewById<Button>(R.id.button_first)}
    private val btn2 by lazy {findViewById<Button>(R.id.button_second)}
    private val find by lazy {findViewById<Button>(R.id.button_find)}
    private val send by lazy {findViewById<Button>(R.id.button_send)}
    private val fileName by lazy {findViewById<TextView>(R.id.fileName)}
    private lateinit var file: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val currentUser = auth.currentUser
        auth.signInAnonymously()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = auth.currentUser
                } else {
                    // If sign in fails, display a message to the user.
                }
            }


        setupObservables()

        messaging.token.addOnCompleteListener{
            if (!it.isSuccessful) {
                Log.w("token", "Fetching FCM registration token failed", it.exception)
            }

            // Get new FCM registration token
            val token = it.result

            // Log and toast
            //val msg = getString(R.string.msg_token_fmt, token)
            Log.d("token", token ?: "não deu")
        }

    }

    private fun setupObservables() {
        btn1.setOnClickListener{
            analytics.logEvent("btn1_clicked", null)
        }
        btn2.setOnClickListener{
            var a = 876432/0
        }
        find.setOnClickListener{
            val pickPhoto = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(pickPhoto, IMAGE_PICK_CODE)
        }
        send.setOnClickListener {
            uploadImageToFirebase(file as Uri)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE){
            fileName.text = data?.data?.path.toString()
            file = data?.data!!
        }
    }

    private fun uploadImageToFirebase(fileUri: Uri?) {
        fileUri?.let{
            val fileName = UUID.randomUUID().toString() +".jpg"
            val refStorage = storage.child("images/$fileName")

            refStorage.putFile(fileUri)
                .addOnSuccessListener(
                    OnSuccessListener<UploadTask.TaskSnapshot> { taskSnapshot ->
                        taskSnapshot.storage.downloadUrl.addOnSuccessListener {
                            val imageUrl = it.toString()
                        }
                    })

                ?.addOnFailureListener(OnFailureListener { e ->
                    print(e.message)
                })
        }
    }

    companion object {
        //image pick code
        private val IMAGE_PICK_CODE = 1000;
        //Permission code
        private val PERMISSION_CODE = 1001;
    }
}