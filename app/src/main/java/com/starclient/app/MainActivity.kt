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
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    // Substitua pelo Package Name exato da versão modded instalada no dispositivo
    private var MODDED_PACKAGE = "com.starclient.minecraft" 
    private val SPLASH_DELAY_MS = 1600L
    private lateinit var configFile: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val loadingLayout = findViewById<LinearLayout>(R.id.loadingLayout)
        val contentLayout = findViewById<LinearLayout>(R.id.contentLayout)
        val statusText = findViewById<TextView>(R.id.statusText)
        val playButton = findViewById<Button>(R.id.playButton)

        checkStoragePermissionAndSetup()

        Handler(Looper.getMainLooper()).postDelayed({
            loadingLayout.visibility = View.GONE
            contentLayout.visibility = View.VISIBLE

            if (isModdedMinecraftInstalled()) {
                statusText.text = "Minecraft Modded Detectado ✔"
                playButton.isEnabled = true
            } else {
                statusText.text = "Versão Modded não instalada"
                playButton.isEnabled = false
            }
        }, SPLASH_DELAY_MS)

        playButton.setOnClickListener {
            launchModdedMinecraft()
        }
    }

    private fun checkStoragePermissionAndSetup() {
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
                return
            }
        }
        setupIndependentStorage()
    }

    private fun setupIndependentStorage() {
        try {
            val baseDir = File(Environment.getExternalStorageDirectory(), "Android/media/starclient")
            if (!baseDir.exists()) baseDir.mkdirs()

            val configDir = File(baseDir, "config")
            if (!configDir.exists()) configDir.mkdirs()

            configFile = File(configDir, "settings.json")
            if (!configFile.exists()) {
                val defaultConfig = JSONObject().apply {
                    put("moddedPackageName", MODDED_PACKAGE)
                    put("clientVersion", "1.0-modded")
                }
                configFile.writeText(defaultConfig.toString(4))
            } else {
                val json = JSONObject(configFile.readText())
                MODDED_PACKAGE = json.optString("moddedPackageName", MODDED_PACKAGE)
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Erro ao carregar configurações", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isModdedMinecraftInstalled(): Boolean {
        return try {
            packageManager.getPackageInfo(MODDED_PACKAGE, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            packageManager.getLaunchIntentForPackage(MODDED_PACKAGE) != null
        }
    }

    private fun launchModdedMinecraft() {
        val intent = packageManager.getLaunchIntentForPackage(MODDED_PACKAGE)
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        } else {
            Toast.makeText(this, "Não foi possível iniciar a versão modded", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && Environment.isExternalStorageManager()) {
            setupIndependentStorage()
        }
    }
}
