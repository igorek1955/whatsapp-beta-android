package com.example.whatsappclone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.bumptech.glide.Glide
import com.example.whatsappclone.model.Users
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.whatsappclone.adapters.MessageAdapter
import com.example.whatsappclone.model.Chat
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseReference


class MessageActivity : AppCompatActivity() {
    lateinit var username: TextView
    lateinit var imageView: ImageView
    lateinit var currentUser: FirebaseUser
    var currentUserPenPalUId: String = ""
    var currentUserPenPalImageUrl = "default"
    lateinit var reference: DatabaseReference

    lateinit var msgRecyclerView: RecyclerView
    lateinit var editTextMsgField: EditText
    lateinit var sendButton: ImageButton

    lateinit var msgSeenListener: ValueEventListener

    /*
    set onclick listener send button
    check message for null or empty
    if empty - toast else -> sendMessage(sender, receiver, message)
    -- dbReb init
    -- hashmap with method params
    -- dbRef.child("Chats").push().setValue(hashmap)
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)
        initVars()
        currentUserPenPalUId = intent.getStringExtra("userid").toString()
        setupToolbar()
        setupButtons()
        setupMessageWindow()
        seenMessage(currentUserPenPalUId)
    }

    private fun setupMessageWindow() {
        val messageList = ArrayList<Chat>()
        val imageUrl = this.currentUserPenPalImageUrl
        reference = FirebaseDatabase.getInstance().getReference("Chats")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (child in snapshot.children) {
                    val chat = child.getValue(Chat::class.java)
                    if ((chat!!.receiver.equals(currentUser.uid) && chat.sender.equals(
                            currentUserPenPalImageUrl
                        )) ||
                        (chat.receiver.equals(currentUserPenPalUId) && chat.sender.equals(
                            currentUser.uid
                        ))
                    ) {
                        messageList.add(chat)
                    }
                    val messageAdapter = MessageAdapter(applicationContext, messageList, imageUrl)
                    msgRecyclerView.adapter = messageAdapter
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }

    private fun setupButtons() {
        sendButton.setOnClickListener {
            val msg = editTextMsgField.text.toString()
            if (msg.isNotEmpty()) {
                sendMessage(currentUser.uid, currentUserPenPalUId, msg)
            } else {
                Toast.makeText(this, "Cannot send empty message", Toast.LENGTH_LONG).show()
            }
            editTextMsgField.setText("")
        }
    }

    private fun sendMessage(sender: String?, receiver: String, message: String) {
        val dbRef = FirebaseDatabase.getInstance().reference
        val msgMap: HashMap<String, Any> = HashMap()
        msgMap["sender"] = sender!!
        msgMap["receiver"] = receiver
        msgMap["message"] = message
        msgMap["isseen"] = false
        dbRef.child("Chats").push().setValue(msgMap)

        // Adding User to chat fragment: Latest Chats with contacts
        val chatRef = FirebaseDatabase.getInstance()
            .getReference("ChatList")
            .child(currentUser.uid)
            .child(currentUserPenPalUId)


        chatRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (!dataSnapshot.exists()) {
                    chatRef.child("id").setValue(currentUserPenPalUId)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })


    }

    private fun setupToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar_message_activity)
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        val dbRefSelectedUser =
            FirebaseDatabase.getInstance().getReference("MyUsers").child(currentUserPenPalUId)
        dbRefSelectedUser.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(Users::class.java)
                currentUserPenPalImageUrl = user!!.imageUrl!!
                username.text = user.username
                if (user.imageUrl.equals("default")) {
                    imageView.setImageResource(R.mipmap.ic_launcher)
                } else {
                    Glide.with(this@MessageActivity).load(user.imageUrl).into(imageView)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun initVars() {
        username = findViewById(R.id.text_view_username_message)
        imageView = findViewById(R.id.image_view_profileImg_message)
        currentUser = FirebaseAuth.getInstance().currentUser!!
        reference =
            FirebaseDatabase.getInstance().getReference("MyUsers").child(currentUser.uid)
        msgRecyclerView = findViewById(R.id.recycler_view_message)
        msgRecyclerView.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(applicationContext)
        layoutManager.stackFromEnd = true
        msgRecyclerView.layoutManager = layoutManager

        editTextMsgField = findViewById(R.id.edit_text_send_message)
        sendButton = findViewById(R.id.button_send_message)
    }


    private fun seenMessage(userId: String) {
        reference = FirebaseDatabase.getInstance().getReference("Chats")
        msgSeenListener = reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (child in snapshot.children) {
                    val chat = snapshot.getValue(Chat::class.java)
                    if (chat!!.receiver.equals(currentUser.uid) && chat.sender.equals(userId)) {
                        val map: HashMap<String, Any> = HashMap()
                        map["isseen"] = true
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }


    private fun checkStatus(status: String) {
        reference =
            FirebaseDatabase.getInstance().getReference("MyUsers").child(currentUser.uid)
        val hashMap: HashMap<String, Any> = HashMap()
        hashMap["status"] = status
        reference.updateChildren(hashMap)
    }


    override fun onResume() {
        super.onResume()
        checkStatus("online")
    }

    override fun onPause() {
        super.onPause()
        reference.removeEventListener(msgSeenListener)
        checkStatus("Offline")
    }

}