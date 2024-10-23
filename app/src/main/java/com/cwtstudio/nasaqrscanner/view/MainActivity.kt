package com.cwtstudio.nasaqrscanner.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.cwtstudio.nasaqrscanner.databinding.ActivityMainBinding
import com.google.zxing.BinaryBitmap
import com.google.zxing.MultiFormatReader
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.HybridBinarizer

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        binding.bnStartScan.setOnClickListener {
            if (!isPermissionGranted()) requestPermission()
            else startActivity(Intent(this@MainActivity, ScanActivity::class.java))
        }


        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    val pickedBitmap = getBitmapFromUri(uri)

                    if (pickedBitmap != null) {
                        val data = scanQrCodeFromBitmap(pickedBitmap)

                        // If QR code scan result is not null, pass it to the next activity
                        if (data != null) {
                            val intent = Intent(this@MainActivity, ResultActivity::class.java)
                            intent.putExtra("scanned_data", data)
                            startActivity(intent)
                        } else {
                            Log.d("QRScanner", "No QR code found in the image.")
                            // Handle the case when no QR code is found
                        }
                    } else {
                        Log.d("PhotoPicker", "Failed to load bitmap from URI.")
                        // Handle the case when the bitmap couldn't be loaded
                    }

                    Log.d("PhotoPicker", "Selected URI: $uri")
                } else {
                    Log.d("PhotoPicker", "No media selected.")
                }

            }

        binding.btnPickQr.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }


    }

    private fun isPermissionGranted(): Boolean {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 101)
    }

    fun getBitmapFromUri(uri: Uri): Bitmap? {
        return try {
            val stream = this.contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(stream)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    fun scanQrCodeFromBitmap(bitmap: Bitmap): String? {
        return try {
            val intArray = IntArray(bitmap.width * bitmap.height)
            bitmap.getPixels(intArray, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
            val source = RGBLuminanceSource(bitmap.width, bitmap.height, intArray)
            val binaryBitmap = BinaryBitmap(HybridBinarizer(source))
            val reader = MultiFormatReader()
            val result = reader.decode(binaryBitmap)
            return result.text
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


}
