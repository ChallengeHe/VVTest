@file:JvmName("GLResUtils")

package com.vv3d.vvtest

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLES20
import android.opengl.GLES20.*
import android.opengl.GLUtils.texImage2D
import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.RuntimeException

/**
 * 岑胜德 on 2020/7/22
 * 说明：由于opengl内部对异常不采取中断程序的处理，这样不方便我们定位异常，
 *     故我们封装函数并主动捕获异常。
 */
private val TAG = "getShaderCode"

//读取着色器代码
fun getShaderCode(context: Context, resId: Int): String {
    return try {
        val builder = StringBuilder()
        val inputStreams = context.resources.openRawResource(resId)
        val reader = InputStreamReader(inputStreams)
        val bufferedReader = BufferedReader(reader)
        var nextLine: String?
        do {
            nextLine = bufferedReader.readLine()
            if (nextLine == null) {
                bufferedReader.close()
                reader.close()
                inputStreams.close()
                break
            }
            builder.append(nextLine)
            builder.append('\n')
        } while (true)
        builder.toString()
    } catch (err: Exception) {
        Log.e(TAG, "异常：$err")
        " "
    }

}

//编译顶点着色器
fun compileVertexShader(shaderCode: String): Int {
    val id = compileShader(GL_VERTEX_SHADER, shaderCode)
    if (id == 0) {
        throw RuntimeException("compileVertexShader 异常：shaderId==$id ")
    }
    return id
}

//编译片段着色器
fun compileFragmentShader(shaderCode: String): Int {
    val id = compileShader(GL_FRAGMENT_SHADER, shaderCode)
    if (id == 0) {
        throw RuntimeException("compileFragmentShader 异常：shaderId==$id ")
    }
    return id
}

private fun compileShader(type: Int, code: String): Int {
    val shaderId = GLES20.glCreateShader(type)
    GLES20.glShaderSource(shaderId, code)//关联着色器代码
    GLES20.glCompileShader(shaderId)//编译着色器
    val status = IntArray(1)
    //参数2:表示要取得编译状态；参数4:表示将状态放在status数组的第0个元素位置。
    GLES20.glGetShaderiv(shaderId, GLES20.GL_COMPILE_STATUS, status, 0)
    GLES20.glGetShaderInfoLog(shaderId)//获取编译状态
    if (status[0] == 0) {
        GLES20.glDeleteShader(shaderId)
        Log.e("compileShader:", "着色器编译失败，status[0] ==${status[0]}")
        return 0
    }
    return shaderId

}

//链接着色器程序
fun linkProgram(vertShaderId: Int, fragShaderId: Int): Int {
    val programId = GLES20.glCreateProgram()
    if (programId == 0) {
        Log.e("glCreateProgram:", "异常：programId==0")
        throw RuntimeException("glCreateProgram 异常")
    }
    GLES20.glAttachShader(programId, vertShaderId)
    GLES20.glAttachShader(programId, fragShaderId)
    GLES20.glLinkProgram(programId)

    val status = IntArray(1)
    GLES20.glGetProgramiv(programId, GLES20.GL_LINK_STATUS, status, 0)
    GLES20.glGetProgramInfoLog(programId)
    if (status[0] == 0) {
        GLES20.glDeleteProgram(programId)
        throw RuntimeException("链接着色器程序失败")
    }
    glUseProgram(programId)
//    GLES20.glValidateProgram(programId)
    return programId
}
/**
 * 加载纹理，返回纹理id
 */
fun loadTexture(context: Context, resId: Int): Int {
    val textureObjectId = IntArray(1)
    GLES20.glGenTextures(1, textureObjectId, 0)
    if (textureObjectId[0] == 0) {
        throw RuntimeException("生成纹理对象失败")
    }
    val option = BitmapFactory.Options()
    option.inScaled = false

    val bitmap = BitmapFactory.decodeResource(context.resources, resId, option)
    if (bitmap == null) {
        GLES20.glDeleteTextures(1, textureObjectId, 0)
        throw RuntimeException("加载纹理bitmap失败")
    }
    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureObjectId[0])
    //参数2：缩小/放大情况，参数3：过滤模式
    GLES20.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR)
    GLES20.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
    texImage2D(GL_TEXTURE_2D, 0, bitmap, 0)//内部对bitmap数据进行了复制
    bitmap.recycle()//所以不需要bitmap了
    glGenerateMipmap(GL_TEXTURE_2D)
    glBindTexture(GL_TEXTURE_2D,0)//既然已经生成了纹理，就可以解绑了
    return textureObjectId[0]
}
fun loadTexture(bitmap: Bitmap): Int{
    val textureObjectId = IntArray(1)
    GLES20.glGenTextures(1, textureObjectId, 0)
    if (textureObjectId[0] == 0) {
        throw RuntimeException("生成纹理对象失败")
    }
    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureObjectId[0])
    //参数2：缩小/放大情况，参数3：过滤模式
    GLES20.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR)
    GLES20.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
    texImage2D(GL_TEXTURE_2D, 0, bitmap, 0)//内部对bitmap数据进行了复制
    bitmap.recycle()//所以不需要bitmap了
    glGenerateMipmap(GL_TEXTURE_2D)
    glBindTexture(GL_TEXTURE_2D,0)//既然已经生成了纹理，就可以解绑了
    return textureObjectId[0]
}