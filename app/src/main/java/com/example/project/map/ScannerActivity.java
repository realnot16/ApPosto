package com.example.project.map;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import java.io.IOException;

import com.example.project.R;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.material.snackbar.Snackbar;


public class ScannerActivity extends AppCompatActivity {


    private SurfaceView cameraView;
    private CameraSource cameraSource;
    private Vibrator v;
    private String code_parking;
    private Boolean canCloseReservation;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        canCloseReservation=getIntent().getBooleanExtra("canCloseReservation",false);
        setContentView(R.layout.map_layout_qr_scanner);
        cameraView = (SurfaceView) findViewById(R.id.scanner_camera_id);
        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        BarcodeDetector barcodeDetector =
                new BarcodeDetector.Builder(this)
                        .setBarcodeFormats(Barcode.QR_CODE)//QR_CODE)
                        .build();

        cameraSource = new CameraSource
                .Builder(this, barcodeDetector)
                .setRequestedPreviewSize(640, 480)
                .build();

        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {

                try {
                    cameraSource.start(cameraView.getHolder());
                } catch (IOException ie) {
                    Log.e("CAMERA SOURCE", ie.getMessage());
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {

                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0) sendResult(barcodes);
            }
        });

     }

    private void sendResult( SparseArray<Barcode> barcodes) {
        if(code_parking==null){
            code_parking=barcodes.valueAt(0).displayValue;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                //deprecated in API 26
                v.vibrate(300);
            }
            if (canCloseReservation==true){
                Intent intent=new Intent();
                intent.putExtra("parking_code",code_parking);
                setResult(Activity.RESULT_OK,intent);
                finish();
            }
            else{
                Toast.makeText(this,R.string.qr_snackbar,Toast.LENGTH_LONG).show();
                //Snackbar.make(findViewById(R.id.myCoordinatorLayout),R.string.qr_snackbar,Snackbar.LENGTH_LONG).show();
            }

        }
    }

}


