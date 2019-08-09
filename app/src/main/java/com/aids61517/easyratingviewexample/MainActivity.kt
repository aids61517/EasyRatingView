package com.aids61517.easyratingviewexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.aids61517.easyratingview.EasyRatingView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ratingContainer.apply {
            EasyRatingView(this@MainActivity).apply {
                emptyDrawableResourceId = R.drawable.ic_review_empty_small
                fullDrawableResourceId = R.drawable.ic_review_full_small
                rating = 3f
                maxRating = 4f
                spacing = 50
            }.also { addView(it) }
        }
    }
}
