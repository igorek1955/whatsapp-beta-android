package com.example.whatsappclone.fragments

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.ProgressDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.whatsappclone.R
import com.example.whatsappclone.model.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import android.content.Intent
import android.net.Uri
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.google.firebase.database.FirebaseDatabase





class ProfileFragment : Fragment() {

    private lateinit var imageView: ImageView
    private lateinit var textView: TextView

    private lateinit var dbRef: DatabaseReference
    private lateinit var fuser: FirebaseUser

    // Profile Image
    private var storageReference: StorageReference? = null
    private var imageUri: Uri = Uri.parse("")
    private var uploadTask: UploadTask? = null

    /*
    create StorageReference
     */


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        initVals(view)
        fillProfile()
        setupClickListener()
        return view
    }

    private fun setupClickListener() {
        imageView.setOnClickListener {
            selectImage()
        }
    }

    private fun selectImage() {
        val i = Intent()
        i.type = "image/*"
        i.action = Intent.ACTION_GET_CONTENT
        resultLauncher.launch(i)
    }

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            if (data != null && data.data != null) {
                imageUri = data.data!!
                if (uploadTask != null && uploadTask!!.isInProgress) {
                    Toast.makeText(context, "Upload in progress..", Toast.LENGTH_LONG).show()
                } else {
                    uploadImage()
                }
            }
        }
    }


    private fun getFileExtension(uri: Uri): String? {
        val contentResolver = requireContext().contentResolver
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private fun uploadImage() {
        val progressDialog = ProgressDialog(context)
        progressDialog.setMessage("Uploading")
        progressDialog.show()

        if (imageUri.toString().isNotEmpty()) {
            val type = getFileExtension(imageUri)
            val fileRef = storageReference!!.child(
                System.currentTimeMillis().toString() + "." + getFileExtension(
                    imageUri))
            uploadTask = fileRef.putFile(imageUri)
            uploadTask!!.continueWithTask{ task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                fileRef.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    val mUri = downloadUri.toString()
                    dbRef = FirebaseDatabase.getInstance().getReference("MyUsers").child(fuser.uid)
                    val map: HashMap<String, Any> = HashMap()
                    map["imageUrl"] = mUri
                    dbRef.updateChildren(map)
                    progressDialog.dismiss()
                } else {
                    Toast.makeText(context, "Failed!!", Toast.LENGTH_SHORT).show();
                }
            }.addOnFailureListener {
                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        } else {
            Toast.makeText(context, "No Image Selected", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss()
        }
    }


    private fun fillProfile() {
        fuser = FirebaseAuth.getInstance().currentUser!!
        dbRef = FirebaseDatabase.getInstance().getReference("MyUsers").child(fuser.uid)
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(Users::class.java)
                textView.text = user!!.username
                if (user.imageUrl.equals("default")) {
                    imageView.setImageResource(R.mipmap.ic_launcher)
                } else {
                    Glide.with(context!!).load(user.imageUrl).into(imageView)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun initVals(view: View) {
        imageView = view.findViewById(R.id.profile_image2)
        textView = view.findViewById(R.id.usernamer)
        // Profile Image reference in storage
        storageReference = FirebaseStorage.getInstance().getReference("uploads");
    }
}