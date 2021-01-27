package com.aids61517.easyratingview

import android.view.Gravity

internal class StarBoundCalculator(private val ratingView: EasyRatingView) {
    var gravity = Gravity.START
        set(value) {
            var g = value
            if (g and Gravity.RELATIVE_HORIZONTAL_GRAVITY_MASK == 0) {
                g = g or Gravity.START
            }
            if (g and Gravity.VERTICAL_GRAVITY_MASK == 0) {
                g = g or Gravity.TOP
            }

            field = g
        }

    fun getBoundStart(indexOfLine: Int): Int {
        if (ratingView.emptyDrawable == null) return 0
        return when (gravity and Gravity.RELATIVE_HORIZONTAL_GRAVITY_MASK) {
            Gravity.CENTER, Gravity.CENTER_HORIZONTAL -> {
                calculateXOfGravityCenter(indexOfLine)
            }
            Gravity.RIGHT, Gravity.END -> {
                calculateXOfGravityEnd(indexOfLine)
            }
            else -> {
                calculateXOfGravityStart()
            }
        }
    }

    fun getBoundTop(indexOfLine: Int): Int {
        if (ratingView.emptyDrawable == null) return 0
        return when (gravity and Gravity.VERTICAL_GRAVITY_MASK) {
            Gravity.CENTER, Gravity.CENTER_VERTICAL -> {
                calculateYOfGravityCenter(indexOfLine)
            }
            Gravity.BOTTOM -> {
                calculateYOfGravityBottom(indexOfLine)
            }
            else -> {
                calculateYOfGravityTop(indexOfLine)
            }
        }
    }

    private fun calculateYOfGravityCenter(indexOfLine: Int): Int {
        val drawableHeight = ratingView.emptyDrawable!!.intrinsicHeight
        val topY = ratingView.paddingTop
        val bottomY = ratingView.height - ratingView.paddingBottom
        val centerY = (topY + bottomY) / 2
        val lines = ratingView.numberOfStars / ratingView.countOfStarsPerRow + 1
        val contentHeight = lines * drawableHeight + (lines - 1) * ratingView.verticalSpacing
        val firstLineY = centerY - contentHeight / 2
        return firstLineY + indexOfLine * (drawableHeight + ratingView.verticalSpacing)
    }

    private fun calculateYOfGravityBottom(indexOfLine: Int): Int {
        val lines = ratingView.numberOfStars / ratingView.countOfStarsPerRow + 1
        val drawableHeight = ratingView.emptyDrawable!!.intrinsicHeight
        return ratingView.height - ratingView.paddingBottom - (lines - indexOfLine) * drawableHeight - (lines - indexOfLine - 1) * ratingView.verticalSpacing
    }

    private fun calculateYOfGravityTop(indexOfLine: Int): Int {
        val drawableHeight = ratingView.emptyDrawable!!.intrinsicHeight
        return ratingView.paddingTop + indexOfLine * (drawableHeight + ratingView.verticalSpacing)
    }

    private fun calculateXOfGravityCenter(indexOfLine: Int): Int {
        val count = getCountOfStars(indexOfLine)
        val drawableWidth = ratingView.emptyDrawable!!.intrinsicWidth
        val startX = ratingView.paddingStart
        val endX = ratingView.width - ratingView.paddingEnd
        val centerX = (startX + endX) / 2
        val contentWidth = count * drawableWidth + (count - 1) * ratingView.spacing
        return centerX - contentWidth / 2
    }

    private fun calculateXOfGravityStart(): Int {
        return ratingView.paddingStart
    }

    private fun calculateXOfGravityEnd(indexOfLine: Int): Int {
        val count = getCountOfStars(indexOfLine)
        val drawableWidth = ratingView.emptyDrawable!!.intrinsicWidth
        return ratingView.width - ratingView.paddingEnd - count * drawableWidth - (count - 1) * ratingView.spacing
    }

    private fun getCountOfStars(indexOfLine: Int): Int {
        val countOfStarsPerRow = ratingView.countOfStarsPerRow
        val numberOfStars = ratingView.numberOfStars
        val startCountOfStar = indexOfLine * countOfStarsPerRow
        val endCountOfStart =
            if (startCountOfStar + countOfStarsPerRow > numberOfStars) numberOfStars else startCountOfStar + countOfStarsPerRow
        return endCountOfStart - startCountOfStar
    }
}