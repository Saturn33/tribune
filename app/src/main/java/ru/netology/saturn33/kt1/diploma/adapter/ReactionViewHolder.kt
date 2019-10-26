package ru.netology.saturn33.kt1.diploma.adapter

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.reaction.view.*
import ru.netology.saturn33.kt1.diploma.R
import ru.netology.saturn33.kt1.diploma.dto.ReactionResponseDto
import ru.netology.saturn33.kt1.diploma.model.ReactionType
import ru.netology.saturn33.kt1.diploma.model.UserBadge
import java.text.SimpleDateFormat
import java.util.*

class ReactionViewHolder(val adapter: ReactionAdapter, itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    init {
        with(itemView) {
            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    val currentPosition = adapterPosition
                    val item = adapter.list[currentPosition]
                    with(this.context as Activity) {
                        setResult(RESULT_OK, Intent().apply {
                            putExtra("userId", item.user.id)
                        })
                        finish()
                    }
                }
            }
        }
    }

    fun bind(reaction: ReactionResponseDto) {
        with(itemView) {
            val dateFormatted = SimpleDateFormat(
                "dd MMM",
                //"dd.MM.yyyy HH:mm",
                Locale.US
            ).format(Date(reaction.date))
            date.text = dateFormatted

            username.text = reaction.user.username

            if (reaction.user.avatar?.id?.isNotEmpty() == true) {
                loadAvatar(author_avatar, reaction.user.avatar.url)
            } else {
                removeAvatar(author_avatar)
            }

            if (reaction.user.badge != null) {
                badge.visibility = View.VISIBLE
                when (reaction.user.badge) {
                    UserBadge.PROMOTER -> {
                        badge.setTextColor(resources.getColor(R.color.badge_promoter, null))
                        badge.text = context.getString(R.string.badge_promoter)
                    }
                    UserBadge.HATER -> {
                        badge.setTextColor(resources.getColor(R.color.badge_hater, null))
                        badge.text = context.getString(R.string.badge_hater)
                    }
                }
            } else {
                badge.visibility = View.GONE
            }

            when (reaction.type) {
                ReactionType.PROMOTE -> reactionImg.setImageResource(R.drawable.like_active)
                ReactionType.DEMOTE -> reactionImg.setImageResource(R.drawable.dislike_active)
            }
        }
    }

    private fun removeAvatar(authorAvatar: ImageView) {
        authorAvatar.setImageResource(R.drawable.ic_person)
    }

    private fun loadImage(photoImg: ImageView, url: String) {
        Glide.with(photoImg.context)
            .load(url)
            .fitCenter()
            .into(photoImg)
    }

    private fun loadAvatar(photoImg: ImageView, url: String) {
        Glide.with(photoImg.context)
            .load(url)
            .centerInside()
            .into(photoImg)
    }
}
