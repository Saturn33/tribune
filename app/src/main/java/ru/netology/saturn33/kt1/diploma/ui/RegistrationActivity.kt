package ru.netology.saturn33.kt1.diploma.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_registration.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response
import ru.netology.saturn33.kt1.diploma.R
import ru.netology.saturn33.kt1.diploma.dto.RegistrationResponseDto
import ru.netology.saturn33.kt1.diploma.helpers.SPref
import ru.netology.saturn33.kt1.diploma.helpers.Validator
import ru.netology.saturn33.kt1.diploma.repositories.Repository
import java.io.IOException

class RegistrationActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    private var job: Job? = null
    private lateinit var dialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        title = getString(R.string.title_registration)

        already_have_account.setOnClickListener {
            startActivity(Intent(this, AuthActivity::class.java))
        }
    }

    override fun onStart() {
        super.onStart()
        dialog = AlertDialog.Builder(this@RegistrationActivity)
            .setCancelable(false)
            .setView(R.layout.progressbar).create()
        val token = SPref.getToken(this)
        Repository.createRetrofit(token)
        if (SPref.isAuthenticated(token)) {
            startActivity(Intent(this, FeedActivity::class.java))
            finish()
            return
        }

        register.setOnClickListener {
            val pass1 = password.text.toString()
            val pass2 = password_confirmation.text.toString()
            if (pass1 != pass2) {
                password.error = getString(R.string.passwords_do_not_match)
                return@setOnClickListener
            }

            var validationResult: Pair<Boolean, String> =
                Validator.isLoginValid(username.text.toString())
            if (!validationResult.first) {
                username.error = validationResult.second
                return@setOnClickListener
            }
            validationResult = Validator.isPasswordValid(pass1)
            if (!validationResult.first) {
                password.error = validationResult.second
                return@setOnClickListener
            }

            job = launch {
                var response: Response<RegistrationResponseDto>? = null
                dialog.show()
                try {
                    response = Repository.register(
                        username.text.toString(),
                        password.text.toString()
                    )
                } catch (e: IOException) {
                    Toast.makeText(
                        this@RegistrationActivity,
                        getString(R.string.communication_error),
                        Toast.LENGTH_LONG
                    ).show()
                }
                dialog.dismiss()

                if (response?.isSuccessful == true) {
                    Toast.makeText(
                        this@RegistrationActivity,
                        getString(R.string.registration_success),
                        Toast.LENGTH_SHORT
                    ).show()
                    SPref.setUserAuth(this@RegistrationActivity, response.body()?.token ?: "")
                    SPref.setReadOnly(this@RegistrationActivity, response.body()?.readOnly ?: false)
                    Repository.createRetrofit(response.body()?.token)
                    startActivity(Intent(this@RegistrationActivity, FeedActivity::class.java))
                    finish()
                } else {
                    try {
                        val json = JSONObject(response?.errorBody()?.string() ?: "")
                        username.error = json.get("error").toString()
                    } catch (e: Exception) {
                        Toast.makeText(
                            this@RegistrationActivity,
                            getString(R.string.unknown_registration_error),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        job?.cancel()
        dialog.dismiss()
    }
}
