package com.vv3d.vvtest

import android.content.Context
import android.content.res.Configuration
import android.opengl.GLES20
import android.util.Log
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.tan

private const val TAG = "OpenGLUtils"

class OpenGLUtils {
    companion object {
        fun getFloatBuffer(ver: FloatArray): FloatBuffer? {
            val vbb = ByteBuffer.allocateDirect(ver.size * 4)
            vbb.order(ByteOrder.nativeOrder())
            val buffer = vbb.asFloatBuffer()
            buffer.put(ver)
            buffer.position(0)
            return buffer
        }

        fun createProgram(vertexShaderCode: String?, fragmentShaderCode: String?): Int {
            val vertexShader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER)
            Log.i(TAG, "------------createProgram vertexShader=$vertexShader")
            GLES20.glShaderSource(vertexShader, vertexShaderCode)
            GLES20.glCompileShader(vertexShader)
            // 存放编译成功shader数量的数组
            val compiled = IntArray(1)
            // 获取Shader的编译情况
            GLES20.glGetShaderiv(vertexShader, GLES20.GL_COMPILE_STATUS, compiled, 0)
            Log.i(TAG, "------------createProgram compiled[0]=" + compiled[0])
            if (compiled[0] == 0) {
                //若编译失败则显示错误日志并删除此shader
                Log.e(TAG, "Could not compile shader vertexShader:")
                Log.e(TAG, GLES20.glGetShaderInfoLog(vertexShader))
                GLES20.glDeleteShader(vertexShader)
            }
            val fragmentShader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER)
            GLES20.glShaderSource(fragmentShader, fragmentShaderCode)
            GLES20.glCompileShader(fragmentShader)
            Log.i(TAG, "------------createProgram fragmentShader=$fragmentShader")
            // 存放编译成功shader数量的数组
            val compiled2 = IntArray(1)
            // 获取Shader的编译情况
            GLES20.glGetShaderiv(fragmentShader, GLES20.GL_COMPILE_STATUS, compiled2, 0)
            Log.i(TAG, "------------createProgram compiled2[0]=" + compiled2[0])
            if (compiled2[0] == 0) {
                //若编译失败则显示错误日志并删除此shader
                Log.i(TAG, "Could not compile shader fragmentShader:")
                Log.i(TAG, GLES20.glGetShaderInfoLog(fragmentShader))
                GLES20.glDeleteShader(fragmentShader)
            }
            val program = GLES20.glCreateProgram()
            checkGlError("glCreateProgram")
            if (program != 0) {
                GLES20.glAttachShader(program, vertexShader)
                checkGlError("glAttachShader")
                GLES20.glAttachShader(program, fragmentShader)
                checkGlError("glAttachShader")
                GLES20.glLinkProgram(program)
                checkGlError("glLinkProgram")
            }
            Log.i(TAG, "------------createProgram program=$program")
            return program
        }

        fun checkGlError(op: String) {
            var error: Int
            while (GLES20.glGetError().also { error = it } != GLES20.GL_NO_ERROR) {
                Log.e(TAG, "$op: glError $error")
                throw RuntimeException("$op: glError $error")
            }
        }

        fun loadTextureForBlackWhiteImg(context: Context, mWidth: Int, mHeight: Int): Int {
            val textures = IntArray(1)
            GLES20.glGenTextures(1, textures, 0)
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0])
            // 设置2D纹理通道当前绑定的纹理的属性
            GLES20.glTexParameterf(
                GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_NEAREST.toFloat()
            )
            GLES20.glTexParameterf(
                GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR.toFloat()
            )
            Log.d(TAG,"loadTextureForBlackWhiteImg width=$mWidth, height=$mHeight")
            val len: Int = mWidth * mHeight * 4
            var data = ByteArray(len)
            val mConfiguration = context.resources.configuration //获取设置的配置信息
            val ori = mConfiguration.orientation //获取屏幕方向
            var isLANDSCAPE = ori == Configuration.ORIENTATION_LANDSCAPE
            var lineRatio = m_LineRatio
            if (!isLANDSCAPE) {
                lineRatio += 270.0f
            }
            generateBlackWhiteImgData(
                data,
                mWidth,
                mHeight,
                lineRatio,
                m_LineWidth
            )
            val dataBuffer: ByteBuffer = BufferUtil.createByteBuffer(data)

            GLES20.glTexImage2D(
                GLES20.GL_TEXTURE_2D,
                0,
                GLES20.GL_RGBA,  /*GLES20.GL_RGBA8*/
                mWidth,
                mHeight,
                0,
                GLES20.GL_RGBA,
                GLES20.GL_UNSIGNED_BYTE,
                dataBuffer
            )
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
            dataBuffer.clear()
            return textures[0]
        }

        private fun generateBlackWhiteImgData(
            data: ByteArray,
            width: Int,
            height: Int,
            lineRatio: Float,
            lineWidth: Float
        ) {
            Log.e(TAG, "generateBlackWhiteImgData: width=$width,height=$height,lineRatio=$lineRatio,lineWidth=$lineWidth" )
            val m_ratio_x = (lineWidth / cos(lineRatio / 180 * Math.PI)).toFloat()
            val m_ratio_dx = tan(lineRatio / 180 * Math.PI).toFloat()
            var point_at_x_r: Float
            var point_at_x_g: Float
            var point_at_x_b: Float
            var n_r: Int
            var n_g: Int
            var n_b: Int

            var d_r: Float
            var d_g: Float
            var d_b: Float
            var num_r: Int
            var num_g: Int
            var num_b: Int

            val IndexTwoG = -0.03566f
            for (i in 0 until height) {
                for (j in 0 until width) {
//                    point_at_x_r = ((1080 + j + IndexR + 0 + (i ) * m_ratio_dx).toFloat())
//                    point_at_x_g = ((1080 + j + IndexG + 0 + (i ) * m_ratio_dx).toFloat())
//                    point_at_x_b = ((1080 + j + IndexB + 0 + (i ) * m_ratio_dx).toFloat())

                    point_at_x_r = ((1080 + j + IndexR + 0 + (i + 0.3) * m_ratio_dx).toFloat())
                    point_at_x_g = ((1080 + j + IndexG + 0 + (i + 0.3) * m_ratio_dx).toFloat())
                    point_at_x_b = ((1080 + j + IndexB + 0 + (i + 0.3) * m_ratio_dx).toFloat())

                    n_r = (point_at_x_r / m_ratio_x).toInt()
                    n_g = (point_at_x_g / m_ratio_x).toInt()
                    n_b = (point_at_x_b / m_ratio_x).toInt()

                    d_r = point_at_x_r - m_ratio_x * n_r
                    d_g = point_at_x_g - m_ratio_x * n_g
                    d_b = point_at_x_b - m_ratio_x * n_b

                    d_r = abs(d_r)
                    d_r = abs(d_r - m_ratio_x / 2)
//                    d_r = abs(2 * d_r / m_ratio_x)
                    d_r = abs(2 * d_r / m_ratio_x + IndexTwoG)
                    if (d_r < 0) d_r = 0f
                    d_r -= d_r.toInt()
                    num_r = (d_r * 255).toInt()

                    d_g = abs(d_g)
                    d_g = abs(d_g - m_ratio_x / 2)
//                    d_g = abs(2 * d_g / m_ratio_x)
                    d_g = abs(2 * d_g / m_ratio_x + IndexTwoG)
                    if (d_g < 0) d_g = 0f
                    d_g -= d_g.toInt()
                    num_g = (d_g * 255).toInt()

                    d_b = abs(d_b)
                    d_b = abs(d_b - m_ratio_x / 2)
//                    d_b = abs(2 * d_b / m_ratio_x)
                    d_b = abs(2 * d_b / m_ratio_x + IndexTwoG)
                    if (d_b < 0) d_b = 0f
                    d_b -= d_b.toInt()
                    num_b = (d_b * 255).toInt()

                    d_r = IntervalIndex[num_r]
                    d_g = IntervalIndex[num_g]
                    d_b = IntervalIndex[num_b]

                    data[(i * width + j) * 4] = (d_r * 255).toInt().toByte()
                    data[(i * width + j) * 4 + 1] = (d_g * 255).toInt().toByte()
                    data[(i * width + j) * 4 + 2] = (d_b * 255).toInt().toByte()
                    data[(i * width + j) * 4 + 3] = 255.toByte()
                }
            }
        }

        //lineRatio=21.250000
// lineWidth=1.492100
// IndexR=-0.060000
// IndexG=0.210000
// IndexB=0.480000
        var IndexR = -0.06f
        var IndexG = 0.21f
        var IndexB = 0.48f
        var m_LineWidth = 1.4921f
        var m_LineRatio = 21.25f

        val mVertexData = floatArrayOf(
            -1f, -1f, 0f,
            1f, -1f, 0f,
            -1f, 1f, 0f,
            1f, 1f, 0f
        )
        val mTextureVertexData = floatArrayOf(
            0.0f, 1.0f,  // bottom left (V1)
            1.0f, 1.0f,  // bottom right (V3)
            0.0f, 0.0f,  // top left (V2)
            1.0f, 0.0f
        )
    }

}
