package com.cwtstudio.nasaqrscanner.view

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.cwtstudio.nasaqrscanner.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity() {
    private val binding by lazy { ActivityResultBinding.inflate(layoutInflater) }
    private var scannedData: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        scannedData = intent.getStringExtra("scanned_data")
        binding.txtResult.text = scannedData

        binding.btnCopy.setOnClickListener {
            if (scannedData != null) {
                val clipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                val clipdata = ClipData.newPlainText("Qr Scanned Data", scannedData)
                clipboardManager.setPrimaryClip(clipdata)
                Toast.makeText(this, "Copied to clipboard", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Something went wrong...", Toast.LENGTH_SHORT).show()
            }
        }
        binding.btnShare.setOnClickListener {
            if (scannedData != null) {
                val intent = Intent(Intent.ACTION_SEND)
                intent.setType("text/plain")
                intent.putExtra(Intent.EXTRA_TEXT, scannedData)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Nothing to share...", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
