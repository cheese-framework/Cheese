package net.codeocean.cheese.backend.impl

import android.content.Context
import android.util.Base64
import net.codeocean.cheese.core.BaseEnv
import net.codeocean.cheese.core.api.PersistentStore

object PersistentStoreImpl : PersistentStore, BaseEnv {
    private const val BASE64_PREFIX = "base64:"


    override fun save(name: String, key: String, value: Any) {
        val sharedPref = cx.getSharedPreferences(name, Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        when(value) {
            is String  -> editor.putString(key, value)
            is Int  -> editor.putInt(key, value)
            is Boolean -> editor.putBoolean(key, value)
            is ByteArray -> {
                val encodedValue = Base64.encodeToString(value, Base64.DEFAULT)
                editor.putString(key, BASE64_PREFIX + encodedValue)
            }
            else -> return
        }
        editor.apply()
    }

    override fun rm(name: String, key: String) {
        val sharedPref = cx.getSharedPreferences(name, Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.remove(key)
        editor.apply()
    }

    override fun get(name: String, key: String): Any? {
        val sharedPref = cx.getSharedPreferences(name, Context.MODE_PRIVATE)
        if (!sharedPref.contains(key)) return null

        val all = sharedPref.all
        val value = all[key] ?: return null
        return when (value) {
            is String -> {
                if (value.startsWith(BASE64_PREFIX)) {
                    try {
                        val decoded = Base64.decode(value, Base64.DEFAULT)
                        if (decoded.isNotEmpty()) decoded else value
                    } catch (_: IllegalArgumentException) {
                        value
                    }
                } else {
                    value
                }
            }
            is Int -> value
            is Boolean -> value
            is Float -> value
            is Long -> value
            else -> null
        }
    }


}