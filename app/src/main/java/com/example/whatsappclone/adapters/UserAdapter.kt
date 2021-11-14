package com.example.whatsappclone.adapters

import android.content.Context
import android.content.Intent

import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView

import com.bumptech.glide.Glide

import android.view.LayoutInflater
import android.view.View

import android.view.ViewGroup
import android.widget.ImageView
import com.example.whatsappclone.MessageActivity
import com.example.whatsappclone.R
import com.example.whatsappclone.model.Users


class UserAdapter
    (private var context: Context?, private var mUsers: List<Users>?, private var isChat: Boolean) :
    RecyclerView.Adapter<UserAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(context).inflate(
            R.layout.user_item,
            parent,
            false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user: Users = mUsers!![position]
        holder.username.text = user.username
        if (user.imageUrl.equals("default")) {
            holder.imageView.setImageResource(R.mipmap.ic_launcher)
        } else {
            // Adding Glide Library
            Glide.with(context!!)
                .load(user.imageUrl)
                .into(holder.imageView)
        }


        // Status check
        if (isChat) {
            if (user.status.equals("online")) {
                holder.imageViewON.visibility = View.VISIBLE
                holder.imageViewOFF.visibility = View.GONE
            } else {
                holder.imageViewON.visibility = View.GONE
                holder.imageViewOFF.visibility = View.VISIBLE
            }
        } else {
            holder.imageViewON.visibility = View.GONE
            holder.imageViewOFF.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
                            val i = Intent(context, MessageActivity::class.java)
                            i.putExtra("userid", user.id)
                            context!!.startActivity(i)
        }
    }

    override fun getItemCount(): Int {
        return mUsers!!.size
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var username: TextView
        var imageView: ImageView
        var imageViewON: ImageView
        var imageViewOFF: ImageView

        init {
            username = itemView.findViewById(R.id.textView30)
            imageView = itemView.findViewById(R.id.imageView)
            imageViewON = itemView.findViewById(R.id.statusimageON)
            imageViewOFF = itemView.findViewById(R.id.statusimageOFF)
        }
    }
}