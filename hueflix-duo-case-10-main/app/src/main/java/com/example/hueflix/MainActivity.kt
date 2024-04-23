package com.example.hueflix

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.sqrt
import kotlin.random.Random

class MainActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var continueImage: ImageView
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private val shakeThreshold = 10f
    private val bubbleDestinations = mutableMapOf<ImageView, Pair<Float, Float>>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        continueImage = findViewById(R.id.imageViewShake)

        // Set up the sensor manager and accelerometer sensor
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)


        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)

        continueImage.setOnClickListener {
            val intent = Intent(this, MainActivity2::class.java)
            startActivity(intent)
        }


        startFloatingAnimation(findViewById(R.id.bubbleView), -20f, 20f, 3000)
        startFloatingAnimation(findViewById(R.id.bubbleView2), -30f, 30f, 3500)
        startFloatingAnimation(findViewById(R.id.bubbleView3), -30f, 30f, 3500)
        startFloatingAnimation(findViewById(R.id.bubbleView4), -30f, 30f, 3500)
    }

    private fun startFloatingAnimation(view: ImageView, startDelta: Float, endDelta: Float, duration: Long) {
        val animator = ObjectAnimator.ofFloat(view, "translationY", startDelta, endDelta)
        animator.duration = duration
        animator.repeatCount = ObjectAnimator.INFINITE
        animator.repeatMode = ObjectAnimator.REVERSE
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.start()
    }
    override fun onSensorChanged(event: SensorEvent) {
        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        val acceleration = sqrt(x * x + y * y + z * z) - SensorManager.GRAVITY_EARTH
        if (acceleration > shakeThreshold) {

            runBubbleAnimation()
        }
    }

    private fun runBubbleAnimation() {
        animateBubbles(findViewById(R.id.bubbleView), findViewById(R.id.bubbleView2), findViewById(R.id.bubbleView3), findViewById(R.id.bubbleView4), duration = 1000)
    }

    private fun getRandomPosition(displayMetrics: DisplayMetrics, view: View): Pair<Float, Float> {
        val maxX = displayMetrics.widthPixels - view.width
        val maxY = displayMetrics.heightPixels - view.height
        val randomX = Random.nextInt(maxX).toFloat()
        val randomY = Random.nextInt(maxY).toFloat()
        return Pair(randomX, randomY)
    }

    private fun animateBubbles(vararg bubbles: ImageView, duration: Long) {
        val displayMetrics = resources.displayMetrics
        val animators = mutableListOf<Animator>()

        bubbles.forEach { bubble ->
            val (randomX, randomY) = getRandomPosition(displayMetrics, bubble)
            val animatorX = ObjectAnimator.ofFloat(bubble, View.X, randomX)
            val animatorY = ObjectAnimator.ofFloat(bubble, View.Y, randomY)
            animators.add(animatorX)
            animators.add(animatorY)
        }

        AnimatorSet().apply {
            playTogether(animators)
            interpolator = AccelerateDecelerateInterpolator()
            this.duration = duration
            start()
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not used in this example
    }

    override fun onResume() {
        super.onResume()
        accelerometer?.also { accel ->
            sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }
}
