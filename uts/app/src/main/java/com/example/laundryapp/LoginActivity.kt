package com.example.laundryapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.laundryapp.databinding.ActivityLoginBinding

/**
 * Halaman login dengan validasi input dan login lokal sederhana.
 * Akun demo: admin / admin123
 */
class LoginActivity : AppCompatActivity() {

    companion object {
        // Nama penyimpanan sesi dan kunci status login, juga dipakai MainActivity dan Dashboard
        const val PREFS_NAME = "LaundryAppPrefs"
        const val KEY_LOGGED_IN = "is_logged_in"

        private const val DEMO_USERNAME = "admin"
        private const val DEMO_PASSWORD = "admin123"
    }

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener { prosesLogin() }
    }

    // Memvalidasi input lalu mencocokkan dengan akun demo
    private fun prosesLogin() {
        val username = binding.etUsername.text?.toString()?.trim().orEmpty()
        val password = binding.etPassword.text?.toString().orEmpty()

        // Membersihkan pesan error dari percobaan sebelumnya
        binding.tilUsername.error = null
        binding.tilPassword.error = null

        var inputValid = true
        if (username.isEmpty()) {
            binding.tilUsername.error = getString(R.string.error_username_kosong)
            inputValid = false
        }
        if (password.isEmpty()) {
            binding.tilPassword.error = getString(R.string.error_password_kosong)
            inputValid = false
        }
        if (!inputValid) return

        if (username == DEMO_USERNAME && password == DEMO_PASSWORD) {
            simpanSesi()
            startActivity(Intent(this, DashboardActivity::class.java))
            finish()   // Login ditutup agar tidak kembali dengan tombol Back
        } else {
            Toast.makeText(this, R.string.error_login_gagal, Toast.LENGTH_SHORT).show()
        }
    }

    // Menandai pengguna sudah login agar tidak diminta login ulang
    private fun simpanSesi() {
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_LOGGED_IN, true)
            .apply()
    }
}
