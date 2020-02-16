package com.charlag.tuta.util

import android.animation.LayoutTransition
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.drawable.RotateDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.charlag.tuta.R
import com.charlag.tuta.entities.Id

class AccountHeaderView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    val appNameLabel: TextView
    val mailAddressLabel: TextView
    val accountsRecycler: RecyclerView
    val addAccountButton: View
    val expandedIcon: ImageView
    var expanded = false
        set(value) {
            field = value
            updateOnExpanded(value)
        }
    var onNewAccount: (() -> Unit)? = null
    private val adapter = AccountsAdapter()

    var onAccountSelected: ((LocalAccountData) -> Unit)?
        get() = adapter.onAccountSelected
        set(value) {
            adapter.onAccountSelected = value
        }

    init {
        LayoutInflater.from(context).inflate(R.layout.view_acctount_header, this, true)

        appNameLabel = findViewById(R.id.appNameLabel)
        mailAddressLabel = findViewById(R.id.mailAddressLabel)
        accountsRecycler = findViewById(R.id.accountsRecycler)
        addAccountButton = findViewById(R.id.addAccountButton)
        expandedIcon = findViewById(R.id.expandedIcon)

        expanded = false
        isClickable = true
        isFocusable = true

        // aka android:animateLayoutChanges="true"
        layoutTransition = LayoutTransition()

        accountsRecycler.layoutManager = LinearLayoutManager(context)
        accountsRecycler.setHasFixedSize(true)
        accountsRecycler.adapter = adapter

        addAccountButton.setOnClickListener { onNewAccount?.invoke() }
    }

    override fun performClick(): Boolean {
        this.expanded = !this.expanded
        return super.performClick()
    }

    fun updateAccounts(accounts: List<LocalAccountData>) {
        this.adapter.accounts = accounts
    }

    private fun updateOnExpanded(expanded: Boolean) {
        accountsRecycler.isVisible = expanded
        addAccountButton.isVisible = expanded

        val iconDrawable = (expandedIcon.drawable as RotateDrawable)
        if (expanded) {
            ObjectAnimator.ofInt(iconDrawable, "level", 0, 5000).start()
        } else {
            ObjectAnimator.ofInt(iconDrawable, "level", 5000, 0).start()
        }
    }

}

private class AccountsAdapter : RecyclerView.Adapter<AccountsAdapter.AccountViewHolder>() {
    var accounts: List<LocalAccountData> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    var onAccountSelected: ((LocalAccountData) -> Unit)? = null

    inner class AccountViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val folderIcon = itemView.findViewById<ImageView>(R.id.folderIcon)
        val folderName = itemView.findViewById<TextView>(R.id.folderName)
        val counterLabel = itemView.findViewById<TextView>(R.id.counterLabel)

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(localAccountData: LocalAccountData) {
            folderIcon.isInvisible = true
            counterLabel.isVisible = false
            folderName.text = localAccountData.mainAddress
        }

        override fun onClick(v: View?) {
            if (adapterPosition != RecyclerView.NO_POSITION) {
                onAccountSelected?.invoke(accounts[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_folder, parent, false)
        return AccountViewHolder(view)
    }

    override fun getItemCount(): Int = accounts.size

    override fun onBindViewHolder(holder: AccountViewHolder, position: Int) {
        holder.bind(accounts[position])
    }
}

class LocalAccountData(val userId: Id, val mainAddress: String)