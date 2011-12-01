package eu.AndroidTraining.Dashboard;

import java.util.ArrayList;

import android.app.Activity;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Startseite extends Activity {
	
	private int state;
	private int start;
	private int destination;
	private EditText start_view;
	private EditText destination_view;
	private TextView textoutput_start;
	private TextView textoutput_destination;
	private Routenberechnung RB;
	private ArrayList<Knoten> Route;
	private Gui my_gui;
    private CompassListener cl;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cl = new CompassListener();
        setContentView(R.layout.state_0);
        
    }
    
    public void state0_Routing(final View view){
    	 setContentView(R.layout.state_1);
    	
    }
    public void state1_go(final View view){
    	
    	start_view = (EditText) findViewById(R.id.tx_start);
    	start = Integer.parseInt(start_view.getText().toString());
    	
    	destination_view = (EditText)findViewById(R.id.tx_destination);
    	destination = Integer.parseInt(destination_view.getText().toString());
    	  	
    	textoutput_start = (TextView) findViewById(R.id.Text1);
    	textoutput_start.setText(String.valueOf(start));
    	    
    	textoutput_destination = (TextView) findViewById(R.id.Text2);
    	textoutput_destination.setText(String.valueOf(destination));
    	
    	// muss evtl. alles in OnCreate in State3
    	RB = new Routenberechnung();
    	RB.Berechne_Weg(start,destination);
    	Route = RB.getWeg();
    	
    	my_gui = new Gui(this, Route);
        setContentView(my_gui);
    	
//    	   	 getContentView(R.layout.);
    	
//    	setContentView(R.layout.state_3);
   	
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
 /*   public void wechsleActivity(final View view) {
    	startActivity(new Intent(this, Unteractivity.class));
    	//setContentView(R.layout.unteractivity);
    }*/
    
    

	
}