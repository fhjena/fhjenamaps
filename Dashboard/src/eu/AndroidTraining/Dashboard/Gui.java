
package eu.AndroidTraining.Dashboard;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class Gui extends View {
	// Canvas von 0/0 bis 500/500
	Paint var_paint;
	private int start_x = 50;
	private int start_y = 30;
	private int end_x	= 150;
	private int end_y 	= 30;
	private int start_raum	= -1;
	private int ziel_raum	= -1;
	private boolean gui_status = false;
	private float degree_float=0;
	private ArrayList<Knoten> Weg;
	 

	
	// Konstruktor
	public Gui(Context c_text, ArrayList<Knoten> Route) {
		super(c_text);
		var_paint = new Paint();
		Weg = Route;
		
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
	
	// Gradzahl des Sensors
	public void set_degree(float f) {
		degree_float = f;
	}

	
	// Testumgebung zeichnen
	
	private void zeichne_hintergrund(Canvas canvas){
		var_paint.setColor(Color.WHITE);
		canvas.drawPaint(var_paint);
	}
	
	private void zeichne_rotierung(Canvas canvas){
		canvas.rotate(degree_float,250F, 250F);
		canvas.drawPaint(var_paint);
	}
	
	private void zeichne_testhaus(Canvas canvas){
		// Farbe = schwarz
		var_paint.setColor(Color.BLACK);
		// Syle = STROKE = Umrandung
		var_paint.setStyle(Paint.Style.STROKE);
		//Haus_1
		canvas.drawRect(180, 400, 260, 320, var_paint);
		canvas.drawRect(340, 180, 410, 260, var_paint);		
		canvas.drawRect(260, 180, 340, 100, var_paint);
		canvas.drawRect(260, 180, 340, 400, var_paint);
		//Haus_2
		canvas.drawRect(20, 450, 160, 520, var_paint);
		canvas.drawRect(20, 520, 90, 590, var_paint);
		canvas.drawRect(90, 520, 160, 590, var_paint);
		// Tueren
		var_paint.setColor(Color.GRAY);
		var_paint.setStyle(Paint.Style.FILL);
		// horizontal
		canvas.drawRect(290, 178, 310, 182, var_paint);
		canvas.drawRect(290, 398, 310, 402, var_paint);
		canvas.drawRect(115, 518, 135, 522, var_paint);
		canvas.drawRect(45, 518, 65, 522, var_paint);
		// vertikal
		canvas.drawRect(158, 475, 162, 495, var_paint);
		canvas.drawRect(258, 350, 262, 370, var_paint);
		canvas.drawRect(258, 270, 262, 290, var_paint);
		canvas.drawRect(338, 210, 342, 230, var_paint);
	}
	
	private void zeichne_testknoten(Canvas canvas){
		var_paint.setStyle(Paint.Style.FILL);
		var_paint.setColor(Color.RED);
		// Knoten_1 (x=300/y=220)
		canvas.drawCircle(300, 220, 3.0f, var_paint);
		// Knoten_2 (x=300/y=280)
		canvas.drawCircle(300, 280, 3.0f, var_paint);
		// Knoten_3 (x=300/y=360)
		canvas.drawCircle(300, 360, 3.0f, var_paint);
		// Knoten_4 (x=300/y=485)
		canvas.drawCircle(300, 485, 3.0f, var_paint);
		// Knoten_5 (x=220/y=280)
		canvas.drawCircle(220, 280, 3.0f, var_paint);
		// Knoten_6 (x=170/y=485)
		canvas.drawCircle(170, 485, 3.0f, var_paint);
		// Knoten_7 (x=125/y=485)
		canvas.drawCircle(125, 485, 3.0f, var_paint);
		// Knoten_8 (x=55/y=485)
		canvas.drawCircle(55, 485, 3.0f, var_paint);
	}
	
    	
//	ArrayList<Point> TempArr = new ArrayList<Point>();

	
	private void zeichne_punkt_zu_punkt(Canvas canvas){
		ArrayList<Knoten> TempArr = new ArrayList<Knoten>();
		TempArr = Weg;
		var_paint.setColor(Color.BLUE);
		for(int i=0;i<TempArr.size()-1;i++){
//		for(int i=0;i<=10;i++){		
//			SYNTAX	canvas.drawLine(startX, startY, stopX, stopY, paint)
			canvas.drawLine(TempArr.get(i).getBildKoords().x, TempArr.get(i).getBildKoords().y, TempArr.get(i+1).getBildKoords().x, TempArr.get(i+1).getBildKoords().y, var_paint);
//			canvas.drawLine(300, 220, 300, 280, var_paint);
//			canvas.drawLine(300, 280, 300, 360, var_paint);
			
		}
	}
	
	protected void onDraw(Canvas canvas){
		zeichne_hintergrund(canvas);
		zeichne_rotierung(canvas);
		zeichne_testhaus(canvas);
		zeichne_testknoten(canvas);
		
		zeichne_punkt_zu_punkt(canvas);
	}

}


