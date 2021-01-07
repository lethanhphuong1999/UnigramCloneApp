package com.example.unigramcloneapp.Adapter

import android.content.Context
import android.content.Intent
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.unigramcloneapp.CommentsActivity
import com.example.unigramcloneapp.Fragments.PostDetailsFragment
import com.example.unigramcloneapp.Fragments.ProfileFragment
import com.example.unigramcloneapp.MainActivity
import com.example.unigramcloneapp.Model.Post
import com.example.unigramcloneapp.Model.User
import com.example.unigramcloneapp.R
import com.example.unigramcloneapp.ShowUsersActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_account_settings.*
import kotlinx.android.synthetic.main.activity_comments.*

class PostAdapter
    (private val mContext: Context,
     private val mPost: List<Post>) : RecyclerView.Adapter<PostAdapter.ViewHolder>()
{
    private var firebaseUser: FirebaseUser? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    {
        val view = LayoutInflater.from(mContext).inflate(R.layout.posts_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int
    {
        return mPost.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {
        firebaseUser = FirebaseAuth.getInstance().currentUser

        val post = mPost[position]

        Picasso.get().load(post.getPostimage()).into(holder.postImage)

        if (post.getDescription().equals(""))
        {
            holder.description.visibility = View.GONE
        }
        else
        {
            holder.description.visibility = View.VISIBLE
            holder.description.setText(post.getDescription())
        }

        publisherInfo(holder.profileImage, holder.userName, holder.publisher, post.getPublisher())
        isLikes(post.getPostid(), holder.likeButton)
        numberOfLikes(holder.likes, post.getPostid())
        getTotalComments(holder.comments, post.getPostid())
        checkSavedStatus(post.getPostid(), holder.saveButton)

        holder.postImage.setOnClickListener {
            val editor = mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit()

            editor.putString("postid", post.getPostid())

            editor.apply()

            (mContext as FragmentActivity).getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, PostDetailsFragment()).commit()
        }

        holder.publisher.setOnClickListener {
            val editor = mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit()

            editor.putString("publisher", post.getPublisher())

            editor.apply()

            (mContext as FragmentActivity).getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, ProfileFragment()).commit()
        }

        holder.profileImage.setOnClickListener {
            val editor = mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit()

            editor.putString("publisher", post.getPublisher())

            editor.apply()

            (mContext as FragmentActivity).getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, ProfileFragment()).commit()
        }

        holder.postImage.setOnClickListener {
            val editor = mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit()

            editor.putString("postid", post.getPostid())

            editor.apply()

            (mContext as FragmentActivity).getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, PostDetailsFragment()).commit()
        }

        holder.likeButton.setOnClickListener {
            if (holder.likeButton.tag == "Thích")
            {
                FirebaseDatabase.getInstance().reference
                    .child("Likes")
                    .child(post.getPostid())
                    .child(firebaseUser!!.uid)
                    .setValue(true)

                addNotification(post.getPublisher(), post.getPostid())
            }
            else
            {
                FirebaseDatabase.getInstance().reference
                    .child("Likes")
                    .child(post.getPostid())
                    .child(firebaseUser!!.uid)
                    .removeValue()

                val intent = Intent(mContext, MainActivity::class.java)
                mContext.startActivity(intent)
            }
        }


        holder.likes.setOnClickListener {
            val intent = Intent(mContext, ShowUsersActivity::class.java)
            intent.putExtra("postid", post.getPostid())
            intent.putExtra("Title", "Lượt thích")
            mContext.startActivity(intent)
        }
        

        holder.commentButton.setOnClickListener {
            val intentComment = Intent(mContext, CommentsActivity::class.java)
            intentComment.putExtra("postid", post.getPostid())
            intentComment.putExtra("publisher", post.getPublisher())
            mContext.startActivity(intentComment)
        }

        holder.comments.setOnClickListener {
            val intentComment = Intent(mContext, CommentsActivity::class.java)
            intentComment.putExtra("postid", post.getPostid())
            intentComment.putExtra("publisher", post.getPublisher())
            mContext.startActivity(intentComment)
        }

        holder.saveButton.setOnClickListener {
            if (holder.saveButton.tag == "Lưu")
            {
                FirebaseDatabase.getInstance().reference
                    .child("Saves")
                    .child(firebaseUser!!.uid)
                    .child(post.getPostid())
                    .setValue(true)
            }
            else
            {
                FirebaseDatabase.getInstance().reference
                    .child("Saves")
                    .child(firebaseUser!!.uid)
                    .child(post.getPostid())
                    .removeValue()
            }
        }
    }

    private fun numberOfLikes(likes: TextView, postid: String)
    {
        val LikesRef = FirebaseDatabase.getInstance().reference
            .child("Likes").child(postid)

        LikesRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot)
            {
                if (p0.exists())
                {
                    likes.text = p0.childrenCount.toString() + " lượt thích"
                }
            }

            override fun onCancelled(p0: DatabaseError)
            {

            }
        })
    }

    private fun getTotalComments(comments: TextView, postid: String)
    {
        val commentsRef = FirebaseDatabase.getInstance().reference
            .child("Comments").child(postid)

        commentsRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot)
            {
                if (p0.exists())
                {
                    comments.text = "Xem tất cả " + p0.childrenCount.toString() + " bình luận"
                }
            }

            override fun onCancelled(p0: DatabaseError)
            {

            }
        })
    }

    private fun isLikes(postid: String, likeButton: ImageView)
    {
        val firebaseUser = FirebaseAuth.getInstance().currentUser

        val LikesRef = FirebaseDatabase.getInstance().reference
            .child("Likes").child(postid)

        LikesRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot)
            {
                if (p0.child(firebaseUser!!.uid).exists())
                {
                    likeButton.setImageResource(R.drawable.heart_clicked)
                    likeButton.tag = "Đã thích"
                }
                else
                {
                    likeButton.setImageResource(R.drawable.heart_not_clicked)
                    likeButton.tag = "Thích"
                }
            }

            override fun onCancelled(p0: DatabaseError)
            {

            }
        })
    }


    inner class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        var profileImage: CircleImageView
        var postImage: ImageView
        var likeButton: ImageView
        var commentButton: ImageView
        var saveButton: ImageView
        var userName: TextView
        var likes: TextView
        var publisher: TextView
        var description: TextView
        var comments: TextView

        init {
            profileImage = itemView.findViewById(R.id.user_profile_image_post)
            postImage = itemView.findViewById(R.id.post_image_home)
            likeButton = itemView.findViewById(R.id.post_image_like_btn)
            commentButton = itemView.findViewById(R.id.post_image_comment_btn)
            saveButton = itemView.findViewById(R.id.post_save_comment_btn)
            userName = itemView.findViewById(R.id.user_name_post)
            likes = itemView.findViewById(R.id.likes)
            publisher = itemView.findViewById(R.id.publisher)
            description = itemView.findViewById(R.id.description)
            comments = itemView.findViewById(R.id.comments)
        }

    }



    private fun publisherInfo(profileImage: CircleImageView, userName: TextView, publisher: TextView, publisherID: String)
    {
        val usersRef = FirebaseDatabase.getInstance().reference.child("Users").child(publisherID)

        usersRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot)
            {
                if (p0.exists())
                {
                    val user = p0.getValue<User>(User::class.java)

                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile).into(profileImage)
                    userName.text = user!!.getUsername()
                    publisher.text = user!!.getUsername()
                }
            }

            override fun onCancelled(p0: DatabaseError)
            {

            }
        })
    }


    private fun checkSavedStatus(postid: String, imageView: ImageView)
    {
        val savesRef = FirebaseDatabase.getInstance().reference
            .child("Saves")
            .child(firebaseUser!!.uid)

        savesRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot)
            {
                if (p0.child(postid).exists())
                {
                    imageView.setImageResource(R.drawable.save_large_icon)
                    imageView.tag == "Đã lưu"
                }
                else
                {
                    imageView.setImageResource(R.drawable.save_unfilled_large_icon)
                    imageView.tag = "Lưu"
                }
            }

            override fun onCancelled(p0: DatabaseError)
            {

            }
        })
    }

    private fun addNotification(userId: String, postId: String)
    {
        val notiRef = FirebaseDatabase.getInstance()
            .reference.child("Notifications")
            .child(userId)

        val notiMap = HashMap<String, Any>()
        notiMap["userid"] = firebaseUser!!.uid
        notiMap["text"] = "thích bài đăng của bạn"
        notiMap["postid"] = postId
        notiMap["ispost"] = true

        notiRef.push().setValue(notiMap)

    }

}