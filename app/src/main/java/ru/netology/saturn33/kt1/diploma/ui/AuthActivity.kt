package ru.netology.saturn33.kt1.diploma.ui

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_auth.*
import ru.netology.saturn33.kt1.diploma.R

class AuthActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        dont_have_account.setOnClickListener {
            finish()
        }

        login.setOnClickListener {
            //TODO после успешного логина просто финишировать активность, переход к фиду уже будет с RegistrationActivity после проверки наличия токена
            startActivity(Intent(this, FeedActivity::class.java))
            finish()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            finish()
            true
        } else {
            return super.onOptionsItemSelected(item)
        }
    }

}
