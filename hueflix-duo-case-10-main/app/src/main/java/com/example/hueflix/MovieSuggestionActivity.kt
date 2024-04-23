package com.example.hueflix

import android.content.Context
import android.content.Intent
import android.graphics.Movie
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import kotlin.math.sqrt

class MovieSuggestionActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var movieViewPager: ViewPager2
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private val shakeThreshold = 15f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_suggestion)

        // Set up the sensor manager and accelerometer sensor
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)


        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)

        movieViewPager = findViewById(R.id.movieViewPager)

        val nextItemVisiblePx = resources.getDimension(R.dimen.viewpager_next_item_visible)
        val currentItemHorizontalMarginPx = resources.getDimension(R.dimen.viewpager_current_item_horizontal_margin)
        val pageTranslationX = nextItemVisiblePx + currentItemHorizontalMarginPx
        movieViewPager.setPadding(nextItemVisiblePx.toInt(), 0, nextItemVisiblePx.toInt(), 0)
        movieViewPager.clipToPadding = false
        movieViewPager.clipChildren = false
        movieViewPager.offscreenPageLimit = 1

        movieViewPager.setPageTransformer { page, position ->
            page.translationX = -pageTranslationX * position
            val scaleFactor = 1 - (0.15f * kotlin.math.abs(position))
            page.scaleY = scaleFactor
            page.scaleX = scaleFactor
        }


        val category = intent.getStringExtra("Category")
        val movies = when (category) {
            "Red" -> {
                val redMovies = listOf(
                    Movie("Her", R.drawable.movieposter_red1, R.drawable.red_gradient,"Spike Jonze", 2013, 8.0f, "·  2h 6m", "·  R"  ),
                    Movie("The Batman", R.drawable.movieposter_red2, R.drawable.red_gradient, "Matt Reeves", 2022, 8.0f, "·  2h 55m", "·  R"  ),
                    Movie("Red Lights", R.drawable.movieposter_red3, R.drawable.red_gradient, "Rodrigo Cortés",2012, 6.2f, "·  1h 53m", "·  R"  ),
                    Movie("Black Widow", R.drawable.movieposter_red4, R.drawable.red_gradient, "Cate Shortland",2021, 6.8f, "·  2h 14m", "·  R"  ),
                    Movie("Star Wars: The Last Jedi", R.drawable.movieposter_red5, R.drawable.red_gradient, "Rian Johnson",2017, 7.0f, "·  2h 32m", "·  R"  ),
                    Movie("Deadpool", R.drawable.movieposter_red6, R.drawable.red_gradient,     "Tim Miller", 2016, 8.0f, "·  1h 48m", "·  R"  )
                )
                redMovies.shuffled().take(3)

            }
            "Green" -> {
                val greenMovies = listOf(
                    Movie("The Matrix", R.drawable.movieposter_green1, R.drawable.green_gradient,"Lana & Lilly Wachowski", 1999, 8.7f, "·  2h 16m", "·  R"  ),
                    Movie("Joker", R.drawable.movieposter_green2, R.drawable.green_gradient, "Todd Phillips",2019, 8.4f, "·  2h 2m", "·  R"  ),
                    Movie("The Green Hornet", R.drawable.movieposter_green3, R.drawable.green_gradient  ,"Michel Gondry", 2011, 5.8f, "·  1h 59m", "·  R" ),
                    Movie("Shrek", R.drawable.movieposter_green4, R.drawable.green_gradient, "Andrew Adamson",2001, 7.8f, "·  1h 30m", "·  R"  ),
                    Movie("Green Room", R.drawable.movieposter_green5, R.drawable.green_gradient, "Jeremy Saulnier",2015, 7.0f, "·  1h 35m", "·  R"  ),
                    Movie("Flubber", R.drawable.movieposter_green6, R.drawable.green_gradient,  "Les Mayfield",1997, 5.3f, "·  1h 33m", "·  R"  )
                )
                greenMovies.shuffled().take(3)
            }
            "Purple" -> listOf(
                Movie("John Wick 3: Parabellum", R.drawable.movieposter7, R.drawable.purple_gradient,   "Chad Stahelski", 2019, 7.4f, "·  2h 11m", "·  R" ),
                Movie("Avatar: The Way of Water", R.drawable.movieposter8, R.drawable.purple_gradient, "James Cameron", 2022, 7.8f, "·  2h 40m", "·  R" ),
                Movie("La La Land", R.drawable.movieposter9, R.drawable.purple_gradient,"Damien Chazelle", 2016, 8.0f, "·  2h 8m", "·  R" )
            )
            "Blue" -> listOf(
                Movie("The Revenant", R.drawable.movieposter10, R.drawable.blue_gradient, "Alejandro González Iñárritu", 2015, 8.0f, "·  2h 36m", "·  R" ),
                Movie("Corpse Bride", R.drawable.movieposter11, R.drawable.blue_gradient, "Tim Burton", 2005, 7.3f, "·  1h 17m", "·  R" ),
                Movie("The Shape of Water", R.drawable.movieposter12, R.drawable.blue_gradient, "Guillermo del Toro", 2017, 7.3f, "·  2h 3m", "·  R" )
            )
            "Yellow" -> listOf(
                Movie("Mad Max: Fury Road", R.drawable.movieposter13, R.drawable.yellow_gradient, "George Miller", 2015, 8.1f, "·  2h", "· R" ),
                Movie("Dune", R.drawable.movieposter14, R.drawable.yellow_gradient, "Denis Villeneuve", 2021, 8.3f, "·  2h 35m", "·  R" ),
                Movie("Asteroid City", R.drawable.movieposter15, R.drawable.yellow_gradient, "Wes Ball", 2022, 7.0f, "·  1h 45m", "·  R" )
            )
            else -> listOf(
                Movie("Her", R.drawable.movieposter_red1, R.drawable.red_gradient,"Spike Jonze", 2013, 8.0f, "·  2h 6m", "·  R"  ),
                Movie("The Batman", R.drawable.movieposter_red2, R.drawable.red_gradient, "Matt Reeves", 2022, 8.0f, "·  2h 55m", "·  R"  ),
                Movie("Red Lights", R.drawable.movieposter_red3, R.drawable.red_gradient, "Rodrigo Cortés",2012, 6.2f, "·  1h 53m", "·  R"  ),
            )
        }

        val backgroundDrawable = when (category) {
            "Red" -> R.drawable.red_gradient
            "Green" -> R.drawable.green_gradient
            "Purple" -> R.drawable.purple_gradient
            "Blue" -> R.drawable.blue_gradient
            "Yellow" -> R.drawable.yellow_gradient
            else -> R.drawable.red_gradient
        }
        findViewById<View>(R.id.contentMovies).setBackgroundResource(backgroundDrawable)


        val adapter = MoviePagerAdapter(movies)
        movieViewPager.adapter = adapter


        val backButton: ImageView = findViewById(R.id.backButton)



        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity2::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onSensorChanged(event: SensorEvent) {
        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        val acceleration = sqrt(x * x + y * y + z * z) - SensorManager.GRAVITY_EARTH
        if (acceleration > shakeThreshold) {
            // On shake detection go for a shuffle
           runMovieShuffle()
        }
    }

    private fun runMovieShuffle() {
        val category = intent.getStringExtra("Category")
        val movies = when (category) {
            "Red" -> {
                val redMovies = listOf(
                    Movie("Her", R.drawable.movieposter_red1, R.drawable.red_gradient,"Spike Jonze", 2013, 8.0f, "·  2h 6m", "·  R"  ),
                    Movie("The Batman", R.drawable.movieposter_red2, R.drawable.red_gradient, "Matt Reeves", 2022, 8.0f, "·  2h 55m", "·  PG-13"  ),
                    Movie("Red Lights", R.drawable.movieposter_red3, R.drawable.red_gradient, "Rodrigo Cortés",2012, 6.2f, "·  1h 53m", "·  R"  ),
                    Movie("Black Widow", R.drawable.movieposter_red4, R.drawable.red_gradient, "Cate Shortland",2021, 6.8f, "·  2h 14m", "·  PG-13"  ),
                    Movie("Star Wars: The Last Jedi", R.drawable.movieposter_red5, R.drawable.red_gradient, "Rian Johnson",2017, 7.0f, "·  2h 32m", "·  PG-13"  ),
                    Movie("Deadpool", R.drawable.movieposter_red6, R.drawable.red_gradient,     "Tim Miller", 2016, 8.0f, "·  1h 48m", "·  R"  )
                )
                redMovies.shuffled().take(3)
            }
            "Green" -> {
                val greenMovies = listOf(
                    Movie("The Matrix", R.drawable.movieposter_green1, R.drawable.green_gradient,"Lana & Lilly Wachowski", 1999, 8.7f, "·  2h 16m", "·  R"  ),
                    Movie("Joker", R.drawable.movieposter_green2, R.drawable.green_gradient, "Todd Phillips",2019, 8.4f, "·  2h 2m", "·  R"  ),
                    Movie("The Green Hornet", R.drawable.movieposter_green3, R.drawable.green_gradient  ,"Michel Gondry", 2011, 5.8f, "·  1h 59m", "·  PG-13" ),
                    Movie("Shrek", R.drawable.movieposter_green4, R.drawable.green_gradient, "Andrew Adamson",2001, 7.8f, "·  1h 30m", "·  PG"  ),
                    Movie("Green Room", R.drawable.movieposter_green5, R.drawable.green_gradient, "Jeremy Saulnier",2015, 7.0f, "·  1h 35m", "·  R"  ),
                    Movie("Flubber", R.drawable.movieposter_green6, R.drawable.green_gradient,  "Les Mayfield",1997, 5.3f, "·  1h 33m", "·  PG"  )
                )
                greenMovies.shuffled().take(3)
            }
            "Purple" -> {
                val purpleMovies =listOf(
                    Movie("John Wick 3: Parabellum", R.drawable.movieposter7, R.drawable.purple_gradient,   "Chad Stahelski", 2019, 7.4f, "·  2h 11m", "·  R" ),
                    Movie("Avatar: The Way of Water", R.drawable.movieposter8, R.drawable.purple_gradient, "James Cameron", 2022, 7.8f, "·  2h 40m", "·  PG-13" ),
                    Movie("La La Land", R.drawable.movieposter9, R.drawable.purple_gradient,"Damien Chazelle", 2016, 8.0f, "·  2h 8m", "·  PG-13" )
                )
                purpleMovies.shuffled().take(3)
            }
            "Blue" -> {
                val blueMovies = listOf(
                    Movie("The Revenant", R.drawable.movieposter10, R.drawable.blue_gradient, "Alejandro González Iñárritu", 2015, 8.0f, "·  2h 36m", "·  R" ),
                    Movie("Corpse Bride", R.drawable.movieposter11, R.drawable.blue_gradient, "Tim Burton", 2005, 7.3f, "·  1h 17m", "·  PG" ),
                    Movie("The Shape of Water", R.drawable.movieposter12, R.drawable.blue_gradient, "Guillermo del Toro", 2017, 7.3f, "·  2h 3m", "·  R" )
                )
                blueMovies.shuffled().take(3)
            }
            "Yellow" -> {
                val yellowMovies = listOf(
                    Movie("Mad Max: Fury Road", R.drawable.movieposter13, R.drawable.yellow_gradient, "George Miller", 2015, 8.1f, "·  2h", "· R" ),
                    Movie("Dune", R.drawable.movieposter14, R.drawable.yellow_gradient, "Denis Villeneuve", 2021, 8.3f, "·  2h 35m", "·  PG-13" ),
                    Movie("Asteroid City", R.drawable.movieposter15, R.drawable.yellow_gradient, "Wes Ball", 2022, 7.0f, "·  1h 45m", "·  PG-13" )
                )
                yellowMovies.shuffled().take(3)
            }
            else -> listOf(
                Movie("Her", R.drawable.movieposter_red1, R.drawable.red_gradient,"Spike Jonze", 2013, 8.0f, "·  2h 6m", "·  R"  ),
                Movie("The Batman", R.drawable.movieposter_red2, R.drawable.red_gradient, "Matt Reeves", 2022, 8.0f, "·  2h 55m", "·  PG-13"  ),
                Movie("Red Lights", R.drawable.movieposter_red3, R.drawable.red_gradient, "Rodrigo Cortés",2012, 6.2f, "·  1h 53m", "·  R"  ),
            )
        }


        val adapter = MoviePagerAdapter(movies)
        movieViewPager.adapter = adapter
    }


    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Unused override for error fix
    }

    data class Movie(
        val title: String,
        val imageResId: Int,
        val gradientResId: Int? = null,
        val director: String,
        val year: Int,
        val rating: Float,
        val duration: String,
        val guideline: String,
    )

    inner class MoviePagerAdapter(val movies: List<Movie>) : RecyclerView.Adapter<MovieViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_movie, parent, false)
            return MovieViewHolder(view)
        }

        override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
            val movie = movieViewPager.adapter?.let { adapter ->
                (adapter as? MoviePagerAdapter)?.movies?.get(position)
            }
            movie?.let { holder.bind(it) }
        }

        override fun getItemCount(): Int = movies.size
    }

    inner class MovieViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val movieImageView: ImageView = view.findViewById(R.id.movieImageView)
        private val movieTitleTextView: TextView = view.findViewById(R.id.movieTitleTextView)
        private val movieDirectorTextView: TextView = view.findViewById(R.id.movieDirectorTextView)
        private val movieYearTextView: TextView = view.findViewById(R.id.movieYearTextView)
//        private val movieRatingTextView: TextView = view.findViewById(R.id.movieRatingTextView)
        private val movieDurationTextView: TextView = view.findViewById(R.id.movieDurationTextView)
        private val movieGuidelineTextView: TextView = view.findViewById(R.id.movieGuidelineTextView)

        init {
            view.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val movie = movieViewPager.adapter?.let { adapter ->
                        (adapter as? MoviePagerAdapter)?.movies?.get(position)
                    }
                    movie?.let {
                        val intent = Intent(view.context, MovieDetailActivity::class.java).apply {
                            putExtra("MOVIE_TITLE", it.title)
                            putExtra("MOVIE_IMAGE_RES_ID", it.imageResId)
                        }
                        view.context.startActivity(intent)
                    }
                }
            }
        }

        fun bind(movie: Movie) {
            movieTitleTextView.text = movie.title
            movieDirectorTextView.text = "Directed by ${movie.director}"
            movieYearTextView.text = movie.year.toString()

            movieDurationTextView.text = movie.duration
            movieGuidelineTextView.text = movie.guideline

            // dumb corner radius for the image
            Glide.with(itemView.context)
                .load(movie.imageResId)
                .apply(RequestOptions().transform(RoundedCorners(28)))
                .into(movieImageView)
        }
    }
}