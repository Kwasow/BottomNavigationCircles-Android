package com.github.kwasow.bottomnavigationcirclesexample

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.kwasow.bottomnavigationcircles.BottomNavigationCircles

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val bottomNavigation = findViewById<BottomNavigationCircles>(R.id.bottomNavigation)
        bottomNavigation.circleColor = Color.RED
        // bottomNavigation.backgroundShape = BottomNavigationCircles.Shape.RoundedRectangle
    }
}
