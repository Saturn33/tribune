package ru.netology.saturn33.kt1.diploma.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_registration.*
import ru.netology.saturn33.kt1.diploma.R
import ru.netology.saturn33.kt1.diploma.helpers.SPref
import ru.netology.saturn33.kt1.diploma.repositories.Repository

class RegistrationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        title = getString(R.string.title_registration)

        already_have_account.setOnClickListener {
            startActivity(Intent(this, AuthActivity::class.java))
        }

        register.setOnClickListener {
            startActivity(Intent(this, FeedActivity::class.java))
            finish()
        }
    }

    override fun onStart() {
        super.onStart()

        val token = SPref.getToken(this)
        Repository.createRetrofit(token)
        if (SPref.isAuthenticated(token)) {
            startActivity(Intent(this, FeedActivity::class.java))
            finish()
        }
    }
}
