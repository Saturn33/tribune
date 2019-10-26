package ru.netology.saturn33.kt1.diploma.ui

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_reaction_list.*
import ru.netology.saturn33.kt1.diploma.R
import ru.netology.saturn33.kt1.diploma.adapter.ReactionAdapter
import ru.netology.saturn33.kt1.diploma.helpers.Stub

class ReactionListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reaction_list)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
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
        val postId = intent.getLongExtra("postId", 0L)
        Toast.makeText(this, "Reactions of post #$postId", Toast.LENGTH_LONG).show()

        with(container) {
            layoutManager = LinearLayoutManager(this@ReactionListActivity)
            val list = Stub.getReactions(50)
            adapter = ReactionAdapter(list)
        }

        swipeContainer.setOnRefreshListener {
            swipeContainer.isRefreshing = false
        }

    }

}
