package com.charlag.tuta

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_error_handler.*

class ErrorHandlerActivity : AppCompatActivity(R.layout.activity_error_handler) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val error = intent.getStringExtra(ERROR_EXTRA)
        errorLabel.text = error

        copyErrorButton.setOnClickListener {
            val clipboardManager = getSystemService(ClipboardManager::class.java)!!
            val clip = ClipData.newPlainText("error", error)
            clipboardManager.setPrimaryClip(clip)
            Toast.makeText(this, "Copied error!", Toast.LENGTH_SHORT).show()
        }

        sendErrorButton.setOnClickListener {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, error)
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }

        restartAppButton.setOnClickListener {
            startActivity(LoginActivity.normalIntent(this))
            finish()
        }
    }

    companion object {
        fun newIntent(context: Context, throwable: Throwable) =
            Intent(context, ErrorHandlerActivity::class.java).apply {
                putExtra(
                    ERROR_EXTRA,
                    throwable.message.toString()
                            + throwable.stackTrace.toString()
                            + (throwable.cause?.toString() ?: "")
                )
            }

        private const val ERROR_EXTRA = "ERROR"
    }
}