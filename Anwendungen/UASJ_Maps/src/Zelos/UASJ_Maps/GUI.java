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
	private int activityState; // Nummer des momentan/zu letzt aktiven Status; siehe State-�bersicht
	private Node location; // Standpunkt der angezeigt werden soll
	private ArrayList<ArrayList<Node>> route; // Route die angezeigt werden soll
	private OnTouchListener touch_on_campus = new OnTouchListener() { // OnTouchListener hinzuf�gen f�r Geb�udeauswahl bei Campusansicht
		public boolean onTouch(View v, MotionEvent event) { // TODO enable wenn verf�gbar
//			if (output.isStateCampus() && MotionEvent.ACTION_UP == event.getAction()) // Campus ansicht und Finger wird vom Display genommen
//				output.performClickOnCampus((int) event.getX(), (int) event.getY()); // x und y Werte �bergeben
			return true;
		}
	};

	/* State-�bersicht:
	 * state1: Main menu (B1): funktioniert
	 * state2: Look up Location (B3): Spinner
	 * state3: Look up Location Output (B4): fertig?
	 * state4: Routing (B5): Spinner
	 * state5: Routing Output (B6): fertig?
	 * state6: Options (B2): ohne funktion
	 * state7: Campus Output (B7): fertig?
	 * 
	 * B1 bis B7 beziehen sich auf das Pflichtenheft Kapitel 7: Grafische Beschreibung der Produktfunktionen
	 */

	@Override
	public void onCreate(Bundle savedInstanceState) { // App wird gestartet
		super.onCreate(savedInstanceState); // onCreate von Activity
		cl = new CompassListener(); // neue Instanz zur Initialisierung des Sensors
		launch_state_1(); // Hauptmenu anzeigen
	}
	
	@Override
	public void onPause() { // App wird pausiert
		cl.onStop(); // stoppt Ausf�hrung des Magnetsensors
		super.onPause(); // onPause von Activity
	}
	
	@Override
	public void onResume() { // App wird wieder ausgef�hrt
		super.onResume(); // onResume von Activity
		
		switch (activityState) { // Welcher Status war zuletzt aktiv?
		
		case 2:
			launch_state_2(); // Look up Location (B3)
			break;
		case 3:
			launch_state_3(); // Look up Location Output (B4)
			break;
		case 4:
			launch_state_4(); // Routing (B5)
			break;
		case 5:
			launch_state_5(); // Routing Output (B6)
			break;
		case 6:
			launch_state_6(); // Options (B2)
			break;
		case 7:
			launch_state_7(); // Campus Output (B7)
			break;
		default:
			launch_state_1(); // Main menu (B1)
			break;
		}
	}
	
	@Override
	public void onDestroy() { // App wird zerst�rt
		cl.onStop(); // stoppt Ausf�hrung des Magnetsensors
		super.onDestroy(); // onDestroy von Activity
	}
	
	@Override
	public void onBackPressed() { // Zur�ck Button wird gedr�ckt
		// in �bergeordnete Ansicht wechseln
		switch (activityState) { // In welchem Status wird auf den Zur�ckButton gedr�ckt?
		
		case 5: // Routing Output (B6)
			cl.onStop(); // stoppt Ausf�hrung des Magnetsensors
			launch_state_4(); // Routing (B5)
			break;
		case 3: // Look up Location Output (B4)
			cl.onStop(); // stoppt Ausf�hrung des Magnetsensors
			launch_state_2(); // Look up Location (B3)
			break;
		case 1: // Main menu (B1)
			cl.onStop(); // stoppt Ausf�hrung des Magnetsensors
			finish(); // App beenden
			break;
		default:
			cl.onStop(); // stoppt Ausf�hrung des Magnetsensors
			launch_state_1(); // Main menu (B1)
			break;
		}
	}
	
	private void launch_state_1() { // Main menu (B1)
		activityState = 1;
		setContentView(R.layout.state_1); // state_1.xml anzeigen
		
		// OnClickListener f�r die Buttons:
		findViewById(R.id.but_LookupLocation1).setOnClickListener(new OnClickListener() {
			// OnClickListener f�r Look up Location
			public void onClick(View v) {
				launch_state_2(); // Look up Location (B3)
			}
		});
		
		findViewById(R.id.but_Routing1).setOnClickListener(new OnClickListener() {
			// OnClickListener f�r Routing
			public void onClick(View v) {
				launch_state_4(); // Routing (B5)
			}
		});
		
		findViewById(R.id.but_Campus1).setOnClickListener(new OnClickListener() {
			// OnClickListener f�r Campus
			public void onClick(View v) {
				launch_state_7(); // Campus Output (B7)
			}
		});
		
		findViewById(R.id.but_options1).setOnClickListener(new OnClickListener() {
			// OnClickListener f�r Options
			public void onClick(View v) {
				launch_state_6(); // Options (B2)
			}
		});
	}

	private void launch_state_2() { // Look up Location (B3)
		activityState = 2;
		setContentView(R.layout.state_2); // state_2.xml anzeigen
		findViewById(R.id.but_Go2).setOnClickListener(new OnClickListener() {
			// On ClickListener f�r Go!
			public void onClick(View v) {
				EditText roomInput = (EditText) findViewById(R.id.editText2); // TODO abfangen von fehleingaben
				Pathfinding pf = new Pathfinding(); // neue Instanz verschaffen
				int i = Integer.parseInt(roomInput.getText().toString()); // eingegebenen Raum umwandeln
				pf.compute_Path(i,i); // "Route berechnen"; wobei hier nur der Knoten mit der ID i ermittelt werden soll 
				location = new Node (pf.getPath().get(0).get(0)); // Location merken, falls App zwischendurch in den Hintergrund kommen sollte
				launch_state_3(); // Look up Location Output (B4)
			}
		});
		// TODO wheel oder wheelpicker
	}
 
	private void launch_state_3() { // Look up Location Output (B4)
		activityState = 3;
		output = new GraphicalOutput(getApplicationContext()); // neue Instanz verschaffen
		output.set_state_position(location); // Location anzeigen

		setContentView(R.layout.state_3); // state_3.xml anzeigen
		// Elemente der Anzeige holen, damit sie bearbeitet werden k�nnen:
		final RelativeLayout rl = (RelativeLayout) findViewById(R.id.relativeLayout3);
		View backgroundView = findViewById(R.id.view3);
		final Button fminus = (Button) findViewById(R.id.but_floor_minus3);
		final Button fplus = (Button) findViewById(R.id.but_floor_plus3);
		final Button showposition = (Button) findViewById(R.id.but_ShowPosition3);
		final Button campus = (Button) findViewById(R.id.but_Campus3);
		
		backgroundView = output; // grafische Ausgabe als Hintergrund setzen

		// OnClickListener f�r die Buttons:
		fminus.setOnClickListener(new OnClickListener() {
			// OnClickListener f�r F-
			public void onClick(View v) {
				fplus.setEnabled(true); // falls F- auf oberstem Stockwerk gedr�ckt wurde, F+ wieder enablen
				if (-1 == output.set_floor(0)) // wenn -1 returned wird, unterstes Stockwerk erreicht
					fminus.setEnabled(false); // F- disabeln
				output.invalidate(); // redraw
			}
		});

		fplus.setOnClickListener(new OnClickListener() {
			// OnClickListener f�r F+
			public void onClick(View v) {
				fminus.setEnabled(true); // falls F+ auf unterstem Stockwerk gedr�ckt wurde, F- wieder enablen
				if (1 == output.set_floor(1)) // wenn 1 returned wird, oberstes Stockwerk erreicht
					fplus.setEnabled(false); // F+ disabeln
				output.invalidate(); // redraw
			}
		});

		showposition.setOnClickListener(new OnClickListener() {
			// OnClickListener f�r Show Position
			public void onClick(View v) {
				output.set_floor(3);
				output.invalidate(); // redraw
			}
		});

		campus.setOnClickListener(new OnClickListener() {
			// OnClickListener f�r Campus
			public void onClick(View v) {
				output.setOnTouchListener(touch_on_campus); // OnTouchListener f�r Campusanzeige hinzuf�gen
				output.set_floor(2);
				output.invalidate(); // redraw
			}
		});

		rl.removeAllViews(); // zun�chst m�ssen alle Views entfernt werden, da sie sonst doppelt vorhanden sind
		// danach "von hinten nach vorne" die ge�nderten Elemente wieder hinzuf�gen, ansonsten w�rde die Route �ber den Button dargestellt werden
		rl.addView(backgroundView);
		rl.addView(fminus);
		rl.addView(fplus);
		rl.addView(showposition);
		rl.addView(campus);

		cl.onResume(); // enable Lagesensor
	}

	private void launch_state_4() { // Routing (B5)
		activityState = 4;
		setContentView(R.layout.state_4); // state_4.xml anzeigen
		findViewById(R.id.but_Go4).setOnClickListener(new OnClickListener() {
			// OnClickListener f�r Go!
			public void onClick(View v) {
				EditText roomInput1 = (EditText) findViewById(R.id.editText41); // TODO abfangen von fehleingaben
				EditText roomInput2 = (EditText) findViewById(R.id.editText42); // TODO abfangen von fehleingaben
				Pathfinding pf = new Pathfinding(); // neue Instanz verschaffen
				pf.compute_Path(Integer.parseInt(roomInput1.getText().toString()), Integer.parseInt(roomInput2.getText().toString())); // Pfad berechnen
				route = new ArrayList<ArrayList<Node>>(pf.getPath()); // Route merken, falls App zwischendurch in den Hintergrund kommen sollte
				launch_state_5(); // Routing Output (B6)
			}
		});
		// TODO wheel oder wheelpicker
	}

	private void launch_state_5() { // Routing Output (B6)
		activityState = 5;
		output = new GraphicalOutput(getApplicationContext()); // neue Instanz verschaffen
		output.set_state_path(route); // Route anzeigen
		
		setContentView(R.layout.state_5); // state_5.xml anzeigen
		// Elemente der Anzeige holen, damit sie bearbeitet werden k�nnen:
		final RelativeLayout rl = (RelativeLayout) findViewById(R.id.relativeLayout5);
		View backgroundView = findViewById(R.id.view5);
		final Button fminus = (Button) findViewById(R.id.but_floor_minus5);
		final Button fplus = (Button) findViewById(R.id.but_floor_plus5);
		final Button routing = (Button) findViewById(R.id.but_Routing5);
		final Button campus = (Button) findViewById(R.id.but_Campus5);
		final Button check = (Button) findViewById(R.id.but_Check5);
		final TextView description = (TextView) findViewById(R.id.textView5);

		backgroundView = output; // grafische Ausgabe als Hintergrund setzen

		// OnClickListener f�r die Buttons:
		fminus.setOnClickListener(new OnClickListener() {
			// OnClickListener f�r F-
			public void onClick(View v) {
				fplus.setEnabled(true); // falls F- auf oberstem Stockwerk gedr�ckt wurde, F+ wieder enablen
				if (-1 == output.set_floor(0)) // wenn -1 returned wird, unterstes Stockwerk erreicht
					fminus.setEnabled(false); // F- disabeln
				output.invalidate(); // redraw
			}
		});

		fplus.setOnClickListener(new OnClickListener() {
			// OnClickListener f�r F+
			public void onClick(View v) {
				fminus.setEnabled(true); // falls F+ auf unterstem Stockwerk gedr�ckt wurde, F- wieder enablen
				if (1 == output.set_floor(1)) // wenn 1 returned wird, oberstes Stockwerk erreicht
					fplus.setEnabled(false); // F+ disabeln
				output.invalidate(); // redraw
			}
		});

		routing.setOnClickListener(new OnClickListener() {
			// OnClickListener f�r Routing
			public void onClick(View v) {
				output.set_floor(3);
				output.invalidate(); // redraw
			}
		});

		campus.setOnClickListener(new OnClickListener() {
			// OnClickListener f�r Campus
			public void onClick(View v) {
				output.setOnTouchListener(touch_on_campus); // OnTouchListener f�r Campusanzeige hinzuf�gen
				output.set_floor(2);
				output.invalidate(); // redraw
			}
		});
		
		check.setOnClickListener(new OnClickListener() {
			// OnClickListener f�r Check
			public void onClick(View v) {
				if (0 == output.set_check()) // Click auf Check an output weiter geben; Sind noch weitere Ebenen abzuarbeiten?
					check.setEnabled(false); // disable Check-Button
				output.invalidate(); // redraw
			}
		});

		if (1 == route.size()) // Nur eine Ebene anzuzeigen?
			check.setEnabled(false); // disable Check-Button

		rl.removeAllViews(); // zun�chst m�ssen alle Views entfernt werden, da sie sonst doppelt vorhanden sind
		// danach "von hinten nach vorne" die ge�nderten Elemente wieder hinzuf�gen, ansonsten w�rde die Route �ber den Button dargestellt werden
		rl.addView(backgroundView);
		rl.addView(fminus);
		rl.addView(fplus);
		rl.addView(routing);
		rl.addView(campus);
		rl.addView(description);
		rl.addView(check);

		cl.onResume(); // enable Lagesensor
	}

	private void launch_state_6() { // Options (B2)
		activityState = 6;
		setContentView(R.layout.state_6); // state_6.xml anzeigen
		// TODO hier passiert noch nichts
	}

	private void launch_state_7() { // Campus Output (B7)
		activityState = 7;
		output = new GraphicalOutput(getApplicationContext()); // neue Instanz verschaffen
		output.setOnTouchListener(touch_on_campus); // OnTouchListener f�r Campusanzeige hinzuf�gen
		output.set_state_campus(); // freie Campusnavigation anzeigen
		
		setContentView(R.layout.state_7); // state_7.xml anzeigen
		// Elemente der Anzeige holen, damit sie bearbeitet werden k�nnen:
		final RelativeLayout rl = (RelativeLayout) findViewById(R.id.relativeLayout7);
		View backgroundView = findViewById(R.id.view7);
		final Button fminus = (Button) findViewById(R.id.but_floor_minus7);
		final Button fplus = (Button) findViewById(R.id.but_floor_plus7);
		final Button campus = (Button) findViewById(R.id.but_Campus7);

		backgroundView = output; // grafische Ausgabe als Hintergrund setzen

		// OnClickListener f�r die Buttons:
		fminus.setOnClickListener(new OnClickListener() {
			// OnClickListener f�r F-
			public void onClick(View v) {
				fplus.setEnabled(true); // falls F- auf oberstem Stockwerk gedr�ckt wurde, F+ wieder enablen
				if (-1 == output.set_floor(0)) // wenn -1 returned wird, unterstes Stockwerk erreicht
					fminus.setEnabled(false); // F- disabeln
				output.invalidate(); // redraw
			}
		});

		fplus.setOnClickListener(new OnClickListener() {
			// OnClickListener f�r F+
			public void onClick(View v) {
				fminus.setEnabled(true); // falls F+ auf unterstem Stockwerk gedr�ckt wurde, F- wieder enablen
				if (1 == output.set_floor(1)) // wenn 1 returned wird, oberstes Stockwerk erreicht
					fplus.setEnabled(false); // F+ disabeln
				output.invalidate(); // redraw
			}
		});

		campus.setOnClickListener(new OnClickListener() {
			// OnClickListener f�r Campus
			public void onClick(View v) {
				output.set_floor(2);
				output.invalidate(); // redraw
			}
		});

		rl.removeAllViews(); // zun�chst m�ssen alle Views entfernt werden, da sie sonst doppelt vorhanden sind
		// danach "von hinten nach vorne" die ge�nderten Elemente wieder hinzuf�gen, ansonsten w�rde die Route �ber den Button dargestellt werden
		rl.addView(backgroundView);
		rl.addView(fminus);
		rl.addView(fplus);
		rl.addView(campus);

		cl.onResume(); // enable Lagesensor
	}

	private class CompassListener implements SensorEventListener { // innere Klasse

		private SensorManager mSensorManager; // beinhaltet alle Sensoren
		private Sensor Magnet_Sensor; // nur Lagesensor
		private float f_old = 0; // Winkel merken

		public CompassListener() { // Konstruktor
			mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE); // SensorManager holen
			Magnet_Sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION); // Lagesensor ausw�hlen
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
				if ((f_new % 5) > 2.5) // Sensor auf 5� Schritte auf bzw. abrunden
					f_new += 5;
				f_old = f_new - (f_new % 5); // neuen Winkel merken
				output.set_degree(f_old); // neuen Winkel an Ausgabe �bergeben
				output.invalidate(); // Bild neu zeichnen
			}
		}

		public void onAccuracyChanged(Sensor sensor, int accuracy) { // not used
		}
	}
}