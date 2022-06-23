package com.agos.tsf2022;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.agos.tsf2022.procesor.ClownDetectionProcessor;
import com.agos.tsf2022.procesor.TextRecognitionProcessor;
import com.agos.tsf2022.util.CameraSource;
import com.agos.tsf2022.util.CameraSourcePreview;
import com.agos.tsf2022.procesor.FaceDetectionProcessor;
import com.agos.tsf2022.util.GraphicOverlay;

public class Main extends AppCompatActivity {


    private CameraSource cameraSource = null;
    private CameraSourcePreview preview;
    private GraphicOverlay graphicOverlay;

    private FaceDetectionProcessor faceDetectionProcessor = null;
    private TextRecognitionProcessor textRecognitionProcessor = null;
    private ClownDetectionProcessor clownDetectionProcessor = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        preview = findViewById(R.id.firePreview);
        graphicOverlay = findViewById(R.id.fireFaceOverlay);

        createCameraSource();

        findViewById(R.id.faces).setSelected(true);

        findViewById(R.id.faces).setOnClickListener(view -> {
            if (faceDetectionProcessor != null) {
                faceDetectionProcessor.setWithText(!faceDetectionProcessor.isWithText());
            } else {
                faceDetectionProcessor = new FaceDetectionProcessor();
                cameraSource.setMachineLearningFrameProcessor(faceDetectionProcessor);
                textRecognitionProcessor = null;
                clownDetectionProcessor = null;

                findViewById(R.id.faces).setSelected(true);
                findViewById(R.id.text).setSelected(false);
                findViewById(R.id.clown).setSelected(false);
            }
        });

        findViewById(R.id.text).setOnClickListener(view -> {
            textRecognitionProcessor = new TextRecognitionProcessor();
            cameraSource.setMachineLearningFrameProcessor(textRecognitionProcessor);
            faceDetectionProcessor = null;
            clownDetectionProcessor = null;

            findViewById(R.id.faces).setSelected(false);
            findViewById(R.id.text).setSelected(true);
            findViewById(R.id.clown).setSelected(false);
        });

        findViewById(R.id.clown).setOnClickListener(view -> {
            clownDetectionProcessor = new ClownDetectionProcessor();
            cameraSource.setMachineLearningFrameProcessor(clownDetectionProcessor);
            faceDetectionProcessor = null;
            textRecognitionProcessor = null;

            findViewById(R.id.faces).setSelected(false);
            findViewById(R.id.text).setSelected(false);
            findViewById(R.id.clown).setSelected(true);
        });

    }

    private void createCameraSource() {
        if (cameraSource == null) {
            cameraSource = new CameraSource(this, graphicOverlay);
        }
        faceDetectionProcessor = new FaceDetectionProcessor();
        cameraSource.setMachineLearningFrameProcessor(faceDetectionProcessor);
        cameraSource.setFacing(CameraSource.CAMERA_FACING_BACK);
    }

    private void startCameraSource() {
        if (cameraSource != null) {
            try {
                preview.start(cameraSource, graphicOverlay);
            } catch (Exception e) {
                cameraSource.release();
                cameraSource = null;
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        startCameraSource();

    }

    @Override
    protected void onPause() {
        super.onPause();
        preview.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (cameraSource != null) {
            cameraSource.release();
        }
    }
}
