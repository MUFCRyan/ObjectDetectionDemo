package com.mufcryan.anabstract.common.ui

import android.content.Context
import android.graphics.Point
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.mufcryan.anabstract.common.bean.WordCloudBean
import com.mufcryan.util.LogUtil
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.cos
import kotlin.math.sin

import android.widget.TextView
import com.mufcryan.anabstract.R
import com.mufcryan.util.DisplayUtil


class WordCloudView: ViewGroup {
  private val data = ArrayList<WordCloudBean>()
  private val placedViewList = ArrayList<View>()
  private val random = Random()
  private val textColors = intArrayOf(
    R.color.Blk_1_alpha_30, R.color.maskparty_navibar_bg, R.color.Ylw_1, R.color.teal_200,
    R.color.Red_3, R.color.purple_200, R.color.Red_1, R.color.purple_700
  )
  private var maxWeight = 1.0F
  private var minWeight = 1.0F
  private var weightDistance = 0.0F

  constructor(context: Context): this(context, null)
  constructor(context: Context, attributeSet: AttributeSet?): this(context, attributeSet, 0)
  constructor(context: Context, attributeSet: AttributeSet?, defaultStyle: Int): super(context, attributeSet, defaultStyle)

  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    val widthMode = MeasureSpec.getMode(widthMeasureSpec)
    val heightMode = MeasureSpec.getMode(heightMeasureSpec)
    val widthSize = MeasureSpec.getSize(widthMeasureSpec)
    val heightSize = MeasureSpec.getSize(heightMeasureSpec)

    measureChildren(widthMeasureSpec, heightMeasureSpec)

    val width = DisplayUtil.getScreenWidth(context) - DisplayUtil.dp2Px(context, 28f)
    val height = DisplayUtil.getScreenWidth(context)
    val realWidth = if (widthMode == MeasureSpec.EXACTLY) { widthSize } else { width }
    val realHeight = if (heightMode == MeasureSpec.EXACTLY) { heightSize } else { height }
    setMeasuredDimension(realWidth, realHeight)
  }

  override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
    val n = childCount
    for(i in 0 until n){
      val view = getChildAt(i)
      if(placedViewList.contains(view)){
        continue
      }

      val width = view.measuredWidth
      val height = view.measuredHeight

      val threeOfWidth = getWidth() / 3
      var pivotX = threeOfWidth + random.nextInt(threeOfWidth)
      val threeOfHeight = getHeight() / 3
      var pivotY = random.nextInt(threeOfHeight)

      val spiral: List<Point> = generateSpiral()
      spiral.forEach { point ->
        pivotX += point.x
        pivotY += point.y

        LogUtil.d("zfc", "current word ${data[i].word} center placed on ($pivotX, $pivotY")
        val rect = getVisualRect(pivotX, pivotY, width, height, view.rotation)
        var isOverlap = false
        placedViewList.forEach { placedView ->
          val viewRect = getVisualRect(placedView)
          if(isOverlap(rect, viewRect)){
            isOverlap = true
            return@forEach
          }
        }

        if (!isOverlap){
          LogUtil.d("zfc", "current word ${data[i].word} has placed")
          val newRect: Rect = getRect(pivotX, pivotY, width, height)
          view.layout(newRect.left, newRect.top, newRect.right, newRect.bottom)
          return@forEach
        }
      }
      placedViewList.add(view)
    }
  }

  private fun isOverlap(rect: Rect, viewRect: Rect) =
    rect.right >= viewRect.left
        && viewRect.right >= rect.left
        && rect.bottom >= viewRect.top
        && viewRect.bottom >= rect.top

  private fun getRect(pivotX: Int, pivotY: Int, width: Int, height: Int): Rect {
    val halfWidth = width / 2
    val halfHeight = height / 2
    return Rect(pivotX - halfWidth, pivotY - halfHeight, pivotX + halfWidth, pivotY + halfHeight)
  }

  private fun getVisualRect(view: View) = getVisualRect(
    (view.left + view.right) / 2,
    (view.top + view.bottom) / 2,
    view.measuredWidth,
    view.measuredHeight,
    view.rotation
  )

  private fun getVisualRect(
    pivotX: Int,
    pivotY: Int,
    width: Int,
    height: Int,
    rotation: Float
  ): Rect {
    var realWidth = width
    var realHeight = height
    if(rotation != 0f){
      realWidth = height
      realHeight = width
    }
    return getRect(pivotX, pivotY, realWidth, realHeight)
  }

  private fun generateSpiral(): List<Point> {
    val spiral = ArrayList<Point>()
    var A = 10
    val w = 5
    val theta = Math.PI
    val tenPI = 10 * Math.PI
    var t = 0.0
    while(t < tenPI){
      val x = (A * cos(w * t + theta)).toInt()
      val y = (A * sin(w * t + theta)).toInt()
      A += 1
      spiral.add(Point(x, y))
      t += 0.1
      LogUtil.d("zfc", "current generated point is ($x, $y)")
    }
    return spiral
  }

  fun setData(data: List<WordCloudBean>){
    this.data.clear()
    this.data.addAll(data)
    this.data.sort()
    if(this.data.isNotEmpty()){
      maxWeight = this.data.first().weight
      minWeight = this.data.last().weight
      weightDistance = maxWeight - minWeight
    } else {
      maxWeight = 1.0F
      minWeight = 1.0F
      weightDistance = 0.0F
    }
    placedViewList.clear()
    removeAllViews()
    data.forEach {
      addTextView(it.word, getTextSizeByWeight(it.weight))
    }
    requestLayout()
  }

  private fun getTextSizeByWeight(weight: Float): Float{
    val currDistance = weight - minWeight
    val ratio = currDistance / weightDistance
    return (ratio / BASE_SIZE_RATIO + BASE_TEXT_SIZE).toFloat()
  }

  private var params = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)

  private var rotates = floatArrayOf(
    0f, 90f, 270f
  )

  private fun addTextView(word: String, size: Float){
    val tv = TextView(context)
    tv.text = word
    tv.textSize = size
    tv.setTextColor(context.resources.getColor(textColors[random.nextInt(textColors.size)]))
    tv.rotation = rotates[random.nextInt(rotates.size)]
    //tv.setOnClickListener(this)
    addView(tv, params)
  }


  companion object {
    const val BASE_SIZE_RATIO = 0.2
    const val BASE_TEXT_SIZE = 14F
  }
}