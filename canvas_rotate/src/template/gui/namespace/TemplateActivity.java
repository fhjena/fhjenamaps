package template.gui.namespace;


import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

public class TemplateActivity extends Activity {
    /** Called when the activity is first created. */

    private Gui my_gui;
    private CompassListener cl;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        my_gui = new Gui(this);
        cl = new CompassListener();
        setContentView(my_gui);
    }
    
    @Override
    protected void onResume()
    {
        super.onResume();
        cl.onResume();
    }

    @Override
    protected void onStop()
    {
    	cl.onStop();
        super.onStop();
    }
    
    private class CompassListener implements SensorEventListener { // innere Klasse


        private SensorManager mSensorManager;
        private Sensor mSensor;
        private float f_alt;

        public CompassListener() { // Konstruktor
    		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
    	}


        protected void onResume() {
            mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_GAME);
        }

        protected void onStop() {
            mSensorManager.unregisterListener(this);
        }

        public void onSensorChanged(SensorEvent event) {
        	float f_neu = -event.values[0]; // Sensor auslesen
        	if (Math.abs(f_neu - f_alt) > 3.5 ) { // Schmitt-Trigger, damit Bild an der Schaltschwelle nicht hin- und herdreht
        		if ( (f_neu%5) > 2.5 ) // Sensor auf 5° Schritte auf bzw. abrunden
            		f_neu += 5;
            	f_alt = f_neu - (f_neu%5);
        	}
        	my_gui.set_degree(f_alt); // neuen Winkel an Ausgabe übergeben
            my_gui.invalidate(); // Bild neu zeichnen
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    }
}




    
    
    
