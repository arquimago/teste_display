package net.alvesnet.testegertec

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.view.MotionEvent
import android.view.View
import android.widget.GridLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import net.alvesnet.testegertec.databinding.ActivityDisplayTestBinding

class DisplayTestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDisplayTestBinding
    private val numRows = 8
    private val numColumns = 5
    private val cells = numRows * numColumns
    private var testedCells = 0
    private var timer: CountDownTimer? = null
    private var testTimeoutMillis = 10_000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDisplayTestBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupButton()
        setupGridLayout()
        startTimer()
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
    }

    private fun setupGridLayout() {
        with(binding){
            gridLayout.rowCount = numRows
            gridLayout.columnCount = numColumns

            for (row in 0 until numRows) {
                for (col in 0 until numColumns) {
                    val view = View(this@DisplayTestActivity)
                    view.tag = false
                    val params = GridLayout.LayoutParams().apply {
                        width = 0
                        height = 0
                        columnSpec = GridLayout.spec(col, 1f)
                        rowSpec = GridLayout.spec(row, 1f)
                        setMargins(2, 2, 2, 2)
                    }

                    val startColor = Color.LTGRAY
                    view.setBackgroundColor(startColor)

                    gridLayout.addView(view, params)
                }
            }
        }
        setupGridLayoutTouch()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupGridLayoutTouch(){
        with(binding){
            gridLayout.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_MOVE) {
                    val cellWidth = gridLayout.width / numColumns
                    val cellHeight = gridLayout.height / numRows

                    val col = (event.x / cellWidth).toInt()
                    val row = (event.y / cellHeight).toInt()

                    if (row in 0 until numRows && col in 0 until numColumns) {
                        val index = row * numColumns + col
                        val cell = gridLayout.getChildAt(index)
                        val alreadyTested = cell.tag as? Boolean ?: false

                        if (!alreadyTested) {
                            testedCells++
                            cell.tag = true
                            cell.setBackgroundColor(Color.GREEN)
                        }

                        if (testedCells == cells) {
                            passed.visibility = View.VISIBLE
                        }
                    }
                }
                true
            }
        }
    }

    private fun startTimer() {
        timer = object : CountDownTimer(testTimeoutMillis, 1_000L) {

            override fun onTick(millisUntilFinished: Long) { /* do nothing */ }

            override fun onFinish() {
                if (testedCells != cells) {
                    Toast.makeText(
                        this@DisplayTestActivity,
                        getString(R.string.failed),
                        Toast.LENGTH_SHORT)
                        .show()
                    if (!isFinishing && !isDestroyed) {
                        onBackPressedDispatcher.onBackPressed()
                    }
                }
            }

        }.start()
    }

    private fun setupButton(){
        binding.passed.setOnClickListener {
            Toast.makeText(
                this@DisplayTestActivity,
                getString(R.string.passed),
                Toast.LENGTH_SHORT)
                .show()
            onBackPressedDispatcher.onBackPressed()
        }
    }
}