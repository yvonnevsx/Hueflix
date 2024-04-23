package com.example.hueflix


import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import java.io.IOException
import kotlin.random.Random


class MainActivity2 : AppCompatActivity() {

    private lateinit var moodViewPager: ViewPager2
    private val client = OkHttpClient()
    private val bridgeIp = "192.168.178.250"
    private val username = "5jlmoycttcJ6yONKbA4ocxoMhfmNobnJlEC91gvh"
    private val lightId = "5"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.image_item_layout)

        moodViewPager = findViewById(R.id.moodViewPager)
        fetchLights()

        val nextItemVisiblePx = resources.getDimension(R.dimen.viewpager_next_item_visible)
        val currentItemHorizontalMarginPx =
            resources.getDimension(R.dimen.viewpager_current_item_horizontal_margin)
        val pageTranslationX = nextItemVisiblePx + currentItemHorizontalMarginPx
        moodViewPager.setPadding(nextItemVisiblePx.toInt(), 0, nextItemVisiblePx.toInt(), 0)
        moodViewPager.clipToPadding = false
        moodViewPager.clipChildren = false
        moodViewPager.offscreenPageLimit = 1

        moodViewPager.setPageTransformer { page, position ->
            page.translationX = -pageTranslationX * position
            val scaleFactor = 1 - (0.15f * kotlin.math.abs(position))
            page.scaleY = scaleFactor
            page.scaleX = scaleFactor
        }

        val moods = listOf(
            Mood("Movie Title 1", R.drawable.red_gradient),
            Mood("Movie Title 2", R.drawable.green_gradient),
            Mood("Movie Title 3", R.drawable.purple_gradient),
            Mood("Movie Title 4", R.drawable.blue_gradient),
            Mood("Movie Title 5", R.drawable.yellow_gradient)
        )

        val adapter = MoodPagerAdapter(moods)
        moodViewPager.adapter = adapter

        val backButton: ImageView = findViewById(R.id.backButton)

        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }


        startFloatingAnimations()
    }

    fun startFloatingAnimations() {
        val bubbleViews = listOf(
            findViewById<ImageView>(R.id.bubbleView),
            findViewById<ImageView>(R.id.bubbleView3),
            findViewById<ImageView>(R.id.bubbleView4),

        )

        // Randomized floating animations for the bubbles
        bubbleViews.forEach { view ->
            val startDelta = (Random.nextFloat() * -100).toFloat()
            val endDelta = (Random.nextFloat() * 100).toFloat()
            val duration = (Random.nextDouble(0.8, 1.2) * 3000).toLong()
            startFloatingAnimation(view, startDelta, endDelta, duration)
        }
    }

    private fun startFloatingAnimation(view: ImageView, startDelta: Float, endDelta: Float, duration: Long) {
        val animator = ObjectAnimator.ofFloat(view, "translationY", startDelta, endDelta)
        animator.duration = duration
        animator.repeatCount = ObjectAnimator.INFINITE
        animator.repeatMode = ObjectAnimator.REVERSE
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.start()
    }

    private fun fetchLights() {
        val request = Request.Builder()
            .url("http://$bridgeIp/api/$username/lights")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.use { responseBody ->
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")
                    val lightsJson = responseBody.string()
                    println(lightsJson)
                }
            }
        })
    }

    private fun toggleLight(turnOn: Boolean, xyColor: Pair<Float, Float>? = null) {
        val requestBody = if (xyColor != null) {
            RequestBody.create(
                "application/json".toMediaTypeOrNull(),
                "{\"on\":$turnOn,\"xy\": [${xyColor.first}, ${xyColor.second}]}"
            )
        } else {
            RequestBody.create(
                "application/json".toMediaTypeOrNull(),
                "{\"on\":$turnOn}"
            )
        }

        val request = Request.Builder()
            .url("http://$bridgeIp/api/$username/lights/$lightId/state")
            .put(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body
                try {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")
                    println(responseBody?.string())
                } finally {
                    responseBody?.close()
                }
            }
        })
    }

    data class Mood(val title: String, val imageResId: Int)

    inner class MoodPagerAdapter(private val moods: List<Mood>) :
        RecyclerView.Adapter<MoodPagerAdapter.MoodViewHolder>() {

        inner class MoodViewHolder(view: View) : RecyclerView.ViewHolder(view),
            View.OnClickListener {
            val moodImageView: ImageView = view.findViewById(R.id.moodImageView)

            init {
                moodImageView.setOnClickListener(this)
            }

            override fun onClick(v: View) {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val intent = Intent(this@MainActivity2, MovieSuggestionActivity::class.java)
                    when (position) {
                        0 -> {
                            toggleLight(true, Pair(0.7f, 0.3f)) // Red
                            intent.putExtra("Category", "Red")
                        } // Red
                        1 -> {
                            toggleLight(true, Pair(0.3f, 0.7f)) // Green
                            intent.putExtra("Category", "Green")
                        }

                        2 -> {
                            toggleLight(true, Pair(0.5f, 0.2f)) // Purple
                            intent.putExtra("Category", "Purple")
                        }

                        3 -> {
                            toggleLight(true, Pair(0.15f, 0.06f)) // Blue
                            intent.putExtra("Category", "Blue")
                        }

                        4 -> {
                            toggleLight(true, Pair(0.5f, 0.5f)) // Yellow
                            intent.putExtra("Category", "Yellow")
                        }
                    }
                    startActivity(intent)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoodViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_mood, parent, false)
            return MoodViewHolder(view)
        }

        override fun onBindViewHolder(holder: MoodViewHolder, position: Int) {
            val mood = moods[position]
            holder.moodImageView.setImageResource(mood.imageResId)
        }

        override fun getItemCount(): Int = moods.size
    }
}

