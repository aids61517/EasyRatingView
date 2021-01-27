package com.aids61517.easyratingview

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.View
import androidx.core.content.ContextCompat
import kotlin.math.floor

class EasyRatingView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttribute: Int
) : View(context, attributeSet, defStyleAttribute) {
    constructor(context: Context, attrs: AttributeSet? = null) : this(context, attrs, 0)

    var emptyDrawableResourceId: Int = 0
        set(value) {
            if (field != value) {
                field = value
                emptyDrawable = ContextCompat.getDrawable(context, value)!!
            }
        }

    var emptyDrawable: Drawable? = null
        set(value) {
            field = value
            invalidate()
            requestLayout()
        }

    var fullDrawableResourceId: Int = 0
        set(value) {
            if (field != value) {
                field = value
                fullDrawable = ContextCompat.getDrawable(context, value)!!
            }
        }

    var fullDrawable: Drawable? = null
        set(value) {
            field = value
            value?.let {
                val srcBitmap = drawableToBitmap(value)
                fullBitmapShader =
                    BitmapShader(srcBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
            }
            invalidate()
        }

    var rating: Float = 0f
        set(value) {
            if (field != value) {
                field = value
                invalidate()
            }
        }

    var numberOfStars: Int = 5
        set(value) {
            if (field != value) {
                field = value
                invalidate()
                requestLayout()
            }
        }

    var spacing: Int = 0
        set(value) {
            if (field != value) {
                field = value
                invalidate()
                requestLayout()
            }
        }

    var verticalSpacing: Int = 0
        set(value) {
            if (field != value) {
                field = value
                invalidate()
                requestLayout()
            }
        }

    var step: Float = 0.5f
        set(value) {
            if (field != value) {
                field = value
                invalidate()
            }
        }

    var maxRating: Float = 0f
        set(value) {
            if (field != value) {
                field = value
                invalidate()
            }
        }

    private var fullBitmapShader: BitmapShader? = null
        set(value) {
            field = value
            fullDrawablePaint = Paint().apply {
                isAntiAlias = true
                shader = fullBitmapShader
            }
        }

    private var fullDrawablePaint: Paint? = null

    var countOfStarsPerRow: Int = 0
        private set

    private val boundCalculator by lazy {
        StarBoundCalculator(this)
    }

    init {
        val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.EasyRatingView)
        numberOfStars = typedArray.getInt(R.styleable.EasyRatingView_numStars, 5)
        spacing = typedArray.getDimensionPixelSize(R.styleable.EasyRatingView_spacing, 0)
        verticalSpacing =
            typedArray.getDimensionPixelSize(R.styleable.EasyRatingView_verticalSpacing, 0)
        rating = typedArray.getFloat(R.styleable.EasyRatingView_rating, 0f)
        step = typedArray.getFloat(R.styleable.EasyRatingView_step, 0.5f)
        maxRating = typedArray.getFloat(R.styleable.EasyRatingView_maxRating, 0f)
        fullDrawableResourceId =
            typedArray.getResourceId(R.styleable.EasyRatingView_fullDrawable, 0)
        emptyDrawableResourceId =
            typedArray.getResourceId(R.styleable.EasyRatingView_emptyDrawable, 0)
        val gravity = typedArray.getInt(R.styleable.EasyRatingView_android_gravity, Gravity.START)
        setGravity(gravity)
        typedArray.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        if (emptyDrawable == null || numberOfStars == 0) {
            setMeasuredDimension(0, 0)
            return
        }

        emptyDrawable?.let {
            val drawableWidth = it.intrinsicWidth
            val expectWidth =
                numberOfStars * drawableWidth + (numberOfStars - 1) * spacing + paddingStart + paddingEnd
            val calculateWidth = resolveSizeAndState(expectWidth, widthMeasureSpec, 0)
            val realWidth = if (calculateWidth > widthSize) widthSize else calculateWidth
            Log.d("EasyRatingView", "onMeasure realWidth = $realWidth")
            countOfStarsPerRow = calculateCountOfStarsPerRow(realWidth)
            Log.d("EasyRatingView", "onMeasure countOfStarsPerRow = $countOfStarsPerRow")
            val expectHeight = calculateViewHeight(realWidth)
            val realHeight = resolveSizeAndState(expectHeight, heightMeasureSpec, 0)
            Log.d("EasyRatingView", "onMeasure expectHeight = $expectHeight")
            Log.d("EasyRatingView", "onMeasure realHeight = $realHeight")
            setMeasuredDimension(realWidth, realHeight)
        }
    }

    private fun calculateCountOfStarsPerRow(width: Int): Int {
        // n * bitmapWidth + (n - 1) * spacing < remainingWidth
        // n * (bitmapWidth + spacing) < remainingWidth + spacing
        val remainingWidth = width - paddingStart - paddingEnd
        val bitmapWidth = emptyDrawable!!.intrinsicWidth
        val calculateN = (remainingWidth + spacing).toFloat() / (bitmapWidth + spacing)
        return floor(calculateN).toInt()
    }

    private fun calculateViewHeight(realWidth: Int): Int {
        val isMultiLine = (numberOfStars > countOfStarsPerRow)
        val drawableHeight = emptyDrawable!!.intrinsicHeight
        return if (isMultiLine) {
            val lines = (numberOfStars / countOfStarsPerRow) + 1
            lines * drawableHeight + paddingTop + paddingBottom + (lines - 1) * verticalSpacing
        } else {
            drawableHeight + paddingTop + paddingBottom
        }
    }

    fun setGravity(gravity: Int) {
        boundCalculator.gravity = gravity
    }

    override fun onDraw(canvas: Canvas) {
        if (emptyDrawable == null || numberOfStars == 0) {
            return
        }

        emptyDrawable?.apply {
            drawStar(canvas, this, numberOfStars)
        }

        fullDrawable?.apply {
            val ratingByStep = getRatingByStep(step)
            val fullCountOfStar = ratingByStep.toInt()
            drawStar(canvas, this, fullCountOfStar)

            val indexLineOfNextStar = (fullCountOfStar + 1) / countOfStarsPerRow
            val lineDrawStarX = boundCalculator.getBoundStart(indexLineOfNextStar)
            val lineDrawStarY = boundCalculator.getBoundTop(indexLineOfNextStar)
            val indexOfStar = (ratingByStep - indexLineOfNextStar * countOfStarsPerRow).toInt()
            val drawStartX = lineDrawStarX + indexOfStar * (intrinsicWidth + spacing)
            canvas.save()
            canvas.translate(drawStartX.toFloat(), lineDrawStarY.toFloat())
            val targetWidth = intrinsicWidth * (ratingByStep % 1)
            canvas.drawRect(
                0f,
                0f,
                targetWidth,
                intrinsicHeight.toFloat(),
                fullDrawablePaint!!
            )
            canvas.restore()
        }
    }

    private fun drawStar(canvas: Canvas, drawable: Drawable, countOfStar: Int) {
        val lines = (countOfStar / countOfStarsPerRow) + 1
        drawable.run {
            val drawableWidth = intrinsicWidth
            val drawableHeight = intrinsicHeight
            repeat(lines) { indexOfLine ->
                val drawStartX = boundCalculator.getBoundStart(indexOfLine)
                val drawStartY = boundCalculator.getBoundTop(indexOfLine)
                val startCountOfStar = indexOfLine * countOfStarsPerRow
                val endCountOfStart =
                    if (startCountOfStar + countOfStarsPerRow > countOfStar) countOfStar else startCountOfStar + countOfStarsPerRow
                for (i in 0 until (endCountOfStart - startCountOfStar)) {
                    val startX = i * (drawableWidth + spacing) + drawStartX
                    setBounds(
                        startX,
                        drawStartY,
                        startX + drawableWidth,
                        drawStartY + drawableHeight
                    )
                    draw(canvas)
                }
            }

        }
    }

    private fun getRatingByStep(step: Float): Float {
        val maxRating = if (maxRating != 0f) maxRating else numberOfStars.toFloat()
        val rating = if (rating > maxRating) maxRating else (rating / maxRating) * numberOfStars
        val newRatingRatio = rating / step
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
