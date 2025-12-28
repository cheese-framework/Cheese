package net.codeocean.cheese.core.activity

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import com.elvishew.xlog.XLog
import net.codeocean.cheese.core.CoreEnv

class IPCActivity : Activity()  {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
// 透明窗口
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        // 隐藏所有系统 UI
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                )
        // 空视图
        setContentView(View(this))
        XLog.i(/* msg = */ "运行进程Activity："+CoreEnv.envContext.activity)
        moveTaskToBack(true)

//        setContentView(R.layout.activity_transparent) // 可加载布局（透明背景）
    }
    override fun onBackPressed() {
        moveTaskToBack(true) // 移至后台，不销毁
    }
}