package com.starclient.app.core.minecraft

import android.app.NativeActivity
import android.os.Bundle

class MinecraftActivity : NativeActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    companion object {
        init {
            try {
                System.loadLibrary("gxcore")
            } catch (e: UnsatisfiedLinkError) {
                e.printStackTrace()
            }
        }
    }
}
