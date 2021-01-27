# EasyRatingView
[![API](https://img.shields.io/badge/API-19%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=19)
[![](https://jitpack.io/v/aids61517/EasyRatingView.svg)](https://jitpack.io/#aids61517/EasyRatingView)

A rating view which can be added spacing between stars.

## Usage

```xml
<com.aids61517.easyratingview.EasyRatingView
  android:id="@+id/ratingView"
  android:layout_width="wrap_content"
  android:layout_height="wrap_content"
  android:gravity="center"
  app:emptyDrawable="@drawable/ic_review_empty_small"
  app:fullDrawable="@drawable/ic_review_full_small"
  app:numStars="5"
  app:rating="2.8"
  app:spacing="10dp"
  app:step="0.5" />
```

result is 

<img alt="result sample" width="171" height="72" src="https://user-images.githubusercontent.com/15688437/61178895-10129000-a62a-11e9-813e-f85a6bf9821f.png" />

required attributes
```groovy
app:emptyDrawable 
app:fullDrawable 
``` 

optional attributes
```groovy
app:numStars, default value is 5
app:step, default value is 0.5 
app:maxRating, default value equals numStars 
app:rating
app:spacing
app:verticalSpacing, works when horizontal spacing is not enought
android:gravity, default value is start|top
``` 

also you can new one programmatically
```xml
EasyRatingView(context).apply {
  emptyDrawableResourceId = //your empty drawable id like R.drawable.ic_review_empty
  fullDrawableResourceId = //your full drawable id like R.drawable.ic_review_full
  rating = 3f
  maxRating = 4f
  spacing = //value in px
}
```
## Download

Add the JitPack repository to the build.gradle file:
```groovy
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```
Add the Gradle dependency:
```groovy
implementation 'com.github.aids61517:EasyRatingView:1.1.0'
```
