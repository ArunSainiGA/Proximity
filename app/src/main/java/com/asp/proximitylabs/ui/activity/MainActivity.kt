package com.asp.proximitylabs.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.asp.proximitylabs.R
import com.asp.proximitylabs.ui.fragment.ListFragment


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, ListFragment.newInstance())
                .commitNow()
        }
    }
}