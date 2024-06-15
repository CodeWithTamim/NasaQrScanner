package com.cwtstudio.nasaqrscanner.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.ScanMode
import com.cwtstudio.nasaqrscanner.databinding.ActivityScanBinding

class ScanActivity : AppCompatActivity() {
    private val binding by lazy { ActivityScanBinding.inflate(layoutInflater) }
    private val codeScanner by lazy { CodeScanner(this, binding.scannerView) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        // params
        codeScanner.camera = CodeScanner.CAMERA_BACK
        codeScanner.formats = CodeScanner.ALL_FORMATS
        codeScanner.autoFocusMode = AutoFocusMode.SAFE
        codeScanner.scanMode = ScanMode.SINGLE
        codeScanner.isAutoFocusEnabled = true
        codeScanner.isFlashEnabled = false

        // callbacks for scanner
        codeScanner.setDecodeCallback {
            val intent = Intent(this@ScanActivity, ResultActivity::class.java)
            intent.putExtra("scanned_data", it.text)
            startActivity(intent)
        }
        codeScanner.setErrorCallback {
            runOnUiThread {
                Toast.makeText(
                    this@ScanActivity,
                    "Camera initialization error: ${it.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        binding.scannerView.setOnClickListener { codeScanner.startPreview() }
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        super.onPause()
        codeScanner.releaseResources()
    }
}
