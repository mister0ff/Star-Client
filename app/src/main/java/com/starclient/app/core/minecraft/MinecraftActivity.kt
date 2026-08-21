package com.starclient.app.core.minecraft

import android.app.NativeActivity
import android.os.Bundle

class MinecraftActivity : NativeActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    companion object {
        init {
            val libs = listOf(
                "c++_shared",           // Dependência C++ do NDK
                "PlayFabMultiplayer",
                "maesdk",
                "inbuiltmods",
                "gxcore"                // O núcleo do preloader por último
            )

            for (lib in libs) {
                try {
                    System.loadLibrary(lib)
                } catch (e: Throwable) {
                    // Loga caso alguma biblioteca falhe no carregamento
                    e.printStackTrace()
                }
            }
        }
    }
}
