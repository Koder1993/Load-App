package com.udacity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateInterpolator
import androidx.core.content.ContextCompat
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0

    private var valueAnimator = ValueAnimator()

    private var buttonMainColorBackground = ContextCompat.getColor(context, R.color.colorPrimary)
    private var buttonLoadColorBackground =
        ContextCompat.getColor(context, R.color.colorPrimaryDark)
    private var buttonTextColor = Color.WHITE
    private var buttonText = resources.getString(R.string.button_name)

    private var loadingCircleColor = ContextCompat.getColor(context, R.color.colorAccent)
    private var loadingButtonWidth = 0f
    private var endCircleAngle = 0f
    private var startCircleAngle = 0f

    private var buttonState: ButtonState by Delegates.observable(ButtonState.Completed) { _, _, new ->
        when (new) {
            ButtonState.Clicked -> {
                buttonText = resources.getString(R.string.button_name)
                updateButtonState(ButtonState.Loading)
                invalidate()
            }
            ButtonState.Loading -> {
                buttonText = context.resources.getString(R.string.button_loading)
                animateButtonAndCircle()
            }
            ButtonState.Completed -> {
                cancelValueAnimator()
                buttonText = resources.getString(R.string.button_completed)
                invalidate()
            }
        }
    }

    private val paintStyle = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        typeface = Typeface.MONOSPACE
        textSize = 40f
        textAlign = Paint.Align.CENTER
    }

    private fun animateButtonAndCircle() {
        valueAnimator = ValueAnimator.ofFloat(0f, widthSize.toFloat())
            .apply {
                duration = 1000
                addUpdateListener { animation ->
                    animation.interpolator = AccelerateInterpolator()
                    loadingButtonWidth = animation.animatedValue as Float
                    endCircleAngle = (loadingButtonWidth * 360) / widthSize
                    invalidate()
                }
                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationStart(animation: Animator) {
                        isEnabled = false
                    }

                    override fun onAnimationEnd(animation: Animator) {
                        isEnabled = true
                        updateButtonState(ButtonState.Completed)
                        loadingButtonWidth = 0f
                        endCircleAngle = 0f
                    }
                })
                start()
            }
    }

    private fun cancelValueAnimator() {
        valueAnimator.cancel()
    }

    fun updateButtonState(newButtonState: ButtonState) {
        buttonState = newButtonState
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {
            paintStyle.color = buttonMainColorBackground
            canvas.drawRect(0f, 0f, widthSize.toFloat(), heightSize.toFloat(), paintStyle)

            paintStyle.color = buttonLoadColorBackground
            canvas.drawRect(
                0f,
                0f,
                loadingButtonWidth,
                heightSize.toFloat(),
                paintStyle
            )

            paintStyle.color = buttonTextColor
            canvas.drawText(buttonText, widthSize / 2.0f, heightSize / 2.0f + 15.0f, paintStyle)

            paintStyle.color = loadingCircleColor
            canvas.drawArc(
                (widthSize - 220f),
                (heightSize / 2) - 40f,
                (widthSize - 150f),
                (heightSize / 2) + 40f,
                startCircleAngle,
                endCircleAngle,
                true,
                paintStyle
            )
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }
}