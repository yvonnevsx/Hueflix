package com.example.hueflix

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView

class MovieDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_detail)

        val movieTitle = intent.getStringExtra("MOVIE_TITLE")
        val movieImageResId = intent.getIntExtra("MOVIE_IMAGE_RES_ID", 0)



        val movieTitleTextView: TextView = findViewById(R.id.movieTitleDetail)
        val movieImageView: ImageView = findViewById(R.id.movieImageDetail)

        movieTitle?.let { movieTitleTextView.text = it }
        if (movieImageResId != 0) {
            movieImageView.setImageResource(movieImageResId)
        }

    }
}