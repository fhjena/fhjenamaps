
package template.gui.namespace;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.*;
import android.view.View;

public class Gui extends View {
	// Canvas von 0/0 bis 500/500
	Paint var_paint;
	// Defaultwert = -1 bzw. Testwerte
	private int anzahl_knoten;
	private int start_x = 50;
	private int start_y = 30;
	private int end_x	= 150;
	private int end_y 	= 30;
	private int start_raum	= -1;
	private int ziel_raum	= -1;
	private boolean gui_status = false;
	private float degree_float=0;
    private SensorManager mSensorManager;


		
	// Konstruktor
	public Gui(Context c_text) {
		super(c_text);
		mSensorManager = (SensorManager) c_text.getSystemService(Context.SENSOR_SERVICE);
		var_paint = new Paint();
	}
	
	// Getter und Setter zur Verwaltung der Start - und Zielraeume
	public void set_start_ziel(int start_raum_,int ziel_raum_){
		start_raum = start_raum_;
		start_raum = ziel_raum_;
	}
	
	public int get_start_raum(){
		return start_raum;
	}
	
	public int get_ziel_raum(){
		return ziel_raum;
	}
		
	// Getter und Setter zur Verwaltung der Koordinaten
	// ------------------------------------------------
	// Getter und Setter zur Verwaltung der Koordinaten
	public void set_x_koordnaten(int von,int bis){
		start_x = von;
		end_x 	= bis;
	}
	
	public void set_y_koordinaten(int von,int bis){
		start_y = von;
		end_y = bis;
	}
	
	public int get_start_x(){
		return start_x;
	}
	
	public int get_start_y(){
		return start_y;
	}
	
	public int get_end_x(){
		return end_x;
	}
	
	public int get_end_y(){
		return end_y;
	}
	
	// Getter und Setter zur Statusverwaltung der GUI
	public void set_gui(boolean status_der_gui){
		gui_status = status_der_gui;
	}
	
	public boolean get_gui(){
		return gui_status;
	}
	
	// Getter und Setter zur Knotenverwaltung
	// TODO Getter und Setter koennen vielleicht geloescht werden
	/*
	public void set_anz_knoten(int anzahl_der_knoten){
		anzahl_knoten = anzahl_der_knoten;
	}
	
	public int get_anz_knoten(){
		return anzahl_knoten;
	}
	*/
	
	// Testumgebung zeichnen
	private void zeichne_testhaus(Canvas canvas){
		// TODO besser mit canvas.clipRect();

		var_paint.setColor(Color.BLACK);
		// Haus_1 zeichen
		// horizonal
		canvas.drawLine(50, 100, 400, 100, var_paint);
		canvas.drawLine(400, 300, 50, 300, var_paint);
		canvas.drawLine(400, 500, 300, 500, var_paint);
		canvas.drawLine(250, 100, 250, 50, var_paint);	
		canvas.drawLine(250,50,150,50,var_paint);
		// vertikal
		canvas.drawLine(150, 100, 150, 300, var_paint);
		canvas.drawLine(50, 300, 50, 100, var_paint);
		canvas.drawLine(300, 300, 300, 500, var_paint);
		canvas.drawLine(400, 100, 400, 500, var_paint);
		canvas.drawLine(150, 100, 150, 50, var_paint);
	}
	
	private void zeichne_punkt_zu_punkt(Canvas canvas){
		// TODO besser mit canvas.clipRect();
		var_paint.setColor(Color.RED);
		// Haus_1 zeichen
		// horizonal
		canvas.drawLine(60, 130, 300, 465, var_paint);
		// vertikal
		//canvas.drawLine(150, 100, 150, 300, var_paint);
	}
	
	private void zeichne_Hintergrund(Canvas canvas){
		var_paint.setColor(Color.WHITE);
		canvas.drawPaint(var_paint);
	}
	
	private void zeichne_rotierung(Canvas canvas){
		canvas.rotate(degree_float,250F, 250F);
		canvas.drawPaint(var_paint);
	}

	
	
	// Die Funktionen zeichnet den Weg von 2 Punkten ein
	// TODO Parameter (Koordinaten) von Nils mit uebergeben & Parameter ueberarbeiten
	
	protected void onDraw(Canvas canvas){
	// Bitmap b = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);		
		
		zeichne_Hintergrund(canvas);
		zeichne_rotierung(canvas);
		
		zeichne_testhaus(canvas);
		zeichne_punkt_zu_punkt(canvas);
		
	



		// TestParameter

		int[] koordinaten_x_array = new int[4];
			koordinaten_x_array[0]= 50;
			koordinaten_x_array[1]= 200;
			koordinaten_x_array[2]= 250;
			koordinaten_x_array[3]= 250;
			
		int[] koordinaten_y_array = new int[4];
			koordinaten_y_array[0]= 50;
			koordinaten_y_array[1]= 50;		
			koordinaten_y_array[2]= 50;		
			koordinaten_y_array[3]= 150;		
			
		// canvas.drawLine(koordinaten_x_array[0], koordinaten_y_array[0], koordinaten_x_array[0]+1, koordinaten_y_array[0]+1, var_paint);
			
		
		/*
		var_paint.setColor(Color.BLACK);
		for(int i=0;i<=koordinaten_x_arr.length;i++){
			canvas.drawLine(koordinaten_x_arr[i], koordinaten_y_arr[i], koordinaten_x_arr[i]+1, koordinaten_y_arr[i]+1, var_paint);
		}
		*/
		
		// TODO Welches von den beiden !?
		// canvas.drawPicture();
		// canvas.drawPaint();
		
		
		
		
		
		
		

	}

	private class CompassListener implements SensorEventListener { // innere Klasse


        private Sensor mSensor;

        public CompassListener() { // Konstruktor
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
    	}


        protected void onResume() {
            mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_GAME);
        }

        protected void onStop() {
            mSensorManager.unregisterListener(this);
        }

        public void onSensorChanged(SensorEvent event) {
        	float f = -event.values[0]; // Sensor auslesen
        	if ( (f%5) > 2.5 ) // Sensor auf 5° Schritte auf bzw. abrunden
        		f += 5;
        	f = f - (f%5);
        	degree_float = f; // Winkel an äußere Klasse übergeben
            invalidate(); // Bild neu zeichnen
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    }
}


