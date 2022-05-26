package com.vv3d.vvtest

import android.util.Log
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

internal object NLog {
    private const val TAG = "NLog"

    private const val MIN_STACK_OFFSET = 3

    /**
     * Drawing toolbox
     */
    private const val TOP_LEFT_CORNER = '╔'
    private const val BOTTOM_LEFT_CORNER = '╚'
    private const val MIDDLE_CORNER = '╟'
    private const val HORIZONTAL_DOUBLE_LINE = '║'
    private const val DOUBLE_DIVIDER = "════════════════════════════════════════════"
    private const val SINGLE_DIVIDER = "────────────────────────────────────────────"
    private val TOP_BORDER = TOP_LEFT_CORNER + DOUBLE_DIVIDER + DOUBLE_DIVIDER
    private val BOTTOM_BORDER = BOTTOM_LEFT_CORNER + DOUBLE_DIVIDER + DOUBLE_DIVIDER
    private val MIDDLE_BORDER = MIDDLE_CORNER + SINGLE_DIVIDER + SINGLE_DIVIDER

    /**
     * It is used for json pretty print
     */
    private const val JSON_INDENT = 2

    private fun getStackOffset(trace: Array<StackTraceElement>): Int {
        var i = MIN_STACK_OFFSET
        while (i < trace.size) {
            val e = trace[i]
            val name = e.className
            if (name != NLog::class.java.name) {
                return --i
            }
            i++
        }
        return -1
    }

    private fun generateTag(): String {
        val caller = Throwable().stackTrace[2]
        Log.e(TAG, caller.toString())
        var tag = "%s.%s(L:%d)"
        var callerClazzName = caller.className
        callerClazzName = callerClazzName.substring(callerClazzName.lastIndexOf(".") + 1)
        tag = "$TAG:${String.format(tag, callerClazzName, caller.methodName, caller.lineNumber)}"
        return tag
    }

    private fun getMethodNames(): String {
        val sElements = Thread.currentThread().stackTrace

        var stackOffset = getStackOffset(sElements)

        stackOffset++
        val builder = StringBuilder()
        builder.append("\r\n").append(TOP_BORDER).append("\r\n")
            // 添加当前线程名
            .append("║ " + "Thread: " + Thread.currentThread().name).append("\r\n")
            .append(MIDDLE_BORDER).append("\r\n")
            // 添加类名、方法名、行数
            .append("║ ")
            .append(sElements[stackOffset].className)
            .append(".")
            .append(sElements[stackOffset].methodName)
            .append(" ")
            .append(" (")
            .append(sElements[stackOffset].fileName)
            .append(":")
            .append(sElements[stackOffset].lineNumber)
            .append(")")
            .append("\r\n")
            .append(MIDDLE_BORDER).append("\r\n")
            // 添加打印的日志信息
            .append("║ ").append("%s").append("\r\n")
            .append(BOTTOM_BORDER).append("\r\n")
        return builder.toString()
    }

    @JvmStatic
    fun v(content: String) {
        v(null, content)
    }

    @JvmStatic
    @JvmOverloads
    fun v(tag: String?, content: String, printFullMsg: Boolean = false, tr: Throwable? = null) {
        if (!LoggerConfig.ON) return
        var mTag = tag
        if (mTag.isNullOrEmpty()) {
            mTag = TAG
        }
        if (tr == null)
            Log.v(mTag, if (printFullMsg) String.format(getMethodNames(), content) else content)
        else
            Log.v(mTag, if (printFullMsg) String.format(getMethodNames(), content) else content, tr)
    }

    @JvmStatic
    fun d(content: String) {
        d(null, content)
    }

    @JvmStatic
    @JvmOverloads
    fun d(tag: String?, content: String, printFullMsg: Boolean = false, tr: Throwable? = null) {
        if (!LoggerConfig.ON) return
        var mTag = tag
        if (mTag.isNullOrEmpty()) {
            mTag = TAG
        }
        if (tr == null)
            Log.d(mTag, if (printFullMsg) String.format(getMethodNames(), content) else content)
        else
            Log.d(mTag, if (printFullMsg) String.format(getMethodNames(), content) else content, tr)
    }

    @JvmStatic
    @JvmOverloads
    fun i(content: String, printFullMsg: Boolean = false) {
        i(null, content, printFullMsg)
    }

    @JvmStatic
    @JvmOverloads
    fun i(tag: String?, content: String, printFullMsg: Boolean = false, tr: Throwable? = null) {
        if (!LoggerConfig.ON) return
        var mTag = tag
        if (mTag.isNullOrEmpty()) {
            mTag = TAG
        }
        if (tr == null)
            Log.i(mTag, if (printFullMsg) String.format(getMethodNames(), content) else content)
        else
            Log.i(mTag, if (printFullMsg) String.format(getMethodNames(), content) else content, tr)
    }

    @JvmStatic
    fun w(content: String) {
        w(null, content)
    }

    @JvmStatic
    @JvmOverloads
    fun w(tag: String?, content: String, printFullMsg: Boolean = false, tr: Throwable? = null) {
        if (!LoggerConfig.ON) return
        var mTag = tag
        if (mTag.isNullOrEmpty()) {
            mTag = TAG
        }
        if (tr == null)
            Log.w(mTag, if (printFullMsg) String.format(getMethodNames(), content) else content)
        else
            Log.w(mTag, if (printFullMsg) String.format(getMethodNames(), content) else content, tr)
    }

    @JvmStatic
    fun e(content: String) {
        e(null, content)
    }

    @JvmStatic
    @JvmOverloads
    fun e(tag: String?, content: String, printFullMsg: Boolean = false, tr: Throwable? = null) {
        if (!LoggerConfig.ON) return
        var mTag = tag
        if (mTag.isNullOrEmpty()) {
            mTag = TAG
        }
        if (tr == null)
            Log.e(mTag, if (printFullMsg) String.format(getMethodNames(), content) else content)
        else
            Log.e(mTag, if (printFullMsg) String.format(getMethodNames(), content) else content, tr)
    }

    @JvmStatic
    fun json(json: String) {
        if (!LoggerConfig.ON) return
        var jsonStr = json

        if (jsonStr.isBlank()) {
            d("Empty/Null json content")
            return
        }

        try {
            jsonStr = jsonStr.trim { it <= ' ' }
            if (jsonStr.startsWith("{")) {
                val jsonObject = JSONObject(jsonStr)
                var message = jsonObject.toString(JSON_INDENT)
                message = message.replace("\n".toRegex(), "\n║ ")
                val s = getMethodNames()
                println(String.format(s, message))
                return
            }
            if (jsonStr.startsWith("[")) {
                val jsonArray = JSONArray(jsonStr)
                var message = jsonArray.toString(JSON_INDENT)
                message = message.replace("\n".toRegex(), "\n║ ")
                val s = getMethodNames()
                println(String.format(s, message))
                return
            }
            e("Invalid Json")
        } catch (e: JSONException) {
            e("Invalid Json")
        }
    }

    @JvmStatic
    @JvmOverloads
    fun byteArray(byteArray: ByteArray?, printFullMsg: Boolean = false) {
        if (!LoggerConfig.ON) return
        if (byteArray == null) return
        val iMax: Int = byteArray.size - 1
        if (iMax == -1) return

        val sb = StringBuilder()
        sb.append("[")

        for (i in 0..iMax) {
            val hexString = Integer.toHexString(byteArray[i].toInt() and 0xFF)
            if (hexString.length < 2) {
                sb.append(0)
            }
            sb.append(hexString)
            if (i == iMax) sb.append("]")
        }
        Log.i(
            TAG,
            if (printFullMsg) String.format(getMethodNames(), sb.toString()) else sb.toString()
        )
    }

}