package com.franklinharper.jpmc.nycschools

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.franklinharper.jpmc.nycschools.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // We're using the Jetpack Navigation component; which launches the MainFragment.
        // This behavior is defined in navigation/nav_graph.xml
    }
}