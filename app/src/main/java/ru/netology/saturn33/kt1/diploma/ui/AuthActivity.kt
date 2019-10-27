package ru.netology.saturn33.kt1.diploma.ui

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_auth.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response
import ru.netology.saturn33.kt1.diploma.R
import ru.netology.saturn33.kt1.diploma.dto.AuthenticationResponseDto
import ru.netology.saturn33.kt1.diploma.helpers.SPref
import ru.netology.saturn33.kt1.diploma.helpers.Validator
import ru.netology.saturn33.kt1.diploma.repositories.Repository
import java.io.IOException

class AuthActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    private var job: Job? = null
    private lateinit var dialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        dont_have_account.setOnClickListener {
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

    override fun onStart() {
        super.onStart()
        dialog = AlertDialog.Builder(this@AuthActivity)
            .setCancelable(false)
            .setView(R.layout.progressbar).create()

        login.setOnClickListener {
            var validationResult: Pair<Boolean, String> =
                Validator.isLoginValid(username.text.toString())
            if (!validationResult.first) {
                username.error = validationResult.second
                return@setOnClickListener
            }
            validationResult = Validator.isPasswordValid(password.text.toString())
            if (!validationResult.first) {
                password.error = validationResult.second
                return@setOnClickListener
            }
            dialog.show()

            job = launch {
                var response: Response<AuthenticationResponseDto>? = null
                try {
                    response = Repository.auth(
                        username.text.toString(),
                        password.text.toString()
                    )
                } catch (e: IOException) {
                    Toast.makeText(
                        this@AuthActivity,
                        getString(R.string.communication_error),
                        Toast.LENGTH_LONG
                    ).show()
                }
                dialog.dismiss()

                if (response?.isSuccessful == true) {
                    Toast.makeText(
                        this@AuthActivity,
                        getString(R.string.auth_success),
                        Toast.LENGTH_SHORT
                    ).show()
                    SPref.setUserAuth(this@AuthActivity, response.body()?.token ?: "")
                    SPref.setReadOnly(this@AuthActivity, response.body()?.readOnly ?: false)
                    finish()
                } else {
                    try {
                        val json = JSONObject(response?.errorBody()?.string() ?: "")
                        username.error = json.get("error").toString()
                    } catch (e: Exception) {
                        Toast.makeText(
                            this@AuthActivity,
                            getString(R.string.unknown_auth_error),
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
