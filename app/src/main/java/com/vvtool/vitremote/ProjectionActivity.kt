package com.vvtool.vitremote

import android.app.Activity
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Bundle

/** Invisible activity that shows the system "start screen capture" consent dialog. */
class ProjectionActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            val mpm = getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
            @Suppress("DEPRECATION")
            startActivityForResult(mpm.createScreenCaptureIntent(), 1)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        @Suppress("DEPRECATION")
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && data != null) {
            try {
                CaptureService.start(this, resultCode, data)
            } catch (e: Exception) {
                android.util.Log.e("vitRemote", "Could not start screen capture service", e)
                RemoteService.instance?.notifyAll("err", "Could not start screen capture: ${e.javaClass.simpleName}: ${e.message ?: "unknown error"}")
                android.widget.Toast.makeText(this, "Screen capture start failed: ${e.message ?: e.javaClass.simpleName}", android.widget.Toast.LENGTH_LONG).show()
            }
        } else {
            RemoteService.instance?.notifyAll("err", "Screen capture was denied on the phone")
        }
        finish()
    }
}
