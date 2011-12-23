package usajmaps.prototyping;


import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;


	// View beinhaltet Grundlegende Elemente der Benutzerschnittstellenkomponenten
public class GraphicalOutput extends View {
	// Paint beinhaltet Informationen über Style und Farbe wie geometrische Formen gezeichnet werden
	private Paint var_paint;
	// Dynamisches Array
	private ArrayList<Node> var_way;	
	private boolean graphical_output_status = false;
	// Gradzahl vom Magnetsensor
	private float degree = 0;

	// Konstruktor
	public GraphicalOutput(Context c_txt, ArrayList<Node> route) {
	/*
		Parameter super();
		Ruft den Konstruktor der Superklasseauf und uebergibt ihm alle relevanten Parameter   	   
		um die Abgeleitette Klasse vollstaendig konstruieren zu koennen.

	*/	
		super(c_txt);
		var_paint = new Paint();
		var_way = route;
	}

	// Setzt den Status der Grafischen Ausgabe
	public void set_graphical_output(boolean graphical_output_stat) {
		graphical_output_status = graphical_output_stat;
	}

	// Liefert den Status der Grafischen Ausgabe	
	public boolean get_graphical_output() {
		return graphical_output_status;
	}

	// Setzt den Wert der Gradzahl vom Sensor
	public void set_degree(float f) {
		degree = f;
	}



	// ----- TESTUMGEBUNG -----
	// ------------------------
	
	// Hintergrund zeichnen
	private void draw_background(Canvas canvas) {
		// Farbe = Weiß
		var_paint.setColor(Color.WHITE);
		// Fuellt das Canvas mit dem Elementen von Typ Paint
		canvas.drawPaint(var_paint);
	}

	// Um wieviel Grad rotiert das Canvas ?
	private void draw_rotate(Canvas canvas) {
		// das Drehen des Canvas wird vor allen anderen Operationen ausgefuehrt
		canvas.rotate(degree, 250, 350);
		// Fuellt das Canvas mit dem Elementen von Paint
		canvas.drawPaint(var_paint);
	}

	// Testumgebung zeichnen (Haus1,Haus 2, Tueren)
	private void draw_house(Canvas canvas) {
		// Farbe = schwarz
		var_paint.setColor(Color.BLACK);
		// Style des Paint setzen (STROKE=Umrandung / FILL = Ausgefuellt)
		var_paint.setStyle(Paint.Style.STROKE);
		// Haus_1
			canvas.drawRect(180, 400, 260, 320,var_paint);
			canvas.drawRect(340, 180, 410, 260,var_paint);
			canvas.drawRect(260, 180, 340, 100,var_paint);
			canvas.drawRect(260, 180, 340, 400,var_paint);
		// Haus_2
			canvas.drawRect(20, 450, 160, 520,var_paint);
			canvas.drawRect(20, 520, 90, 590,var_paint);
			canvas.drawRect(90, 520, 160, 590,var_paint);
		// Tueren von Haus 1 & Haus 2
		// Farbe Grau
		var_paint.setColor(Color.GRAY);
		// FILL = Ausgefuellt
		var_paint.setStyle(Paint.Style.FILL);
		// horizontal
			canvas.drawRect(290, 178, 310, 182,var_paint);
			canvas.drawRect(290, 398, 310, 402,var_paint);
			canvas.drawRect(115, 518, 135, 522,var_paint);
			canvas.drawRect(45, 518, 65, 522,var_paint);
		// vertikal
			canvas.drawRect(158, 475, 162, 495,var_paint);
			canvas.drawRect(258, 350, 262, 370,var_paint);
			canvas.drawRect(258, 270, 262, 290,var_paint);
			canvas.drawRect(338, 210, 342, 230,var_paint);
	}

	// Knoten zeichnen
	private void draw_nodes(Canvas canvas) {
		var_paint.setStyle(Paint.Style.FILL);
		var_paint.setColor(Color.RED);
		// Knoten_1 (x=300/y=220)
		canvas.drawCircle(300, 220,3.0f,var_paint);
		// Knoten_2 (x=300/y=280)
		canvas.drawCircle(300, 280,3.0f,var_paint);
		// Knoten_3 (x=300/y=360)
		canvas.drawCircle(300, 360,3.0f,var_paint);
		// Knoten_4 (x=300/y=485)
		canvas.drawCircle(300, 485,3.0f,var_paint);
		// Knoten_5 (x=220/y=280)
		canvas.drawCircle(220, 280,3.0f,var_paint);
		// Knoten_6 (x=170/y=485)
		canvas.drawCircle(170, 485,3.0f,var_paint);
		// Knoten_7 (x=125/y=485)
		canvas.drawCircle(125, 485,3.0f,var_paint);
		// Knoten_8 (x=55/y=485)
		canvas.drawCircle(55, 485,3.0f,var_paint);
	}

	// Verbindungen (Punkt zu Punkt) zeichnen
	private void draw_p2p(Canvas canvas) {
		// Vektor mit Knoten
		ArrayList<Node> TempArr = new ArrayList<Node>();
		TempArr = var_way;
		var_paint.setColor(Color.BLUE);
		// 1. bis i. Element zeichnen und zwar immer von Punkt zu Punkt 
		for (int i = 0; i < TempArr.size() - 1; i++) {
			canvas.drawLine(TempArr.get(i).getPictureCoords().x, TempArr.get(i).getPictureCoords().y, TempArr.get(i+1).getPictureCoords().x, TempArr.get(i + 1).getPictureCoords().y, var_paint);	
		}
	}
	/*
	 onDraw wird von der Klasse View ueberschrieben.
	 Implemeniert letztendlich die Zeichnung.
	*/
	protected void onDraw(Canvas canvas) {
		draw_background(canvas);
		draw_rotate(canvas);
		draw_house(canvas);
		draw_nodes(canvas);
		draw_p2p(canvas);
	}

}