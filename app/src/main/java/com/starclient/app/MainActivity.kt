package com.starclient.app

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val splashDelayMs = 1600L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val loadingLayout = findViewById<LinearLayout>(R.id.loadingLayout)
        val contentLayout = findViewById<LinearLayout>(R.id.contentLayout)
        val statusText = findViewById<TextView>(R.id.statusText)
        val playButton = findViewById<Button>(R.id.playButton)

        checkStoragePermission()

        Handler(Looper.getMainLooper()).postDelayed({
            loadingLayout.visibility = View.GONE
            contentLayout.visibility = View.VISIBLE

            statusText.text = "Star Client Embutido ✔"
            playButton.isEnabled = true
            playButton.text = "JOGAR"
        }, splashDelayMs)

        playButton.setOnClickListener {
            launchInternalGame()
        }
    }

    private fun checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                try {
                    val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                        data = Uri.parse("package:$packageName")
                    }
                    startActivity(intent)
                } catch (e: Exception) {
                    startActivity(Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION))
                }
            }
        }
    }

    private fun launchInternalGame() {
        try {
            val intent = Intent().apply {
                setClassName(packageName, "com.mojang.minecraftpe.MainActivity")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Falha ao iniciar o jogo interno: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
