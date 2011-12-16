package Zelos.UASJ_Maps;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class GraphicalOutput extends View {
	private Paint var_paint;
	private ArrayList<ArrayList<Node>> var_way;		
	private Node position;
	private boolean graphical_output_status = false;
	private float degree = -1;
	private int x_ref = 150;
	private int y_ref = -160;
	private short check_counter=0; // Zählvariable wie oft der Checkbutton gedrückt wurde
	private short current_floor=-1; // Speichert ID der aktuellen Ebene
	private int state=-1; // 0: Routenberechnung, 1: Positionsanzeige, 2: Campusanzeige

	
	//TODO: x_ref & y_ref entferen, dafür Canvas.Translate(x,y) nutzen
	// Konstruktor
	public GraphicalOutput(Context c_txt, ArrayList<ArrayList<Node>> route) {
		super(c_txt);
		var_paint = new Paint();
//		var_way = route;
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
	
	/** 
	 * Angabe in welche Etage gewechselt werden soll
	 * @param f (Codierung)
	 * @return Wert, ob mit Etagen wechsel irgendwelche Grenzen gesetzt werden (nicht weiter nach oben/unten...)
	 * nicht weiter nach oben: 1
	 * nicht weiter nach unten: -1
	 * alles normal: 0
	 */
	public short set_floor(int f){
		
		switch(f){
		case 0: // 1 Ebene nach unten
			current_floor --;	// geht nur, wenn die GUI diese Funktion nicht aufruft, wenn es nicht weiter nach unten/nach oben geht
			break;
		case 1:	// 1 Ebene nach oben
			current_floor ++; // geht nur, wenn die GUI diese Funktion nicht aufruft, wenn es nicht weiter nach unten/nach oben geht
			break;
		case 2:	// Campus anzeigen
			current_floor = 0;
			break;
			
		}
		//TODO: EbenenIDs in den IFs einfügen für die es nicht mehr weiter geht. (Geht erst vollständig nachdem jede Ebene eine ID zugewiesen bekommen hat
		if((current_floor == 1) ||  (current_floor == 2) || (current_floor == 3) || (current_floor == 4) || (current_floor == 5))
			return 1; // Wenn in irgendeinem Haus die oberste Ebene erreicht ist, dann 1
		else	if((current_floor == 1) ||  (current_floor == 2) || (current_floor == 3) || (current_floor == 4) || (current_floor == 5))
					return -1;
		else		// Wenn in irgendeinem Haus die unterste Ebene erreicht ist, dann -1
			return 0;
	}
	
	/**
	 * Angabe, dass Checkbutton gedrückt wird
	 * @return Wert, wieviele Ebenen auf dem Weg noch abzuarbeiten sind(z.b. wenn auf letzter Ebene, return 0)
	 */
	public short set_check(){
		check_counter ++;				// Checkcounter erhöhen
		current_floor = (short) var_way.get(check_counter).get(0).getFloorID(); // Current_floor aktualisieren (auf nächste abzuschreitende Ebene)
		return (short) (var_way.size()-(check_counter+1));		// restliche Ebendenanzahl zurückgeben
		// TODO: So wie es jetzt ist, wird beim Drücken von "CHECK" automatisch die nächste Ebene auf dem Weg angezeigt.Auch, wenn man gerade einfach mal in den Ebenen gewechselt hat. Ist das so OK? 
	}
	
	/**
	 * Setzt Status auf Routenanzeige
	 * @param list : 2D-Array mit abzuschreitenden Ebenen und Knoten
	 */
	public void set_state_path(ArrayList<ArrayList<Node>> list){
		state = 0;						// Status auf Routenanzeige setzen
		check_counter = 0;				// Check_Counter resetten
		var_way = list;					// Route speicher/aktualisieren
		current_floor = (short) list.get(0).get(0).getFloorID();		// erste Ebene ermitteln
	}
	
	/**
	 * Setzt Status auf Positionsanzeige
	 * @param n : Knoten, der angezeigt werden soll
	 */
	public void set_state_position(Node n){
		state = 1;						// Status auf Positionsanzeige setzen
		position = n;
		current_floor = (short) position.getFloorID(); // EbenenID für Postion holen
	}
	
	/**
	 * Setzt Status auf Campusanzeige(freie Navigation)
	 */
	public void set_state_campus(){
		state = 2;						// Status auf freie Navigation setzen
		//current_floor = CampusID	//TODO: CampusID eintragen
	}

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
//		TempArr = var_way;
//		var_paint.setColor(Color.BLUE);
//		for (int i = 0; i < TempArr.size() - 1; i++) {
//			canvas.drawLine(TempArr.get(i).getPictureCoords().x + x_ref,
//					TempArr.get(i).getPictureCoords().y + y_ref,
//					TempArr.get(i + 1).getPictureCoords().x + x_ref, TempArr
//							.get(i + 1).getPictureCoords().y + y_ref, var_paint);
//		}
	}

	/**
	 * onDraw wird von der Klasse View überschrieben,zeichnet im Endeffekt aus das Canvas
	 */
	protected void onDraw(Canvas canvas) {
		draw_background(canvas);
		draw_rotate(canvas);
//		draw_floor(current_floor); // aktuelle Ebene zeichnen. Egal in welchem Zustand man ist.
		//TODO: draw_floor(FloorID) erstellen in der jede Ebene ausgewählt werden kann
		switch(state){ // jenachdem in welchem Zustand und auf welcher Ebene man sich befindet wird nun noch eine Route oder eine Position in die Ebene gezeichnet
		case 0:			// Wenn in Routenberechnung, dann soll Weg angezeigt werden, falls dieser in aktueller Ebene vorhanden
			if(var_way_contains_floor(current_floor)!=-1){ // Wenn auf aktueller Ebene Route vorhanden
				draw_route(canvas, var_way.get(var_way_contains_floor(current_floor))); // zeichen Route für folgenden 
			}
//			draw_route(ArrayList<Node>);
			break;
		case 1: // Wenn in Positionsanzeige(keine Routenanzeige)
			if(current_floor == position.getFloorID())
				draw_position(canvas, position);
			break;
		case 2:	// Wenn in Campusanzeige(keine Routenanzeige & keine Positionsanzeige
			//TODO: überlegen, ob hier noch etwas rein soll. Theoretisch wäre ansonsten alles abgedeckt. brauch man den Case-Fall überhaupt?
			break;
		}
		
		
//		draw_house(canvas);
//		draw_nodes(canvas);
//		draw_p2p(canvas);
		
	}
	
	/**
	 * Zeichnet die Position des Knotens ins Canvas
	 * @param p
	 */
	private void draw_position(Canvas c, Node p) {
		// TODO: Postion muss noch auf Canvas gezeichnet werden + Umrechnung der Koordinaten
	}

	/**
	 * Durchsucht die Wegstrecke, ob angegebene Ebene enthalten ist
	 * @param floorID : EbenenID nach der gesucht werden soll
	 * @return 	-1, wenn Ebene nicht enthalten.
	 * 			Index, im array, wenn enthalten
	 */
	private int var_way_contains_floor(int floorID){
		for(int i =0; i < var_way.size();i++){
			if(var_way.get(i).get(0).getFloorID() == floorID)
				return i;
		}
		return -1;
	}
	
	/**
	 * Zeichnet die Route auf das Canvas
	 * @param c : canvas auf das gezeichnet wird
	 * @param list : Abzugehende Knoten auf einer Ebene
	 */
	private void draw_route(Canvas c, ArrayList<Node> list){
		//zeichne Route
		//TODO: Algorithmus zum Routenzeichnen muss erstellt werden + Berechnung der Bildknotenkoords aus den Knotenkoordinaten
	}

}
