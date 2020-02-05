package com.charlag.tuta.mail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.RecyclerView
import com.charlag.tuta.R

interface ListedAttachment {
    val name: String
    val size: Long
}

class AttachmentAdapter<T : ListedAttachment>(
    @DrawableRes
    private val iconRes: Int,
    private val onItemSelected: (file: T) -> Unit,
    private val onAction: (file: T) -> Unit
) : RecyclerView.Adapter<AttachmentAdapter<T>.ViewHolder>() {
    val items = mutableListOf<T>()

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_attachment, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameView = itemView.findViewById<TextView>(
            R.id.nameLabel
        )
        val actionButton = itemView.findViewById<ImageButton>(
            R.id.downloadButton
        )
        var file: T? = null

        init {
            itemView.setOnClickListener {
                file?.let {
                    onItemSelected(it)
                }
            }
            actionButton.setOnClickListener {
                file?.let {
                    onAction(it)
                }
            }
            actionButton.setImageResource(iconRes)
        }

        fun bind(file: T) {
            nameView.text = file.name
            this.file = file
        }
    }

}