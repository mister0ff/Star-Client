package com.starclient.app

import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.starclient.app.core.minecraft.MinecraftActivity

class MainActivity : AppCompatActivity() {

    private lateinit var loadingLayout: LinearLayout
    private lateinit var contentLayout: LinearLayout
    private lateinit var statusText: TextView
    private lateinit var playButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ---------- Layout raiz (fundo estilo "stone") ----------
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(48, 48, 48, 48)
            background = buildStoneBackground()
        }

        // ---------- Loading ----------
        loadingLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            visibility = View.GONE
        }
        val loadingText = TextView(this).apply {
            text = "CARREGANDO..."
            setTextColor(Color.WHITE)
            textSize = 18f
            typeface = Typeface.MONOSPACE
            setShadowLayer(4f, 2f, 2f, Color.BLACK)
        }
        loadingLayout.addView(loadingText)

        // ---------- Conteúdo principal ----------
        contentLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            visibility = View.VISIBLE
        }

        // Título estilo "Minecraft"
        val titleText = TextView(this).apply {
            text = "STAR CLIENT"
            setTextColor(Color.parseColor("#55FF55")) // verde grama
            textSize = 32f
            typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
            setShadowLayer(6f, 3f, 3f, Color.parseColor("#1B1B1B"))
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 64)
        }

        // Status
        statusText = TextView(this).apply {
            text = "Star Client Pronto"
            setTextColor(Color.LTGRAY)
            textSize = 14f
            typeface = Typeface.MONOSPACE
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 32)
        }

        // Botão estilo bloco do Minecraft (grama/madeira)
        playButton = Button(this).apply {
            text = "JOGAR"
            textSize = 18f
            typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
            setTextColor(Color.WHITE)
            setShadowLayer(3f, 2f, 2f, Color.BLACK)
            isAllCaps = true
            background = buildMinecraftButtonDrawable()
            stateListAnimator = null // remove sombra material padrão
            val paddingH = 64
            val paddingV = 28
            setPadding(paddingH, paddingV, paddingH, paddingV)
        }

        contentLayout.addView(titleText)
        contentLayout.addView(statusText)
        contentLayout.addView(playButton)

        root.addView(loadingLayout)
        root.addView(contentLayout)

        setContentView(root)

        // ---------- Lógica original ----------
        checkAndRequestStoragePermission()

        loadingLayout.visibility = View.GONE
        contentLayout.visibility = View.VISIBLE
        statusText.text = "Star Client Pronto"

        playButton.setOnClickListener {
            if (!isMinecraftInstalled()) {
                Toast.makeText(this, "Erro: Minecraft Bedrock não está instalado!", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if (!hasStoragePermission()) {
                Toast.makeText(this, "Conceda a permissão de armazenamento!", Toast.LENGTH_SHORT).show()
                checkAndRequestStoragePermission()
                return@setOnClickListener
            }

            val intent = Intent(this, MinecraftActivity::class.java)
            startActivity(intent)
        }
    }

    // Fundo em degradê "pedra" escura, tipo menu do Minecraft
    private fun buildStoneBackground(): GradientDrawable {
        return GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(
                Color.parseColor("#2B2B2B"),
                Color.parseColor("#1A1A1A")
            )
        )
    }

    // Botão com borda em relevo (efeito de bloco 3D pixelado)
    private fun buildMinecraftButtonDrawable(): LayerDrawable {
        val base = GradientDrawable().apply {
            setColor(Color.parseColor("#6B8E3D")) // verde grama
            cornerRadius = 0f // sem arredondamento = visual "quadrado/pixelado"
        }

        val border = GradientDrawable().apply {
            setColor(Color.TRANSPARENT)
            setStroke(6, Color.parseColor("#3A5220")) // borda verde escura
            cornerRadius = 0f
        }

        val layers = arrayOf<android.graphics.drawable.Drawable>(base, border)
        val layerDrawable = LayerDrawable(layers)
        return layerDrawable
    }

    private fun isMinecraftInstalled(): Boolean {
        return try {
            packageManager.getPackageInfo("com.mojang.minecraftpe", 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    private fun hasStoragePermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            true
        }
    }

    private fun checkAndRequestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager()) {
            try {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                    data = Uri.parse("package:$packageName")
                }
                startActivity(intent)
            } catch (e: Exception) {
                val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                startActivity(intent)
            }
        }
    }
}
