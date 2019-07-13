package com.aids61517.easyratingview

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat

class EasyRatingView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttribute: Int
) : View(context, attributeSet, defStyleAttribute) {
    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    private lateinit var emptyDrawable: Drawable
    private lateinit var fullDrawable: Drawable

    private val fullBitmapShader by lazy {
        val srcBitmap = drawableToBitmap(fullDrawable)
        BitmapShader(srcBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
    }

    private val fullDrawablePaint by lazy {
        Paint().apply {
            isAntiAlias = true
            shader = fullBitmapShader
        }
    }

    var rating: Float = 0f
        set(value) {
            field = if (value > numberStars) numberStars.toFloat() else value
            invalidate()
        }

    private val numberStars: Int
    private val spacing: Int
    private val step: Float

    init {
        val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.EasyRatingView)
        numberStars = typedArray.getInt(R.styleable.EasyRatingView_numStars, 5)
        spacing = typedArray.getDimensionPixelSize(R.styleable.EasyRatingView_spacing, 0)
        rating = typedArray.getFloat(R.styleable.EasyRatingView_rating, 0f)
        step = typedArray.getFloat(R.styleable.EasyRatingView_step, 0.5f)
        val fullDrawableId = typedArray.getResourceId(R.styleable.EasyRatingView_fullDrawable, 0)
        val emptyDrawableId = typedArray.getResourceId(R.styleable.EasyRatingView_emptyDrawable, 0)
        typedArray.recycle()
        if (fullDrawableId != 0) {
            fullDrawable = ContextCompat.getDrawable(context, fullDrawableId)!!
        }
        if (emptyDrawableId != 0) {
            emptyDrawable = ContextCompat.getDrawable(context, emptyDrawableId)!!
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (::emptyDrawable.isInitialized.not() || numberStars == 0) {
            setMeasuredDimension(0, 0)
            return
        }

        val drawableWidth = emptyDrawable.intrinsicWidth
        val drawableHeight = emptyDrawable.intrinsicHeight
        val expectWidth = numberStars * drawableWidth + (numberStars - 1) * spacing + paddingStart + paddingEnd
        val realWidth = resolveSizeAndState(expectWidth, widthMeasureSpec, 0)
        val expectHeight = drawableHeight + paddingTop + paddingBottom
        val realHeight = resolveSizeAndState(expectHeight, heightMeasureSpec, 0)
        setMeasuredDimension(realWidth, realHeight)
    }

    override fun onDraw(canvas: Canvas) {
        if (::emptyDrawable.isInitialized.not() || numberStars == 0) {
            return
        }

        val drawStartX = paddingStart
        val drawStartY = paddingTop
        emptyDrawable.apply {
            val width = emptyDrawable.intrinsicWidth
            val height = emptyDrawable.intrinsicHeight
            for (i in 0 until numberStars) {
                val startX = i * (width + spacing) + drawStartX
                setBounds(startX, drawStartY, startX + width, drawStartY + height)
                draw(canvas)
            }
        }

        if (::fullDrawable.isInitialized.not()) {
            return
        }

        fullDrawable.apply {
            val finalRating = rating.getFinalRatingByStep(step)
            val width = intrinsicWidth
            val height = intrinsicHeight
            val fullCount = finalRating.toInt()
            for (i in 0 until fullCount) {
                val startX = i * (width + spacing) + drawStartX
                setBounds(startX, drawStartY, startX + width, drawStartY + height)
                draw(canvas)
            }

            val offsetX = fullCount * (width + spacing) + drawStartX
            val offsetY = drawStartY.toFloat()
            canvas.save()
            canvas.translate(offsetX.toFloat(), offsetY)
            val targetWidth = width * (finalRating % 1)
            canvas.drawRect(
                0f,
                0f,
                targetWidth,
                height.toFloat(),
                fullDrawablePaint
            )
            canvas.restore()
        }
    }

    private fun Float.getFinalRatingByStep(step: Float): Float {
        val newRatingRatio = this / step
        val multiple = if ((newRatingRatio % 1) >= 0.5) {
            newRatingRatio.toInt() + 1
        } else {
            newRatingRatio.toInt()
        }
        return multiple * step
    }

    private fun drawableToBitmap(drawable: Drawable): Bitmap {
        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }

        val width = drawable.intrinsicWidth
        val height = drawable.intrinsicWidth
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, width, height)
        drawable.draw(canvas)
        return bitmap
    }
}
