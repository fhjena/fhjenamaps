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

	// TODO Kommentare hinzufügen
	private int state;							
	private int start;
	private int destination;
	private EditText start_view;
	private EditText destination_view;
	private TextView textoutput_start;
	private TextView textoutput_destination;
	private Pathfinding RB;
	private ArrayList<Node> Route;
	private GraphicalOutput go;			// beinhaltet grafische Darstellung der Testumgebung
	private CompassListener cl; // beinhaltet Lagesensor

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		cl = new CompassListener(); // neue Instanz zur Initialisierung des Sensors
		setContentView(R.layout.state_0);

	}

	//bei Buttonklick "Routing" wird state1.xml aufgerufen
	
	public void state0_Routing(final View view) {
		setContentView(R.layout.state_1);

	}

	//bei Buttonklick "GO" Start-u Zielpunkte aus Edittext einlesen
	
	public void state1_go(final View view) {
//TODO Kommentare einfügen
		start_view = (EditText) findViewById(R.id.tx_start);
		start = Integer.parseInt(start_view.getText().toString());

		destination_view = (EditText) findViewById(R.id.tx_destination);
		destination = Integer.parseInt(destination_view.getText().toString());
	
	//Start- u. Zielpunkt zum Test nochmal in TextView ausgeben
		// TODO brauchen wir das noch?
		textoutput_start = (TextView) findViewById(R.id.Text1);
		textoutput_start.setText(String.valueOf(start));

		textoutput_destination = (TextView) findViewById(R.id.Text2);
		textoutput_destination.setText(String.valueOf(destination));

		// TODO muss evtl. alles in OnCreate in State3
		RB = new Pathfinding();
		RB.compute_Path(start, destination);
		Route = RB.getPath();

		go = new GraphicalOutput(this, Route);
		cl.onResume(); // enable Lagesensor

		DisplayMetrics metrics = new DisplayMetrics(); // get Display
		getWindowManager().getDefaultDisplay().getMetrics(metrics); // get Dimensions of Display
		int display_width = metrics.widthPixels;
		int display_height = metrics.heightPixels;

		TextView tv = new TextView(this);
		for (int i = 0; i < Route.size(); i++) { // print all Node IDs
			tv.setText(tv.getText() + (Route.get(i).getID() + "\t\t\t"));
		}

		TableLayout tl = new TableLayout(this); // Layout contain Graphic and Text
		tl.addView(go, new LayoutParams(display_width, display_height - 150)); // add Graphic 
		tl.addView(tv); // add Text

		setContentView(tl); // show new Content

	}

	private class CompassListener implements SensorEventListener { // innere Klasse

		private SensorManager mSensorManager; // beinhaltet alle Sensoren
		private Sensor Magnetsensor; // nur Lagesensor
		private float f_old = 0; // Winkel merken

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
			float f_new = -event.values[0]; // Sensor auslesen
			if (Math.abs(f_new - f_old) > 3.5) { // Hysterese, damit Bild an der Schaltschwelle nicht hin- und herdreht
				if ((f_new % 5) > 2.5) // Sensor auf 5° Schritte auf bzw. abrunden
					f_new += 5;
				f_old = f_new - (f_new % 5); // neuen Winkel merken
				go.set_degree(f_old); // neuen Winkel an Ausgabe übergeben
				go.invalidate(); // Bild neu zeichnen
			}
		}

		public void onAccuracyChanged(Sensor sensor, int accuracy) { // not used
		}
	}
	

}