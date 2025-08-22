package com.zeezaglobal.posresturant.ui

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.zeezaglobal.posresturant.Application.POSApp
import com.zeezaglobal.posresturant.MainActivity
import com.zeezaglobal.posresturant.R
import com.zeezaglobal.posresturant.Repository.ApiRepository
import com.zeezaglobal.posresturant.Repository.GroupRepository
import com.zeezaglobal.posresturant.ViewModel.GroupViewModelFactory
import com.zeezaglobal.posresturant.ViewModel.LoadingViewmodel


class LoadingScreen : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar
    private val handler = Handler(Looper.getMainLooper())
    private var progressStatus = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading_screen)

        progressBar = findViewById(R.id.loadingBar)
        val application = this.application as POSApp
         val repository = ApiRepository((application).database.groupDao(),(application).database.itemDao())
         val viewModel: LoadingViewmodel by viewModels {
            GroupViewModelFactory(repository)
        }

            viewModel.addDatatoRoom()


        // Start loading progress
        Thread {
            while (progressStatus < 100) {
                progressStatus += 1
                handler.post {
                    progressBar.progress = progressStatus
                }
                try {

                    // Adjust speed of loading here (e.g., 50 ms per increment = ~5 seconds)
                    Thread.sleep(50)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
            // When done, go to MainActivity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }.start()
    }


}