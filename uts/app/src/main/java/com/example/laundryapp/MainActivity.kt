package com.example.laundryapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.laundryapp.databinding.ActivityMainBinding

/**
 * Layar pembuka. Setelah sebentar, memeriksa apakah pengguna sudah login.
 * Jika sudah, langsung ke Dashboard. Jika belum, ke Login.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Menunggu sebentar agar splash terlihat, lalu berpindah halaman
        Handler(Looper.getMainLooper()).postDelayed({ arahkanPengguna() }, SPLASH_DELAY)
    }

    // Memeriksa status login yang tersimpan di SharedPreferences
    private fun arahkanPengguna() {
        val prefs = getSharedPreferences(LoginActivity.PREFS_NAME, MODE_PRIVATE)
        val sudahLogin = prefs.getBoolean(LoginActivity.KEY_LOGGED_IN, false)

        val tujuan = if (sudahLogin) DashboardActivity::class.java else LoginActivity::class.java
        startActivity(Intent(this, tujuan))
        finish()
    }

    companion object {
        private const val SPLASH_DELAY = 1000L   // dalam milidetik
    }
}
