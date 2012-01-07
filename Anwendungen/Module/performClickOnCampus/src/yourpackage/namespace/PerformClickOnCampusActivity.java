package yourpackage.namespace;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class PerformClickOnCampusActivity extends Activity {
    /** Called when the activity is first created. */
	private CompassListener cl;
	private SampleView output;
	private OnTouchListener touch_on_campus = new OnTouchListener() { // OnTouchListener hinzufügen für Gebäudeauswahl bei Campusansicht
		public boolean onTouch(View v, MotionEvent event) {
			output.performClickOnCampus((int) event.getX(), (int) event.getY()); // x und y Werte übergeben
			output.invalidate();
			return true;
		}
	};
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cl = new CompassListener();
        output = new SampleView(getApplicationContext());
        cl.onResume();
        setContentView(output);
        output.setOnTouchListener(touch_on_campus);
    }
    
    @Override
    public void onDestroy() {
    	cl.onStop();
    	super.onStop();
    }
    
    private class SampleView extends View {
    	
    	private float rotation = 0;
    	private Point p = new Point(100,100);

		public SampleView(Context context) {
			super(context);
		}

		public void performClickOnCampus(int x, int y) {
			p.set(x, y);
		}

		public void set_degree(float f_old) {
			rotation = f_old;
		}
		
		@Override
		public void onDraw(Canvas canvas) {
			canvas.drawColor(Color.WHITE);
			canvas.rotate(rotation,p.x,p.y);
			canvas.drawLine(-10+p.x, -10+p.y, 10+p.x, 10+p.y, new Paint());
			canvas.drawLine(-10+p.x, 10+p.y, 10+p.x, -10+p.y, new Paint());
		}
    }
    
    private class CompassListener implements SensorEventListener { // innere Klasse

		private SensorManager mSensorManager; // beinhaltet alle Sensoren
		private Sensor Magnet_Sensor; // nur Lagesensor
		private float f_old = 0; // Winkel merken

		public CompassListener() { // Konstruktor
			mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE); // SensorManager holen
			Magnet_Sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION); // Lagesensor auswählen
		}

		protected void onResume() { // enable Lagesensor
			mSensorManager.registerListener(this, Magnet_Sensor, SensorManager.SENSOR_DELAY_GAME);
		}

		protected void onStop() { // disable Lagesensor
			mSensorManager.unregisterListener(this);
		}

		public void onSensorChanged(SensorEvent event) {
			float f_new = -event.values[0]; // Sensor auslesen
			if (Math.abs(f_new - f_old) > 3.5) { // Hysterese, damit Bild an der Schaltschwelle nicht hin- und herdreht
				if ((f_new % 5) > 2.5) // Sensor auf 5° Schritte auf bzw. abrunden
					f_new += 5;
				f_old = f_new - (f_new % 5); // neuen Winkel merken
				if (output != null) {
					output.set_degree(f_old); // neuen Winkel an Ausgabe übergeben
					output.invalidate(); // Bild neu zeichnen
				}
			}
		}

		public void onAccuracyChanged(Sensor sensor, int accuracy) { // not used
		}
	}
}