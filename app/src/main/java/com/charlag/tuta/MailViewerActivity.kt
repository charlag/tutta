package com.charlag.tuta

import android.os.Bundle
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import com.charlag.tuta.entities.GeneratedId
import com.charlag.tuta.entities.tutanota.MailBody
import kotlinx.android.synthetic.main.activity_mail_viewer.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MailViewerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mail_viewer)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (BuildConfig.DEBUG) {
            WebView.setWebContentsDebuggingEnabled(true)
        }

        val mailBodyId = intent.getStringExtra("mailBodyId")
        GlobalScope.launch {
            val mailBody = withContext(Dispatchers.IO) {
                DependencyDump.api.loadElementEntity<MailBody>(GeneratedId(mailBodyId))
            }
            withContext(Dispatchers.Main) {
                webView.loadData(mailBody.compressedText ?: mailBody.text, "text/html", "UTF-8")
            }
        }


    }
}
