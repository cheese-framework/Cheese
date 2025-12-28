package net.codeocean.cheese.frontend.python

import com.elvishew.xlog.XLog
import net.codeocean.cheese.core.aidl.AIDLService.Companion.iPythonCallback

object Log {

    fun i(msg: String) {

        iPythonCallback?.i(msg)

    }

    fun d(msg: String)  {

        iPythonCallback?.d(msg)
    }

    fun w(msg: String)  {

        iPythonCallback?.w(msg)
    }

    fun e(msg: String) {

        iPythonCallback?.e(msg)
    }


}