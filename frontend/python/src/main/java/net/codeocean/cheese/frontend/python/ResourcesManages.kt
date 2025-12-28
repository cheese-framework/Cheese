package net.codeocean.cheese.frontend.python

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.AssetManager
import android.content.res.Resources

 var resources1: Resources?=null
class ResourcesManages {

    @SuppressLint("PrivateApi")
    fun load(apkFilePath: String,context: Context){
        try {
            val assetManager = AssetManager::class.java.newInstance()
            AssetManager::class.java.getDeclaredMethod("addAssetPath", String::class.java).invoke(
                assetManager, apkFilePath
            )
            resources1 = Resources(
                assetManager,
                context.resources.displayMetrics,
                context.resources.configuration
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}