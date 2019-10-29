package ru.netology.saturn33.kt1.diploma.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_feed.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import ru.netology.saturn33.kt1.diploma.PAGE_SIZE
import ru.netology.saturn33.kt1.diploma.R
import ru.netology.saturn33.kt1.diploma.REACTIONS_REQUEST_CODE
import ru.netology.saturn33.kt1.diploma.adapter.PostAdapter
import ru.netology.saturn33.kt1.diploma.dto.PostResponseDto
import ru.netology.saturn33.kt1.diploma.helpers.SPref
import ru.netology.saturn33.kt1.diploma.repositories.Repository
import java.io.IOException

class FeedActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    private var job: Job? = null
    private var profileJob: Job? = null
    private lateinit var dialog: AlertDialog
    private var filterByUser: Long = 0
    private var menuLink: Menu? = null
    private var loadMore = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed)

        requestToken()
        dialog = AlertDialog.Builder(this@FeedActivity)
            .setCancelable(false)
            .setView(R.layout.progressbar).create()

        fab.setOnClickListener {
            startActivity(Intent(this, PostActivity::class.java))
        }
        getProfileInfo()

        job = launch {
            dialog.show()
            try {
                val result = Repository.getRecentPosts()
                if (result.isSuccessful) {
                    with(container) {
                        layoutManager = LinearLayoutManager(this@FeedActivity)
                        val list = result.body() ?: mutableListOf<PostResponseDto>()
                        adapter = PostAdapter(list)
                    }
                }
            } catch (e: IOException) {
                Toast.makeText(
                    this@FeedActivity,
                    getString(R.string.feed_load_error),
                    Toast.LENGTH_SHORT
                ).show()
            }
            dialog.dismiss()
        }

        swipeContainer.setOnRefreshListener {
            job = getNewPosts()
        }
    }

    private fun getProfileInfo() {
        profileJob = launch {
            try {
                val result = Repository.getProfile()
                if (result.isSuccessful) {
                    SPref.setReadOnly(this@FeedActivity, result.body()?.isReadOnly ?: false)
                    if (SPref.isReadOnly(this@FeedActivity)) {
                        fab.hide()
                    } else {
                        fab.show()
                    }
                }
            } catch (e: IOException) {
                Toast.makeText(
                    this@FeedActivity,
                    getString(R.string.profile_load_error),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun requestToken() {
        with(GoogleApiAvailability.getInstance()) {
            val code = isGooglePlayServicesAvailable(this@FeedActivity)
            if (code == ConnectionResult.SUCCESS) {
                return@with
            }

            if (isUserResolvableError(code)) {
                getErrorDialog(this@FeedActivity, code, 9000).show()
                return
            }

            Toast.makeText(
                this@FeedActivity,
                getString(R.string.googleplay_services_unavailable),
                Toast.LENGTH_LONG
            ).show()
            return
        }

        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
            launch {
                println(it.token)
                Repository.registerPushToken(it.token)
            }
        }
    }

    internal fun filterByUser(userId: Long) {
        menuLink?.findItem(R.id.action_clear_user_filter)
            ?.setIcon(if (userId == 0L) R.drawable.filter_by_user else R.drawable.filter_by_user_active)
        filterByUser = userId
        job = getNewPosts()
    }

    internal fun getOlderPosts(postId: Long) {
        if (!loadMore) return
        job = launch {
            dialog.show()
            try {
                val response = Repository.getPostsBefore(filterByUser, postId)

                if (response.isSuccessful) {
                    val newItems = response.body() ?: mutableListOf()
                    if (newItems.size > 0) {
                        val oldLastIndex = (container.adapter as PostAdapter).list.lastIndex
                        (container.adapter as PostAdapter).list.addAll(newItems)
                        (container.adapter as PostAdapter).notifyItemRangeInserted(
                            oldLastIndex + 1,
                            newItems.size
                        )
                    }
                    if (newItems.size < PAGE_SIZE) {
                        loadMore = false
                        Toast.makeText(
                            this@FeedActivity,
                            getString(R.string.no_more_posts),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                } else {
                    Toast.makeText(
                        this@FeedActivity,
                        getString(R.string.feed_update_error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: IOException) {
                Toast.makeText(
                    this@FeedActivity,
                    getString(R.string.feed_load_error),
                    Toast.LENGTH_SHORT
                ).show()
            }
            dialog.dismiss()
        }
    }

    private fun getNewPosts(): Job {
        loadMore = true
        getProfileInfo()
        return launch {
            dialog.show()
            try {
                val response = Repository.getRecentPosts(filterByUser)

                if (response.isSuccessful) {
                    val newItems = response.body() ?: mutableListOf()
                    (container.adapter as PostAdapter).list.clear()
                    (container.adapter as PostAdapter).list.addAll(0, newItems)
                    (container.adapter as PostAdapter).notifyDataSetChanged()
                } else {
                    Toast.makeText(
                        this@FeedActivity,
                        getString(R.string.feed_update_error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: IOException) {
                Toast.makeText(
                    this@FeedActivity,
                    getString(R.string.feed_load_error),
                    Toast.LENGTH_SHORT
                ).show()
            }
            swipeContainer.isRefreshing = false
            dialog.dismiss()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.feed_menu, menu)
        menuLink = menu

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
            R.id.action_clear_user_filter -> {
                filterByUser(0)
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
                    val userId = data?.getLongExtra("userId", 0L) ?: 0
//                    Toast.makeText(this, "Returned userId $userId", Toast.LENGTH_LONG).show()
                    filterByUser(userId)
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        job?.cancel()
        profileJob?.cancel()
        dialog.dismiss()
    }
}
