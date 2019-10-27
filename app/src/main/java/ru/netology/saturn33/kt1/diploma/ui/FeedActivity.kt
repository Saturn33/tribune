package ru.netology.saturn33.kt1.diploma.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_feed.*
import ru.netology.saturn33.kt1.diploma.R
import ru.netology.saturn33.kt1.diploma.REACTIONS_REQUEST_CODE
import ru.netology.saturn33.kt1.diploma.adapter.PostAdapter
import ru.netology.saturn33.kt1.diploma.helpers.SPref
import ru.netology.saturn33.kt1.diploma.helpers.Stub

class FeedActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed)

        if (SPref.isReadOnly(this)) {
            fab.hide()
        } else {
            fab.show()
            fab.setOnClickListener {
                startActivity(Intent(this, PostActivity::class.java))
            }
        }
    }

    override fun onStart() {
        super.onStart()
        with(container) {
            layoutManager = LinearLayoutManager(this@FeedActivity)
            val list = Stub.getPosts()
            adapter = PostAdapter(list)
        }

        swipeContainer.setOnRefreshListener {
            swipeContainer.isRefreshing = false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.feed_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_exit -> {
                SPref.removeUserAuth(this@FeedActivity)
                startActivity(Intent(this, RegistrationActivity::class.java))
                finish()
                true
            }
            R.id.action_profile -> {
                startActivity(Intent(this, ProfileActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REACTIONS_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    val userId = data?.getLongExtra("userId", 0L)
                    Toast.makeText(this, "Returned userId $userId", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
