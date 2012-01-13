package Zelos.UASJ_Maps;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.hardware.*;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.*;
import android.view.View.*;
import android.widget.*;

public class GUI extends Activity {
	
	private GraphicalOutput output; // beinhaltet grafische Darstellung
	private DataBase myDB;			// Datenbank
	private CompassListener cl; // beinhaltet Lagesensor
	private int activityState = 0; // Nummer des momentan/zu letzt aktiven Status; siehe State-übersicht
	private Node location; // Standpunkt der angezeigt werden soll
	private ArrayList<ArrayList<Node>> route; // Route die angezeigt werden soll
	private float mPosX = 250.f;    //x Wert für Verschiebung GraphicalOutput  
	private float mPosY = -130.f;   //y Wert für Verschiebung GraphicalOutput        
	private float mLastTouchX;     	//x Koordinate bei TouchEvent
	private float mLastTouchY;      //y Koordinate bei TouchEvent     
	private ScaleGestureDetector mScaleDetector; //Skalierungsdetektor
	private float mScaleFactor = 1.f;	//Skalierungsfaktor Canvas         
	private float x_z = 420.f;			//x Koordinate um die gezoomt wird
	private float y_z = 130.f;			//y Koordinate um die gezoomt wird
	private int NONE;
	static final int DRAG = 1;			//Modus Verschieben (Singletouch)
	static final int ZOOMM = 2;			//Modus Zoomen (Multitouch
	int mode = NONE;					//Modus keines von beiden (Verschieben,Zoomen)
	File file = new File("/data/data/Zelos.UASJ_Maps/databases/database.db");
	InputStream Dateiname=null;
	private OnTouchListener touch_on_campus = new OnTouchListener() { // OnTouchListener hinzufügen für Gebüudeauswahl bei Campusansicht
		public boolean onTouch(View v, MotionEvent event) { // TODO enable wenn verfügbar
//			if (output.isStateCampus() && MotionEvent.ACTION_UP == event.getAction()) // Campus ansicht und Finger wird vom Display genommen
//				output.performClickOnCampus((int) event.getX(), (int) event.getY()); // x und y Werte übergeben
			return true;
		}
	};
	boolean DBcreated = false;
	
	private OnTouchListener touch_single_multi = new OnTouchListener() {
		public boolean onTouch(View v, MotionEvent event) {
			
			float x_z0 = 0;
			float y_z0 = 0;
			float x_z1 = 0;
			float y_z1 = 0;
		
			mScaleDetector.onTouchEvent(event); //Skalierungsdetektor überwacht alle Events
			
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
					}			
								
				 else if (mode == ZOOMM) {			//Modus "Zoom"
						
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

	/* State-übersicht:
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
		setContentView(R.layout.splashscreen);
		cl = new CompassListener(); // neue Instanz zur Initialisierung des Sensors
		mScaleDetector = new ScaleGestureDetector(this, new ScaleListener());  //neue Instanz Skalierungsdetektor
		myDB = new DataBase(this);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD); // Tastensperre deaktivieren
		
		if (!file.exists()){	//prüfen ob Datenbank schon existiert
			new AsyncTask<Void, Void, Void>() {

				@Override
				protected Void doInBackground(Void... params) {
		        	//Zugriff auf Assets-Ordner in dem CSV-Datei liegt
		        	AssetManager assetManager = getAssets();
		    		try {
		    			Dateiname = assetManager.open("DataBase.csv");
		    		} catch (IOException e) {
		    			// TODO Auto-generated catch block
		    			e.printStackTrace();
		    		}
		    		myDB.WriteCSVintoDataBase(Dateiname);	//CSV-Datei in Datenbank übertragen
					return null;
				}
				
				@Override
				protected void onPostExecute(Void params) {
					DBcreated = true;
				}
			}.execute(null,null,null);
        }
		else
			DBcreated = true;
		
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				try {
					while(!DBcreated)
						Thread.sleep(100);
					Thread.sleep(3000);
				} catch (InterruptedException e) { //unused
				}
				return null;
			}
			
			@Override
			protected void onPostExecute(Void params) {
				if (0 == activityState)
					launch_state_1(); // Hauptmenu anzeigen
			}
		}.execute(null,null,null);
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
		// in übergeordnete Ansicht wechseln
		if (DBcreated)
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
		
		final Button go = (Button) findViewById(R.id.but_Go2);
		final RoomSpinner RS = new RoomSpinner(getApplicationContext(), (Spinner) findViewById(R.id.spinner21), (Spinner) findViewById(R.id.spinner22), (Spinner) findViewById(R.id.spinner23)); // neue Verwaltung für die Spinner instanziieren
		
		go.setOnClickListener(new OnClickListener() {
			// On ClickListener für Go!
			public void onClick(View v) {
				String roomInput = new String(RS.getString()); // String des ausgewählten Raums holen
				Pathfinding pf = new Pathfinding(getApplicationContext()); // neue Instanz verschaffen
				pf.compute_Path(roomInput,roomInput); // "Route berechnen"; wobei hier nur der Knoten mit der ID i ermittelt werden soll 
				location = new Node (pf.getPath().get(0).get(0)); // Location merken, falls App zwischendurch in den Hintergrund kommen sollte
				launch_state_3(); // Look up Location Output (B4)
			}
		});
	}
 
	private void launch_state_3() { // Look up Location Output (B4)
		activityState = 3;
		output = new GraphicalOutput(getApplicationContext()); // neue Instanz verschaffen
		output.set_state_position(location); // Location anzeigen
		output.setOnTouchListener(touch_single_multi);	

		setContentView(R.layout.state_3); // state_3.xml anzeigen
		// Elemente der Anzeige holen, damit sie bearbeitet werden künnen:
		final RelativeLayout rl = (RelativeLayout) findViewById(R.id.relativeLayout3);
		View backgroundView = findViewById(R.id.view3);
		final Button fminus = (Button) findViewById(R.id.but_floor_minus3);
		final Button fplus = (Button) findViewById(R.id.but_floor_plus3);
		final Button showposition = (Button) findViewById(R.id.but_ShowPosition3);
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

		showposition.setOnClickListener(new OnClickListener() {
			// OnClickListener für Show Position
			public void onClick(View v) {
				output.set_floor(3);
				output.invalidate(); // redraw
			}
		});

		campus.setOnClickListener(new OnClickListener() {
			// OnClickListener für Campus
			public void onClick(View v) {
				output.set_floor(2);
				output.invalidate(); // redraw
			}
		});

		rl.removeAllViews(); // zunüchst müssen alle Views entfernt werden, da sie sonst doppelt vorhanden sind
		// danach "von hinten nach vorne" die geünderten Elemente wieder hinzufügen, ansonsten würde die Route über den Button dargestellt werden
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
		final RoomSpinner RS1 = new RoomSpinner(getApplicationContext(), (Spinner) findViewById(R.id.spinner411), (Spinner) findViewById(R.id.spinner412), (Spinner) findViewById(R.id.spinner413)); // neue Verwaltung für die Spinner instanziieren
		final RoomSpinner RS2 = new RoomSpinner(getApplicationContext(), (Spinner) findViewById(R.id.spinner421), (Spinner) findViewById(R.id.spinner422), (Spinner) findViewById(R.id.spinner423)); // neue Verwaltung für die Spinner instanziieren
		findViewById(R.id.but_Go4).setOnClickListener(new OnClickListener() {
			// OnClickListener für Go!
			public void onClick(View v) {
				Pathfinding pf = new Pathfinding(); // neue Instanz verschaffen
				pf.compute_Path(RS1.getString(), RS2.getString()); // Pfad berechnen
				route = new ArrayList<ArrayList<Node>>(pf.getPath()); // Route merken, falls App zwischendurch in den Hintergrund kommen sollte
				launch_state_5(); // Routing Output (B6)
			}
		});
	}

	private void launch_state_5() { // Routing Output (B6)
		activityState = 5;
		output = new GraphicalOutput(getApplicationContext()); // neue Instanz verschaffen
		output.set_state_path(route); // Route anzeigen
		output.setOnTouchListener(touch_single_multi);	
		
		setContentView(R.layout.state_5); // state_5.xml anzeigen
		// Elemente der Anzeige holen, damit sie bearbeitet werden künnen:
		final RelativeLayout rl = (RelativeLayout) findViewById(R.id.relativeLayout5);
		View backgroundView = findViewById(R.id.view5);
		final Button fminus = (Button) findViewById(R.id.but_floor_minus5);
		final Button fplus = (Button) findViewById(R.id.but_floor_plus5);
		final Button routing = (Button) findViewById(R.id.but_Routing5);
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

		routing.setOnClickListener(new OnClickListener() {
			// OnClickListener für Routing
			public void onClick(View v) {
				output.set_floor(3);
				output.invalidate(); // redraw
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
			// OnClickListener für Check
			public void onClick(View v) {
				if (0 == output.set_check()) // Click auf Check an output weiter geben; Sind noch weitere Ebenen abzuarbeiten?
					check.setEnabled(false); // disable Check-Button
				output.invalidate(); // redraw
			}
		});

		if (1 == route.size()) // Nur eine Ebene anzuzeigen?
			check.setEnabled(false); // disable Check-Button

		rl.removeAllViews(); // zunüchst müssen alle Views entfernt werden, da sie sonst doppelt vorhanden sind
		// danach "von hinten nach vorne" die geünderten Elemente wieder hinzufügen, ansonsten würde die Route über den Button dargestellt werden
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
		output.set_state_campus(); // freie Campusnavigation anzeigen
		output.setOnTouchListener(touch_single_multi);	
		
		setContentView(R.layout.state_7); // state_7.xml anzeigen
		// Elemente der Anzeige holen, damit sie bearbeitet werden künnen:
		final RelativeLayout rl = (RelativeLayout) findViewById(R.id.relativeLayout7);
		View backgroundView = findViewById(R.id.view7);
		final Button fminus = (Button) findViewById(R.id.but_floor_minus7);
		final Button fplus = (Button) findViewById(R.id.but_floor_plus7);
		final Button campus = (Button) findViewById(R.id.but_Campus7);
		final TextView house_floor = (TextView) findViewById(R.id.house_floor7);

		backgroundView = output; // grafische Ausgabe als Hintergrund setzen

		// OnClickListener für die Buttons:
		fminus.setOnClickListener(new OnClickListener() {
			// OnClickListener für F-
			public void onClick(View v) {
				fplus.setEnabled(true); // falls F- auf oberstem Stockwerk gedrückt wurde, F+ wieder enablen
				if (-1 == output.set_floor(0)) // wenn -1 returned wird, unterstes Stockwerk erreicht
					fminus.setEnabled(false); // F- disabeln
				output.invalidate(); // redraw
				house_floor.setText(output.get_HouseNumber(true) + "." + output.get_floorNumber(true) + ".");
			}
		});

		fplus.setOnClickListener(new OnClickListener() {
			// OnClickListener für F+
			public void onClick(View v) {
				fminus.setEnabled(true); // falls F+ auf unterstem Stockwerk gedrückt wurde, F- wieder enablen
				if (1 == output.set_floor(1)) // wenn 1 returned wird, oberstes Stockwerk erreicht
					fplus.setEnabled(false); // F+ disabeln
				output.invalidate(); // redraw
				house_floor.setText(" " + output.get_HouseNumber(true) + "." + output.get_floorNumber(true) + ". ");
			}
		});

		campus.setOnClickListener(new OnClickListener() {
			// OnClickListener für Campus
			public void onClick(View v) {
				output.set_floor(2);
				output.invalidate(); // redraw
				house_floor.setText("Campus");
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

		public CompassListener() { // Konstruktor
			mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE); // SensorManager holen
			Magnet_Sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION); // Lagesensor auswühlen
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
				if ((f_new % 5) > 2.5) // Sensor auf 5ü Schritte auf bzw. abrunden
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