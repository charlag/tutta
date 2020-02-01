package com.charlag.tuta.mail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.charlag.tuta.R
import com.charlag.tuta.entities.tutanota.File

class AttachmentAdapter(
    private val onItemSelected: (file: File) -> Unit,
    private val onDownloadFile: (file: File) -> Unit
) : RecyclerView.Adapter<AttachmentAdapter.ViewHolder>() {
    val items = mutableListOf<File>()

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
        val downloadButton = itemView.findViewById<ImageButton>(
            R.id.downloadButton
        )
        var file: File? = null

        init {
            itemView.setOnClickListener {
                file?.let {
                    onItemSelected(it)
                }
            }
            downloadButton.setOnClickListener {
                file?.let {
                    onDownloadFile(it)
                }
            }
        }

        fun bind(file: File) {
            nameView.text = file.name
            this.file = file
        }
    }

}