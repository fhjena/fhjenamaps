package Zelos.UASJ_Maps;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.hardware.*;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;

public class GUI extends Activity {

	private Pathfinding RB;
	private ArrayList<ArrayList<Node>> Route;
	private GraphicalOutput go; // beinhaltet grafische Darstellung der Testumgebung
	private CompassListener cl; // beinhaltet Lagesensor

	/*
	 * TODO-liste und was so funktioniert:
	 * state1: Main menu: funktioniert
	 * state2: ShowPosition menu: auswahldinger fehlen
	 * state3: ShowPosition: onclicklistener fehlen
	 * state4: Routing menu: ein button ist, der rest fehlt
	 * state5: Routing: ohne funktion
	 * state6: Options: ohne funktion
	 * state7: Campus: ohne funktion
	 */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); // onCreate von Activity
		cl = new CompassListener(); // neue Instanz zur Initialisierung des Sensors
//		go = new GraphicalOutput(getApplicationContext()); TODO enable wenn Konstruktor vorhanden
		setContentView(R.layout.state_1); // state_1.xml anzeigen
	}
	
	@Override
	public void onStop() {
		cl.onStop(); // stoppt Ausführung des Magnet_Sensors
		super.onStop(); // onStop von Activity
	}

	public void launch_state_2(final View view) {
		setContentView(R.layout.state_2);
		// TODO wheel oder wheelpicker
	}

	public void launch_state_3(final View view) {
		// TODO begin löschen: routenberechnung, weil das hier ja showposition ist
		RB = new Pathfinding();
		RB.compute_Path(0, 7);
		Route = RB.getPath();
		go = new GraphicalOutput(getApplicationContext(), Route);
		// ende löschen --> setstatus(x);

		setContentView(R.layout.state_3);
		final RelativeLayout rl = (RelativeLayout) findViewById(R.id.relativeLayout3);
		View v = findViewById(R.id.view3);
		Button fminus = (Button) findViewById(R.id.floor_minus3);
		Button fplus = (Button) findViewById(R.id.floor_plus3);
		Button routing = (Button) findViewById(R.id.but_Routing3);
		Button campus = (Button) findViewById(R.id.but_Campus3);

		v = go;

		fminus.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO
				go.invalidate();
			}
		});

		fplus.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO
				go.invalidate();
			}
		});

		routing.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				launch_state_4(view);
			}
		});

		campus.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				launch_state_7(view);
			}
		});

		rl.removeAllViews();
		rl.addView(v);
		rl.addView(fminus);
		rl.addView(fplus);
		rl.addView(routing);
		rl.addView(campus);

		cl.onResume();
		// TODO vielleicht lieber in die override, aber momentan gehts auch ohne, da sich die app bei resume noch nicht den status gemerkt hat, also auch onResume und was es sonst noch so gibt mal overriden
	}

	public void launch_state_4(final View view) {
		setContentView(R.layout.state_4);
		// TODO wheel oder wheelpicker
	}

	public void launch_state_5(final View view) {
		setContentView(R.layout.state_5);
		// TODO
	}

	public void launch_state_6(final View view) {
		setContentView(R.layout.state_6);
		// TODO
	}

	public void launch_state_7(final View view) {
		setContentView(R.layout.state_7);
		// TODO
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
				go.set_degree(f_old); // neuen Winkel an Ausgabe übergeben
				go.invalidate(); // Bild neu zeichnen
			}
		}

		public void onAccuracyChanged(Sensor sensor, int accuracy) { // not used
		}
	}
}