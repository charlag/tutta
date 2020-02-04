package com.charlag.tuta.compose

import android.graphics.Color
import android.text.SpanWatcher
import android.text.Spannable
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ImageSpan
import android.util.Patterns
import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.EditText
import androidx.core.text.getSpans
import androidx.core.widget.doBeforeTextChanged
import androidx.core.widget.doOnTextChanged
import com.charlag.tuta.R
import com.google.android.material.chip.ChipDrawable

fun setupRecipientField(
    setupField: AutoCompleteTextView,
    fieldType: RecipientField,
    autocompleteProvider: ContactAutocompleteProvider,
    onAddRecipient: (type: RecipientField, address: String) -> Unit,
    onRemoveRecipient: (type: RecipientField, address: String) -> Unit
) {
    val spanWatcher = object : SpanWatcher {
        override fun onSpanChanged(
            text: Spannable?,
            what: Any?,
            ostart: Int,
            oend: Int,
            nstart: Int,
            nend: Int
        ) {
        }

        override fun onSpanAdded(text: Spannable?, what: Any?, start: Int, end: Int) {
            if (what is RecipientSpan) {
                onAddRecipient(what.fieldType, what.mailAddress)
            }
        }

        override fun onSpanRemoved(text: Spannable?, what: Any?, start: Int, end: Int) {
            if (what is RecipientSpan) {
                what.remove()
                onRemoveRecipient(what.fieldType, what.mailAddress)
            }
        }
    }
    setupField.text.setSpan(
        spanWatcher,
        0,
        setupField.text.length,
        Spannable.SPAN_INCLUSIVE_INCLUSIVE
    )

    setupField.doOnTextChanged { text, start, before, count ->
        val lastChar = (start + count - 1).coerceAtLeast(0)
        if (text != null && text.isNotEmpty() && text[lastChar].isSeparator()) {
            tryCompleteRecipient(setupField, lastChar, fieldType)
        }
    }
    setupField.movementMethod = LinkMovementMethod()
    setupField.doBeforeTextChanged { _, start, count, _ ->
        val fieldText = setupField.text ?: return@doBeforeTextChanged
        val spans =
            fieldText.getSpans<RecipientSpan>(start = start + 1, end = start + 1 + count)
        for (span in spans) {
            fieldText.removeSpan(span)
        }
    }
    setupField.highlightColor = Color.TRANSPARENT
    setupField.onFocusChangeListener
    setupField.setOnFocusChangeListener { _, hasFocus ->
        if (!hasFocus) {
            tryCompleteRecipient(setupField, setupField.text.length, fieldType)
        }
    }

    setupField.setAdapter(RecipientAutocompleteAdapter(autocompleteProvider))
}


private fun tryCompleteRecipient(
    setupField: EditText,
    end: Int,
    fieldType: RecipientField
) {
    val fieldText = setupField.text ?: return
    val spans = fieldText.getSpans<RecipientSpan>(end = end)
    // Start from the last spam before this position or from the beginning
    var textStart =
        if (spans.isEmpty()) 0 else fieldText.getSpanEnd(spans.last())
    // Clean up all whitespaces in the beginning
    while (textStart < fieldText.lastIndex && fieldText[textStart].isWhitespace()) {
        textStart++
    }
    val newText = fieldText.substring(textStart, end)
    if (emailRegex.matches(newText)) {
        val chip = ChipDrawable.createFromResource(setupField.context, R.xml.recipient_chip)
        chip.setText(newText)
        chip.setBounds(
            0, 0, chip.intrinsicWidth, chip.intrinsicHeight
        )
        val span = RecipientSpan(chip, newText, fieldType, setupField)
        fieldText.setSpan(
            span,
            textStart,
            textStart + newText.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        setupField.text!!.setSpan(
            span.clickableSpan,
            textStart,
            textStart + newText.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
}

private fun Char.isSeparator() = this.isWhitespace() || this == ','
private val emailRegex = Patterns.EMAIL_ADDRESS.toRegex()

private class RecipientSpan(
    chip: ChipDrawable,
    val mailAddress: String,
    val fieldType: RecipientField,
    private val field: EditText
) : ImageSpan(chip) {
    val clickableSpan = object : ClickableSpan() {
        override fun onClick(widget: View) {
            remove()
        }

        override fun updateDrawState(ds: TextPaint) {
            // noOp
        }
    }

    fun remove() {
        val text = field.text ?: return
        val spanStart = text.getSpanStart(this)
        val spanEnd = text.getSpanEnd(this)
        text.removeSpan(this)
        text.removeSpan(clickableSpan)
        if (spanStart != -1 && spanEnd != -1) {
            text.delete(spanStart, spanEnd)
        }
    }
}