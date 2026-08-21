package com.starclient.app

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class MainActivity : AppCompatActivity() {

    private val MINECRAFT_PACKAGE = "com.mojang.minecraftpe"
    private val SPLASH_DELAY_MS = 1600L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val loadingLayout = findViewById<android.widget.LinearLayout>(R.id.loadingLayout)
        val contentLayout = findViewById<android.widget.LinearLayout>(R.id.contentLayout)
        val statusText = findViewById<TextView>(R.id.statusText)
        val playButton = findViewById<Button>(R.id.playButton)

        checkStoragePermissionAndSetup()

        Handler(Looper.getMainLooper()).postDelayed({
            loadingLayout.visibility = android.view.View.GONE
            contentLayout.visibility = android.view.View.VISIBLE

            if (isMinecraftInstalled()) {
                statusText.text = "Minecraft Bedrock encontrado ✔"
                playButton.isEnabled = true
            } else {
                statusText.text = "Minecraft Bedrock não está instalado"
                playButton.isEnabled = false
            }
        }, SPLASH_DELAY_MS)

        playButton.setOnClickListener {
            launchMinecraft()
        }
    }

    private fun checkStoragePermissionAndSetup() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                try {
                    val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                    intent.data = Uri.parse("package:$packageName")
                    startActivity(intent)
                } catch (e: Exception) {
                    startActivity(Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION))
                }
                return
            }
        }
        setupIndependentStorage()
    }

    /**
     * Cria a pasta independente: /storage/emulated/0/Android/media/starclient/
     */
    private fun setupIndependentStorage() {
        try {
            val baseDir = File(Environment.getExternalStorageDirectory(), "Android/media/starclient")
            if (!baseDir.exists()) baseDir.mkdirs()

            val configDir = File(baseDir, "config")
            if (!configDir.exists()) configDir.mkdirs()
        } catch (e: Exception) {
            Toast.makeText(this, "Não foi possível criar a pasta do app", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && Environment.isExternalStorageManager()) {
            setupIndependentStorage()
        }
    }

    private fun isMinecraftInstalled(): Boolean {
        val foundByPackageInfo = try {
            packageManager.getPackageInfo(MINECRAFT_PACKAGE, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
        if (foundByPackageInfo) return true
        return packageManager.getLaunchIntentForPackage(MINECRAFT_PACKAGE) != null
    }

    private fun launchMinecraft() {
        val intent = packageManager.getLaunchIntentForPackage(MINECRAFT_PACKAGE)
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        } else {
            Toast.makeText(this, "Não foi possível abrir o Minecraft", Toast.LENGTH_SHORT).show()
        }
    }
}
