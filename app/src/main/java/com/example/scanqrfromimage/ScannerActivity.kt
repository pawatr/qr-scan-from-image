package com.example.scanqrfromimage

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.zxing.Result
import kotlinx.android.synthetic.main.activity_scanner.*
import me.dm7.barcodescanner.zxing.ZXingScannerView
import me.dm7.barcodescanner.zxing.ZXingScannerView.ResultHandler


class ScannerActivity : Activity(), ResultHandler {

    private lateinit var barcodeDetector: ZXingScannerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner)
        setupBarcodeScanner()
    }

    private fun setupBarcodeScanner() {
        // Barcode detector
        barcodeDetector = ZXingScannerView(this)
        barcodeDetector.id = CAMERA_ID
        barcodeDetector.setLaserEnabled(true)
        barcodeDetector.setBorderColor(ContextCompat.getColor(this, R.color.design_default_color_primary))

        scan_container.addView(barcodeDetector)
    }

    private fun activateDetector() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            barcodeDetector.setResultHandler(this)
            barcodeDetector.startCamera()
        } else {
            requestBarcodeScanner()
        }
    }

    private fun requestBarcodeScanner() {
        // Check camera permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            setupBarcodeScanner()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA)
        }
    }

    public override fun onResume() {
        super.onResume()
        activateDetector()
    }

    public override fun onPause() {
        super.onPause()
        barcodeDetector.stopCamera() // Stop camera on pause
    }

    override fun handleResult(rawResult: Result?) {
        barcodeDetector.stopCameraPreview()
        if (rawResult != null && rawResult.text.isNotBlank()){
            // Do something with the result here
            Log.v(TAG, rawResult.text) // Prints scan results
            Log.v(TAG, rawResult.barcodeFormat.toString()) // Prints the scan format (qrcode, pdf417 etc.)

            Toast.makeText(this, rawResult.text, Toast.LENGTH_SHORT).show()
            // If you would like to resume scanning, call this method below:
        } else {
            barcodeDetector.resumeCameraPreview(this)
        }
    }

    companion object {
        private const val TAG = "ScannerActivity"
        private const val CAMERA_ID = 9990
        private const val REQUEST_CAMERA = 9001
    }
}