package com.dinia.message

import androidx.appcompat.app.AppCompatActivity
import android.os.*
import android.widget.ImageView
import com.squareup.picasso.Picasso

class HighResPicture : AppCompatActivity() {
    private lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_high_res_picture)
        imageView = findViewById(R.id.image)
        Picasso.get()
            .load("${App.BASE_URL}/img/${intent.getStringExtra("LINK")}")
            .into(imageView)
    }
}