package Zelos.UASJ_Maps;

import java.io.*;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.hardware.*;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class GUI extends Activity {
	
	private GraphicalOutput output; // beinhaltet grafische Darstellung
	private DataBase myDB;			// Datenbank
	private CompassListener cl; // beinhaltet Lagesensor
	private boolean DBcreated = false; // Wurde Datenbank erstellt?
	private int activityState = 0; // Nummer des momentan/zu letzt aktiven Status; siehe State-übersicht
	private Pathfinding pf; // neue Instanz verschaffen
	private float mPosX = 250.f;    //x Wert für Verschiebung GraphicalOutput  
	private float mPosY = -130.f;   //y Wert für Verschiebung GraphicalOutput        
	private float mLastTouchX;     	//x Koordinate bei TouchEvent
	private float mLastTouchY;      //y Koordinate bei TouchEvent     
	private ScaleGestureDetector mScaleDetector; //Skalierungsdetektor
	private float mScaleFactor = 1.f;	//Skalierungsfaktor Canvas         
	private float x_z = 420.f;			//x Koordinate um die gezoomt wird
	private float y_z = 130.f;			//y Koordinate um die gezoomt wird
	private int NONE;
	private static final int DRAG = 1;			//Modus Verschieben (Singletouch)
	private static final int ZOOMM = 2;			//Modus Zoomen (Multitouch
	private int mode = NONE;					//Modus keines von beiden (Verschieben,Zoomen)
	private File file = new File("/data/data/Zelos.UASJ_Maps/databases/database.db");
	private InputStream Dateiname = null;
	
	private OnTouchListener touch_single_multi = new OnTouchListener() {
	    public boolean onTouch(View v, MotionEvent event) {
	        float x_z0 = 0;
	        float y_z0 = 0;
	        float x_z1 = 0;
	        float y_z1 = 0;
	        mScaleDetector.onTouchEvent(event); //Skalierungsdetektor überwacht alle Events
//				TODO mal mit thomas drüber reden, dass die nich im anschluss an eine verschiebnung ausgeführt wurde
//				if (event.getAction() == MotionEvent.ACTION_UP)
//					if (output.isStateCampus()) // Wird Campus angezeigt?
//						output.performClickOnCampus((int) event.getX(), (int) event.getY()); // x und y Werte übergeben
	        final int action = event.getAction();
	        switch (action & MotionEvent.ACTION_MASK) {
	        case MotionEvent.ACTION_DOWN: {    	//Ein Finger auf Touchscreen
	            final float x = event.getX();   //x-Koordinate holen
	            final float y = event.getY(); 	//y-Koordinate holen

	            mLastTouchX = x;             	//x-Koordinate speichern
	            mLastTouchY = y;             	//y-Koordinate speichern
	            mode = DRAG;					//Modus "Verschieben" setzen
	            break;
	        }
	        case MotionEvent.ACTION_POINTER_DOWN:	//zwei Finger auf Touchscreen
	            x_z0 = event.getX(0);
	            y_z0 = event.getY(0);
	            x_z1 = event.getX(1);
	            y_z1 = event.getY(1);
	            x_z = (x_z1+x_z0)/2;				//x Mittelpunkt berechnen
	            y_z = (y_z1+y_z0)/2;				//y Mittelpunkt berechnen
	            mode = ZOOMM;						// Modus "Zoom" setzen
	            break;
	        case MotionEvent.ACTION_MOVE: {
	            final float x = event.getX();       //aktuelle x Koordinate
	            final float y = event.getY();       //aktuelle y Koordinate

	            if (mode == DRAG) {                //Modus "Verschieben" aktiv
	                final float dx = x - mLastTouchX;   //Differenz dx berechnen
	                final float dy = y - mLastTouchY; 	//Differenz dy berechnen

	                mPosX += dx;                 	//Differenz dx speichern
	                mPosY += dy; 					//Differenz dy speichern

	                mLastTouchX = x;            	//neuen x-Wert speichern
	                mLastTouchY = y; 				//neuen y-Wert speichern

	                //	go.moveX(dx);
	                //	go.moveY(dy);

	                output.set_position(mPosX, mPosY);	//Positionswerte an GO weiterreichen
	                output.invalidate();
	            } else if (mode == ZOOMM) {			//Modus "Zoom"
	                output.set_zoom(mScaleFactor, x_z, y_z); //Skalierungsfaktor und Mittelpunkte an GO weiterreichen
	                output.invalidate();
	            }
	            break;
	        }
	        case MotionEvent.ACTION_POINTER_UP: {	//zweiter Finger verlässt Touchscreen
	            mode = NONE;						//kein Modus mehr aktiv --> verhindert springen
	            x_z = 420.f;						//Mittelpunkt x auf Touchscreenmitte setzen
	            y_z = 130.f;						//Mittelpunkt y auf Touchscreenmitte setzen
	            break;
	        }
	        }
	        return true;
	    }
	};

	/**Diese Klasse verwaltet die Anzeigen für den Benutzer. Gleichzeitig wertet sie Benutzereingaben aus.
	 * Die Klasse besitzt verschiedene Zustände, die sich wie folgt auf das Pflichtenheft beziehen:
	 * state1: Main menu (B1)
	 * state2: Look up Location (B3)
	 * state3: Look up Location Output (B4)
	 * state4: Routing (B5)
	 * state5: Routing Output (B6)
	 * state6: Options (B2)
	 * state7: Campus Output (B7)
	 * 
	 * B1 bis B7 beziehen sich auf das Pflichtenheft Kapitel 7: Grafische Beschreibung der Produktfunktionen
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) { // App wird gestartet
		super.onCreate(savedInstanceState); // onCreate von Activity
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD); // Tastensperre deaktivieren
		setContentView(R.layout.splashscreen); // SplashScreen anzeigen
		cl = new CompassListener(); // neue Instanz zur Initialisierung des Sensors
		mScaleDetector = new ScaleGestureDetector(this, new ScaleListener());  //neue Instanz Skalierungsdetektor
		myDB = new DataBase(getApplicationContext());
		pf = new Pathfinding(getApplicationContext()); // Routenfindung instanziieren, damit D
		
		new AsyncTask<Void, Void, Void>() { // neuen Task erstellen zum Anlegen der DB
			@Override
			protected Void doInBackground(Void... params) {
				if (!file.exists())	{ //prüfen ob Datenbank schon existiert
		        	//Zugriff auf Assets-Ordner in dem CSV-Datei liegt
		        	AssetManager assetManager = getAssets();
		    		try {
		    			Dateiname = assetManager.open("DataBase.csv");
		    		} catch (IOException e) {} // unused
		    		myDB.WriteCSVintoDataBase(Dateiname);	//CSV-Datei in Datenbank übertragen
				}
				return null;
			}
			
			@Override
			protected void onPostExecute(Void params) {
				DBcreated = true; // DB wurde erstellt
			}
		}.execute(null,null,null); // Task starten; keine Übergabeparameter
		
		new AsyncTask<Void, Void, Void>() { // neuen Task erstellen für Anzeige Splashscreen
			@Override
			protected Void doInBackground(Void... params) { // Aufgabe des AsyncTask
				try {
					while(!DBcreated) // Solange DB noch nicht erstellt wurde
						Thread.yield(); // Prozessorzeit abgeben, damit andere Tasks abgearbeitet werden können
					Thread.sleep(3000); //  nachdem DB erstellt wurde, noch 3000ms Splashscreen anzeigen
				} catch (InterruptedException e) {} //unused
				return null;
			}

			@Override
			protected void onPostExecute(Void params) { // wird ausgeführt nachdem doInBackground fertig ist
				if (0 == activityState) // Wird noch immer der Splashscreen angezeigt?
					launch_state_1(); // Hauptmenu anzeigen
			}
		}.execute(null,null,null); // Task starten; keine Übergabeparameter
	}
	
	@Override
	public void onPause() { // App wird pausiert
		cl.onStop(); // stoppt Ausführung des Magnetsensors
		super.onPause(); // onPause von Activity
	}
	
	@Override
	public void onResume() { // App wird wieder ausgeführt
		super.onResume(); // onResume von Activity
		cl.onResume(); // setzt Ausführung des Magnetsensors fort
	}
	
	@Override
	public void onDestroy() { // App wird zerstürt
		cl.onStop(); // stoppt Ausführung des Magnetsensors
		super.onDestroy(); // onDestroy von Activity
	}
	
	@Override
	public void onBackPressed() { // Zurück Button wird gedrückt
		if (DBcreated) // Wurde Datenbank bereits erstellt?
			// in übergeordnete Ansicht wechseln
			switch (activityState) { // In welchem Status wird auf den ZurückButton gedrückt?
			case 5: // Routing Output (B6)
				cl.onStop(); // stoppt Ausführung des Magnetsensors
				launch_state_4(); // Routing (B5)
				break;
			case 3: // Look up Location Output (B4)
				cl.onStop(); // stoppt Ausführung des Magnetsensors
				launch_state_2(); // Look up Location (B3)
				break;
			case 1: // Main menu (B1)
				cl.onStop(); // stoppt Ausführung des Magnetsensors
				finish(); // App beenden
				break;
			default:
				cl.onStop(); // stoppt Ausführung des Magnetsensors
				launch_state_1(); // Main menu (B1)
				break;
			}
	}
	
	/**
	 * @param house_floor aktualisiert Ausgabetext von house_floor
	 * @return true wenn Campus angezeigt wird, ansonsten false
	 */
	private boolean updateHouseFloor(TextView house_floor) {
		if (output.get_HouseNumber(true) != "Campus") { // Wird gerade nicht der Campus angezeigt?
			house_floor.setText(output.get_HouseNumber(true) + "." + output.get_floorNumber(true) + "."); // Hausnummer + . + Etagennummer + .
			return false; // Campus wird nicht angezeigt
		} else {
			house_floor.setText("Campus"); // es wird gerade der Campus angezeigt
			return true; // Campus wird angezeigt
		}
	}
	
	private void launch_state_1() { // Main menu (B1)
		activityState = 1;
		setContentView(R.layout.state_1); // state_1.xml anzeigen
		
		// OnClickListener für die Buttons:
		findViewById(R.id.but_LookupLocation1).setOnClickListener(new OnClickListener() {
			// OnClickListener für Look up Location
			public void onClick(View v) {
				launch_state_2(); // Look up Location (B3)
			}
		});
		
		findViewById(R.id.but_Routing1).setOnClickListener(new OnClickListener() {
			// OnClickListener für Routing
			public void onClick(View v) {
				launch_state_4(); // Routing (B5)
			}
		});
		
		findViewById(R.id.but_Campus1).setOnClickListener(new OnClickListener() {
			// OnClickListener für Campus
			public void onClick(View v) {
				launch_state_7(); // Campus Output (B7)
			}
		});
		
		findViewById(R.id.but_options1).setOnClickListener(new OnClickListener() {
			// OnClickListener für Options
			public void onClick(View v) {
				launch_state_6(); // Options (B2)
			}
		});
	}

	private void launch_state_2() { // Look up Location (B3)
		activityState = 2;
		setContentView(R.layout.state_2); // state_2.xml anzeigen
		
		final Button go = (Button) findViewById(R.id.but_Go2); // Button holen zur Bearbeitung
		final RoomSpinner RS = new RoomSpinner(getApplicationContext(), (Spinner) findViewById(R.id.spinner21), (Spinner) findViewById(R.id.spinner22), (Spinner) findViewById(R.id.spinner23)); // neue Verwaltung für die Spinner instanziieren
		
		go.setOnClickListener(new OnClickListener() {
			// On ClickListener für Go!
			public void onClick(View v) {
				String roomInput = new String(RS.getString()); // String des ausgewählten Raums holen
				pf.compute_Path(roomInput,roomInput); // "Route berechnen"; wobei hier nur der Knoten mit der ID i ermittelt werden soll
				launch_state_3(); // Look up Location Output (B4)
			}
		});
	}
 
	private void launch_state_3() { // Look up Location Output (B4)
		activityState = 3;
		output = new GraphicalOutput(getApplicationContext()); // neue Instanz verschaffen
		output.set_state_position(pf.getPath().get(0).get(0)); // Location anzeigen
		output.setOnTouchListener(touch_single_multi);	

		setContentView(R.layout.state_3); // state_3.xml anzeigen
		// Elemente der Anzeige holen, damit sie bearbeitet werden können:
		final RelativeLayout rl = (RelativeLayout) findViewById(R.id.relativeLayout3);
		View backgroundView = findViewById(R.id.view3);
		final Button fminus = (Button) findViewById(R.id.but_floor_minus3);
		final Button fplus = (Button) findViewById(R.id.but_floor_plus3);
		final Button showposition = (Button) findViewById(R.id.but_ShowPosition3);
		final Button campus = (Button) findViewById(R.id.but_Campus3);
		final TextView house_floor = (TextView) findViewById(R.id.house_floor3);
		
		backgroundView = output; // grafische Ausgabe als Hintergrund setzen
		updateHouseFloor(house_floor); // Anzeige oben links aktualisieren
		short merk = output.set_floor(3); // Anzeige auf aktuelle Route
		if (1 == merk) // wenn 1 returned wird, oberstes Stockwerk erreicht
			fplus.setEnabled(false); // F+ disabeln
		else if (-1 == merk) // wenn -1 returned wird, unterstes Stockwerk erreicht
			fminus.setEnabled(false); // F- disabeln

		// OnClickListener für die Buttons:
		fminus.setOnClickListener(new OnClickListener() {
			// OnClickListener für F-
			public void onClick(View v) {
				fplus.setEnabled(true); // falls F- auf oberstem Stockwerk gedrückt wurde, F+ wieder enablen
				if (-1 == output.set_floor(0)) // wenn -1 returned wird, unterstes Stockwerk erreicht
					fminus.setEnabled(false); // F- disabeln
				output.invalidate(); // redraw
				updateHouseFloor(house_floor); // Anzeige oben links aktualisieren
			}
		});

		fplus.setOnClickListener(new OnClickListener() {
			// OnClickListener für F+
			public void onClick(View v) {
				fminus.setEnabled(true); // falls F+ auf unterstem Stockwerk gedrückt wurde, F- wieder enablen
				if (1 == output.set_floor(1)) // wenn 1 returned wird, oberstes Stockwerk erreicht
					fplus.setEnabled(false); // F+ disabeln
				output.invalidate(); // redraw
				updateHouseFloor(house_floor); // Anzeige oben links aktualisieren
			}
		});

		showposition.setOnClickListener(new OnClickListener() {
			// OnClickListener für Show Position
			public void onClick(View v) {
				short merk = output.set_floor(3); // Anzeige auf aktuelle Position
				if (1 == merk) // wenn 1 returned wird, oberstes Stockwerk erreicht
					fplus.setEnabled(false); // F+ disabeln
				else if (-1 == merk) // wenn -1 returned wird, unterstes Stockwerk erreicht
				output.invalidate(); // redraw TODO initial zoom setzen
				updateHouseFloor(house_floor); // Anzeige oben links aktualisieren
			}
		});

		campus.setOnClickListener(new OnClickListener() {
			// OnClickListener für Campus
			public void onClick(View v) {
				fminus.setEnabled(false); // F- Button ausgrauen
				fplus.setEnabled(false); // F+ Button ausgrauen
				campus.setEnabled(false); // Campus Button ausgrauen
				output.set_floor(2); // Campus anzeigen
				output.invalidate(); // redraw
				updateHouseFloor(house_floor); // Anzeige oben links aktualisieren
			}
		});

		rl.removeAllViews(); // zunüchst müssen alle Views entfernt werden, da sie sonst doppelt vorhanden sind
		// danach "von hinten nach vorne" die geünderten Elemente wieder hinzufügen, ansonsten würde die Route über den Button dargestellt werden
		rl.addView(backgroundView);
		rl.addView(fminus);
		rl.addView(fplus);
		rl.addView(showposition);
		rl.addView(campus);
		rl.addView(house_floor);

		cl.onResume(); // enable Lagesensor
	}

	private void launch_state_4() { // Routing (B5)
		activityState = 4;
		setContentView(R.layout.state_4); // state_4.xml anzeigen
		final RoomSpinner RS1 = new RoomSpinner(getApplicationContext(), (Spinner) findViewById(R.id.spinner411), (Spinner) findViewById(R.id.spinner412), (Spinner) findViewById(R.id.spinner413)); // neue Verwaltung für die Spinner instanziieren
		final RoomSpinner RS2 = new RoomSpinner(getApplicationContext(), (Spinner) findViewById(R.id.spinner421), (Spinner) findViewById(R.id.spinner422), (Spinner) findViewById(R.id.spinner423)); // neue Verwaltung für die Spinner instanziieren
		findViewById(R.id.but_Go4).setOnClickListener(new OnClickListener() {
			// OnClickListener für Go!
			public void onClick(View v) {
				pf.compute_Path(RS1.getString(), RS2.getString()); // Pfad berechnen
				launch_state_5(); // Routing Output (B6)
			}
		});
	}

	private void launch_state_5() { // Routing Output (B6)
		activityState = 5;
		output = new GraphicalOutput(getApplicationContext()); // neue Instanz verschaffen
		output.set_state_path(pf.getPath()); // Route anzeigen
		output.setOnTouchListener(touch_single_multi);	
		
		setContentView(R.layout.state_5); // state_5.xml anzeigen
		// Elemente der Anzeige holen, damit sie bearbeitet werden können:
		final RelativeLayout rl = (RelativeLayout) findViewById(R.id.relativeLayout5);
		View backgroundView = findViewById(R.id.view5);
		final Button fminus = (Button) findViewById(R.id.but_floor_minus5);
		final Button fplus = (Button) findViewById(R.id.but_floor_plus5);
		final Button routing = (Button) findViewById(R.id.but_Routing5);
		final Button campus = (Button) findViewById(R.id.but_Campus5);
		final Button check = (Button) findViewById(R.id.but_Check5);
		final TextView description = (TextView) findViewById(R.id.textView5);
		final TextView house_floor = (TextView) findViewById(R.id.house_floor5);

		backgroundView = output; // grafische Ausgabe als Hintergrund setzen
		updateHouseFloor(house_floor); // Anzeige oben links aktualisieren
		description.setText("Route:\n" + output.get_PathDescription()); // Routenbeschreibung einfügen
		if (1 == pf.getPath().size()) // Nur eine Ebene anzuzeigen?
			check.setEnabled(false); // disable Check-Button
		short merk = output.set_floor(3); // Anzeige auf aktuelle Route
		if (1 == merk) // wenn 1 returned wird, oberstes Stockwerk erreicht
			fplus.setEnabled(false); // F+ disabeln
		else if (-1 == merk) // wenn -1 returned wird, unterstes Stockwerk erreicht
			fminus.setEnabled(false); // F- disabeln

		// OnClickListener für die Buttons:
		fminus.setOnClickListener(new OnClickListener() {
			// OnClickListener für F-
			public void onClick(View v) {
				fplus.setEnabled(true); // falls F- auf oberstem Stockwerk gedrückt wurde, F+ wieder enablen
				if (-1 == output.set_floor(0)) // wenn -1 returned wird, unterstes Stockwerk erreicht
					fminus.setEnabled(false); // F- disabeln
				output.invalidate(); // redraw
				updateHouseFloor(house_floor); // Anzeige oben links aktualisieren
				description.setText("Route:\n" + output.get_PathDescription()); // Routenbeschreibung einfügen
			}
		});

		fplus.setOnClickListener(new OnClickListener() {
			// OnClickListener für F+
			public void onClick(View v) {
				fminus.setEnabled(true); // falls F+ auf unterstem Stockwerk gedrückt wurde, F- wieder enablen
				if (1 == output.set_floor(1)) // wenn 1 returned wird, oberstes Stockwerk erreicht
					fplus.setEnabled(false); // F+ disabeln
				output.invalidate(); // redraw
				updateHouseFloor(house_floor); // Anzeige oben links aktualisieren
				description.setText("Route:\n" + output.get_PathDescription()); // Routenbeschreibung einfügen
			}
		});

		routing.setOnClickListener(new OnClickListener() {
			// OnClickListener für Routing
			public void onClick(View v) {
				short merk = output.set_floor(3); // Anzeige auf aktuelle Route
				if (1 == merk) // wenn 1 returned wird, oberstes Stockwerk erreicht
					fplus.setEnabled(false); // F+ disabeln
				else if (-1 == merk) // wenn -1 returned wird, unterstes Stockwerk erreicht
					fminus.setEnabled(false); // F- disabeln
				output.invalidate(); // redraw TODO initial zoom setzen
				if (!updateHouseFloor(house_floor)) { // Anzeige oben links aktualisieren; Ist C
					fminus.setEnabled(true); // F- Button anzeigen
					fplus.setEnabled(true); // F+ Button anzeigen
					campus.setEnabled(true); // Campus Button anzeigen
					description.setText("Route:\n" + output.get_PathDescription()); // Routenbeschreibung einfügen
				}
			}
		});

		campus.setOnClickListener(new OnClickListener() {
			// OnClickListener für Campus
			public void onClick(View v) {
				fminus.setEnabled(false); // F- Button ausgrauen
				fplus.setEnabled(false); // F+ Button ausgrauen
				campus.setEnabled(false); // Campus Button ausgrauen
				output.set_floor(2); // Campus anzeigen
				output.invalidate(); // redraw
				updateHouseFloor(house_floor); // Anzeige oben links aktualisieren
				description.setText("Route:\n" + output.get_PathDescription()); // Routenbeschreibung einfügen
			}
		});
		
		check.setOnClickListener(new OnClickListener() {
			// OnClickListener für Check
			public void onClick(View v) {
				short merk = output.set_check(); // Click auf Check an output weiter geben
				if (1 == merk) // wenn 1 returned wird, oberstes Stockwerk erreicht
					fplus.setEnabled(false); // F+ disabeln
				else if (-1 == merk) // wenn -1 returned wird, unterstes Stockwerk erreicht
					fminus.setEnabled(false); // F- disabeln
				else if (0 == merk) // wenn 0 returned wird, letzten Routenteil erreicht
					check.setEnabled(false); // disable Check-Button
				output.invalidate(); // redraw
				updateHouseFloor(house_floor); // Anzeige oben links aktualisieren
				description.setText("Route:\n" + output.get_PathDescription()); // Routenbeschreibung einfügen
			}
		});

		rl.removeAllViews(); // zunüchst müssen alle Views entfernt werden, da sie sonst doppelt vorhanden sind
		// danach "von hinten nach vorne" die geünderten Elemente wieder hinzufügen, ansonsten würde die Route über den Button dargestellt werden
		rl.addView(backgroundView);
		rl.addView(fminus);
		rl.addView(fplus);
		rl.addView(routing);
		rl.addView(campus);
		rl.addView(description);
		rl.addView(check);
		rl.addView(house_floor);

		cl.onResume(); // enable Lagesensor
	}

	private void launch_state_6() { // Options (B2)
		activityState = 6;
		setContentView(R.layout.state_6); // state_6.xml anzeigen
		final CheckBox checkCompass = (CheckBox) findViewById(R.id.checkCompass6); // CheckBox holen zur Bearbeitung
		
		checkCompass.setChecked(cl.getEnabled()); // setzt Hacken, wenn CompassListener enabled
		
		checkCompass.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			// OnCheckListener für CheckButton
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				cl.setEnabled(isChecked); // setzt enable, wenn Hacken gesetzt
			}
		});
		
		findViewById(R.id.but_refreshDB6).setOnClickListener(new OnClickListener() {
			// OnClickListener für Button
			public void onClick(View v) { // alte DB löschen; neue DB anlegen; SplashScreen anzeigen
				setContentView(R.layout.splashscreen); // SplashScreen anzeigen
				DBcreated = false; // DB noch nicht erstellt
				new AsyncTask<Void, Void, Void>() { // neuen Task erstellen zum Anlegen der DB
					@Override
					protected Void doInBackground(Void... params) {
						if (file.exists()) //prüfen ob Datenbank schon existiert
							file.delete(); // alte DB Löschen
						// neue DB anlegen:
			        	//Zugriff auf Assets-Ordner in dem CSV-Datei liegt
			        	AssetManager assetManager = getAssets();
			    		try {
			    			Dateiname = assetManager.open("DataBase.csv");
			    		} catch (IOException e) {} // unused
			    		myDB.WriteCSVintoDataBase(Dateiname);	//CSV-Datei in Datenbank übertragen
						return null;
					}
					
					@Override
					protected void onPostExecute(Void params) {
						pf = new Pathfinding(getApplicationContext()); // Datenbank neu öffnen
						DBcreated = true; // DB wurde erstellt
					}
				}.execute(null,null,null); // Task starten; keine Übergabeparameter
				
				new AsyncTask<Void, Void, Void>() { // neuen Task erstellen für Anzeige Splashscreen
					@Override
					protected Void doInBackground(Void... params) { // Aufgabe des AsyncTask
						while(!DBcreated) // Solange DB noch nicht erstellt wurde
							Thread.yield(); // Prozessorzeit abgeben, damit andere Tasks abgearbeitet werden können
						return null;
					}

					@Override
					protected void onPostExecute(Void params) { // wird ausgeführt nachdem doInBackground fertig ist
						launch_state_6(); // Hauptmenu anzeigen
					}
				}.execute(null,null,null); // Task starten; keine Übergabeparameter
			}
		});
	}

	private void launch_state_7() { // Campus Output (B7)
		activityState = 7;
		output = new GraphicalOutput(getApplicationContext()); // neue Instanz verschaffen
		output.set_state_campus(); // freie Campusnavigation anzeigen
		output.setOnTouchListener(touch_single_multi);	
		
		setContentView(R.layout.state_7); // state_7.xml anzeigen
		// Elemente der Anzeige holen, damit sie bearbeitet werden können:
		final RelativeLayout rl = (RelativeLayout) findViewById(R.id.relativeLayout7);
		View backgroundView = findViewById(R.id.view7);
		final Button fminus = (Button) findViewById(R.id.but_floor_minus7);
		final Button fplus = (Button) findViewById(R.id.but_floor_plus7);
		final Button campus = (Button) findViewById(R.id.but_Campus7);
		final TextView house_floor = (TextView) findViewById(R.id.house_floor7);

		backgroundView = output; // grafische Ausgabe als Hintergrund setzen
		fminus.setEnabled(false); // F- Button ausgrauen
		fplus.setEnabled(false); // F+ Button ausgrauen
		campus.setEnabled(false); // Campus Button ausgrauen
		updateHouseFloor(house_floor); // Anzeige oben links aktualisieren

		// OnClickListener für die Buttons:
		fminus.setOnClickListener(new OnClickListener() {
			// OnClickListener für F-
			public void onClick(View v) {
				fplus.setEnabled(true); // falls F- auf oberstem Stockwerk gedrückt wurde, F+ wieder enablen
				if (-1 == output.set_floor(0)) // wenn -1 returned wird, unterstes Stockwerk erreicht
					fminus.setEnabled(false); // F- disabeln
				output.invalidate(); // redraw
				updateHouseFloor(house_floor); // Anzeige oben links aktualisieren
			}
		});

		fplus.setOnClickListener(new OnClickListener() {
			// OnClickListener für F+
			public void onClick(View v) {
				fminus.setEnabled(true); // falls F+ auf unterstem Stockwerk gedrückt wurde, F- wieder enablen
				if (1 == output.set_floor(1)) // wenn 1 returned wird, oberstes Stockwerk erreicht
					fplus.setEnabled(false); // F+ disabeln
				output.invalidate(); // redraw
				updateHouseFloor(house_floor); // Anzeige oben links aktualisieren
			}
		});

		campus.setOnClickListener(new OnClickListener() {
			// OnClickListener für Campus
			public void onClick(View v) {
				fminus.setEnabled(false); // F- Button ausgrauen
				fplus.setEnabled(false); // F+ Button ausgrauen
				campus.setEnabled(false); // Campus Button ausgrauen
				output.set_floor(2); // Campus anzeigen
				output.invalidate(); // redraw
				updateHouseFloor(house_floor); // Anzeige oben links aktualisieren
			}
		});

		rl.removeAllViews(); // zunüchst müssen alle Views entfernt werden, da sie sonst doppelt vorhanden sind
		// danach "von hinten nach vorne" die geünderten Elemente wieder hinzufügen, ansonsten würde die Route über den Button dargestellt werden
		rl.addView(backgroundView);
		rl.addView(fminus);
		rl.addView(fplus);
		rl.addView(campus);
		rl.addView(house_floor);

		cl.onResume(); // enable Lagesensor
	}
	
	private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener { //innere Klasse
		@Override        
		public boolean onScale(ScaleGestureDetector detector) { //Skalierfunktion            
			mScaleFactor *= detector.getScaleFactor();			//Skalierungsfaktor holen
			mScaleFactor = Math.max(0.5f, Math.min(mScaleFactor, 5.0f)); //Begrenzung des Zoomwertes                     
			return true;
		}
	}

	private class CompassListener implements SensorEventListener { // innere Klasse

		private SensorManager mSensorManager; // beinhaltet alle Sensoren
		private Sensor Magnet_Sensor; // nur Lagesensor
		private float f_old = 0; // Winkel merken
		private boolean enabled = true; // Ist CompassListener aktiviert?

		public CompassListener() { // Konstruktor
			mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE); // SensorManager holen
			Magnet_Sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION); // Lagesensor auswühlen
		}
		
		public boolean getEnabled() {
			return enabled;
		}

		public void setEnabled(boolean b) {
			enabled = b;
		}
		
		public void onResume() { // enable Lagesensor
			if (enabled) // Ist CompassListener aktiviert?
				mSensorManager.registerListener(this, Magnet_Sensor, SensorManager.SENSOR_DELAY_GAME);
		}

		public void onStop() { // disable Lagesensor
			mSensorManager.unregisterListener(this);
		}

		public void onSensorChanged(SensorEvent event) {
			float f_new = -event.values[0]; // Sensor auslesen
			if (Math.abs(f_new - f_old) > 3.5) { // Hysterese, damit Bild an der Schaltschwelle nicht hin- und herdreht
				if ((f_new % 5) > 2.5) // Sensor auf 5% Schritte auf bzw. abrunden
					f_new += 5;
				f_old = f_new - (f_new % 5); // neuen Winkel merken
				
				if (output!=null) {
					output.set_degree(f_old); // neuen Winkel an Ausgabe übergeben
					output.invalidate(); // Bild neu zeichnen
				}
			}
		}

		public void onAccuracyChanged(Sensor sensor, int accuracy) { // not used
		}
	}
}