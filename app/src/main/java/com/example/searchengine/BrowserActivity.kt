package com.example.searchengine

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity


class BrowserActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_browser)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        val Browse_Url = intent.getStringExtra("Browse_Url")
        var myWebView: WebView = findViewById(R.id.browse_url)
        myWebView.setWebViewClient(WebViewClient())
        myWebView.loadUrl(Browse_Url.toString())

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.getItemId()) {
            R.id.about_app -> {
                aboutApp()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun aboutApp() {
        val aboutPage = Intent(this, AppInfoActivity::class.java).apply {}
        startActivity(aboutPage)
    }
}