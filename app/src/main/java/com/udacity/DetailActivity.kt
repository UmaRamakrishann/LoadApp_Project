package com.udacity

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_detail)
		setSupportActionBar(toolbar)

		filename_text.setText(intent.getStringExtra("url"))
		val status: String ?= intent.getStringExtra("status")
		status_text.setText(status)
		val color: Int = when (status.equals("Success")) {
			true -> Color.GREEN
			false -> Color.RED
		}
		status_text.setTextColor(color)
		ok_btn.setOnClickListener(View.OnClickListener { finish() })
	}

}
