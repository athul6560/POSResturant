package com.zeezaglobal.posresturant.ui.loginPage

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.zeezaglobal.posresturant.MainActivity
import com.zeezaglobal.posresturant.R
import com.zeezaglobal.posresturant.Utils.StorePreferenceManager

class LoginActivity : AppCompatActivity() {

    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var sharedPrefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login) // your XML file name

        // Initialize views
        usernameEditText = findViewById(R.id.editTextText)
        passwordEditText = findViewById(R.id.editTextTextPassword)
        loginButton = findViewById(R.id.button3)

        // Initialize SharedPreferences
        sharedPrefs = getSharedPreferences("login_prefs", Context.MODE_PRIVATE)

        // Set login button click listener
        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show()
            } else {
                handleLogin(username, password)
            }
        }
    }

    private fun handleLogin(username: String, password: String) {
        when {
            username == "ernakulam@beanbarrel.in" && password == "1234" -> {
                StorePreferenceManager.saveStoreId(this, 0) // 0 = Ernakulam
                Toast.makeText(this, "Login successful: Ernakulam branch", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            username == "aluva@beanbarrel.in" && password == "1234" -> {
                StorePreferenceManager.saveStoreId(this, 1) // 1 = Aluva
                Toast.makeText(this, "Login successful: Aluva branch", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            else -> {
                Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show()
            }
        }
    }


}
