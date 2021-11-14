package com.example.whatsappclone.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.whatsappclone.adapters.UserAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.whatsappclone.R
import com.example.whatsappclone.model.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class UsersFragment : Fragment() {

    /*
  2 - fill usersFragment -> recyclerView, userAdapter, userList -> setAdapter
  2.1 - add load users method (addValueEventListener)
 */


    lateinit var recyclerView: RecyclerView
    lateinit var userAdapter: UserAdapter
    var userList: ArrayList<Users> = ArrayList()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_users, container, false)

        recyclerView = view.findViewById(R.id.recycler_view_users_fragment)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)

        loadUsers()
        return view
    }

    private fun loadUsers() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val dbRef = FirebaseDatabase.getInstance().getReference("MyUsers")

        dbRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()

                for (snap in snapshot.children) {
                    Log.i("CUSTOM", "snapshot: ${snap.value.toString()}")
                    var user: Users? = snap.getValue(Users::class.java)
                    if (user != null && !user.id.equals(currentUser!!.uid)) {
                        userList.add(user)
                    }
                }
                userAdapter = UserAdapter(context, userList, false)
                recyclerView.adapter = userAdapter
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}