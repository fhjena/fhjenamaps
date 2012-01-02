package Zelos.UASJ_Maps;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.hardware.*;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.*;
import android.widget.*;

public class GUI extends Activity {

	private GraphicalOutput output; // beinhaltet grafische Darstellung
	private CompassListener cl; // beinhaltet Lagesensor
	private int activityState; // Nummer des momentan/zu letzt aktiven Status
	private Node location; // Standpunkt der angezeigt werden soll
	private ArrayList<ArrayList<Node>> path; // Route die angezeigt werden soll
	private OnTouchListener touch_on_campus = new OnTouchListener() { // OnTouchListener hinzufügen für Gebäudeauswahl bei Campusansicht
		public boolean onTouch(View v, MotionEvent event) { // TODO enable wenn verfügbar
//			if (output.isStateCampus() && MotionEvent.ACTION_UP == event.getAction()) // Campus ansicht und Finger wird vom Display genommen
//				output.performClickOnCampus((int) event.getX(), (int) event.getY()); // x und y Werte übergeben
			return true;
		}
	};

	/*
	 * TODO-liste und was so funktioniert:
	 * state1: Main menu: funktioniert
	 * state2: ShowPosition menu: Spinner
	 * state3: ShowPosition view: fertig?
	 * state4: Routing menu: Spinner
	 * state5: Routing view: fertig?
	 * state6: Options: ohne funktion
	 * state7: Campus view: fertig?
	 */

	@Override
	public void onCreate(Bundle savedInstanceState) { // App wird gestartet
		super.onCreate(savedInstanceState); // onCreate von Activity
		cl = new CompassListener(); // neue Instanz zur Initialisierung des Sensors
		output = new GraphicalOutput(getApplicationContext()); // neue Instanz verschaffen
		output.setOnTouchListener(touch_on_campus); // OnTouchListener für Campusanzeige hinzufügen
		launch_state_1(); // Hauptmenu anzeigen
	}
	
	@Override
	public void onPause() { // App wird beendet
		cl.onStop(); // stoppt Ausführung des Magnetsensors
		super.onPause(); // onPause von Activity
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		switch (activityState) {
		
		case 2:
			launch_state_2();
			break;
		case 3:
			launch_state_3();
			break;
		case 4:
			launch_state_4();
			break;
		case 5:
			launch_state_5();
			break;
		case 6:
			launch_state_6();
			break;
		case 7:
			launch_state_7();
			break;
		default:
			launch_state_1();
			break;
		}
	}
	
	@Override
	public void onDestroy() { // App wird zerstört
		cl.onStop(); // stoppt Ausführung des Magnetsensors
		super.onDestroy(); // onDestroy von Activity
	}
	
	@Override
	public void onBackPressed() { // Zurück Button wird gedrückt
		// in übergeordnete Ansicht wechseln
		switch (activityState) {
		
		case 5:
			cl.onStop(); // stoppt Ausführung des Magnetsensors
			launch_state_4();
			break;
		case 3:
			cl.onStop(); // stoppt Ausführung des Magnetsensors
			launch_state_2();
			break;
		case 1:
			cl.onStop(); // stoppt Ausführung des Magnetsensors
			finish();
			break;
		default:
			cl.onStop(); // stoppt Ausführung des Magnetsensors
			launch_state_1();
			break;
		}
	}
	
	private void launch_state_1() { // Main menu
		activityState = 1;
		setContentView(R.layout.state_1); // state_1.xml anzeigen
		
		// OnClickListener für die Buttons:
		findViewById(R.id.but_LookupLocation1).setOnClickListener(new OnClickListener() {
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

	private void launch_state_2() { // ShowPosition menu
		activityState = 2;
		setContentView(R.layout.state_2); // state_2.xml anzeigen
		findViewById(R.id.but_Go2).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				EditText roomInput = (EditText) findViewById(R.id.editText2); // TODO abfangen von fehleingaben
				Pathfinding pf = new Pathfinding();
				pf.compute_Path(Integer.parseInt(roomInput.getText().toString()), Integer.parseInt(roomInput.getText().toString()));
				location = new Node (pf.getPath().get(0).get(0));
				launch_state_3(); // ShowPosition view
			}
		});
		// TODO wheel oder wheelpicker
	}
 
	private void launch_state_3() { // SchowPosition view
		activityState = 3;
		output = new GraphicalOutput(getApplicationContext()); // neue Instanz verschaffen
		output.setOnTouchListener(touch_on_campus); // OnTouchListener für Campusanzeige hinzufügen
		output.set_state_position(location);

		setContentView(R.layout.state_3); // state_3.xml anzeigen
		// Elemente der Anzeige holen, damit sie bearbeitet werden können:
		final RelativeLayout rl = (RelativeLayout) findViewById(R.id.relativeLayout3);
		View backgroundView = findViewById(R.id.view3);
		final Button fminus = (Button) findViewById(R.id.but_floor_minus3);
		final Button fplus = (Button) findViewById(R.id.but_floor_plus3);
		final Button routing = (Button) findViewById(R.id.but_Routing3);
		final Button campus = (Button) findViewById(R.id.but_Campus3);
		
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
				cl.onStop(); // stoppt Ausführung des Magnetsensors
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

		cl.onResume(); // enable Lagesensor
	}

	private void launch_state_4() { // Routing menu
		activityState = 4;
		setContentView(R.layout.state_4); // state_4.xml anzeigen
		findViewById(R.id.but_Go4).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				EditText roomInput1 = (EditText) findViewById(R.id.editText41); // TODO abfangen von fehleingaben
				EditText roomInput2 = (EditText) findViewById(R.id.editText42); // TODO abfangen von fehleingaben
				Pathfinding pf = new Pathfinding();
				pf.compute_Path(Integer.parseInt(roomInput1.getText().toString()), Integer.parseInt(roomInput2.getText().toString()));
				path = new ArrayList<ArrayList<Node>>(pf.getPath());
				launch_state_5();
			}
		});
		// TODO wheel oder wheelpicker
	}

	private void launch_state_5() { // Routing view
		activityState = 5;
		output = new GraphicalOutput(getApplicationContext()); // neue Instanz verschaffen
		output.setOnTouchListener(touch_on_campus); // OnTouchListener für Campusanzeige hinzufügen
		output.set_state_path(path);
		
		setContentView(R.layout.state_5); // state_5.xml anzeigen
		// Elemente der Anzeige holen, damit sie bearbeitet werden können:
		final RelativeLayout rl = (RelativeLayout) findViewById(R.id.relativeLayout5);
		View backgroundView = findViewById(R.id.view5);
		final Button fminus = (Button) findViewById(R.id.but_floor_minus5);
		final Button fplus = (Button) findViewById(R.id.but_floor_plus5);
		final Button route = (Button) findViewById(R.id.but_Routing5);
		final Button campus = (Button) findViewById(R.id.but_Campus5);
		final Button check = (Button) findViewById(R.id.but_Check5);
		final TextView description = (TextView) findViewById(R.id.textView5);

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

		route.setOnClickListener(new OnClickListener() {
			// OnClickListener für Routing
			public void onClick(View v) {
				output.set_floor(3);
				output.invalidate();
			}
		});

		campus.setOnClickListener(new OnClickListener() {
			// OnClickListener für Campus
			public void onClick(View v) {
				output.set_floor(2);
				output.invalidate(); // redraw
			}
		});
		
		check.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (0 == output.set_check())
					check.setEnabled(false);
				output.invalidate();
			}
		});
		

		if (1 == path.size())
			check.setEnabled(false);

		rl.removeAllViews(); // zunächst müssen alle Views entfernt werden, da sie sonst doppelt vorhanden sind
		// danach "von hinten nach vorne" die geänderten Elemente wieder hinzufügen, ansonsten würde die Route über den Button dargestellt werden
		rl.addView(backgroundView);
		rl.addView(fminus);
		rl.addView(fplus);
		rl.addView(route);
		rl.addView(campus);
		rl.addView(description);
		rl.addView(check);

		cl.onResume(); // enable Lagesensor
	}

	private void launch_state_6() { // Options
		activityState = 6;
		setContentView(R.layout.state_6);
		// TODO hier passiert noch nichts
	}

	private void launch_state_7() { // Campus view
		activityState = 7;
		output = new GraphicalOutput(getApplicationContext()); // neue Instanz verschaffen
		output.setOnTouchListener(touch_on_campus); // OnTouchListener für Campusanzeige hinzufügen
		output.set_state_campus();
		
		setContentView(R.layout.state_7); // state_7.xml anzeigen
		// Elemente der Anzeige holen, damit sie bearbeitet werden können:
		final RelativeLayout rl = (RelativeLayout) findViewById(R.id.relativeLayout7);
		View backgroundView = findViewById(R.id.view7);
		final Button route = (Button) findViewById(R.id.but_Routing7);
		final Button campus = (Button) findViewById(R.id.but_Campus7);
		final Button position = (Button) findViewById(R.id.but_Position7);

		backgroundView = output; // grafische Ausgabe als Hintergrund setzen

		route.setOnClickListener(new OnClickListener() {
			// OnClickListener für Routing
			public void onClick(View v) {
				cl.onStop(); // stoppt Ausführung des Magnetsensors
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
		
		position.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				cl.onStop(); // stoppt Ausführung des Magnetsensors
				launch_state_2();
			}
		});

		rl.removeAllViews(); // zunächst müssen alle Views entfernt werden, da sie sonst doppelt vorhanden sind
		// danach "von hinten nach vorne" die geänderten Elemente wieder hinzufügen, ansonsten würde die Route über den Button dargestellt werden
		rl.addView(backgroundView);
		rl.addView(route);
		rl.addView(campus);
		rl.addView(position);

		cl.onResume(); // enable Lagesensor
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
				output.set_degree(f_old); // neuen Winkel an Ausgabe übergeben
				output.invalidate(); // Bild neu zeichnen
			}
		}

		public void onAccuracyChanged(Sensor sensor, int accuracy) { // not used
		}
	}
}