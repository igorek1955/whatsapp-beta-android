package com.example.whatsappclone.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.whatsappclone.R
import com.example.whatsappclone.model.Chat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MessageAdapter(private var context: Context?, private var mChat: List<Chat>?, private var imageUrl: String) :
    RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

    //Firebase
    lateinit var firebaseUser: FirebaseUser

    companion object {
        const val MSG_TYPE_LEFT: Int = 0
        const val MSG_TYPE_RIGHT: Int = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (viewType == MSG_TYPE_RIGHT){
            val view: View = LayoutInflater.from(context).inflate(
                R.layout.chat_item_right,
                parent,
                false
            )
            return ViewHolder(view)
        } else {
            val view: View = LayoutInflater.from(context).inflate(
                R.layout.chat_item_left,
                parent,
                false
            )
            return ViewHolder(view)
        }

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return mChat!!.size
    }

    override fun getItemViewType(position: Int): Int {
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        if (mChat?.get(position)?.sender.equals(firebaseUser.uid)) {
            return MSG_TYPE_RIGHT
        }
        return MSG_TYPE_LEFT
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var messageItemView: TextView
        var messageImageView: ImageView
        var messageSeenText: TextView
        init {
            messageItemView = itemView.findViewById(R.id.show_message)
            messageImageView = itemView.findViewById(R.id.profile_image)
            messageSeenText = itemView.findViewById(R.id.txt_seen_status)
        }
        fun bind(position: Int) {
            messageItemView.text = mChat!!.get(position).message
            if (imageUrl.equals("default")) {
                messageImageView.setImageResource(R.mipmap.ic_launcher)
            } else {
                Glide.with(context!!).load(imageUrl).into(messageImageView)
            }
            if (position == mChat!!.size - 1) {
                messageSeenText.visibility = View.VISIBLE
                if (mChat!![position].isseen){
                    messageSeenText.text = "Seen"
                } else {
                    messageSeenText.text = "Delivered"
                }
            }
        }
    }
}