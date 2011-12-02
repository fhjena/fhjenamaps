package eu.AndroidTraining.Dashboard;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class GraphicalOutput extends View {
	// Canvas von 0/0 bis 500/500
	Paint var_paint;
	private int start_x = 50;
	private int start_y = 30;
	private int end_x = 150;
	private int end_y = 30;
	private int start_raum = -1;
	private int ziel_raum = -1;
	private boolean GraphicalOutput_status = false;
	private float degree_float = 0;
	private ArrayList<Knoten> Weg;
	private int x_ref = 150;
	private int y_ref = -160;

	// Konstruktor
	public GraphicalOutput(Context c_text, ArrayList<Knoten> Route) {
		super(c_text);
		var_paint = new Paint();
		Weg = Route;

	}

	// Getter und Setter zur Verwaltung der Start - und Zielraeume
	public void set_start_ziel(int start_raum_, int ziel_raum_) {
		start_raum = start_raum_;
		start_raum = ziel_raum_;
	}

	public int get_start_raum() {
		return start_raum;
	}

	public int get_ziel_raum() {
		return ziel_raum;
	}

	// Getter und Setter zur Verwaltung der Koordinaten
	// ------------------------------------------------
	// Getter und Setter zur Verwaltung der Koordinaten
	public void set_x_koordnaten(int von, int bis) {
		start_x = von;
		end_x = bis;
	}

	public void set_y_koordinaten(int von, int bis) {
		start_y = von;
		end_y = bis;
	}

	public int get_start_x() {
		return start_x;
	}

	public int get_start_y() {
		return start_y;
	}

	public int get_end_x() {
		return end_x;
	}

	public int get_end_y() {
		return end_y;
	}

	// Getter und Setter zur Statusverwaltung der GraphicalOutput
	public void set_GraphicalOutput(boolean status_der_GraphicalOutput) {
		GraphicalOutput_status = status_der_GraphicalOutput;
	}

	public boolean get_GraphicalOutput() {
		return GraphicalOutput_status;
	}

	// Gradzahl des Sensors
	public void set_degree(float f) {
		degree_float = f;
	}

	// Testumgebung zeichnen

	private void zeichne_hintergrund(Canvas canvas) {
		var_paint.setColor(Color.WHITE);
		canvas.drawPaint(var_paint);
	}

	private void zeichne_rotierung(Canvas canvas) {
		// canvas.rotate(degree_float,
		// ((float)((canvas.getWidth()/2))),((float)((canvas.getHeight()/2))));
		canvas.rotate(degree_float, 250 + x_ref, 350 + y_ref);
		canvas.drawPaint(var_paint);
	}

	private void zeichne_testhaus(Canvas canvas) {
		// Farbe = schwarz
		var_paint.setColor(Color.BLACK);
		// Syle = STROKE = Umrandung
		var_paint.setStyle(Paint.Style.STROKE);
		// Haus_1
		canvas.drawRect(180 + x_ref, 400 + y_ref, 260 + x_ref, 320 + y_ref,
				var_paint);
		canvas.drawRect(340 + x_ref, 180 + y_ref, 410 + x_ref, 260 + y_ref,
				var_paint);
		canvas.drawRect(260 + x_ref, 180 + y_ref, 340 + x_ref, 100 + y_ref,
				var_paint);
		canvas.drawRect(260 + x_ref, 180 + y_ref, 340 + x_ref, 400 + y_ref,
				var_paint);
		// Haus_2
		canvas.drawRect(20 + x_ref, 450 + y_ref, 160 + x_ref, 520 + y_ref,
				var_paint);
		canvas.drawRect(20 + x_ref, 520 + y_ref, 90 + x_ref, 590 + y_ref,
				var_paint);
		canvas.drawRect(90 + x_ref, 520 + y_ref, 160 + x_ref, 590 + y_ref,
				var_paint);
		// Tueren
		var_paint.setColor(Color.GRAY);
		var_paint.setStyle(Paint.Style.FILL);
		// horizontal
		canvas.drawRect(290 + x_ref, 178 + y_ref, 310 + x_ref, 182 + y_ref,
				var_paint);
		canvas.drawRect(290 + x_ref, 398 + y_ref, 310 + x_ref, 402 + y_ref,
				var_paint);
		canvas.drawRect(115 + x_ref, 518 + y_ref, 135 + x_ref, 522 + y_ref,
				var_paint);
		canvas.drawRect(45 + x_ref, 518 + y_ref, 65 + x_ref, 522 + y_ref,
				var_paint);
		// vertikal
		canvas.drawRect(158 + x_ref, 475 + y_ref, 162 + x_ref, 495 + y_ref,
				var_paint);
		canvas.drawRect(258 + x_ref, 350 + y_ref, 262 + x_ref, 370 + y_ref,
				var_paint);
		canvas.drawRect(258 + x_ref, 270 + y_ref, 262 + x_ref, 290 + y_ref,
				var_paint);
		canvas.drawRect(338 + x_ref, 210 + y_ref, 342 + x_ref, 230 + y_ref,
				var_paint);
	}

	private void zeichne_testknoten(Canvas canvas) {
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

	// ArrayList<Point> TempArr = new ArrayList<Point>();

	private void zeichne_punkt_zu_punkt(Canvas canvas) {
		ArrayList<Knoten> TempArr = new ArrayList<Knoten>();
		TempArr = Weg;
		var_paint.setColor(Color.BLUE);
		for (int i = 0; i < TempArr.size() - 1; i++) {
			// for(int i=0;i<=10;i++){
			// SYNTAX canvas.drawLine(startX, startY, stopX, stopY, paint)
			canvas.drawLine(TempArr.get(i).getBildKoords().x + x_ref, TempArr
					.get(i).getBildKoords().y + y_ref, TempArr.get(i + 1)
					.getBildKoords().x + x_ref, TempArr.get(i + 1)
					.getBildKoords().y + y_ref, var_paint);
			// canvas.drawLine(300, 220, 300, 280, var_paint);
			// canvas.drawLine(300, 280, 300, 360, var_paint);

		}
	}

	protected void onDraw(Canvas canvas) {
		zeichne_hintergrund(canvas);
		zeichne_rotierung(canvas);
		zeichne_testhaus(canvas);
		zeichne_testknoten(canvas);

		zeichne_punkt_zu_punkt(canvas);
	}

}
