package eu.AndroidTraining.Dashboard;

import java.util.ArrayList;

import android.app.Activity;

import android.content.Context;
import android.hardware.*;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
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
	private float rotation = 0;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		cl = new CompassListener(); // neue Instanz zur Initialisierung des Sensors
//		System.out.println("Datenverzeichnisssssss:" + Environment.getDataDirectory());
		setContentView(R.layout.state_0);
		

	}

	//bei Buttonklick "Routing" wird state1.xml aufgerufen
	
	public void state0_Routing(final View view) {
		RB = new Pathfinding();
		RB.compute_Path(0, 4);
		Route = RB.getPath();
		go = new GraphicalOutput(getApplicationContext(), Route);

		setContentView(R.layout.state_1);
		final RelativeLayout rl = (RelativeLayout) findViewById(R.id.relativeLayout1);
		View v = findViewById(R.id.go_view);
		Button b1 = (Button) findViewById(R.id.button1);
		Button b2 = (Button) findViewById(R.id.button2);
		v = go;
		
		b1.setText("clockwise");
		b2.setText("counter clockwise");
		
		b1.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				go.set_degree(rotation+=90);
				go.invalidate();
			}
		});
		
		b2.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				go.set_degree(rotation-=90);
				go.invalidate();
			}
		});
		
		rl.removeAllViews();
		rl.addView(v);
		rl.addView(b1);
		rl.addView(b2);

	}

	//bei Buttonklick "GO" Start-u Zielpunkte aus Edittext einlesen
	
	public void state1_go(final View view) {
//		RB = new Pathfinding();
//		RB.compute_Path(start, destination);
//		Route = RB.getPath();
//		
//		GraphicalOutput v = new GraphicalOutput(getApplicationContext(),Route);
//		v = (GraphicalOutput) findViewById(R.id.view1);
//		setContentView(v);
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