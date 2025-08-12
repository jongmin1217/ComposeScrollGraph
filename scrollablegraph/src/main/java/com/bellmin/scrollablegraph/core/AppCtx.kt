package com.bellmin.scrollablegraph.core

import android.annotation.SuppressLint
import android.content.Context


@SuppressLint("StaticFieldLeak")
object AppCtx {
    @Volatile private var _app: Context? = null
    val app: Context
        get() = _app ?: error("AppCtx not initialized. Check InitProvider/Manifest.")

    internal fun init(context: Context) {
        _app = context.applicationContext
    }
}