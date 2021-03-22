package com.hazz.kotlinmvp.view

import android.content.Context
import android.graphics.*
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.view.View
import com.hazz.kotlinmvp.R
import java.lang.ref.WeakReference

/**
 * Created by xuhao on 2017/12/4.
 * desc:LoadingView 加载动画
 */
class LoadingView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {
    private var mOuterCircleRadius: Int
    private val mOuterCircleColor: Int
    private var mInnerTriangleRadius: Int
    private val mInnerTriangleColor: Int
    private val mBackgroundColor: Int
    private var mStrokeWidth: Int
    private val mIsNeedBackground: Boolean
    private var mPaint: Paint? = null
    private var mTrianglePaint: Paint? = null
    private var mBackGroundPaint: Paint? = null
    private var isReverse = false
    private var mProgress = 0
    private var mStartAngle = -90
    private var mRotateAngle = 0
    private val mDel = 30
    private var mRectF: RectF? = null
    private var mRoundRectF: RectF? = null
    private var mPath: Path? = null
    private var mRotateCenter: PointF? = null
    private var mHandler: MyHandler? = null

    private fun init() {
        //设置画进度圈的画笔
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaint!!.color = mOuterCircleColor
        mPaint!!.strokeWidth = mStrokeWidth.toFloat()
        mPaint!!.style = Paint.Style.STROKE

        //设置画三角形的画笔
        mTrianglePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mTrianglePaint!!.color = mInnerTriangleColor

        //设置画背景的画笔
        mBackGroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mBackGroundPaint!!.color = mBackgroundColor
        mPath = Path()
        mRotateCenter = PointF()
        mRoundRectF = RectF()
        mHandler = MyHandler(this)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        //判断外圆的半径
        mOuterCircleRadius = Math.min(mOuterCircleRadius.toFloat(),
                (Math.min(w - paddingRight - paddingLeft, h - paddingTop - paddingBottom) - 4 * mPaint!!.strokeWidth) / 2).toInt()
        if (mOuterCircleRadius < 0) {
            mStrokeWidth = Math.min(w - paddingRight - paddingLeft, h - paddingTop - paddingBottom) / 2
            mOuterCircleRadius = Math.min(w - paddingRight - paddingLeft, h - paddingTop - paddingBottom) / 4
        }
        val left = ((w - 2 * mOuterCircleRadius) / 2).toFloat()
        val top = ((h - 2 * mOuterCircleRadius) / 2).toFloat()
        val diameter = (2 * mOuterCircleRadius).toFloat()
        mRectF = RectF(left, top, left + diameter, top + diameter)

        //判断内圆的半径大小
        mInnerTriangleRadius = if (mInnerTriangleRadius < mOuterCircleRadius) mInnerTriangleRadius else 3 * mOuterCircleRadius / 5
        if (mInnerTriangleRadius < 0) {
            mInnerTriangleRadius = 0
        }
        //计算内圆的圆心，圆心应该和外圆圆心相同
        val centerX = left + mOuterCircleRadius
        val centerY = top + mOuterCircleRadius
        //计算内圆的内接三角形的三个定点组成的path
        mPath!!.moveTo(centerX - mInnerTriangleRadius / 2, (centerY - Math.sqrt(3.0) * mInnerTriangleRadius / 2).toFloat())
        mPath!!.lineTo(centerX + mInnerTriangleRadius, centerY)
        mPath!!.lineTo(centerX - mInnerTriangleRadius / 2, (centerY + Math.sqrt(3.0) * mInnerTriangleRadius / 2).toFloat())
        mPath!!.close()
        mRotateCenter!![(measuredWidth / 2).toFloat()] = (measuredHeight / 2).toFloat()
        mRoundRectF!!.left = 0f
        mRoundRectF!!.top = 0f
        mRoundRectF!!.right = w.toFloat()
        mRoundRectF!!.bottom = h.toFloat()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(measureSize(widthMeasureSpec, 140), measureSize(heightMeasureSpec, 140))
    }

    private fun measureSize(measureSpec: Int, defaultSize: Int): Int {
        val specMode = MeasureSpec.getMode(measureSpec)
        val specSize = MeasureSpec.getSize(measureSpec)
        var resultSize = defaultSize
        when (specMode) {
            MeasureSpec.EXACTLY -> resultSize = specSize
            MeasureSpec.AT_MOST, MeasureSpec.UNSPECIFIED -> resultSize = Math.min(specSize, resultSize)
        }
        return resultSize
    }

    override fun onDraw(canvas: Canvas) {
        if (mIsNeedBackground) {
            canvas.drawRoundRect(mRoundRectF, 8f, 8f, mBackGroundPaint)
        }
        if (isReverse) {
            mProgress -= mDel
            mStartAngle += mDel
            if (mStartAngle >= 270) {
                mStartAngle = -90
                isReverse = false
            }
            mRotateAngle += mDel
            if (mRotateAngle >= 360) {
                mRotateAngle = 0
            }
            canvas.save()
            canvas.rotate(mRotateAngle.toFloat(), mRotateCenter!!.x, mRotateCenter!!.y)
            canvas.drawPath(mPath, mTrianglePaint)
            canvas.restore()
        } else {
            mProgress += mDel
            if (mProgress >= 360) {
                isReverse = true
            }
            canvas.drawPath(mPath, mTrianglePaint)
        }
        canvas.drawArc(mRectF, mStartAngle.toFloat(), mProgress.toFloat(), false, mPaint)
        mHandler!!.sendEmptyMessageDelayed(MyHandler.REFRESH_VIEW, 80)
    }

    private class MyHandler(loadingView: LoadingView) : Handler() {
        private val mLoadingViewWeakReference: WeakReference<LoadingView?>
        override fun handleMessage(msg: Message) {
            if (mLoadingViewWeakReference.get() != null) {
                when (msg.what) {
                    REFRESH_VIEW -> mLoadingViewWeakReference.get()!!.postInvalidate()
                }
            }
        }

        companion object {
            const val REFRESH_VIEW = 0
        }

        init {
            mLoadingViewWeakReference = WeakReference(loadingView)
        }
    }

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.LoadingView)
        mOuterCircleRadius = typedArray.getDimensionPixelSize(R.styleable.LoadingView_outerCircleRadius, 50)
        mOuterCircleColor = typedArray.getColor(R.styleable.LoadingView_outerCircleColor, -0xdd74de)
        mInnerTriangleRadius = typedArray.getDimensionPixelSize(R.styleable.LoadingView_innerTriangleRadius, 25)
        mInnerTriangleColor = typedArray.getColor(R.styleable.LoadingView_innerTriangleColor, -0xdd74de)
        mIsNeedBackground = typedArray.getBoolean(R.styleable.LoadingView_isNeedBackground, true)
        mBackgroundColor = typedArray.getColor(R.styleable.LoadingView_backgroundColor, -0x44ddddde)
        mStrokeWidth = typedArray.getDimensionPixelSize(R.styleable.LoadingView_strokeWidth, 5)
        typedArray.recycle()
        init()
    }
}