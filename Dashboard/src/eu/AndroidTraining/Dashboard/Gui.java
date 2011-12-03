package eu.AndroidTraining.Dashboard;

import java.util.ArrayList;

import android.app.Activity;

import android.content.Context;
import android.hardware.*;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.*;

public class GUI<Ziel, Start> extends Activity {

	private int state;
	private int start;
	private int destination;
	private EditText start_view;
	private EditText destination_view;
	private TextView textoutput_start;
	private TextView textoutput_destination;
	private Pathfinding RB;
	private ArrayList<Node> Route;
	private GraphicalOutput go;
	private CompassListener cl; // beinhaltet Lagesensor

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		cl = new CompassListener(); // neue Instanz zur Initialisierung des Sensors
		setContentView(R.layout.state_0);

	}

	public void state0_Routing(final View view) {
		setContentView(R.layout.state_1);

	}

	public void state1_go(final View view) {

		start_view = (EditText) findViewById(R.id.tx_start);
		start = Integer.parseInt(start_view.getText().toString());

		destination_view = (EditText) findViewById(R.id.tx_destination);
		destination = Integer.parseInt(destination_view.getText().toString());

		textoutput_start = (TextView) findViewById(R.id.Text1);
		textoutput_start.setText(String.valueOf(start));

		textoutput_destination = (TextView) findViewById(R.id.Text2);
		textoutput_destination.setText(String.valueOf(destination));

		// muss evtl. alles in OnCreate in State3
		RB = new Pathfinding();
		RB.Berechne_Weg(start, destination);
		Route = RB.getPath();

		go = new GraphicalOutput(this, Route);
		cl.onResume(); // enable Lagesensor

		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		int display_width = metrics.widthPixels;
		int display_height = metrics.heightPixels;

		TextView tv = new TextView(this);
		for (int i = 0; i < Route.size(); i++) {
			tv.setText(tv.getText() + (Route.get(i).getID() + "\t\t\t"));
		}

		// TableLayout tl = new TableLayout(this);
		// tl.addView(tv);
		// tl.addView(go);

		TableLayout tl = new TableLayout(this);
		tl.addView(go, new LayoutParams(display_width, display_height - 150));
		tl.addView(tv);

		setContentView(tl);

		// getContentView(R.layout.);

		// setContentView(R.layout.state_3);

	}

	// @Override
	// protected void onResume()
	// {
	//
	// if (cl != null) {
	// super.onResume();
	// cl.onResume();
	// }
	// }
	//
	// @Override
	// protected void onStop()
	// {
	// if (cl != null) {
	// cl.onStop();
	// super.onStop();
	// }
	// }

	private class CompassListener implements SensorEventListener { // innere Klasse

		private SensorManager mSensorManager; // beinhaltet alle Sensoren
		private Sensor Magnetsensor; // nur Lagesensor
		private float f_alt = 0; // Winkel merken

		public CompassListener() { // Konstruktor
			mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE); // SensorManager holen
			Magnetsensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION); // Lagesensor auswählen
		}

		protected void onResume() { // enable Lagesensor
			mSensorManager.registerListener(this, Magnetsensor, SensorManager.SENSOR_DELAY_GAME);
		}

		protected void onStop() { // disable Lagesensor
			mSensorManager.unregisterListener(this);
		}

		public void onSensorChanged(SensorEvent event) {
			float f_neu = -event.values[0]; // Sensor auslesen
			if (Math.abs(f_neu - f_alt) > 3.5) { // Hysterese, damit Bild an der Schaltschwelle nicht hin- und herdreht
				if ((f_neu % 5) > 2.5) // Sensor auf 5° Schritte auf bzw. abrunden
					f_neu += 5;
				f_alt = f_neu - (f_neu % 5); // neuen Winkel merken
				go.set_degree(f_alt); // neuen Winkel an Ausgabe übergeben
				go.invalidate(); // Bild neu zeichnen
			}
		}

		public void onAccuracyChanged(Sensor sensor, int accuracy) { // not used
		}
	}
	/*
	 * public void wechsleActivity(final View view) { startActivity(new
	 * Intent(this, Unteractivity.class));
	 * //setContentView(R.layout.unteractivity); }
	 */

}