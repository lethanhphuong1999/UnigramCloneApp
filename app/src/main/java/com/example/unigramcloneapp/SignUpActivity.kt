package com.example.unigramcloneapp

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_sign_in.signup_link_btn
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.util.*
import kotlin.collections.HashMap

class SignUpActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        signin_link_btn.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
            intent.setType("message/rfc822");
        }

        signup_btn.setOnClickListener {
            CreateAccount();
        }
    }

    private fun CreateAccount()
    {
        val fullName =fullname_signup.text.toString()
        val userName =username_signup.text.toString()
        val email = email_signup.text.toString()
        val password = password_signup.text.toString()

        when{
            TextUtils.isEmpty(fullName) -> Toast.makeText(this,"Bắt buộc phải có học và tên.", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(userName) -> Toast.makeText(this,"Bắt buộc phải có tên người dùng.", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(email) -> Toast.makeText(this,"Bắt buộc phải có email.", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(password) -> Toast.makeText(this,"Bắt buộc phải có mật khẩu.", Toast.LENGTH_LONG).show()

            else -> {
                val progressDialog = ProgressDialog(this@SignUpActivity)
                progressDialog.setTitle("Đăng Ký")
                progressDialog.setMessage("Vui lòng đợi, quá trình này có thể chờ một lúc...")
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()

                val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
                mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener{ task->
                        if (task.isSuccessful)
                        {
                            saveUserInfo(fullName, userName, password, email, progressDialog)
                        }
                        else
                        {
                            val message = task.exception!!.toString()
                            Toast.makeText(this,"LỖI: $message", Toast.LENGTH_LONG).show()
                            mAuth.signOut()
                            progressDialog.dismiss()
                        }
                    }
            }
        }
    }

    private fun saveUserInfo(fullName: String, userName: String, password: String, email: String, progressDialog: ProgressDialog) {

            val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid
            val usersRef: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Users")

            val userMap = HashMap<String, Any>()
            userMap["uid"] = currentUserID
            userMap["fullname"] = fullName.toLowerCase()
            userMap["username"] = userName.toLowerCase()
            userMap["password"] = password
            userMap["email"] = email
            userMap["bio"] = "Xin chào, tôi là một người dùng mạng xã hội Unigram"
            userMap["image"] = "https://firebasestorage.googleapis.com/v0/b/um-clone-app.appspot.com/o/Default%20Images%2Fprofile.png?alt=media&token=64cc14ef-0a61-40d1-9320-6d710fe79cde"

            usersRef.child(currentUserID).setValue(userMap)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful)
                    {
                        progressDialog.dismiss()
                        Toast.makeText(this,"Tài khoản đã được tạo thành công.", Toast.LENGTH_LONG).show()


                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(currentUserID)
                            .child("Following").child(currentUserID)
                            .setValue(true)


                        val intent = Intent(this@SignUpActivity, MainActivity::class.java)
                        intent.setType("message/rfc822");
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                    }
                    else
                    {
                        val message = task.exception!!.toString()
                        Toast.makeText(this,"LỖI: $message", Toast.LENGTH_LONG).show()
                        FirebaseAuth.getInstance().signOut()
                        progressDialog.dismiss()
                    }
                }
    }

    /*private fun saveUserInfo(fullName: String, userName: String, password: String, email: ProgressDialog, progressDialog: ProgressDialog)
    {

    }*/


}