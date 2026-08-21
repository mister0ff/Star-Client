package com.starclient.app

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.StateListDrawable
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.net.Uri
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

    // Paleta "Red Client"
    private val colorBg = Color.parseColor("#0A0404")
    private val colorGrid = Color.parseColor("#33B40000") // rgba(180,0,0,0.2) aprox p/ grade
    private val colorPanelBg = Color.parseColor("#D90F0303") // rgba(15,3,3,0.85)
    private val colorPanelBorder = Color.parseColor("#3D0808")
    private val colorTitle = Color.parseColor("#FF2222")
    private val colorTitleShadow = Color.parseColor("#300000")
    private val colorSubtitle = Color.parseColor("#777777")
    private val colorBtnBg = Color.parseColor("#3B0A0A")
    private val colorBtnBgHover = Color.parseColor("#571010")
    private val colorBtnBorder = Color.parseColor("#6E1515")
    private val colorBtnText = Color.parseColor("#E0E0E0")
    private val colorFooter = Color.parseColor("#555555")
    private val colorFooterAccent = Color.parseColor("#FF3333")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ---------- Fundo geral com "grade de pixels" ----------
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(48, 48, 48, 48)
            background = buildGridBackground()
        }

        // ---------- Painel central (menu-container) ----------
        val panel = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(60, 80, 60, 60)
            background = buildPanelBackground()
        }

        // Título "RED CLIENT" / pode trocar por "STAR CLIENT"
        val titleText = TextView(this).apply {
            text = "STAR CLIENT"
            setTextColor(colorTitle)
            textSize = 22f
            typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
            setShadowLayer(6f, 3f, 3f, colorTitleShadow)
            gravity = Gravity.CENTER
            letterSpacing = 0.08f
        }

        val subtitleText = TextView(this).apply {
            text = "v1.20.4 • EDITION"
            setTextColor(colorSubtitle)
            textSize = 9f
            typeface = Typeface.MONOSPACE
            gravity = Gravity.CENTER
            setPadding(0, 16, 0, 70)
        }

        // Status (equivalente ao "Logged as Player", usado como feedback dinâmico)
        statusText = TextView(this).apply {
            text = "Star Client Pronto"
            setTextColor(Color.LTGRAY)
            textSize = 10f
            typeface = Typeface.MONOSPACE
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 28)
        }

        // Botão principal estilo "mc-button"
        playButton = Button(this).apply {
            text = "JOGAR"
            textSize = 12f
            typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
            setTextColor(colorBtnText)
            setShadowLayer(2f, 2f, 2f, Color.BLACK)
            isAllCaps = true
            background = buildMcButtonDrawable()
            stateListAnimator = null
            setPadding(40, 36, 40, 36)
            val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            lp.bottomMargin = 20
            layoutParams = lp
        }

        // Rodapé "Logged as Player"
        val footerText = TextView(this).apply {
            text = "STATUS: PRONTO"
            setTextColor(colorFooter)
            textSize = 8f
            typeface = Typeface.MONOSPACE
            gravity = Gravity.CENTER
            setPadding(0, 30, 0, 0)
        }

        // ---------- Loading (mantido, oculto por padrão) ----------
        loadingLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            visibility = View.GONE
        }
        val loadingText = TextView(this).apply {
            text = "CARREGANDO..."
            setTextColor(Color.WHITE)
            textSize = 12f
            typeface = Typeface.MONOSPACE
            setShadowLayer(3f, 2f, 2f, Color.BLACK)
        }
        loadingLayout.addView(loadingText)

        contentLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            visibility = View.VISIBLE
        }
        contentLayout.addView(titleText)
        contentLayout.addView(subtitleText)
        contentLayout.addView(statusText)
        contentLayout.addView(playButton)
        contentLayout.addView(footerText)

        panel.addView(contentLayout)
        panel.addView(loadingLayout)
        root.addView(panel)

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

    // Fundo com "grade" de pixels vermelha sobre preto (simula o background-image do CSS)
    private fun buildGridBackground(): LayerDrawable {
        val baseBg = GradientDrawable().apply {
            setColor(colorBg)
        }

        // Linha vertical fina
        val vLine = GradientDrawable().apply {
            setColor(Color.TRANSPARENT)
            setStroke(2, colorGrid)
        }

        val layers = arrayOf<android.graphics.drawable.Drawable>(baseBg, vLine)
        return LayerDrawable(layers)
    }

    // Painel central com borda escura + fundo semi-transparente (menu-container)
    private fun buildPanelBackground(): GradientDrawable {
        return GradientDrawable().apply {
            setColor(colorPanelBg)
            setStroke(6, colorPanelBorder) // borda grossa tipo bloco
            cornerRadius = 0f
        }
    }

    // Botão com efeito de relevo 3D (inset) que inverte ao pressionar, igual ao CSS
    private fun buildMcButtonDrawable(): StateListDrawable {
        // Estado normal: relevo "para fora"
        val normalBase = GradientDrawable().apply {
            setColor(colorBtnBg)
            setStroke(5, colorBtnBorder)
            cornerRadius = 0f
        }

        // Estado pressionado: cor mais clara + relevo invertido (simulado com cor mais escura no topo)
        val pressedBase = GradientDrawable().apply {
            setColor(Color.parseColor("#2E0505"))
            setStroke(5, Color.parseColor("#A31F1F"))
            cornerRadius = 0f
        }

        val states = StateListDrawable()
        states.addState(intArrayOf(android.R.attr.state_pressed), pressedBase)
        states.addState(intArrayOf(), normalBase)
        return states
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
