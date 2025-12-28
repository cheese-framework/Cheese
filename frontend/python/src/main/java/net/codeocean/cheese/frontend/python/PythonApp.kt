package net.codeocean.cheese.frontend.python

import android.content.res.AssetManager
import com.elvishew.xlog.XLog
import net.codeocean.cheese.core.CoreApp
import net.codeocean.cheese.core.Misc.isMainProcess

class PythonApp: CoreApp() {

    override fun onCreate() {
        super.onCreate()
    }



    override fun getAssets(): AssetManager {
//        if (!isMainProcess()) {
//            XLog.e("不是主进程")
//            return  resources1.assets
//        }
//        XLog.e("是主进程")
//        return super.getAssets()
        XLog.e("资源："+resources1?.assets)
        return resources1?.assets ?: super.getAssets()
    }
}