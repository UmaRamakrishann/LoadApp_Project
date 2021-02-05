package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
	context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

	private var widthSize = 0
	private var heightSize = 0
	private var paint: Paint
	private var progress: Float = 0f
	private var text = context.getString(R.string.button_download)
	private var arcMeasure: Float = 0f
	private var circleRectLeft = 0f
	private var circleRectRight = 0f
	private var circleRectTop = 0f
	private var circleRectBottom = 0f
	private var progressAnimator = ValueAnimator()
	private var buttonColor = 0
	private var textColor = 0

	private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
		when (new) {
			ButtonState.Loading -> {
				progressAnimator = ValueAnimator.ofFloat(0f, widthSize.toFloat()).apply {
					addUpdateListener { updatedAnimation ->
						progress = updatedAnimation.animatedValue as Float
						arcMeasure = 360f * progress / 100f
						invalidate()
					}
					duration = 2000
					start()
				}
			}
			ButtonState.Completed -> {
				progressAnimator.cancel()
				progress = 0f
				arcMeasure = 0f
				invalidate()
			}
		}
	}

	init {
		context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
			buttonColor = getColor(R.styleable.LoadingButton_buttonColor, 0)
			textColor = getColor(R.styleable.LoadingButton_textColor, 0)
		}
		paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
			textAlign = Paint.Align.CENTER
			textSize = resources.getDimension(R.dimen.default_text_size)
			color = buttonColor
		}
		isClickable = true
	}

	override fun performClick(): Boolean {
		if (super.performClick()) return true
		invalidate()
		return true
	}

	override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
		super.onSizeChanged(w, h, oldw, oldh)
		widthSize = w
		heightSize = h
		circleRectLeft = 0.7f * widthSize.toFloat()
		circleRectRight = 0.7f * widthSize.toFloat() + heightSize / 2
		circleRectTop = heightSize.toFloat() / 4
		circleRectBottom = heightSize.toFloat() * 3 / 4
	}

	fun setState(state: ButtonState) {
		buttonState = state
	}

	override fun onDraw(canvas: Canvas) {
		super.onDraw(canvas)
		paint.color = buttonColor
		canvas.drawRect(0f, 0f, widthSize.toFloat(), heightSize.toFloat(), paint)
		paint.color = ContextCompat.getColor(context, R.color.colorPrimaryDark)
		canvas.drawRect(0f, 0f, widthSize.toFloat() * progress / 100, heightSize.toFloat(), paint)
		paint.color = ContextCompat.getColor(context, R.color.colorAccent)
		canvas.drawArc(
			circleRectLeft,
			circleRectTop,
			circleRectRight,
			circleRectBottom,
			0f,
			arcMeasure,
			true,
			paint
		)
		paint.color = textColor
		text = when (buttonState) {
			ButtonState.Completed -> context.getString(R.string.button_download)
			ButtonState.Clicked -> context.getString(R.string.button_download)
			ButtonState.Loading -> context.getString(R.string.button_loading)
		}
		canvas.drawText(text, widthSize.toFloat() / 2, heightSize.toFloat() / 2, paint)
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