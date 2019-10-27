package ru.netology.saturn33.kt1.diploma.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.netology.saturn33.kt1.diploma.R
import ru.netology.saturn33.kt1.diploma.dto.ReactionResponseDto

class ReactionAdapter(val list: MutableList<ReactionResponseDto>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ReactionViewHolder(
            this,
            LayoutInflater.from(parent.context).inflate(R.layout.reaction, parent, false)
        )
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ReactionViewHolder)
            holder.bind(list[position])
    }
}
