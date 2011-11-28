package com.defaultpackage;

import android.app.Activity;
import android.content.Context;
import android.graphics.*;
import android.hardware.*;
import android.os.Bundle;
import android.view.View;

public class CompassActivity extends Activity {

    private CompassListener ca;
    private SampleView mView;
    private float rotation = 0;

    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        ca = new CompassListener();
        mView = new SampleView(this);
        setContentView(mView);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        ca.onResume();
    }

    @Override
    protected void onStop()
    {
    	ca.onStop();
        super.onStop();
    }

    private class SampleView extends View {
        private Paint   mPaint = new Paint();
        private Path    mPath = new Path();
        private boolean mAnimate;

        public SampleView(Context context) {
            super(context);

            // Construct a wedge-shaped path
            mPath.moveTo(0, -50);
            mPath.lineTo(-20, 60);
            mPath.lineTo(0, 50);
            mPath.lineTo(20, 60);
            mPath.close();
        }

        protected void onDraw(Canvas canvas) {
            Paint paint = mPaint;

            canvas.drawColor(Color.WHITE);

            paint.setAntiAlias(true);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.FILL);

            int w = canvas.getWidth();
            int h = canvas.getHeight();
            int cx = w / 2;
            int cy = h / 2;

            canvas.translate(cx, cy);
            canvas.rotate(rotation);
            canvas.drawPath(mPath, mPaint);
        }

        protected void onAttachedToWindow() {
            mAnimate = true;
            super.onAttachedToWindow();
        }

        protected void onDetachedFromWindow() {
            mAnimate = false;
            super.onDetachedFromWindow();
        }
    }
    
    private class CompassListener implements SensorEventListener { // innere Klasse


        private SensorManager mSensorManager;
        private Sensor mSensor;

        public CompassListener() { // Konstruktor
    		mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
    	}


        protected void onResume() {
            mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_GAME);
        }

        protected void onStop() {
            mSensorManager.unregisterListener(this);
        }

        public void onSensorChanged(SensorEvent event) {
        	float f = -event.values[0]; // Sensor auslesen
        	if ( (f%5) > 2.5 ) // Sensor auf 5° Schritte auf bzw. abrunden
        		f += 5;
        	f = f - (f%5);
            rotation = f; // Winkel an äußere Klasse übergeben
            mView.invalidate(); // Bild neu zeichnen
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    }
}