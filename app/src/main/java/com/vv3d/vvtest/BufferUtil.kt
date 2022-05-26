package com.vv3d.vvtest

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

/**
 * Damon on 2021/3/8
 */
internal object BufferUtil {
    /**
     * Float类型占4Byte
     */
    private const val BYTES_PER_FLOAT = 4

    /**
     * Short类型占2Byte
     */
    private const val BYTES_PER_SHORT = 2

    /**
     * 创建一个FloatBuffer
     * 分配一个块Native内存，用于与GL通讯传递。(我们通常用的数据存在于Dalvik的内存中，1.无法访问硬件；2.会被垃圾回收)
     */
    @JvmStatic
    fun createFloatBuffer(array: FloatArray): FloatBuffer {
        val buffer = ByteBuffer
            // 分配顶点坐标分量个数 * Float占的Byte位数
            .allocateDirect(array.size * BYTES_PER_FLOAT)
            // 按照本地字节序排序
            .order(ByteOrder.nativeOrder())
            // Byte类型转Float类型
            .asFloatBuffer()
        // 将Dalvik的内存数据复制到Native内存中
        buffer.put(array)
        return buffer
    }

    /**
     * 创建一个FloatBuffer
     */
    @JvmStatic
    fun createShortBuffer(array: ShortArray): ShortBuffer {
        val buffer = ByteBuffer
            // 分配顶点坐标分量个数 * Float占的Byte位数
            .allocateDirect(array.size * BYTES_PER_SHORT)
            // 按照本地字节序排序
            .order(ByteOrder.nativeOrder())
            // Byte类型转Float类型
            .asShortBuffer()

        // 将Dalvik的内存数据复制到Native内存中
        buffer.put(array)
        return buffer
    }

    /**
     * 创建一个 ByteBuffer
     */
    @JvmStatic
    fun createByteBuffer(array: ByteArray): ByteBuffer {
        val buffer = ByteBuffer.allocateDirect(array.size)
        buffer.put(array)
        buffer.position(0)
        return buffer
    }
}