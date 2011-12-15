package usajmaps.prototyping;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class GraphicalOutput extends View {
	private Paint 							var_paint;
	private ArrayList<Node> 				var_way;	
	private boolean graphical_output_status = false;
	private float degree 					= -1;
	private int x_ref 						= 150;
	private int y_ref 						= -160;

	// Konstruktor
	public GraphicalOutput(Context c_txt, ArrayList<Node> route) {
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
		var_paint.setColor(Color.WHITE);
		canvas.drawPaint(var_paint);
	}

	// Um wieviel Grad rotiert das Canvas ?
	private void draw_rotate(Canvas canvas) {
		canvas.rotate(degree, 250 + x_ref, 350 + y_ref);
		canvas.drawPaint(var_paint);
	}

	// Testumgebung zeichnen
	private void draw_house(Canvas canvas) {
		// Farbe = schwarz
		var_paint.setColor(Color.BLACK);
		// STROKE = Umrandung
		var_paint.setStyle(Paint.Style.STROKE);
		// Haus_1
		canvas.drawRect(180 + x_ref, 400 + y_ref, 260 + x_ref, 320 + y_ref, var_paint);
		canvas.drawRect(340 + x_ref, 180 + y_ref, 410 + x_ref, 260 + y_ref,	var_paint);
		canvas.drawRect(260 + x_ref, 180 + y_ref, 340 + x_ref, 100 + y_ref,	var_paint);
		canvas.drawRect(260 + x_ref, 180 + y_ref, 340 + x_ref, 400 + y_ref,	var_paint);
		// Haus_2
		canvas.drawRect(20 + x_ref, 450 + y_ref, 160 + x_ref, 520 + y_ref,	var_paint);
		canvas.drawRect(20 + x_ref, 520 + y_ref, 90 + x_ref, 590 + y_ref,	var_paint);
		canvas.drawRect(90 + x_ref, 520 + y_ref, 160 + x_ref, 590 + y_ref, 	var_paint);
		// Tueren
		var_paint.setColor(Color.GRAY);
		var_paint.setStyle(Paint.Style.FILL);
		// horizontal
		canvas.drawRect(290 + x_ref, 178 + y_ref, 310 + x_ref, 182 + y_ref,	var_paint);
		canvas.drawRect(290 + x_ref, 398 + y_ref, 310 + x_ref, 402 + y_ref,	var_paint);
		canvas.drawRect(115 + x_ref, 518 + y_ref, 135 + x_ref, 522 + y_ref,	var_paint);
		canvas.drawRect(45 + x_ref, 518 + y_ref, 65 + x_ref, 522 + y_ref,	var_paint);
		// vertikal
		canvas.drawRect(158 + x_ref, 475 + y_ref, 162 + x_ref, 495 + y_ref,	var_paint);
		canvas.drawRect(258 + x_ref, 350 + y_ref, 262 + x_ref, 370 + y_ref,	var_paint);
		canvas.drawRect(258 + x_ref, 270 + y_ref, 262 + x_ref, 290 + y_ref,	var_paint);
		canvas.drawRect(338 + x_ref, 210 + y_ref, 342 + x_ref, 230 + y_ref,	var_paint);
	}

	// Knoten zeichnen
	private void draw_nodes(Canvas canvas) {
		var_paint.setStyle(Paint.Style.FILL);
		var_paint.setColor(Color.RED);
		// Knoten_1 (x=300/y=220)
		canvas.drawCircle(300 + x_ref, 220 + y_ref, 3.0f, var_paint);
		// Knoten_2 (x=300/y=280)
		canvas.drawCircle(300 + x_ref, 280 + y_ref, 3.0f, var_paint);
		// Knoten_3 (x=300/y=360)
		canvas.drawCircle(300 + x_ref, 360 + y_ref, 3.0f, var_paint);
		// Knoten_4 (x=300/y=485)
		canvas.drawCircle(300 + x_ref, 485 + y_ref, 3.0f, var_paint);
		// Knoten_5 (x=220/y=280)
		canvas.drawCircle(220 + x_ref, 280 + y_ref, 3.0f, var_paint);
		// Knoten_6 (x=170/y=485)
		canvas.drawCircle(170 + x_ref, 485 + y_ref, 3.0f, var_paint);
		// Knoten_7 (x=125/y=485)
		canvas.drawCircle(125 + x_ref, 485 + y_ref, 3.0f, var_paint);
		// Knoten_8 (x=55/y=485)
		canvas.drawCircle(55 + x_ref, 485 + y_ref, 3.0f, var_paint);
	}

	// Verbindungen (Punkt zu Punkt) zeichnen
	private void draw_p2p(Canvas canvas) {
		ArrayList<Node> TempArr = new ArrayList<Node>();
		TempArr = var_way;
		var_paint.setColor(Color.BLUE);
		for (int i = 0; i < TempArr.size() - 1; i++) {
			canvas.drawLine(TempArr.get(i).getPictureCoords().x + x_ref, TempArr.get(i).getPictureCoords().y + y_ref, TempArr.get(i + 1).getPictureCoords().x + x_ref, TempArr.get(i + 1).getPictureCoords().y + y_ref, var_paint);
		}
	}

	// onDraw wird von der Klasse View überschrieben,zeichnet im Endeffekt aus das Canvas
	protected void onDraw(Canvas canvas) {
		draw_background(canvas);
		draw_rotate(canvas);
		draw_house(canvas);
		draw_nodes(canvas);
		draw_p2p(canvas);
	}

}
