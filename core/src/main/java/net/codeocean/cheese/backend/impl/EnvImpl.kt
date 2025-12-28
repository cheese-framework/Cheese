package net.codeocean.cheese.backend.impl

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.AssetManager
import net.codeocean.cheese.core.BaseEnv
import net.codeocean.cheese.core.CoreFactory
import net.codeocean.cheese.core.api.Assets

import net.codeocean.cheese.core.api.Env
import net.codeocean.cheese.core.runtime.ScriptExecutionController
import net.codeocean.cheese.core.service.AccessibilityService
import java.util.concurrent.ConcurrentHashMap

object EnvImpl : Env, BaseEnv {
    override val context: Context
        get() = cx
    override val version: String
        get() = getAppVersion()
    override val hotVersion: String
        get() = getHOTVersion()
    override val settings: MutableMap<String, Any>
        get() = ConcurrentHashMap<String, Any>().apply {}
    override val activity: Activity?
        get() = act

    fun getHOTVersion(): String{
        val result = ScriptExecutionController.parseCheeseToml(CoreFactory.getPath().WORKING_DIRECTORY)
        val version = result?.getTable("build")
            ?.getTable("hot")
            ?.getString("version").orEmpty()
        return version
    }
    fun getAppVersion(): String {
        return try {
            val packageInfo = cx.packageManager.getPackageInfo(context.packageName, 0)
            // 返回版本名称（如 "1.2.0"）
            packageInfo.versionName?:"0.0.0"
        } catch (e: PackageManager.NameNotFoundException) {
            "0.0.0" // 默认值（理论上不会触发）
        }
    }

}