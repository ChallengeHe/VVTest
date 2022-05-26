package com.vv3d.vvtest

import android.content.Context
import android.graphics.Color
import android.graphics.SurfaceTexture
import android.graphics.SurfaceTexture.OnFrameAvailableListener
import android.media.MediaPlayer
import android.opengl.GLES11Ext
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.util.Log
import android.view.Surface
import androidx.media3.common.util.Assertions
import java.nio.FloatBuffer
import java.util.concurrent.atomic.AtomicBoolean
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

private const val TAG = "GL3DRender"

class GL3DRender(
    private val mContext: Context,
    private val mMediaPlayer: MediaPlayer,
    var mWidth: Int = 0,
    var mHeight: Int = 0
) : GLSurfaceView.Renderer {
    var mColorRed: Float = 1f
    var mColorGreen: Float = 1f
    var mColorBlue: Float = 1f
    var mDisVideoValue = 1.0f
    var refreshTexture2: Boolean = false
    private var isFirstPrepareVideo: Boolean = false
    private var mScreenWidth: Int = 0
    private var mScreenHeight: Int = 0
    private var mTextureId2: Int = 0
    private lateinit var mSurfaceTexture: SurfaceTexture
    private var mTextureId: Int = 0
    private var mPurseColorLoca: Int = 0
    private var mDisplayVideoHandle: Int = 0
    private var mTextureCoordHandle: Int = 0

    private var uInputColorLoca: Int = 0
    private var mTextureHandle2: Int = 0
    private var mTextureHandle: Int = 0
    private var mPositionHandle: Int = 0
    private val frameAvailable = AtomicBoolean()
    private var mVertexBuffer: FloatBuffer? = null
    private var mTextureVertexBuffer: FloatBuffer? = null
    private var mProgramId = 0

    init {
        mVertexBuffer = OpenGLUtils.getFloatBuffer(OpenGLUtils.mVertexData)
        mTextureVertexBuffer = OpenGLUtils.getFloatBuffer(OpenGLUtils.mTextureVertexData)
        isFirstPrepareVideo = true
    }


    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {
        val vertCode: String = getShaderCode(mContext, R.raw.gl_3d_render_vert_shader)
        val fragCode: String = getShaderCode(mContext, R.raw.gl_3d_render_frag_shader)
        mProgramId = OpenGLUtils.createProgram(vertCode, fragCode)

        mPositionHandle = GLES20.glGetAttribLocation(mProgramId, "aPosition")
        OpenGLUtils.checkGlError("glGetAttribLocation")

        mTextureHandle = GLES20.glGetUniformLocation(mProgramId, "sTexture")
        mTextureHandle2 = GLES20.glGetUniformLocation(mProgramId, "sTexture2")
        OpenGLUtils.checkGlError("glGetUniformLocation")
        uInputColorLoca = GLES20.glGetUniformLocation(mProgramId, "uInputColor")
        OpenGLUtils.checkGlError("glGetUniformLocation")


        mTextureCoordHandle = GLES20.glGetAttribLocation(mProgramId, "aTexCoord")
        OpenGLUtils.checkGlError("glGetAttribLocation")


        mDisplayVideoHandle = GLES20.glGetUniformLocation(mProgramId, "displayVideo")
        OpenGLUtils.checkGlError("glGetUniformLocation mDisplayVideoHandle")

        mPurseColorLoca = GLES20.glGetUniformLocation(mProgramId, "purse_color")
        OpenGLUtils.checkGlError("glGetUniformLocation purse_color")

        val textures = IntArray(1)
        GLES20.glGenTextures(1, textures, 0)
        mTextureId = textures[0]
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mTextureId)
        OpenGLUtils.checkGlError("glBindTexture ")
        GLES20.glTexParameterf(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER,
            GLES20.GL_NEAREST.toFloat()
        )
        GLES20.glTexParameterf(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER,
            GLES20.GL_LINEAR.toFloat()
        )

        mSurfaceTexture = SurfaceTexture(mTextureId)
        mSurfaceTexture.setOnFrameAvailableListener {
            frameAvailable.set(true)
            mOnFrameAvailableListener?.invoke()
        }

        val surface = Surface(mSurfaceTexture)
        mMediaPlayer.setSurface(surface)
        surface.release()

        mTextureId2 = OpenGLUtils.loadTextureForBlackWhiteImg(
            mContext,
            mWidth,
            mHeight
        )

    }
    private var mOnFrameAvailableListener: (() -> Unit)? = null
    fun setOnFrameAvailableListener(mLister: () -> Unit) {
        mOnFrameAvailableListener = mLister
    }
    private var mVideoPlayCallListener: (() -> Unit)? = null
    fun setVideoPlayCallLister(mLister: () -> Unit) {
        mVideoPlayCallListener = mLister
    }

    override fun onSurfaceChanged(p0: GL10?, width: Int, height: Int) {
        mScreenWidth = width
        mScreenHeight = height
        if (isFirstPrepareVideo) {
            mMediaPlayer.prepareAsync()
            mMediaPlayer.setOnPreparedListener { mediaPlayer ->
                Log.d(TAG, "onPrepared ")
                mVideoPlayCallListener?.invoke()
                mediaPlayer.start()
            }
            isFirstPrepareVideo = false
        }
    }

    override fun onDrawFrame(p0: GL10?) {
        GLES20.glClearColor(0f, 0f, 0f, 0f)
        if (refreshTexture2) {
            if (mTextureId2 > 0) {
                val tex = intArrayOf(mTextureId2)
                GLES20.glDeleteTextures(1, tex, 0)
            }
            mTextureId2 = OpenGLUtils.loadTextureForBlackWhiteImg(mContext, mWidth, mHeight)
            refreshTexture2 = false
        }
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT or GLES20.GL_COLOR_BUFFER_BIT)
        if (frameAvailable.compareAndSet(true, false)) {
            mSurfaceTexture.updateTexImage()
        }
        GLES20.glUseProgram(mProgramId)
        OpenGLUtils.checkGlError("glUseProgram")


        GLES20.glUniform1f(mDisplayVideoHandle, mDisVideoValue)
        OpenGLUtils.checkGlError("glUniform1i displayVideo")

        GLES20.glUniform4f(
            uInputColorLoca,
            mColorRed,
            mColorGreen,
            mColorBlue,
            1f
        )
        OpenGLUtils.checkGlError("glUniform4f uInputColor")


        mVertexBuffer!!.position(0)
        GLES20.glEnableVertexAttribArray(mPositionHandle)
        GLES20.glVertexAttribPointer(
            mPositionHandle, 3, GLES20.GL_FLOAT, false,
            12, mVertexBuffer
        )
        OpenGLUtils.checkGlError("glVertexAttribPointer")

        mTextureVertexBuffer!!.position(0)
        GLES20.glEnableVertexAttribArray(mTextureCoordHandle)
        OpenGLUtils.checkGlError("glEnableVertexAttribArray")
        GLES20.glVertexAttribPointer(
            mTextureCoordHandle,
            2,
            GLES20.GL_FLOAT,
            false,
            8,
            mTextureVertexBuffer
        )
        OpenGLUtils.checkGlError("glVertexAttribPointer")

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        OpenGLUtils.checkGlError("glActiveTexture")
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mTextureId)
        OpenGLUtils.checkGlError("glBindTexture")

        GLES20.glUniform1i(mTextureHandle, 0)
        OpenGLUtils.checkGlError("glUniform1i")

        GLES20.glActiveTexture(GLES20.GL_TEXTURE1)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureId2)
        OpenGLUtils.checkGlError("glBindTexture")
        GLES20.glUniform1i(mTextureHandle2, 1)

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)

        GLES20.glDisableVertexAttribArray(mPositionHandle)
        GLES20.glDisableVertexAttribArray(mTextureCoordHandle)
    }


}
