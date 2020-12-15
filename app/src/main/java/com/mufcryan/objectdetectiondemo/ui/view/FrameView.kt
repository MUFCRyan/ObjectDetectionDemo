package com.mufcryan.objectdetectiondemo.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View

class FrameView: View {
    constructor(context: Context): this(context, null)
    constructor(context: Context, attr: AttributeSet?): this(context, attr, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int): super(context, attrs, defStyleAttr){
        init()
    }

    private lateinit var paint: Paint
    private fun init(){
        paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = Color.RED
        paint.strokeCap = Paint.Cap.SQUARE
        paint.style = Paint.Style.STROKE
    }

    private var rect: Rect? = null

    fun setColor(color: String){
        paint.color = Color.parseColor(color)
    }

    fun setRect(left: Int, top: Int, right: Int, bottom: Int){
        rect = Rect(left, top, right, bottom)
        invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {
            rect?.let {shape ->
                canvas.drawRect(shape, paint)
            }
        }
    }
}