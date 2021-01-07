package com.example.unigramcloneapp.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.unigramcloneapp.Fragments.PostDetailsFragment
import com.example.unigramcloneapp.Fragments.ProfileFragment
import com.example.unigramcloneapp.Model.Notification
import com.example.unigramcloneapp.Model.Post
import com.example.unigramcloneapp.Model.User
import com.example.unigramcloneapp.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_comments.*
import kotlinx.android.synthetic.main.fragment_profile.view.*

class NotificationAdapter(
    private val mContext: Context,
    private val mNotification: List<Notification>)
    : RecyclerView.Adapter<NotificationAdapter.ViewHolder>()
{

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    {
        val view = LayoutInflater.from(mContext).inflate(R.layout.notifications_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mNotification.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {
        val notification = mNotification[position]

        if (notification.getText().equals("đã bắt đầu theo dõi bạn"))
        {
            holder.text.text = "đã bắt đầu theo dõi bạn"
        }
        else if (notification.getText().equals("thích bài đăng của bạn"))
        {
            holder.text.text = "thích bài đăng của bạn"
        }
        else if (notification.getText().contains("đã bình luận:"))
        {
            holder.text.text = notification.getText().replace("đã bình luận:", "đã bình luận: ")
        }
        else
        {
            holder.text.text = notification.getText()
        }



        userInfo(holder.profileImage, holder.userName, notification.getUserId())


        if (notification.isIsPost())
        {
            holder.postImage.visibility = View.VISIBLE
            getPostImage(holder.postImage, notification.getPostId())
        }
        else
        {
            holder.postImage.visibility = View.GONE
        }


        holder.itemView.setOnClickListener {
            if (notification.isIsPost())
            {
                val editor = mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit()

                editor.putString("postid", notification.getPostId())

                editor.apply()

                (mContext as FragmentActivity).getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, PostDetailsFragment()).commit()
            }
            else
            {
                val editor = mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit()

                editor.putString("userid", notification.getUserId())

                editor.apply()

                (mContext as FragmentActivity).getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, ProfileFragment()).commit()
            }
        }
    }


    inner class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        var postImage: ImageView
        var profileImage: CircleImageView
        var userName: TextView
        var text: TextView

        init {
            postImage = itemView.findViewById(R.id.notification_post_image)
            profileImage = itemView.findViewById(R.id.notification_profile_image)
            userName = itemView.findViewById(R.id.username_notification)
            text = itemView.findViewById(R.id.comment_notification)
        }
    }


    private fun userInfo(imageView: ImageView, userName: TextView, publisherId: String)
    {
        val usersRef = FirebaseDatabase.getInstance().getReference()
            .child("Users").child(publisherId)

        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot)
            {
                if (p0.exists())
                {
                    val user = p0.getValue<User>(User::class.java)

                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile).into(imageView)
                    userName.text = user!!.getUsername()

                }
            }

            override fun onCancelled(p0: DatabaseError)
            {

            }
        })
    }


    private fun getPostImage(imageView: ImageView, postID: String)
    {
        val postRef = FirebaseDatabase.getInstance()
            .reference.child("Posts")
            .child(postID)

        postRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot)
            {
                if (p0.exists())
                {
                    val post = p0.getValue<Post>(Post::class.java)

                    Picasso.get().load(post!!.getPostimage()).placeholder(R.drawable.profile).into(imageView)

                }
            }

            override fun onCancelled(p0: DatabaseError)
            {

            }
        })
    }

}