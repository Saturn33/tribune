package ru.netology.saturn33.kt1.diploma.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.netology.saturn33.kt1.diploma.R
import ru.netology.saturn33.kt1.diploma.dto.PostResponseDto

class PostAdapter(val list: MutableList<PostResponseDto>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return PostViewHolder(
            this,
            LayoutInflater.from(parent.context).inflate(R.layout.post, parent, false)
        )
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is PostViewHolder)
            holder.bind(list[position])
    }
}