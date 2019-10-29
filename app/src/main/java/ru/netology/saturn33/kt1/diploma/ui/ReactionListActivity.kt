package ru.netology.saturn33.kt1.diploma.ui

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_reaction_list.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import ru.netology.saturn33.kt1.diploma.R
import ru.netology.saturn33.kt1.diploma.adapter.ReactionAdapter
import ru.netology.saturn33.kt1.diploma.dto.ReactionResponseDto
import ru.netology.saturn33.kt1.diploma.repositories.Repository
import java.io.IOException

class ReactionListActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    private var job: Job? = null
    private lateinit var dialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reaction_list)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            if (intent.getBooleanExtra("createNewFeed", false))
                startActivity(Intent(this, FeedActivity::class.java))
            finish()
            true
        } else {
            return super.onOptionsItemSelected(item)
        }
    }

    override fun onStart() {
        super.onStart()
        val postId = intent.getLongExtra("postId", 0L)
//        Toast.makeText(this, "Reactions of post #$postId", Toast.LENGTH_LONG).show()

        dialog = AlertDialog.Builder(this@ReactionListActivity)
            .setCancelable(false)
            .setView(R.layout.progressbar).create()

        job = launch {
            dialog.show()
            try {
                val result = Repository.getReactions(postId)
                if (result.isSuccessful) {
                    with(container) {
                        layoutManager = LinearLayoutManager(this@ReactionListActivity)
                        val list = mutableListOf<ReactionResponseDto>()
                        list.addAll(result.body() ?: mutableListOf())
                        adapter = ReactionAdapter(list)
                    }
                }
            } catch (e: IOException) {
                Toast.makeText(
                    this@ReactionListActivity,
                    getString(R.string.reactions_load_error),
                    Toast.LENGTH_SHORT
                ).show()
            }
            dialog.dismiss()
        }

        swipeContainer.setOnRefreshListener {
            launch {
                try {
                    val response = Repository.getReactions(intent.getLongExtra("postId", 0L))

                    if (response.isSuccessful) {
                        val newItems = response.body()!!
                        (container.adapter as ReactionAdapter).list.clear()
                        (container.adapter as ReactionAdapter).list.addAll(0, newItems)
                        (container.adapter as ReactionAdapter).notifyDataSetChanged()
                    } else {
                        Toast.makeText(
                            this@ReactionListActivity,
                            getString(R.string.reactions_update_error),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: IOException) {
                    Toast.makeText(
                        this@ReactionListActivity,
                        getString(R.string.reactions_update_error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                swipeContainer.isRefreshing = false
            }
        }
    }

    override fun onStop() {
        super.onStop()
        job?.cancel()
        dialog.dismiss()
    }
}
