package com.charlag.tuta.mail

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.charlag.tuta.R
import com.charlag.tuta.util.dpToPx

class MailSwipeCallback(
    private val context: Context,
    private inline val onSwipe: (direction: Int, position: Int) -> Unit
) :
    ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
    private val deletePaint = Paint().apply {
        color = context.getColor(R.color.colorAccent)
    }
    private val archivePaint = Paint().apply {
        color = context.getColor(R.color.blue_20)
    }
    private val fgPaint = Paint().apply {
        color = context.getColor(R.color.colorPrimary)
    }
    private val trashIcon = context.getDrawable(R.drawable.ic_delete_black_24dp)!!.apply {
        setTint(Color.WHITE)
    }
    private val archiveIcon = context.getDrawable(R.drawable.ic_archive_black_24dp)!!.apply {
        setTint(Color.WHITE)
    }
    private val iconSize = dpToPx(context, 24).toInt()

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        onSwipe(direction, viewHolder.adapterPosition)
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            val view = viewHolder.itemView
            val bgPaint = if (dX < 0) deletePaint else archivePaint
            c.drawRect(
                view.left.toFloat(),
                view.top.toFloat(),
                view.right.toFloat(),
                view.bottom.toFloat(),
                bgPaint
            )

            if (dX < 0) {
                val iconTop = view.top + (view.bottom - view.top) / 2 - (iconSize / 2)
                val iconRight = view.right - iconSize
                trashIcon.setBounds(
                    iconRight - iconSize,
                    iconTop,
                    iconRight,
                    iconTop + iconSize
                )
                trashIcon.draw(c)
            } else {
                val iconTop = view.top + (view.bottom - view.top) / 2 - (iconSize / 2)
                val iconLeft = view.left + iconSize
                archiveIcon.setBounds(
                    iconLeft,
                    iconTop,
                    iconLeft + iconSize,
                    iconTop + iconSize
                )
                archiveIcon.draw(c)
            }

            c.drawRect(
                view.left.toFloat() + dX,
                view.top.toFloat() + dY,
                view.right.toFloat() + dX,
                view.bottom.toFloat() + dY,
                fgPaint
            )
        }
        super.onChildDraw(
            c,
            recyclerView,
            viewHolder,
            dX,
            dY,
            actionState,
            isCurrentlyActive
        )
    }
}