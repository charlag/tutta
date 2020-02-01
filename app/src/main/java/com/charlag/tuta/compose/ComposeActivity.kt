package com.charlag.tuta.compose

import android.graphics.Color
import android.os.Bundle
import android.text.SpanWatcher
import android.text.Spannable
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ImageSpan
import android.util.Log
import android.util.Patterns
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.getSpans
import androidx.core.view.isVisible
import androidx.core.widget.doBeforeTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import com.charlag.tuta.R
import com.google.android.material.chip.ChipDrawable
import kotlinx.android.synthetic.main.activity_compose.*
import kotlinx.coroutines.launch

class ComposeActivity : AppCompatActivity() {
    private val viewModel: ComposeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compose)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }

        val adapter =
            ArrayAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                mutableListOf<String>()
            )
        fromSpinner.adapter = adapter

        viewModel.enabledMailAddresses.observe(this) { mailAddresses ->
            adapter.clear()
            adapter.addAll(mailAddresses)
            adapter.notifyDataSetChanged()
        }
        setupRecipientField(toField, RecipientField.TO)
        setupRecipientField(ccField, RecipientField.CC)
        setupRecipientField(bccField, RecipientField.BCC)

        expandRecipientsButton.setOnClickListener {
            expandRecipients()
        }
    }

    private fun expandRecipients() {
        ccTextView.isVisible = true
        ccField.isVisible = true
        ccBccSeparator.isVisible = true
        bccTextView.isVisible = true
        bccField.isVisible = true
        bccSubjectSeparator.isVisible = true
    }

    private fun setupRecipientField(setupField: EditText, fieldType: RecipientField) {

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

            override fun onSpanRemoved(text: Spannable?, what: Any?, start: Int, end: Int) {
                if (what is RecipientSpan) {
                    what.remove()
                    viewModel.removeRecipient(what.fieldType, what.mailAddress)
                }
            }

            override fun onSpanAdded(text: Spannable?, what: Any?, start: Int, end: Int) {
                if (what is RecipientSpan) {
                    viewModel.addRecipient(what.fieldType, what.mailAddress)
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
            if (count == 1 && text != null && text.isNotEmpty() && text[start].isSeparator()) {
                tryCompleteRecipient(setupField, start, fieldType)
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
        while (fieldText[textStart].isWhitespace()) {
            textStart++
        }
        val newText = fieldText.substring(textStart, end)
        if (emailRegex.matches(newText)) {
            val chip = ChipDrawable.createFromResource(this, R.xml.recipient_chip)
            chip.setText(newText)
            chip.setBounds(0, 0, chip.intrinsicWidth, chip.intrinsicHeight
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.add("Send")
            .setIcon(R.drawable.ic_send_black_24dp)
            .setOnMenuItemClickListener {
                send()
                true
            }
            .setVisible(true)
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        return super.onCreateOptionsMenu(menu)
    }

    private fun send() {
        Toast.makeText(this, "Sending….", Toast.LENGTH_SHORT).show()
        val activity = this

        lifecycleScope.launch {
            try {
                val didSend = viewModel.send(
                    subjectField.text.toString(),
                    contentField.text.toString(),
                    fromSpinner.selectedItem as String
                )
                if (didSend) {
                    Toast.makeText(activity, "Sent", Toast.LENGTH_SHORT).show()
                    finish()
                }

            } catch (e: Exception) {
                Log.e("Compose", "error", e)
                Toast.makeText(activity, "Error $e", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private val emailRegex = Patterns.EMAIL_ADDRESS.toRegex()
    }
}

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