package xyz.dps0340.iwasthere

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import splitties.activities.start
import splitties.toast.toast

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val signInbutton = findViewById<Button>(R.id.signInButton)
        val id = findViewById<EditText>(R.id.editTextTextPersonName)
        val pw = findViewById<EditText>(R.id.editTextTextPassword)
        signInbutton.setOnClickListener {
            if(id.text.toString().isEmpty() || pw.text.toString().isEmpty()) {
                toast("invalid id or password")
                return@setOnClickListener
            }
            start<MainActivity> {  }
        }
    }
}