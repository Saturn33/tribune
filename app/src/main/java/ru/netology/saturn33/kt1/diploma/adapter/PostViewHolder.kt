package ru.netology.saturn33.kt1.diploma.adapter

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.footer.view.*
import kotlinx.android.synthetic.main.post.view.*
import ru.netology.saturn33.kt1.diploma.R
import ru.netology.saturn33.kt1.diploma.REACTIONS_REQUEST_CODE
import ru.netology.saturn33.kt1.diploma.dto.PostResponseDto
import ru.netology.saturn33.kt1.diploma.model.UserBadge
import ru.netology.saturn33.kt1.diploma.ui.ReactionListActivity
import java.text.SimpleDateFormat
import java.util.*

class PostViewHolder(val adapter: PostAdapter, itemView: View) : RecyclerView.ViewHolder(itemView) {
    private fun updateActionInfo(
        imgButton: ImageView,
        txtView: TextView,
        active: Boolean,
        count: Int,
        activeDrawable: Int,
        inactiveDrawable: Int,
        activeTextColor: Int,
        inactiveTextColor: Int
    ) {
        if (active) {
            imgButton.setImageResource(activeDrawable)
            txtView.setTextColor(itemView.context.resources.getColor(activeTextColor, null))
        } else {
            imgButton.setImageResource(inactiveDrawable)
            txtView.setTextColor(itemView.context.resources.getColor(inactiveTextColor, null))
        }
        txtView.text = count.toString()
    }

    init {
        with(itemView) {
            likeBtn.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    val currentPosition = adapterPosition
                    val item = adapter.list[currentPosition]
                    if (!item.promotedByMe && !item.demotedByMe) {
                        item.promotedByMe = true
                        item.promotes++

                        adapter.notifyItemChanged(currentPosition)
                    }
                }
            }

            dislikeBtn.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    val currentPosition = adapterPosition
                    val item = adapter.list[currentPosition]
                    if (!item.promotedByMe && !item.demotedByMe) {
                        item.demotedByMe = true
                        item.demotes++

                        adapter.notifyItemChanged(currentPosition)
                    }
                }
            }

            see_reactions.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    val currentPosition = adapterPosition
                    val item = adapter.list[currentPosition]
                    (this.context as Activity).startActivityForResult(
                        Intent(
                            this.context,
                            ReactionListActivity::class.java
                        ).apply {
                            putExtra("postId", item.id)
                        }, REACTIONS_REQUEST_CODE
                    )
                }
            }

            link.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    val currentPosition = adapterPosition
                    val item = adapter.list[currentPosition]

                    item.link?.let { link ->
                        this.context.startActivity(
                            Intent().apply {
                                action = Intent.ACTION_VIEW
                                data = Uri.parse(link)
                            })
                    }
                }
            }
        }
    }

    fun bind(post: PostResponseDto) {
        with(itemView) {
            val dateFormatted = SimpleDateFormat(
                "dd MMM",
                //"dd.MM.yyyy HH:mm",
                Locale.US
            ).format(Date(post.date))
            date.text = dateFormatted

            username.text = post.author.username
            content.text = post.text

            updateActionInfo(
                likeBtn,
                likesCount,
                post.promotedByMe,
                post.promotes,
                R.drawable.like_active,
                R.drawable.like,
                R.color.promote,
                R.color.normal
            )

            updateActionInfo(
                dislikeBtn,
                dislikesCount,
                post.demotedByMe,
                post.demotes,
                R.drawable.dislike_active,
                R.drawable.dislike,
                R.color.demote,
                R.color.normal
            )

            if (post.attachment.id.isNotEmpty()) {
                loadImage(image, post.attachment.url)
            }

            if (post.author.avatar?.id?.isNotEmpty() == true) {
                loadAvatar(author_avatar, post.author.avatar.url)
            } else {
                removeAvatar(author_avatar)
            }

            link.isVisible = post.link != null

            if (post.author.badge != null) {
                badge.visibility = View.VISIBLE
                when (post.author.badge) {
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