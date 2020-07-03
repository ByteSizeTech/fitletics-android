package com.example.fitletics

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.SurfaceHolder
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.barcode.BarcodeDetector
import kotlinx.android.synthetic.main.activity_connect_pc_qr_scanner.*
import java.lang.Exception


class ConnectPCQRActivity : AppCompatActivity() {

    private val requestCodeCameraPermission = 1001
    private lateinit var cameraSource: CameraSource
    private lateinit var detector: BarcodeDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connect_pc_qr_scanner)

        //confirms camera permissions if it were granted in advance
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            askForCameraPermission()    //asks if NOT granted
        }else
        {
            setupControls()     //sets up camera, QR detector and surface view
        }

        /*===========TEMPORARY CODE FOR TESTING===========*/

        //changes the activity from by tapping the preview (for now)

        camera_view.setOnClickListener()
        {
            val intent = Intent(this, BAnalysisOngoing::class.java)
            startActivity(intent)
        }

        /*===============================================*/
    }

    //function that sets up the activity provided the permissions were granted
    private fun setupControls()
    {
        detector = BarcodeDetector.Builder(this@ConnectPCQRActivity).build()
        cameraSource = CameraSource.Builder(this@ConnectPCQRActivity, detector).setAutoFocusEnabled(true).setRequestedPreviewSize(2246,1080).build()    //TODO get dimension of screen dynamically
        camera_view.holder.addCallback(surfaceCallBack)
    }

    //asks for permission if it was not granted
    private fun askForCameraPermission()
    {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), requestCodeCameraPermission)
    }

    //override function that asks the permissions
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == requestCodeCameraPermission && grantResults.isNotEmpty())
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                setupControls()     //sets up activity if user clicks "allow"
            }
            else
            {
                Toast.makeText(applicationContext, "Permission Denied!", Toast.LENGTH_SHORT).show()     // why would u do this :c
            }
        }
    }

    private val surfaceCallBack = object : SurfaceHolder.Callback   //object that holds the camera projection for the SurfaceView in XML

    {
        override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int)
        {
            //pass
        }

        override fun surfaceDestroyed(p0: SurfaceHolder)
        {
            cameraSource.stop()
        }

        @SuppressLint("MissingPermission") //<- permissions were checked prior in another function
        override fun surfaceCreated(surfaceHolder: SurfaceHolder)
        {
            try
            {
                cameraSource.start(surfaceHolder)
            }
            catch (exception: Exception)
            {
                Toast.makeText(applicationContext, "Something went wrong!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}