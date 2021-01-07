package com.example.unigramcloneapp.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.example.unigramcloneapp.Model.Comment
import com.example.unigramcloneapp.Model.User
import com.example.unigramcloneapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.comments_item_layout.view.*


class CommentAdapter (private val mCoontext: Context,
                       private val mComment: MutableList<Comment>?
) : RecyclerView.Adapter<CommentAdapter.ViewHolder>()
{
    private var firebaseUser: FirebaseUser? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentAdapter.ViewHolder {
        val view = LayoutInflater.from(mCoontext).inflate(R.layout.comments_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mComment!!.size
    }

    override fun onBindViewHolder(holder: CommentAdapter.ViewHolder, position: Int) {
        firebaseUser = FirebaseAuth.getInstance().currentUser

        val comment = mComment!![position]
        holder.commentTV.text = comment.getComment()
        getUserInfo(holder.imageProfile, holder.userNameTV, comment.getPublisher())
    }



    inner class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        var imageProfile: CircleImageView
        var userNameTV: TextView
        var commentTV: TextView

        init {
            imageProfile = itemView.findViewById(R.id.user_profile_image_comment)
            userNameTV = itemView.findViewById(R.id.user_name_comment)
            commentTV = itemView.findViewById(R.id.comment_comment)
        }
    }


    private fun getUserInfo(imageProfile: CircleImageView, userNameTV: TextView, publisher: String)
    {
        val userREf = FirebaseDatabase.getInstance()
            .reference.child("Users")
            .child(publisher)

        userREf.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot)
            {
                if (p0.exists())
                {
                    val user = p0.getValue(User::class.java)

                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile).into(imageProfile)

                    userNameTV.text = user!!.getUsername()
                }
            }

            override fun onCancelled(p0: DatabaseError)
            {

            }
        })
    }
}