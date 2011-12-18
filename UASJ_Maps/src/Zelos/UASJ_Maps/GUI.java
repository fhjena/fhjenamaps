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

	private GraphicalOutput output; // beinhaltet grafische Darstellung der Testumgebung
	private CompassListener cl; // beinhaltet Lagesensor

	/*
	 * TODO-liste und was so funktioniert:
	 * state1: Main menu: funktioniert
	 * state2: ShowPosition menu: auswahldinger fehlen
	 * state3: ShowPosition view: onclicklistener fehlen
	 * state4: Routing menu: ein button ist, der rest fehlt
	 * state5: Routing view: ohne funktion
	 * state6: Options: ohne funktion
	 * state7: Campus view: ohne funktion
	 */

	@Override
	public void onCreate(Bundle savedInstanceState) { // App wird gestartet
		super.onCreate(savedInstanceState); // onCreate von Activity
		cl = new CompassListener(); // neue Instanz zur Initialisierung des Sensors
//		output = new GraphicalOutput(getApplicationContext()); TODO enable wenn Konstruktor vorhanden
		launch_state_1(); // Hauptmenu anzeigen
		// TODO begin löschen: routenberechnung, weil das hier ja showposition ist
		Pathfinding RB = new Pathfinding();
		RB.compute_Path(0, 0);
		ArrayList<ArrayList<Node>> Route = RB.getPath();
		output = new GraphicalOutput(getApplicationContext(), Route);
		// ende löschen
	}
	
	@Override
	public void onPause() { // App wird beendet
		cl.onPause(); // stoppt Ausführung des Magnetsensors
		super.onPause(); // onPause von Activity
	}
	
	private void launch_state_1() {
		setContentView(R.layout.state_1); // state_1.xml anzeigen
		
		// OnClickListener für die Buttons:
		findViewById(R.id.but_ShowPosition1).setOnClickListener(new OnClickListener() {
			// OnClickListener für ShowPosition
			public void onClick(View v) {
				launch_state_2(); // ShowPositions menu
			}
		});
		
		findViewById(R.id.but_Routing1).setOnClickListener(new OnClickListener() {
			// OnClickListener für Routing
			public void onClick(View v) {
				launch_state_4(); // Routing menu
			}
		});
		
		findViewById(R.id.but_Campus1).setOnClickListener(new OnClickListener() {
			// OnClickListener für Campus
			public void onClick(View v) {
				launch_state_7(); // Campus view
			}
		});
		
		findViewById(R.id.but_options1).setOnClickListener(new OnClickListener() {
			// OnClickListener für Options
			public void onClick(View v) {
				launch_state_6(); // Options
			}
		});
	}

	private void launch_state_2() {
		setContentView(R.layout.state_2); // state_2.xml anzeigen
		findViewById(R.id.but_Go2).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				EditText roomInput = (EditText) findViewById(R.id.editText2); // TODO abfangen von fehleingaben
				Pathfinding pf = new Pathfinding();
				pf.compute_Path(Integer.parseInt(roomInput.getText().toString()), Integer.parseInt(roomInput.getText().toString()));
				System.out.println(pf.getPath());
				System.out.println(pf.getPath().get(0)); // TODO das ding ist leer --> nils fragen
				launch_state_3(pf.getPath().get(0).get(0)); // ShowPosition view
			}
		});
		// TODO wheel oder wheelpicker
	}
 
	private void launch_state_3(Node position) {
		output.set_state_position(position);

		setContentView(R.layout.state_3); // state_3.xml anzeigen
		// Elemente der Anzeige holen, damit sie bearbeitet werden können:
		final RelativeLayout rl = (RelativeLayout) findViewById(R.id.relativeLayout3);
		View backgroundView = findViewById(R.id.view3);
		final Button fminus = (Button) findViewById(R.id.floor_minus3);
		final Button fplus = (Button) findViewById(R.id.floor_plus3);
		Button routing = (Button) findViewById(R.id.but_Routing3);
		Button campus = (Button) findViewById(R.id.but_Campus3);

		backgroundView = output; // grafische Ausgabe als Hintergrund setzen

		// OnClickListener für die Buttons:
		fminus.setOnClickListener(new OnClickListener() {
			// OnClickListener für F-
			public void onClick(View v) {
				fplus.setEnabled(true); // falls F- auf oberstem Stockwerk gedrückt wurde, F+ wieder enablen
				if (-1 == output.set_floor(0)) // wenn -1 returned wird, unterstes Stockwerk erreicht
					fminus.setEnabled(false); // F- disabeln
				output.invalidate(); // redraw
			}
		});

		fplus.setOnClickListener(new OnClickListener() {
			// OnClickListener für F+
			public void onClick(View v) {
				fminus.setEnabled(true); // falls F+ auf unterstem Stockwerk gedrückt wurde, F- wieder enablen
				if (1 == output.set_floor(1)) // wenn 1 returned wird, oberstes Stockwerk erreicht
					fplus.setEnabled(false); // F+ disabeln
				output.invalidate(); // redraw
			}
		});

		routing.setOnClickListener(new OnClickListener() {
			// OnClickListener für Routing
			public void onClick(View v) {
				launch_state_4();
			}
		});

		campus.setOnClickListener(new OnClickListener() {
			// OnClickListener für Campus
			public void onClick(View v) {
				output.set_floor(2);
				output.invalidate(); // redraw
			}
		});

		rl.removeAllViews(); // zunächst müssen alle Views entfernt werden, da sie sonst doppelt vorhanden sind
		// danach "von hinten nach vorne" die geänderten Elemente wieder hinzufügen, ansonsten würde die Route über den Button dargestellt werden
		rl.addView(backgroundView);
		rl.addView(fminus);
		rl.addView(fplus);
		rl.addView(routing);
		rl.addView(campus);

		cl.onResume();
		// TODO vielleicht lieber in die override, aber momentan gehts auch ohne, da sich die app bei resume noch nicht den status gemerkt hat, also auch onResume und was es sonst noch so gibt mal overriden
	}

	private void launch_state_4() {
		setContentView(R.layout.state_4);
		findViewById(R.id.but_Go4).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				EditText roomInput1 = (EditText) findViewById(R.id.editText41); // TODO abfangen von fehleingaben
				EditText roomInput2 = (EditText) findViewById(R.id.editText42); // TODO abfangen von fehleingaben
				Pathfinding pf = new Pathfinding();
				pf.compute_Path(Integer.parseInt(roomInput1.getText().toString()), Integer.parseInt(roomInput2.getText().toString()));
				System.out.println(pf.getPath()); // TODO das ding ist leer --> nils fragen
				launch_state_5(pf.getPath());
			}
		});
		// TODO wheel oder wheelpicker
	}

	private void launch_state_5(ArrayList<ArrayList<Node>> path) {
		output.set_state_path(path);
		
		setContentView(R.layout.state_5); // state_5.xml anzeigen
		// Elemente der Anzeige holen, damit sie bearbeitet werden können:
		final RelativeLayout rl = (RelativeLayout) findViewById(R.id.relativeLayout5);
		View backgroundView = findViewById(R.id.view5);
		final Button fminus = (Button) findViewById(R.id.floor_minus5);
		final Button fplus = (Button) findViewById(R.id.floor_plus5);
		Button routing = (Button) findViewById(R.id.but_Routing5);
		Button campus = (Button) findViewById(R.id.but_Campus5);

		backgroundView = output; // grafische Ausgabe als Hintergrund setzen

		// OnClickListener für die Buttons:
		fminus.setOnClickListener(new OnClickListener() {
			// OnClickListener für F-
			public void onClick(View v) {
				fplus.setEnabled(true); // falls F- auf oberstem Stockwerk gedrückt wurde, F+ wieder enablen
				if (-1 == output.set_floor(0)) // wenn -1 returned wird, unterstes Stockwerk erreicht
					fminus.setEnabled(false); // F- disabeln
				output.invalidate(); // redraw
			}
		});

		fplus.setOnClickListener(new OnClickListener() {
			// OnClickListener für F+
			public void onClick(View v) {
				fminus.setEnabled(true); // falls F+ auf unterstem Stockwerk gedrückt wurde, F- wieder enablen
				if (1 == output.set_floor(1)) // wenn 1 returned wird, oberstes Stockwerk erreicht
					fplus.setEnabled(false); // F+ disabeln
				output.invalidate(); // redraw
			}
		});

		routing.setOnClickListener(new OnClickListener() {
			// OnClickListener für Routing
			public void onClick(View v) {
				launch_state_4();
			}
		});

		campus.setOnClickListener(new OnClickListener() {
			// OnClickListener für Campus
			public void onClick(View v) {
				output.set_floor(2);
				output.invalidate(); // redraw
			}
		});

		rl.removeAllViews(); // zunächst müssen alle Views entfernt werden, da sie sonst doppelt vorhanden sind
		// danach "von hinten nach vorne" die geänderten Elemente wieder hinzufügen, ansonsten würde die Route über den Button dargestellt werden
		rl.addView(backgroundView);
		rl.addView(fminus);
		rl.addView(fplus);
		rl.addView(routing);
		rl.addView(campus);

		cl.onResume();
		// TODO vielleicht lieber in die override, aber momentan gehts auch ohne, da sich die app bei resume noch nicht den status gemerkt hat, also auch onResume und was es sonst noch so gibt mal overriden
	}

	private void launch_state_6() {
		setContentView(R.layout.state_6);
		// TODO
	}

	private void launch_state_7() {
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

		protected void onPause() { // disable Lagesensor
			mSensorManager.unregisterListener(this);
		}

		public void onSensorChanged(SensorEvent event) {
			float f_new = -event.values[0]; // Sensor auslesen
			if (Math.abs(f_new - f_old) > 3.5) { // Hysterese, damit Bild an der Schaltschwelle nicht hin- und herdreht
				if ((f_new % 5) > 2.5) // Sensor auf 5° Schritte auf bzw. abrunden
					f_new += 5;
				f_old = f_new - (f_new % 5); // neuen Winkel merken
				output.set_degree(f_old); // neuen Winkel an Ausgabe übergeben
				output.invalidate(); // Bild neu zeichnen
			}
		}

		public void onAccuracyChanged(Sensor sensor, int accuracy) { // not used
		}
	}
}