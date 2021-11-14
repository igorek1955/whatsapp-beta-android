package com.example.whatsappclone.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.whatsappclone.adapters.UserAdapter
import com.example.whatsappclone.model.Users
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.FirebaseAuth
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.whatsappclone.R
import com.example.whatsappclone.model.ChatList
import com.google.firebase.database.*


class ChatsFragment : Fragment() {
    lateinit var userAdapter: UserAdapter
    lateinit var userList: ArrayList<Users>
    var fuser: FirebaseUser? = null
    lateinit var reference: DatabaseReference
    lateinit var userIdSet: HashSet<String>
    lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chats, container, false)
        initVals(view)
        getUsers()
        return view
    }

    private fun getUsers() {
        reference = FirebaseDatabase.getInstance().getReference("ChatList")
            .child(fuser!!.uid)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (child in snapshot.children) {
                    Log.i("ChatFragment", "child: $child")
                    val chat = child.getValue(ChatList::class.java)
                    Log.i("ChatFragment", "chat: $chat")
                    userIdSet.add(chat!!.id)
                }
                Log.i("ChatFragment", "setSize: $userIdSet")
                extractUsers()
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })



    }

    private fun extractUsers() {
        reference = FirebaseDatabase.getInstance().getReference("MyUsers")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                Log.i("ChatFragment", "getUsers snapshot: $snapshot")
                for (child in snapshot.children) {
                    val user = child.getValue(Users::class.java)
                    Log.i("ChatFragment", "getUsers user: $snapshot")
                    if (user!!.id in userIdSet && user !in userList) {
                        userList.add(user)
                    }
                }
                userAdapter = UserAdapter(context, userList, true)
                recyclerView.adapter = userAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun initVals(view: View) {
        recyclerView = view.findViewById(R.id.recycler_view2)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)
        fuser = FirebaseAuth.getInstance().currentUser
        userList = ArrayList()
        userIdSet = HashSet()
    }
}