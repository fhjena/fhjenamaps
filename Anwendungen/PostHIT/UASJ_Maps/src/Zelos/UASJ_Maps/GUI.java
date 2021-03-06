/*
This file is part of UASJ-Maps.

UASJ-Maps is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

UASJ-Maps is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with UASJ-Maps. If not, see http://www.gnu.org/licenses/
*/


/* mainly responsible: David Golz, Thomas Hensel */

package Zelos.UASJ_Maps;

import java.io.*;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.hardware.*;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class GUI extends Activity {

    private GraphicalOutput output; // beinhaltet grafische Darstellung
    private DataBase myDB;			// Datenbank
    private boolean DBcreated = false; // Wurde Datenbank erstellt?
    private DisplayMetrics metrics; // beinhaltet Displayabmessungen
    private CompassListener cl; // beinhaltet Lagesensor
    private short activityState = 0; // Nummer des momentan/zu letzt aktiven Status; siehe State-�bersicht
    private Pathfinding pf; // neue Instanz verschaffen
    private float mPosX = 250.f;    //x Wert f�r Verschiebung GraphicalOutput
    private float mPosY = -130.f;   //y Wert f�r Verschiebung GraphicalOutput
    private float mLastTouchX;     	//x Koordinate bei TouchEvent
    private float mLastTouchY;      //y Koordinate bei TouchEvent
    private float MidX = 150.f;     		//Mittelpunkt x (f�r rotate)
    private float MidY = 130.f;      		//Mittelpunkt y (f�r rotate)
    private ScaleGestureDetector mScaleDetector; //Skalierungsdetektor
    private float mScaleFactor = 1.f;	//Skalierungsfaktor Canvas
    private int NONE;
    private static final int DRAG = 1;			//Modus Verschieben (Singletouch)
    private static final int ZOOM = 2;			//Modus Zoomen (Multitouch
    private int mode = NONE;					//Modus keines von beiden (Verschieben,Zoomen)
    private File file = new File("/data/data/Zelos.UASJ_Maps/databases/database.db");
    private InputStream Dateiname = null;
    private float OffsetCorr = 0;

    private OnTouchListener touch_single_multi = new OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {
            if (output.isStateCampus()) { // Wird momentan der Campus dargestellt?
                if (event.getAction() == MotionEvent.ACTION_DOWN) { // Finger auf TouchScreen
                    mPosX = (int) event.getX(); // x Wert merken
                    mPosY = (int) event.getY(); // y Wert merken
                } else if (event.getAction() == MotionEvent.ACTION_UP && Math.abs(event.getX()-mPosX)<10 && Math.abs(event.getY()-mPosY)<10) // Finger verl�sst Touchscreen && weder in x noch y Richtung mehr als 10 Pixel bewegt
                        if (mPosY < metrics.heightPixels/3) { // Ist m�glicherweise Haus 05 getroffen?
                            if (mPosX > metrics.widthPixels/3 && mPosX < (metrics.widthPixels*2)/3) // Wurde Haus 05 getroffen?
                                performClickOnCampus(6); // Haus 05 wurde getroffen!
                        } else if (mPosY > (metrics.heightPixels*2)/3) { // Ist m�glicherweise Haus 04 getroffen?
                            if (mPosX > metrics.widthPixels/2 && mPosX < (metrics.widthPixels*6)/7) // Wurde Haus 04 getroffen?
                                if (!performClickOnCampus(5)) // Haus 04 wurde getroffen!
                                    Toast.makeText(getApplicationContext(), "Sorry, building 04 is not available in this version.", Toast.LENGTH_LONG).show(); // Haus 04 nicht verf�gbar
                        } else // ansonsten m�glicherweise Haus 01/02/03 getroffen
                            if (mPosX > (metrics.widthPixels*2)/5 && mPosX < (metrics.widthPixels*4)/5) // Wurde Haus 01/02/03 getroffen?
                                performClickOnCampus(4); // Haus 01/02/03 wurde getroffen!
            } else { // es wird eine Ebene dargestellt

                mScaleDetector.onTouchEvent(event); //Skalierungsdetektor �berwacht alle Events
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
                    mode = ZOOM;						// Modus "Zoom" setzen
                    break;

                case MotionEvent.ACTION_MOVE: {
                    final float x = event.getX();       //aktuelle x Koordinate
                    final float y = event.getY();       //aktuelle y Koordinate

                    if (mode == DRAG) {                //Modus "Verschieben" aktiv
                        final float dx = x - mLastTouchX;   //Differenz dx berechnen
                        final float dy = y - mLastTouchY; 	//Differenz dy berechnen

                       if (cl.getEnabledByOptions()) {
                    	   mPosX += dx;
                    	   mPosY += dy;
                       } else {
	                       if((mPosX+dx)>-400*Math.sqrt(mScaleFactor) && (mPosX+dx)<550*Math.sqrt(mScaleFactor)) // TODO 550 etwa Displayh�he/1,5
	                    	   mPosX += dx;                 	//Differenz dx speichern 
	                       else if ((mPosX+dx)<-400*Math.sqrt(mScaleFactor))
	                    	   mPosX = (float) (-400*Math.sqrt(mScaleFactor));
	                       else
	                    	   mPosX = (float) (550*Math.sqrt(mScaleFactor));
	                     
	                       if((mPosY+dy)>-50*Math.sqrt(mScaleFactor) && (mPosY+dy)<300*Math.sqrt(mScaleFactor)) // TODO 300 etwa Displaybreite/1,5
	                    	   mPosY += dy; 					//Differenz dy speichern
	                       else if ((mPosY+dy)<-50*Math.sqrt(mScaleFactor))
	                    	   mPosY = (float) (-50*Math.sqrt(mScaleFactor));
	                       else
	                    	   mPosY = (float) (300*Math.sqrt(mScaleFactor));
                       }

                        mLastTouchX = x;            	//neuen x-Wert speichern
                        mLastTouchY = y; 				//neuen y-Wert speichern

                        output.set_midpoint(MidX, MidY);	//Mittelpunk f�r Zoom �bergeben
                        output.set_position(mPosX, mPosY);	//Positionswerte an GO weiterreichen
                        output.invalidate();
                    } else if (mode == ZOOM) {				//Modus "Zoom"
                        output.set_zoom(mScaleFactor, MidX, MidY); //Skalierungsfaktor und Mittelpunkte an GO weiterreichen
                        output.invalidate();
                    }
                    break;
                }
                case MotionEvent.ACTION_POINTER_UP: {	//zweiter Finger verl�sst Touchscreen
                    mode = NONE;						//kein Modus mehr aktiv --> verhindert springen
                    break;
                }
                }
            }
            return true;
        }
    };

    /**Diese Klasse verwaltet die Anzeigen f�r den Benutzer. Gleichzeitig wertet sie Benutzereingaben aus.
     * Die Klasse besitzt verschiedene Zust�nde, die sich wie folgt auf das Pflichtenheft beziehen:
     * state1: Main menu (B1)
     * state2: Look up Location (B3)
     * state3: Look up Location Output (B4)
     * state4: Routing (B5)
     * state5: Routing Output (B6)
     * state6: Options (B2)
     * state7: Free Navigation Output (B7)
     *
     * B1 bis B7 beziehen sich auf das Pflichtenheft Kapitel 7: Grafische Beschreibung der Produktfunktionen
     */
    @Override
    public void onCreate(Bundle savedInstanceState) { // App wird gestartet
        super.onCreate(savedInstanceState); // onCreate von Activity
        setContentView(R.layout.splashscreen); // SplashScreen anzeigen
        
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD); // Tastensperre deaktivieren
        metrics = new DisplayMetrics(); // neue Instanz verschaffen
        getWindowManager().getDefaultDisplay().getMetrics(metrics); // Displayabmessungen ermitteln
        cl = new CompassListener(); // neue Instanz zur Initialisierung des Sensors
        mScaleDetector = new ScaleGestureDetector(this, new ScaleListener());  //neue Instanz Skalierungsdetektor
        myDB = new DataBase(getApplicationContext());
        pf = new Pathfinding(getApplicationContext()); // Routenfindung instanziieren, damit D

        new AsyncTask<Void, Void, Void>() { // neuen Task erstellen zum Anlegen der DB
            @Override
            protected Void doInBackground(Void... params) { // Aufgabe des AsyncTask
            	// Datenbank anlegen:
                if (!file.exists())	{ //pr�fen ob Datenbank schon existiert
                    //Zugriff auf Assets-Ordner in dem CSV-Datei liegt
                    AssetManager assetManager = getAssets();
                    try {
                        Dateiname = assetManager.open("DataBase.csv");
                    } catch (IOException e) {} // unused
                    myDB.WriteCSVintoDataBase(Dateiname);	//CSV-Datei in Datenbank �bertragen
                }
                
                // GPL Datei kopieren:
                String licensename = new String("GNU_General_Public_License.pdf"); // Dateiname
                String folderpath = new String("/sdcard/UASJ_Maps/"); // Dateiordnerpfad
                String filepath = new String(folderpath + licensename); // Dateiordnerpfad + Dateiname
                File folder = new File(folderpath); // Ordner
                File file = new File(filepath); // Datei
                if ((folder.exists() && !file.exists()) || folder.mkdirs()) { // (wenn Ordner vorhanden und Datei nicht vorhanden) oder Ordner Anlegen erfolgreich
	                InputStream is = null; // Lesedatenstrom
	                FileOutputStream fos = null; // Schreibdatenstrom
	                try {
	                    is = getAssets().open(licensename); // aus assets Lizens lesen
	                    fos = new FileOutputStream(filepath); // in Ordner auf Smartphone Lizens schreiben
	
	                    byte[] buffer = new byte[0xFFFF]; // Buffer
	                    for (int len; (len = is.read(buffer)) != -1;) // byteweises lesen
	                        fos.write(buffer, 0, len); // byteweises schreiben
	                } catch (IOException e) {} // unused
	                finally { // wird nach try ausgef�hrt
	                    if (is != null)
	                        try {
	                            is.close(); // Inputstrem schlie�en
	                        } catch (IOException e) {}
	                    if (fos != null)
	                        try {
	                            fos.close(); // Outputstrem schlie�en
	                        } catch (IOException e) {}
	                }
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void params) { // wird ausgef�hrt nachdem doInBackground fertig ist
                DBcreated = true; // DB wurde erstellt
            }
        }
        .execute(null,null,null); // Task starten; keine �bergabeparameter

        new AsyncTask<Void, Void, Void>() { // neuen Task erstellen f�r Anzeige Splashscreen
            @Override
            protected Void doInBackground(Void... params) { // Aufgabe des AsyncTask
                while (!DBcreated) // Solange DB noch nicht erstellt wurde
				    Thread.yield(); // Prozessorzeit abgeben, damit andere Tasks abgearbeitet werden k�nnen
                
				runOnUiThread(new Runnable() { // Ausf�hrung an UI Thread weitergeben
					// Views k�nnen nur vom UI Thread ver�ndert werden, ansonsten wird eine Exception ausgel�st
					public void run() {
		                ((TextView) findViewById(R.id.textView0)).setText("Starting..."); // Text auf Splashscreen �ndern
					}
				});
				
                try {
					Thread.sleep(3000); //  nachdem DB erstellt wurde, noch 3000ms Splashscreen anzeigen
				} catch (InterruptedException e) {} // unused
                return null;
            }

            @Override
            protected void onPostExecute(Void params) { // wird ausgef�hrt nachdem doInBackground fertig ist
                if (0 == activityState) // Wird noch immer der Splashscreen angezeigt?
                    launch_state_1(); // Hauptmen� anzeigen
            }
        }
        .execute(null,null,null); // Task starten; keine �bergabeparameter
    }

    @Override
    public void onPause() { // App wird pausiert
        cl.onStop(); // stoppt Ausf�hrung des Magnetsensors
        super.onPause(); // onPause von Activity
    }

    @Override
    public void onResume() { // App wird wieder ausgef�hrt
        super.onResume(); // onResume von Activity
        cl.onResume(); // setzt Ausf�hrung des Magnetsensors fort
    }

    @Override
    public void onDestroy() { // App wird zerst�rt
        cl.onStop(); // stoppt Ausf�hrung des Magnetsensors
        super.onDestroy(); // onDestroy von Activity
    }

    @Override
    public void onBackPressed() { // Zur�ck Button wird gedr�ckt
        if (DBcreated) // Wurde Datenbank bereits erstellt?
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

    /**enabled die zum activityState geh�renden F-, F+ und Campus Button
     * aktualisiert zudem die Anzeige von Haus und Etage oben links
     * @param HouseID f�r set_floor von GraphicalOutput
     * @return true, wenn Haus vef�gbar ist; ansonsten false
     */
    private boolean performClickOnCampus(int HouseID) {
        if (5 == HouseID) // Haus 4 wurde ausgew�hlt
            return false; // Haus 4 nicht verf�gbar
        output.set_floor(HouseID);
        cl.setEnabled(true);
        setInitZoom();	//Initialzoom setzen
        output.invalidate();
        switch (activityState) {
        case 3:
            findViewById(R.id.but_floor_minus3).setEnabled(true);
            findViewById(R.id.but_floor_plus3).setEnabled(true);
            findViewById(R.id.but_Campus3).setEnabled(true);
            updateHouseFloor((TextView) findViewById(R.id.house_floor3));
            break;
        case 5:
            findViewById(R.id.but_floor_minus5).setEnabled(true);
            findViewById(R.id.but_floor_plus5).setEnabled(true);
            findViewById(R.id.but_Campus5).setEnabled(true);
            updateHouseFloor((TextView) findViewById(R.id.house_floor5));
            break;
        default:
            findViewById(R.id.but_floor_minus7).setEnabled(true);
            findViewById(R.id.but_floor_plus7).setEnabled(true);
            findViewById(R.id.but_Campus7).setEnabled(true);
            updateHouseFloor((TextView) findViewById(R.id.house_floor7));
            break;
        }
        return true;
    }

    /**aktualisiert Ausgabetext von house_floor
     * @param house_floor
     * @return true, wenn Campus angezeigt wird; ansonsten false
     */
    private boolean updateHouseFloor(TextView house_floor) {
        if (output.get_HouseNumber(true) != "Campus") { // Wird gerade nicht der Campus angezeigt?
            house_floor.setText(output.get_HouseNumber(true) + "." + output.get_floorNumber(true) + "."); // Ausgabe Hausnummer + . + Etagennummer + .
            return false; // Campus wird nicht angezeigt
        } else {
            house_floor.setText("Campus"); // es wird gerade der Campus angezeigt
            return true; // Campus wird angezeigt
        }
    }

    private void setInitZoom() {

        mPosX = 150.f;   		//x Wert f�r Verschiebung GraphicalOutput
        mPosY = 160.f;	 		//yWert f�r Verschiebung GraphicalOutput
        MidX = 200;		 		//x Wert f�r Mittelpunkt Zoom
        MidY = 130;				//y Wert f�r Mittelpunkt Zoom
        mScaleFactor = 1.5f;	//Zoomfaktor

        //Werte der GO �bergeben
        output.set_position(mPosX, mPosY);
        output.set_midpoint(MidX, MidY);
        output.set_zoom(mScaleFactor, MidX, MidY);
    }

    private void launch_state_1() { // zeigt Main menu (B1) an
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

        findViewById(R.id.but_FreeNavigation1).setOnClickListener(new OnClickListener() {
            // OnClickListener f�r Free Navigation
            public void onClick(View v) {
                launch_state_7(); // Free Navigation Output (B7)
            }
        });

        findViewById(R.id.but_options1).setOnClickListener(new OnClickListener() {
            // OnClickListener f�r Options
            public void onClick(View v) {
                launch_state_6(); // Options (B2)
            }
        });
    }

    private void launch_state_2() { // zeigt Look up Location (B3) an
        activityState = 2;
        setContentView(R.layout.state_2); // state_2.xml anzeigen


        final Button go = (Button) findViewById(R.id.but_Go2); // Button holen zur Bearbeitung
        final RoomSpinner RS = new RoomSpinner(getApplicationContext(), (Spinner) findViewById(R.id.spinner21), (Spinner) findViewById(R.id.spinner22), (Spinner) findViewById(R.id.spinner23)); // neue Verwaltung f�r die Spinner instanziieren

        go.setOnClickListener(new OnClickListener() {
            // On ClickListener f�r Go!
            public void onClick(View v) {
                String roomInput = new String(RS.getString()); // String des ausgew�hlten Raums holen
                if (pf.compute_route(roomInput,roomInput)) // "Route berechnen"; wobei hier nur der Knoten f�r den Raum roomInput ermittelt werden soll; Location gefunden?
                    launch_state_3(); // Look up Location Output (B4)
                else // keine Route gefunden
                    Toast.makeText(getApplicationContext(), "Sorry, room not available in this version.\nRefresh DataBase?", Toast.LENGTH_LONG).show(); // Errorausgabe
            }
        });
    }

    private void launch_state_3() { // zeigt Look up Location Output (B4) an
        activityState = 3;
        output = new GraphicalOutput(getApplicationContext()); // neue Instanz verschaffen
        output.set_state_location(pf.getRoute().get(0).get(0)); // Location anzeigen
        output.setOnTouchListener(touch_single_multi);

        setInitZoom();	//Initialzoom setzen

        setContentView(R.layout.state_3); // state_3.xml anzeigen
        // Elemente der Anzeige holen, damit sie bearbeitet werden k�nnen:
        final RelativeLayout rl = (RelativeLayout) findViewById(R.id.relativeLayout3);
        View backgroundView = findViewById(R.id.view3);
        final Button fminus = (Button) findViewById(R.id.but_floor_minus3);
        final Button fplus = (Button) findViewById(R.id.but_floor_plus3);
        final Button showlocation = (Button) findViewById(R.id.but_ShowLocation3);
        final Button campus = (Button) findViewById(R.id.but_Campus3);
        final TextView house_floor = (TextView) findViewById(R.id.house_floor3);

        backgroundView = output; // grafische Ausgabe als Hintergrund setzen
        updateHouseFloor(house_floor); // Anzeige oben links aktualisieren
        short merk = output.set_floor(3); // Anzeige auf aktuelle Route
        if (1 == merk) // wenn 1 returned wird, oberstes Stockwerk erreicht
            fplus.setEnabled(false); // F+ disabeln
        else if (-1 == merk) // wenn -1 returned wird, unterstes Stockwerk erreicht
            fminus.setEnabled(false); // F- disabeln

        // OnClickListener f�r die Buttons:
        fminus.setOnClickListener(new OnClickListener() {
            // OnClickListener f�r F-
            public void onClick(View v) {
                fplus.setEnabled(true); // falls F- auf oberstem Stockwerk gedr�ckt wurde, F+ wieder enablen
                if (-1 == output.set_floor(0)) // wenn -1 returned wird, unterstes Stockwerk erreicht
                    fminus.setEnabled(false); // F- disabeln
                output.invalidate(); // redraw
                updateHouseFloor(house_floor); // Anzeige oben links aktualisieren
            }
        });

        fplus.setOnClickListener(new OnClickListener() {
            // OnClickListener f�r F+
            public void onClick(View v) {
                fminus.setEnabled(true); // falls F+ auf unterstem Stockwerk gedr�ckt wurde, F- wieder enablen
                if (1 == output.set_floor(1)) // wenn 1 returned wird, oberstes Stockwerk erreicht
                    fplus.setEnabled(false); // F+ disabeln
                output.invalidate(); // redraw
                updateHouseFloor(house_floor); // Anzeige oben links aktualisieren
            }
        });

        showlocation.setOnClickListener(new OnClickListener() {
            // OnClickListener f�r Show Location
            public void onClick(View v) {
                campus.setEnabled(true); // Campus enabeln
                fplus.setEnabled(true); // F+ enabeln
                fminus.setEnabled(true); // F- enabeln

                short merk = output.set_floor(3); // Anzeige auf aktuelle Position; R�ckgabewert merken
                if (1 == merk) // wenn 1 returned wird, oberstes Stockwerk erreicht
                    fplus.setEnabled(false); // F+ disabeln
                else if (-1 == merk) // wenn -1 returned wird, unterstes Stockwerk erreicht
                    fminus.setEnabled(false); // F- disabeln
                cl.setEnabled(true); // Compass aktivieren

                setInitZoom();	//Initialzoom setzen

                output.invalidate(); // redraw
                updateHouseFloor(house_floor); // Anzeige oben links aktualisieren
            }
        });

        campus.setOnClickListener(new OnClickListener() {
            // OnClickListener f�r Campus
            public void onClick(View v) {
                fminus.setEnabled(false); // F- Button ausgrauen
                fplus.setEnabled(false); // F+ Button ausgrauen
                campus.setEnabled(false); // Campus Button ausgrauen
                output.set_floor(2); // Campus anzeigen
                updateHouseFloor(house_floor); // Anzeige oben links aktualisieren
                output.set_zoom(((float) (metrics.heightPixels))/((float) (850)), 0, (metrics.heightPixels*2)/3); // kompletten Campus anzeigen
                cl.setEnabled(false); // Compass deaktivieren
                output.set_position(250, -130); // kompletten Campus anzeigen
                output.invalidate(); // redraw
            }
        });

        rl.removeAllViews(); // zun�chst m�ssen alle Views entfernt werden, da sie sonst doppelt vorhanden sind
        // danach "von hinten nach vorne" die ge�nderten Elemente wieder hinzuf�gen, ansonsten w�rde die Route �ber den Button dargestellt werden
        rl.addView(backgroundView);
        rl.addView(fminus);
        rl.addView(fplus);
        rl.addView(showlocation);
        rl.addView(campus);
        rl.addView(house_floor);

        cl.onResume(); // Lagesensor wieder ausf�hren
        cl.setEnabled(true); // Compass aktivieren
    }

    private void launch_state_4() { // zeigt Routing (B5) an
        activityState = 4;
        setContentView(R.layout.state_4); // state_4.xml anzeigen
        final RoomSpinner RS1 = new RoomSpinner(getApplicationContext(), (Spinner) findViewById(R.id.spinner411), (Spinner) findViewById(R.id.spinner412), (Spinner) findViewById(R.id.spinner413)); // neue Verwaltung f�r die Spinner instanziieren
        final RoomSpinner RS2 = new RoomSpinner(getApplicationContext(), (Spinner) findViewById(R.id.spinner421), (Spinner) findViewById(R.id.spinner422), (Spinner) findViewById(R.id.spinner423)); // neue Verwaltung f�r die Spinner instanziieren
        findViewById(R.id.but_Go4).setOnClickListener(new OnClickListener() {
            // OnClickListener f�r Go!
            public void onClick(View v) {
                if (pf.compute_route(RS1.getString(), RS2.getString())) // Pfad berechnen; Weg gefunden?
                    launch_state_5(); // Routing Output (B6)
                else // keine Route gefunden
                    Toast.makeText(getApplicationContext(), "Sorry, no route found.\nRefresh DataBase?", Toast.LENGTH_LONG).show(); // Errorausgabe
            }
        });
    }

    private void launch_state_5() { // zeigt Routing Output (B6) an
        activityState = 5;
        output = new GraphicalOutput(getApplicationContext()); // neue Instanz verschaffen
        output.set_state_routing(pf.getRoute()); // Route anzeigen
        output.setOnTouchListener(touch_single_multi);

        setInitZoom();	//Initialzoom setzen

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
        final TextView house_floor = (TextView) findViewById(R.id.house_floor5);

        backgroundView = output; // grafische Ausgabe als Hintergrund setzen
        updateHouseFloor(house_floor); // Anzeige oben links aktualisieren
        description.setText("Route:\n" + output.get_RouteDescription()); // Routenbeschreibung einf�gen
        if (1 == pf.getRoute().size()) // Nur eine Ebene anzuzeigen?
            check.setEnabled(false); // disable Check-Button
        short merk = output.set_floor(3); // Anzeige auf aktuelle Route
        if (1 == merk) // wenn 1 returned wird, oberstes Stockwerk erreicht
            fplus.setEnabled(false); // F+ disabeln
        else if (-1 == merk) // wenn -1 returned wird, unterstes Stockwerk erreicht
            fminus.setEnabled(false); // F- disabeln

        // OnClickListener f�r die Buttons:
        fminus.setOnClickListener(new OnClickListener() {
            // OnClickListener f�r F-
            public void onClick(View v) {
                fplus.setEnabled(true); // falls F- auf oberstem Stockwerk gedr�ckt wurde, F+ wieder enablen
                if (-1 == output.set_floor(0)) // wenn -1 returned wird, unterstes Stockwerk erreicht
                    fminus.setEnabled(false); // F- disabeln
                output.invalidate(); // redraw
                updateHouseFloor(house_floor); // Anzeige oben links aktualisieren
                description.setText("Route:\n" + output.get_RouteDescription()); // Routenbeschreibung einf�gen
            }
        });

        fplus.setOnClickListener(new OnClickListener() {
            // OnClickListener f�r F+
            public void onClick(View v) {
                fminus.setEnabled(true); // falls F+ auf unterstem Stockwerk gedr�ckt wurde, F- wieder enablen
                if (1 == output.set_floor(1)) // wenn 1 returned wird, oberstes Stockwerk erreicht
                    fplus.setEnabled(false); // F+ disabeln
                output.invalidate(); // redraw
                updateHouseFloor(house_floor); // Anzeige oben links aktualisieren
                description.setText("Route:\n" + output.get_RouteDescription()); // Routenbeschreibung einf�gen
            }
        });

        routing.setOnClickListener(new OnClickListener() {
            // OnClickListener f�r Routing
            public void onClick(View v) {
                short merk = output.set_floor(3); // Anzeige auf aktuelle Route; R�ckgabewert merken
                setInitZoom(); //Initialzoom setzen
                output.invalidate(); // redraw
                if (updateHouseFloor(house_floor)) { // Anzeige oben links aktualisieren; Wird Campus angezeigt?
                    fplus.setEnabled(false); // F+ disabeln
                    fminus.setEnabled(false); // F- disabeln
					campus.setEnabled(false); // Campus Button disabeln
                    output.set_zoom(((float) (metrics.heightPixels))/((float) (850)), 0, (metrics.heightPixels*2)/3); // kompletten Campus anzeigen
                    cl.setEnabled(false); // Compass deaktivieren
                    output.set_position(250, -130); // kompletten Campus anzeigen
				} else {
                    // Buttons zun�chst einblenden
                    fminus.setEnabled(true); // F- Button anzeigen
                    fplus.setEnabled(true); // F+ Button anzeigen
                    campus.setEnabled(true); // Campus Button anzeigen
                    if (1 == merk) // wenn 1 returned wird, oberstes Stockwerk erreicht
                        fplus.setEnabled(false); // F+ disabeln
                    else if (-1 == merk) // wenn -1 returned wird, unterstes Stockwerk erreicht
                        fminus.setEnabled(false); // F- disabeln
                    description.setText("Route:\n" + output.get_RouteDescription()); // Routenbeschreibung einf�gen
                    cl.setEnabled(true); // Compass aktivieren
                }
            }
        });
        
        campus.setOnClickListener(new OnClickListener() {
            // OnClickListener f�r Campus
            public void onClick(View v) {
                fminus.setEnabled(false); // F- Button ausgrauen
                fplus.setEnabled(false); // F+ Button ausgrauen
                campus.setEnabled(false); // Campus Button ausgrauen

                output.set_floor(2); // Campus anzeigen
                updateHouseFloor(house_floor); // Anzeige oben links aktualisieren
                description.setText("Route:\n" + output.get_RouteDescription()); // Routenbeschreibung einf�gen
                output.set_zoom(((float) (metrics.heightPixels))/((float) (850)), 0, (metrics.heightPixels*2)/3);
                cl.setEnabled(false); // Compass deaktivieren
                output.set_position(250, -130);
                output.invalidate(); // redraw
            }
        });
		
		check.setOnClickListener(new OnClickListener() {
            // OnClickListener f�r Check
            public void onClick(View v) {
                fminus.setEnabled(true); // F- Button anzeigen
                fplus.setEnabled(true); // F+ Button anzeigen
				campus.setEnabled(true); // Campus Button anzeigen
                if (0 == output.set_check()) // Click auf Check an output weiter geben
                    check.setEnabled(false); // disable Check-Button
                
                short merk = output.set_floor(-1); // -1 �ndert Anzeige nicht; hier ist nur der R�ckgabewert entscheident
                if (1 == merk) // wenn 1 returned wird, oberstes Stockwerk erreicht
                    fplus.setEnabled(false); // F+ disabeln
                else if (-1 == merk) // wenn -1 returned wird, unterstes Stockwerk erreicht
                    fminus.setEnabled(false); // F- disabeln
                
                if (output.get_HouseNumber(true) == "Campus") { // Wird Campus angezeigt?
                    fplus.setEnabled(false); // F+ disabeln
                    fminus.setEnabled(false); // F- disabeln
					campus.setEnabled(false); // Campus Button disabeln
                    output.set_zoom(((float) (metrics.heightPixels))/((float) (850)), 0, (metrics.heightPixels*2)/3); // kompletten Campus anzeigen
                    cl.setEnabled(false); // Compass deaktivieren
                    output.set_position(250, -130); // kompletten Campus anzeigen
                } else {
                    setInitZoom(); // Initalzoom setzen
                    cl.setEnabled(true); // Compass aktivieren
                }
                output.invalidate(); // redraw
                updateHouseFloor(house_floor); // Anzeige oben links aktualisieren
                description.setText("Route:\n" + output.get_RouteDescription()); // Routenbeschreibung einf�gen
            }
        });

        rl.removeAllViews(); // zun�chst m�ssen alle Views entfernt werden, da sie sonst doppelt vorhanden sind
        // danach "von hinten nach vorne" die ge�nderten Elemente wieder hinzuf�gen, ansonsten w�rde die Route �ber den Button dargestellt werden
        rl.addView(backgroundView);
        rl.addView(fminus);
        rl.addView(fplus);
        rl.addView(routing);
        rl.addView(campus);
        rl.addView(description);
        rl.addView(check);
        rl.addView(house_floor);

        cl.onResume(); // Lagesensor wieder ausf�hren
        cl.setEnabled(true); // Compass aktivieren
    }

    private void launch_state_6() { // zeigt Options (B2) an
        activityState = 6;
        setContentView(R.layout.state_6); // state_6.xml anzeigen
        final CheckBox checkCompass = (CheckBox) findViewById(R.id.checkCompass6); // CheckBox holen zur Bearbeitung
        Spinner offset = (Spinner) findViewById(R.id.spinner6);
        ArrayAdapter<CharSequence> AA = ArrayAdapter.createFromResource(getApplicationContext(), R.array.Degree, android.R.layout.simple_spinner_item); // String Array ausw�hlen
        AA.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // grafische Anzeige definieren
        offset.setAdapter(AA);
        offset.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				switch(arg2){
				case 1:
					OffsetCorr = 90;
					break;
				case 2:
					OffsetCorr = 180;
					break;
				case 3:
					OffsetCorr = -90;
					break;
				default:
					OffsetCorr = 0;
					break;
				}
			}
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
        
        switch((int) OffsetCorr) {
        case 90:
        	offset.setSelection(1);
        	break;
        case 180:
        	offset.setSelection(2);
        	break;
        case -90:
        	offset.setSelection(3);
        	break;
        default:
        	offset.setSelection(0);
        	break;
        }
        
        checkCompass.setChecked(cl.getEnabledByOptions()); // setzt Hacken, wenn CompassListener enabled

        checkCompass.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            // OnCheckListener f�r CheckButton
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                cl.setEnabledByOptions(isChecked); // setzt enable, wenn Hacken gesetzt
            }
        });

        findViewById(R.id.but_refreshDB6).setOnClickListener(new OnClickListener() {
            // OnClickListener f�r Button
            public void onClick(View v) { // alte DB l�schen; neue DB anlegen; SplashScreen anzeigen
                setContentView(R.layout.splashscreen); // SplashScreen anzeigen
                DBcreated = false; // DB noch nicht erstellt
                new AsyncTask<Void, Void, Void>() { // neuen Task erstellen zum Anlegen der DB
                    @Override
                    protected Void doInBackground(Void... params) { // Aufgabe des AsyncTask
                        if (file.exists()) //pr�fen ob Datenbank schon existiert
                            file.delete(); // alte DB L�schen
                        // neue DB anlegen:
                        //Zugriff auf Assets-Ordner in dem CSV-Datei liegt
                        AssetManager assetManager = getAssets();
                        try {
                            Dateiname = assetManager.open("DataBase.csv");
                        } catch (IOException e) {} // unused
                        myDB.WriteCSVintoDataBase(Dateiname);	//CSV-Datei in Datenbank �bertragen
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void params) { // wird ausgef�hrt nachdem doInBackground fertig ist
                        pf = new Pathfinding(getApplicationContext()); // Datenbank neu �ffnen
                        DBcreated = true; // DB wurde erstellt
                    }
                }
                .execute(null,null,null); // Task starten; keine �bergabeparameter

                new AsyncTask<Void, Void, Void>() { // neuen Task erstellen f�r Anzeige Splashscreen
                    @Override
                    protected Void doInBackground(Void... params) { // Aufgabe des AsyncTask
                        while (!DBcreated) // Solange DB noch nicht erstellt wurde
                            Thread.yield(); // Prozessorzeit abgeben, damit andere Tasks abgearbeitet werden k�nnen
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void params) { // wird ausgef�hrt nachdem doInBackground fertig ist
                        launch_state_6(); // Optionsmen� anzeigen
                    }
                }
                .execute(null,null,null); // Task starten; keine �bergabeparameter
            }
        });
    }

    private void launch_state_7() { // zeigt Free Navigation Output (B7) an
        activityState = 7;
        output = new GraphicalOutput(getApplicationContext()); // neue Instanz verschaffen
        output.set_state_free_navigation(); // freie Campusnavigation anzeigen
        output.setOnTouchListener(touch_single_multi);

        setContentView(R.layout.state_7); // state_7.xml anzeigen
        // Elemente der Anzeige holen, damit sie bearbeitet werden k�nnen:
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

        output.set_zoom(((float) (metrics.heightPixels))/((float) (850)), 0, (metrics.heightPixels*2)/3); // kompletten Campus anzeigen
        output.set_position(250, -130); // kompletten Campus anzeigen

        // OnClickListener f�r die Buttons:
        fminus.setOnClickListener(new OnClickListener() {
            // OnClickListener f�r F-
            public void onClick(View v) {
                fplus.setEnabled(true); // falls F- auf oberstem Stockwerk gedr�ckt wurde, F+ wieder enablen
                if (-1 == output.set_floor(0)) // wenn -1 returned wird, unterstes Stockwerk erreicht
                    fminus.setEnabled(false); // F- disabeln
                output.invalidate(); // redraw
                updateHouseFloor(house_floor); // Anzeige oben links aktualisieren
            }
        });

        fplus.setOnClickListener(new OnClickListener() {
            // OnClickListener f�r F+
            public void onClick(View v) {
                fminus.setEnabled(true); // falls F+ auf unterstem Stockwerk gedr�ckt wurde, F- wieder enablen
                if (1 == output.set_floor(1)) // wenn 1 returned wird, oberstes Stockwerk erreicht
                    fplus.setEnabled(false); // F+ disabeln
                output.invalidate(); // redraw
                updateHouseFloor(house_floor); // Anzeige oben links aktualisieren
            }
        });

        campus.setOnClickListener(new OnClickListener() {
            // OnClickListener f�r Campus
            public void onClick(View v) {
                fminus.setEnabled(false); // F- Button ausgrauen
                fplus.setEnabled(false); // F+ Button ausgrauen
                campus.setEnabled(false); // Campus Button ausgrauen
                output.set_floor(2); // Campus anzeigen
                updateHouseFloor(house_floor); // Anzeige oben links aktualisieren
                output.set_zoom(((float) (metrics.heightPixels))/((float) (850)), 0, (metrics.heightPixels*2)/3); // kompletten Campus anzeigen
                cl.setEnabled(false); // Compass deaktivieren
                output.set_position(250, -130); // kompletten Campus anzeigen
                output.invalidate(); // redraw
            }
        });

        rl.removeAllViews(); // zun�chst m�ssen alle Views entfernt werden, da sie sonst doppelt vorhanden sind
        // danach "von hinten nach vorne" die ge�nderten Elemente wieder hinzuf�gen, ansonsten w�rde die Route �ber den Button dargestellt werden
        rl.addView(backgroundView);
        rl.addView(fminus);
        rl.addView(fplus);
        rl.addView(campus);
        rl.addView(house_floor);

        cl.onResume(); // Lagesensor wieder ausf�hren
        cl.setEnabled(false); // Compass deaktivieren
    }

    // -------------------------------------------------------------------------------------------
    // CompassListener-Klasse
    // -------------------------------------------------------------------------------------------
    
    /* mainly responsible: Thomas Hensel */
    
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener { //innere Klasse
        @Override
        public boolean onScale(ScaleGestureDetector detector) { //Skalierfunktion
            mScaleFactor *= detector.getScaleFactor();			//Skalierungsfaktor holen
            mScaleFactor = Math.max(0.5f, Math.min(mScaleFactor, 5.0f)); //Begrenzung des Zoomwertes
            return true;
        }
    }
    
    // -------------------------------------------------------------------------------------------
    // CompassListener-Klasse
    // -------------------------------------------------------------------------------------------
    
    /* mainly responsible: David Golz */

    private class CompassListener implements SensorEventListener { // innere Klasse

        private SensorManager mSensorManager; // beinhaltet alle Sensoren
        private Sensor Magnet_Sensor; // nur Lagesensor
        private float f_old = 0; // Winkel merken
        private boolean enabled = true; // Ist CompassListener aktiviert?
        private boolean enabledByOptions = false; // Ist CompassListener �ber die Options aktiviert? (h�herwertig als Variable enabled)

        public CompassListener() { // Konstruktor
            mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE); // SensorManager holen
            Magnet_Sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION); // Lagesensor ausw�hlen
        }

        /**@return enabledByOptions
         */
        public boolean getEnabledByOptions() {
            return enabledByOptions;
        }

        /**aktiviert/deaktiviert Compass f�r grafische Ausgabe
         * @param b wird auf enabled geschrieben
         */
        public void setEnabled(boolean b) {
            enabled = b; 
            f_old = 0; // Winkel auf 0 setzen
            if (!enabled && output!=null) // wenn deaktiviert && Instanz von output vorhanden
                output.set_degree(f_old); // Winkel an Ausgabe weiter geben
        }

        /**aktiviert/deaktiviert Compass durch CheckBox im Optionsmen�
         * @param b wird auf enabledByOptions geschrieben
         */
        public void setEnabledByOptions(boolean b) {
            enabledByOptions = b;
            f_old = 0; // Winkel auf 0 setzen
            if (((!enabled) || (!enabledByOptions)) && output!=null) // wenn deaktiviert durch App oder Nutzer && Instanz von output vorhanden
                output.set_degree(f_old); // Winkel an Ausgabe weiter geben
        }

        public void onResume() { // setzt Ausf�hrung des Compass fort
            mSensorManager.registerListener(this, Magnet_Sensor, SensorManager.SENSOR_DELAY_GAME); // enable Lagesensor
        }

        public void onStop() { // stoppt Ausf�hrung des Compass
            mSensorManager.unregisterListener(this); // disable Lagesensor
        }

        /**wird ausgef�hrt, wenn sich der Drehwinkel �ndert
         * gibt Winkel an grafische Ausgabe weiter
         */
        public void onSensorChanged(SensorEvent event) {
            if (enabledByOptions && enabled) { // wenn Compass durch Benutzer && App aktiviert
                float f_new = -event.values[0]; // Sensor auslesen
                if (Math.abs(f_new - f_old) > 3.5) { // Hysterese, damit Bild an der Schaltschwelle nicht hin- und herdreht
                    if ((f_new % 5) > 2.5) // Sensor auf 5% Schritte auf bzw. abrunden
                        f_new += 5;
                    f_old = f_new - (f_new % 5); // neuen Winkel merken

                    if (output!=null) { // Instanz von grafischer Ausgabe vorhanden
                        output.set_degree(f_old + OffsetCorr); // neuen Winkel an Ausgabe �bergeben
                        output.invalidate(); // Bild neu zeichnen
                    } else
                    	this.onStop();
                }
            }
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) { // unused
        }
    }
}