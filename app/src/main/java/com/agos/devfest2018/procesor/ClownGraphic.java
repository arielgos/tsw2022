package com.agos.devfest2018.procesor;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.agos.devfest2018.util.GraphicOverlay;
import com.google.android.gms.vision.CameraSource;
import com.google.firebase.ml.vision.common.FirebaseVisionPoint;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceContour;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceLandmark;

import java.util.List;

/**
 * Graphic instance for rendering face position, orientation, and landmarks within an associated
 * graphic overlay view.
 */
public class ClownGraphic extends GraphicOverlay.Graphic {

    private static final float FACE_POSITION_RADIUS = 10.0f;
    private static final float BOX_STROKE_WIDTH = 5.0f;


    private final Paint facePositionPaint;
    private final Paint nosePaint;
    private final Paint boxPaint;

    private volatile FirebaseVisionFace firebaseVisionFace;

    public ClownGraphic(GraphicOverlay overlay) {
        super(overlay);


        facePositionPaint = new Paint();
        facePositionPaint.setColor(Color.BLACK);

        nosePaint = new Paint();
        nosePaint.setColor(Color.RED);
        nosePaint.setStyle(Paint.Style.FILL_AND_STROKE);

        boxPaint = new Paint();
        boxPaint.setColor(Color.BLACK);
        boxPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        boxPaint.setStrokeWidth(BOX_STROKE_WIDTH);
    }


    /**
     * Updates the face instance from the detection of the most recent frame. Invalidates the relevant
     * portions of the overlay to trigger a redraw.
     */
    public void updateFace(FirebaseVisionFace face, int facing) {
        firebaseVisionFace = face;
        postInvalidate();
    }

    /**
     * Draws the face annotations for position on the supplied canvas.
     */
    @Override
    public void draw(Canvas canvas) {
        FirebaseVisionFace face = firebaseVisionFace;
        if (face == null) {
            return;
        }

        // Draws a circle at the position of the detected face, with the face's track id below.
        float x = translateX(face.getBoundingBox().centerX());
        float y = translateY(face.getBoundingBox().centerY());

        float xOffset = scaleX(face.getBoundingBox().width() / 2.0f);
        float yOffset = scaleY(face.getBoundingBox().height() / 2.0f);

        canvas.drawCircle(x, y, FACE_POSITION_RADIUS, facePositionPaint);

        canvas.drawRect(x - (xOffset - 50), y - 10, x + (xOffset - 50), y + 10, boxPaint);

        canvas.drawCircle(x + (xOffset / 2) - 20, y, (face.getBoundingBox().height() / 8), boxPaint);
        canvas.drawCircle(x - (xOffset / 2) + 20, y, (face.getBoundingBox().height() / 8), boxPaint);

        drawLandmarkPosition(canvas, face, FirebaseVisionFaceLandmark.NOSE_BASE);
    }

    private void drawLandmarkPosition(Canvas canvas, FirebaseVisionFace face, int landmarkID) {
        FirebaseVisionFaceLandmark landmark = face.getLandmark(landmarkID);
        if (landmark != null) {
            if (landmarkID == FirebaseVisionFaceLandmark.NOSE_BASE) {
                FirebaseVisionPoint point = landmark.getPosition();
                canvas.drawCircle(
                        translateX(point.getX()),
                        translateY(point.getY()),
                        (face.getBoundingBox().height() / 10), nosePaint);
            }
        }
    }


}