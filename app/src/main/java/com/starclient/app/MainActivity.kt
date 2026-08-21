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
import androidx.core.content.FileProvider
import java.io.File
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private var moddedPackageName = "com.starclient.minecraft"
    private val splashDelayMs = 1600L
    private lateinit var configFile: File
    private lateinit var baseDir: File

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
                playButton.text = "JOGAR"
            } else {
                val apkFile = File(baseDir, "starclient.apk")
                if (apkFile.exists()) {
                    statusText.text = "APK do jogo encontrado na pasta"
                    playButton.isEnabled = true
                    playButton.text = "INSTALAR JOGO"
                } else {
                    statusText.text = "Coloque o arquivo 'starclient.apk' em:\n/Android/media/starclient/"
                    playButton.isEnabled = false
                    playButton.text = "JOGO NÃO ENCONTRADO"
                }
            }
        }, splashDelayMs)

        playButton.setOnClickListener {
            if (isModdedMinecraftInstalled()) {
                launchModdedMinecraft()
            } else {
                val apkFile = File(baseDir, "starclient.apk")
                if (apkFile.exists()) {
                    installApk(apkFile)
                } else {
                    Toast.makeText(this, "Arquivo starclient.apk não encontrado", Toast.LENGTH_SHORT).show()
                }
            }
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
            baseDir = File(Environment.getExternalStorageDirectory(), "Android/media/starclient")
            if (!baseDir.exists()) {
                baseDir.mkdirs()
            }

            val configDir = File(baseDir, "config")
            if (!configDir.exists()) {
                configDir.mkdirs()
            }

            val packsDir = File(baseDir, "packs")
            if (!packsDir.exists()) {
                packsDir.mkdirs()
            }

            configFile = File(configDir, "settings.json")
            if (!configFile.exists()) {
                val defaultConfig = JSONObject().apply {
                    put("moddedPackageName", moddedPackageName)
                    put("clientVersion", "1.0-modded")
                    put("autoLaunch", false)
                }
                configFile.writeText(defaultConfig.toString(4))
            } else {
                val json = JSONObject(configFile.readText())
                moddedPackageName = json.optString("moddedPackageName", moddedPackageName)
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Erro ao inicializar diretórios", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isModdedMinecraftInstalled(): Boolean {
        return try {
            packageManager.getPackageInfo(moddedPackageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            packageManager.getLaunchIntentForPackage(moddedPackageName) != null
        }
    }

    private fun launchModdedMinecraft() {
        val intent = packageManager.getLaunchIntentForPackage(moddedPackageName)
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        } else {
            Toast.makeText(this, "Não foi possível iniciar o pacote: $moddedPackageName", Toast.LENGTH_SHORT).show()
        }
    }

    private fun installApk(apkFile: File) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !packageManager.canRequestPackageInstalls()) {
            val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).apply {
                data = Uri.parse("package:$packageName")
            }
            startActivity(intent)
            Toast.makeText(this, "Conceda permissão para instalar APKs e tente novamente", Toast.LENGTH_LONG).show()
            return
        }

        try {
            val apkUri = FileProvider.getUriForFile(this, "$packageName.provider", apkFile)
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(apkUri, "application/vnd.android.package-archive")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Erro ao abrir o APK: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && Environment.isExternalStorageManager()) {
            setupIndependentStorage()
        }
    }
}
