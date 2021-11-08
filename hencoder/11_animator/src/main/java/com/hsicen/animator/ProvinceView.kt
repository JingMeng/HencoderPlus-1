package com.hsicen.animator

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

/**
 * <p>作者：Hsicen  2019/7/22 10:19
 * <p>邮箱：codinghuang@163.com
 * <p>作用：
 * <p>描述：字符串动画
 */
class ProvinceView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val mPaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG)
    }

    private val mMatrixFrom by lazy {
        Matrix()
    }

    private val mMatrixTo by lazy {
        Matrix()
    }

    private val mCamera by lazy {
        Camera()
    }

    var mProvince = "北京市"
        set(value) {
            field = value
            invalidate()
        }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        mPaint.color = Color.GREEN
        mPaint.textSize = 30f.sp2px
        mPaint.textAlign = Paint.Align.CENTER

        canvas.drawText(mProvince, width / 2f, height / 2f, mPaint)

        mCamera.save()
        canvas.save()
        mCamera.translate(3f, 5f, 0f)
        mCamera.rotateX(40f)
        mCamera.applyToCanvas(canvas)
        canvas.restore()
        mCamera.restore()
    }

}