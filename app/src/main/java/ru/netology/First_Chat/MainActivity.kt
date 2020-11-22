package ru.netology.First_Chat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.database.FirebaseListAdapter
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import android.text.format.DateFormat
import android.widget.*
import com.github.library.bubbleview.BubbleTextView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText


class MainActivity : AppCompatActivity() {

    private val SIGN_IN_CODE : Int = 1
    private lateinit var activity_mains : RelativeLayout
    private lateinit var adapter : FirebaseListAdapter<Massage>
    private lateinit var emojiconEditText: EmojiconEditText
    private lateinit var emojiButton: ImageView
    private lateinit var submitButton: ImageView
    private lateinit var emojIconActions: EmojIconActions

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SIGN_IN_CODE) {
            if (resultCode == RESULT_OK) {
                Snackbar.make(activity_mains,"Вы авторизованы",Snackbar.LENGTH_LONG).show()
                displayAllMessage()
            } else{
                Snackbar.make(activity_mains,"Вы не авторизованы",Snackbar.LENGTH_LONG).show()
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        activity_mains = findViewById(R.id.activity_main)

        submitButton = findViewById(R.id.submit_button)
        emojiButton = findViewById(R.id.emoji_button)
        emojiconEditText = findViewById(R.id.textField)
        emojIconActions = EmojIconActions(
            applicationContext,
            activity_mains,
            emojiconEditText,
            emojiButton
        )

        emojIconActions.ShowEmojIcon()
        submitButton.setOnClickListener(View.OnClickListener {
            FirebaseDatabase.getInstance().reference.push().setValue(FirebaseAuth.getInstance().currentUser!!.email?.let { it1 ->
                Massage(
                    it1,
                    emojiconEditText.text.toString()

                )
            })
            emojiconEditText.setText("")
        })


//        Пользователь еще не авторизован
        if(FirebaseAuth.getInstance().currentUser == null)
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(),SIGN_IN_CODE)
        else {
            Snackbar.make(activity_mains,"Вы авторизованы",Snackbar.LENGTH_LONG).show()
            displayAllMessage()
        }
    }

    private fun displayAllMessage() {
        val listOfMessages = findViewById<ListView>(R.id.list_of_message)
        adapter = object : FirebaseListAdapter<Massage>(
            this,
            Massage::class.java,
            R.layout.list_item,
            FirebaseDatabase.getInstance().reference
        ) {
            override fun populateView(v: View, model: Massage, position: Int) {
                val mess_user: TextView = v.findViewById(R.id.message_user)
                val mess_time: TextView = v.findViewById(R.id.message_time)
                val mess_text: BubbleTextView = v.findViewById(R.id.message_text)
                mess_user.text = model.name
                mess_text.text = model.textMassage
                mess_time.text = DateFormat.format("dd-MM-yyyy HH:mm:ss",model.massageTime).toString()
            }
        }
        listOfMessages.adapter = adapter
    }
}