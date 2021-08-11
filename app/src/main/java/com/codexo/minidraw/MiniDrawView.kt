package com.codexo.minidraw

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import kotlin.math.abs

/**
 * a Draw canvas with a undo and redo action
 */
class MiniDrawView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val TAG = MiniDrawView::class.java.simpleName

    private val backgroundColor = Color.WHITE
    private val drawColor = Color.BLACK

    private var motionTouchEventX = 0f
    private var motionTouchEventY = 0f

    private var currentX = 0f
    private var currentY = 0f

    private val touchTolerance = ViewConfiguration.get(context).scaledTouchSlop

    private lateinit var frame: Rect
    private val strWidth = 10f

    private val paint = Paint()

    private var currPath = Path()
    private val paths = ArrayList<Path>()
    private val pathStash = ArrayList<Path>()

    init {
        paint.apply {   // Set up the paint with which to draw.
            color = drawColor
            // Smooths out edges of what is drawn without affecting shape.
            isAntiAlias = true
            // Dithering affects how colors with higher-precision than the device are down-sampled.
            isDither = true
            style = Paint.Style.STROKE // default: FILL
            strokeJoin = Paint.Join.ROUND // default: MITER
            strokeCap = Paint.Cap.ROUND // default: BUTT
            strokeWidth = strWidth // default: Hairline-width (really thin)
        }


    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        // Calculate a rectangular frame around the picture.
        val inset = 40
        frame = Rect(inset, inset, w - inset, h - inset)
    }

    override fun onDraw(canvas: Canvas) {
        // Draw the drawing so far
        for (path in paths) {
            canvas.drawPath(path, paint)
        }
        canvas.drawPath(currPath, paint)
        canvas.drawRect(frame, paint)
    }

    private fun touchStart() {
        currPath.reset()
        currPath.moveTo(motionTouchEventX, motionTouchEventY)
        currentX = motionTouchEventX
        currentY = motionTouchEventY
    }

    private fun touchMove() {
        val dx = abs(motionTouchEventX - currentX)
        val dy = abs(motionTouchEventY - currentY)
        if (dx >= touchTolerance || dy >= touchTolerance) {
            currPath.quadTo(
                currentX,
                currentY,
                (motionTouchEventX + currentX) / 2,
                (motionTouchEventY + currentY) / 2
            )
            currentX = motionTouchEventX
            currentY = motionTouchEventY
        }
    }

    private fun touchUp() {
        currPath.lineTo(currentX, currentY)
        paths.add(currPath)   // Add the current path to the drawing stash so far
        currPath = Path()    // Reset the path so it doesn't get drawn again.
        Log.d(TAG, "touchUp: $paths")
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        motionTouchEventX = event.x
        motionTouchEventY = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> touchStart()
            MotionEvent.ACTION_MOVE -> touchMove()
            MotionEvent.ACTION_UP -> touchUp()
        }
        invalidate()
        return true
    }

    fun onUndo() {
        if (paths.isNotEmpty()) {
            pathStash.add(paths.removeAt(paths.lastIndex))
            invalidate()
        }
    }

    fun onRedo() {
        if (pathStash.isNotEmpty()) {
            paths.add(pathStash.removeAt(pathStash.lastIndex))
            invalidate()
        }
    }

    fun onClearDrawing() {
        if (paths.isNotEmpty()) {
            paths.clear()
            pathStash.clear()
            invalidate()
        }
    }
}