package com.vv3d.vvtest

import android.content.res.Configuration
import android.media.AudioManager
import android.media.MediaPlayer
import android.opengl.GLSurfaceView
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.vv3d.vvtest.databinding.ActivityMainBinding


private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    private lateinit var mGlMediaplayerRender: GL3DRender
    private var mGlMediaplayerRenderRedGreen: GL3DRender? = null
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private var mMediaPlayer: MediaPlayer = MediaPlayer()
    private var mMediaPlayerRedGreen: MediaPlayer = MediaPlayer()
    private var isRedGreenVideo = false
    private var isFirstPlayRedGreen = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        initWindowAttr()
        setContentView(binding.root)
        checkNotchSupport()

        initMediaPlayer()
        initSurfaceView()
        initRedGreenMediaPlayer()

        binding.widthWheelView.setOnWidthSelectedListener { width ->
            OpenGLUtils.m_LineWidth = width
            if (isRedGreenVideo) {
                mGlMediaplayerRenderRedGreen?.refreshTexture2 = true
            } else {
                mGlMediaplayerRender.refreshTexture2 = true
            }
        }

        binding.ratioWheelView.setOnRatioSelectedListener { ratio ->
            OpenGLUtils.m_LineRatio = ratio
            if (isRedGreenVideo) {
                mGlMediaplayerRenderRedGreen?.refreshTexture2 = true
            } else {
                mGlMediaplayerRender.refreshTexture2 = true
            }
        }

        binding.colorRedWheelView.setOnColorSelectedListener { colorRed ->
            OpenGLUtils.IndexR = colorRed
            if (isRedGreenVideo) {
                mGlMediaplayerRenderRedGreen?.refreshTexture2 = true
            } else {
                mGlMediaplayerRender.refreshTexture2 = true
            }
        }

        binding.colorGreenWheelView.setOnColorSelectedListener { colorGreen ->
            OpenGLUtils.IndexG = colorGreen
            if (isRedGreenVideo) {
                mGlMediaplayerRenderRedGreen?.refreshTexture2 = true
            } else {
                mGlMediaplayerRender.refreshTexture2 = true
            }
        }

        binding.colorBlueWheelView.setOnColorSelectedListener { colorBlue ->
            OpenGLUtils.IndexB = colorBlue
            if (isRedGreenVideo) {
                mGlMediaplayerRenderRedGreen?.refreshTexture2 = true
            } else {
                mGlMediaplayerRender.refreshTexture2 = true
            }
        }

        binding.switchRed.setOnCheckedChangeListener { _, checked ->
            mGlMediaplayerRender.mColorRed = if (checked) 1f else 0f
        }
        binding.switchGreen.setOnCheckedChangeListener { _, checked ->
            mGlMediaplayerRender.mColorGreen = if (checked) 1f else 0f
        }
        binding.switchBlue.setOnCheckedChangeListener { _, checked ->
            mGlMediaplayerRender.mColorBlue = if (checked) 1f else 0f
        }

        binding.tvDeviceModel.text = "????????????:${Build.MODEL}"
        binding.tvDeviceSn.text = "??????SN:${Build.SERIAL}"
        binding.tvDeviceResolution.text =
            "?????????:${resources.displayMetrics.widthPixels}x${resources.displayMetrics.heightPixels}@${resources.displayMetrics.densityDpi}dpi"

        binding.btnRedGreen.setOnClickListener {
            if (!isRedGreenVideo) {
                (it as Button).text = getString(R.string.play_test_video)
                isRedGreenVideo = true
                if (isFirstPlayRedGreen) {
                    binding.surfaceViewRedgreen.setEGLContextClientVersion(2)
                    mGlMediaplayerRenderRedGreen = GL3DRender(this, mMediaPlayerRedGreen)
                    binding.surfaceViewRedgreen.setRenderer(mGlMediaplayerRenderRedGreen)
                    binding.surfaceViewRedgreen.holder.addCallback(object : SurfaceHolder.Callback {
                        override fun surfaceCreated(p0: SurfaceHolder) {
                            Log.e(TAG, "surfaceCreated: ")
                        }

                        override fun surfaceChanged(
                            p0: SurfaceHolder,
                            format: Int,
                            width: Int,
                            height: Int
                        ) {
                            mGlMediaplayerRenderRedGreen?.mWidth = width
                            mGlMediaplayerRenderRedGreen?.mHeight = height
                            binding.surfaceViewRedgreen.requestRender()
                            binding.tvDeviceResolution.text =
                                "?????????:${width}x${height}@${resources.displayMetrics.densityDpi}dpi"
                        }

                        override fun surfaceDestroyed(p0: SurfaceHolder) {
                            Log.e(TAG, "surfaceDestroyed: ")
                        }

                    })
                    isFirstPlayRedGreen = false
                } else {
                    if (mMediaPlayerRedGreen != null) {
                        mMediaPlayerRedGreen.start()
                    }
                }
                if (mMediaPlayer != null) {
                    mMediaPlayer.pause()
                }
                binding.surfaceViewRedgreen.visibility = View.VISIBLE
                binding.surfaceView.visibility = View.GONE
            } else {
                (it as Button).text = getString(R.string.play_red_green)
                binding.surfaceViewRedgreen.visibility = View.GONE
                binding.surfaceView.visibility = View.VISIBLE
                if (mMediaPlayerRedGreen != null) {
                    mMediaPlayerRedGreen.pause()
                }
                if (mMediaPlayer != null) {
                    mMediaPlayer.start()
                }
                isRedGreenVideo = false
            }
        }
    }

    private fun initRedGreenMediaPlayer() {
        val assetMg = applicationContext.assets
        val fileDescriptor = assetMg.openFd("redGreen.mp4")
        mMediaPlayerRedGreen.setDataSource(
            fileDescriptor.fileDescriptor,
            fileDescriptor.startOffset,
            fileDescriptor.length
        )
        mMediaPlayerRedGreen.setAudioStreamType(AudioManager.STREAM_MUSIC)
        mMediaPlayerRedGreen.isLooping = true
        mMediaPlayerRedGreen.setScreenOnWhilePlaying(true)
        mMediaPlayerRedGreen.setOnCompletionListener { // ????????????????????????
        }
    }

    private fun initSurfaceView() {
        mGlMediaplayerRender = GL3DRender(this, mMediaPlayer)
        binding.surfaceView.setEGLContextClientVersion(2)
        binding.surfaceView.setRenderer(mGlMediaplayerRender)
        binding.surfaceView.renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
        mGlMediaplayerRender.setVideoPlayCallLister {
            preparedInit()
        }
        mGlMediaplayerRender.setOnFrameAvailableListener {
            binding.surfaceView.requestRender()
        }
        binding.surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(p0: SurfaceHolder) {
                Log.e(TAG, "surfaceCreated: ")
            }

            override fun surfaceChanged(p0: SurfaceHolder, format: Int, width: Int, height: Int) {
                mGlMediaplayerRender.mWidth = width
                mGlMediaplayerRender.mHeight = height
                binding.surfaceView.requestRender()
                binding.tvDeviceResolution.text =
                    "?????????:${width}x${height}@${resources.displayMetrics.densityDpi}dpi"
            }

            override fun surfaceDestroyed(p0: SurfaceHolder) {
                Log.e(TAG, "surfaceDestroyed: ")
            }

        })
    }

    //lineRatio=21.250000
// lineWidth=1.492100
// IndexR=-0.060000
// IndexG=0.210000
// IndexB=0.480000
    private fun preparedInit() {
        binding.widthWheelView.widthValue = OpenGLUtils.m_LineWidth
        binding.ratioWheelView.ratioValue = OpenGLUtils.m_LineRatio
        binding.colorRedWheelView.colorValue = OpenGLUtils.IndexR
        binding.colorGreenWheelView.colorValue = OpenGLUtils.IndexG
        binding.colorBlueWheelView.colorValue = OpenGLUtils.IndexB
    }

    override fun onDestroy() {
        super.onDestroy()
        mMediaPlayer.stop()
        mMediaPlayer.release()
        mMediaPlayerRedGreen.stop()
        mMediaPlayerRedGreen.release()
    }

    private fun initMediaPlayer() {
        val assetMg = applicationContext.assets
        //???????????????????????????
        val fileDescriptor =
            assetMg.openFd(if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) "land.mp4" else "port.mp4")
        mMediaPlayer.setDataSource(
            fileDescriptor.fileDescriptor,
            fileDescriptor.startOffset,
            fileDescriptor.length
        )
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
        mMediaPlayer.isLooping = true
    }

    private fun initWindowAttr() {
        val params = window.attributes
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
            val controller = window.decorView?.windowInsetsController
            controller?.hide(WindowInsets.Type.systemBars())
            controller?.systemBarsBehavior =
                WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            params.systemUiVisibility =
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN)
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            params.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
        window.attributes = params
    }

    private fun checkNotchSupport() {
        if (isPiePlus()) {
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        }
    }
}