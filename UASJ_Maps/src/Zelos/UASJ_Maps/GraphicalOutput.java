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
	public GraphicalOutput(Context c_txt) {
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
			current_floor = 0; // =CampusID
			break;
		case 3: // Anzeige wieder auf aktuelle Route/Position setzen
			if(state==0) 
				current_floor = (short) var_way.get(check_counter).get(0).getFloorID(); // Current_floor aktualisieren (auf nächste abzuschreitende Ebene)
			else if(state==1)
				current_floor = (short) position.getFloorID(); // Current_floor aktualisieren (auf nächste abzuschreitende Ebene)
			break;
		case 4: // aus Campus Haus 1 anklicken
			current_floor = 9; // haus 1 Ebene 0
			break;
		case 5: // aus Campus Haus 2 anklicken
			current_floor = 15; // haus 2 Ebene 0
			break;
		case 6: // aus Campus Haus 3 anklicken
			current_floor = 21; // haus 3 Ebene 0
			break;
		case 7: // aus Campus Haus 4 anklicken
			current_floor = 26; // haus 4 Ebene 0
			break;
		case 8: // aus Campus Haus 5 anklicken
			current_floor = 3; // haus 5 Ebene 0
			break;
			
		}
		if((current_floor == 7) ||  (current_floor == 13) || (current_floor == 19) || (current_floor == 24) || (current_floor == 29))
			return 1; // Wenn in irgendeinem Haus die oberste Ebene erreicht ist, dann 1
		else	if((current_floor == 1) ||  (current_floor == 8) || (current_floor == 14) || (current_floor == 20) || (current_floor == 25))
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
		current_floor = 0;	// = CampusID
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

	// Verbindungen (Punkt zu Punkt) zeichnen
//	private void draw_p2p(Canvas canvas) {
//		ArrayList<Node> TempArr = new ArrayList<Node>();
//		TempArr = var_way;
//		var_paint.setColor(Color.BLUE);
//		for (int i = 0; i < TempArr.size() - 1; i++) {
//			canvas.drawLine(TempArr.get(i).getPictureCoords().x + x_ref,
//					TempArr.get(i).getPictureCoords().y + y_ref,
//					TempArr.get(i + 1).getPictureCoords().x + x_ref, TempArr
//							.get(i + 1).getPictureCoords().y + y_ref, var_paint);
//		}
//	}

	/**
	 * onDraw wird von der Klasse View überschrieben,zeichnet im Endeffekt aus das Canvas
	 */
	protected void onDraw(Canvas canvas) {
		draw_background(canvas);
		draw_rotate(canvas);
		draw_floor(current_floor, canvas); // aktuelle Ebene zeichnen. Egal in welchem Zustand man ist.
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
//		this.v
		//TODO: Algorithmus zum Routenzeichnen muss erstellt werden + Berechnung der Bildknotenkoords aus den Knotenkoordinaten
	}
	
	/**
	 * Zeigt die jeweilige Ebene an
	 * @param floorID
	 */
	private void draw_floor(int floorID, Canvas c){
		switch(floorID){
		case 0:
			draw_floor_00(c);
			break;
		case 1:
			draw_floor_01(c);
			break;
		case 2:
			draw_floor_02(c);
			break;
		case 3:
			draw_floor_03(c);
			break;
		case 4:
			draw_floor_04(c);
			break;
		case 5:
			draw_floor_05(c);
			break;
		case 6:
			draw_floor_06(c);
			break;
		case 7:
			draw_floor_07(c);
			break;
		case 8:
			draw_floor_08(c);
			break;
		case 9:
			draw_floor_09(c);
			break;
		case 10:
			draw_floor_10(c);
			break;
		case 11:
			draw_floor_11(c);
			break;
		case 12:
			draw_floor_12(c);
			break;
		case 13:
			draw_floor_13(c);
			break;
		case 14:
			draw_floor_14(c);
			break;
		case 15:
			draw_floor_15(c);
			break;
		case 16:
			draw_floor_16(c);
			break;
		case 17:
			draw_floor_17(c);
			break;
		case 18:
			draw_floor_18(c);
			break;
		case 19:
			draw_floor_19(c);
			break;
		case 20:
			draw_floor_20(c);
			break;
		case 21:
			draw_floor_21(c);
			break;
		case 22:
			draw_floor_22(c);
			break;
		case 23:
			draw_floor_23(c);
			break;
		case 24:
			draw_floor_24(c);
			break;
		case 25:
			draw_floor_25(c);
			break;
		case 26:
			draw_floor_26(c);
			break;
		case 27:
			draw_floor_27(c);
			break;
		case 28:
			draw_floor_28(c);
			break;
		case 29:
			draw_floor_29(c);
			break;
		default: 
			System.out.println("EbenenID nicht vorhanden");
			c.drawText("EbenenID nicht vorhanden", 300, 200, new Paint());
			break;
		}
	}
	/*##### Funktionen zum Zeichnen der einzelnen Ebenen*/
	private void draw_floor_00(Canvas c){ 		// Zeichnet Campus
		c.drawText("Campus", 300, 200, new Paint());
	}
	
	private void draw_floor_01(Canvas c){ 		// Zeichnet 5.-02
		c.drawText("Haus:5 Ebene: -2", 300, 200, new Paint());
	}
	
	private void draw_floor_02(Canvas c){ 		// Zeichnet 5.-01
		c.drawText("Haus:5 Ebene: -1", 300, 200, new Paint());
	}
	
	private void draw_floor_03(Canvas c){ 		// Zeichnet 5.00
		c.drawText("Haus:5 Ebene: 00", 300, 200, new Paint());
	}
	
	private void draw_floor_04(Canvas c){ 		// Zeichnet 5.01
		c.drawText("Haus:5 Ebene: 01", 300, 200, new Paint());
	}
	
	private void draw_floor_05(Canvas c){ 		// Zeichnet 5.02
		c.drawText("Haus:5 Ebene: 02", 300, 200, new Paint());
	}
	
	private void draw_floor_06(Canvas c){ 		// Zeichnet 5.03
		c.drawText("Haus:5 Ebene: 03", 300, 200, new Paint());
	}
	
	private void draw_floor_07(Canvas c){ 		// Zeichnet 5.3Z
		c.drawText("Haus:5 Ebene: 3Z", 300, 200, new Paint());
	}
	
	private void draw_floor_08(Canvas c){ 		// Zeichnet 1.-01
		c.drawText("Haus:1 Ebene: -1", 300, 200, new Paint());
	}
	
	private void draw_floor_09(Canvas c){ 		// Zeichnet 1.00
		c.drawText("Haus:1 Ebene: 00", 300, 200, new Paint());
	}
	
	private void draw_floor_10(Canvas c){ 		// Zeichnet 1.01
		c.drawText("Haus:1 Ebene: 01", 300, 200, new Paint());
	}
	
	private void draw_floor_11(Canvas c){ 		// Zeichnet 1.02
		c.drawText("Haus:1 Ebene: 02", 300, 200, new Paint());
	}
	
	private void draw_floor_12(Canvas c){ 		// Zeichnet 1.03
		c.drawText("Haus:1 Ebene: 03", 300, 200, new Paint());
	}
	
	private void draw_floor_13(Canvas c){ 		// Zeichnet 1.04
		c.drawText("Haus:1 Ebene: 04", 300, 200, new Paint());
	}
	
	private void draw_floor_14(Canvas c){ 		// Zeichnet 2.-01
		c.drawText("Haus:2 Ebene: -1", 300, 200, new Paint());
	}
	
	private void draw_floor_15(Canvas c){ 		// Zeichnet 2.00
		c.drawText("Haus:2 Ebene: 00", 300, 200, new Paint());
	}
	
	private void draw_floor_16(Canvas c){ 		// Zeichnet 2.01
		c.drawText("Haus:2 Ebene: 01", 300, 200, new Paint());
	}
	
	private void draw_floor_17(Canvas c){ 		// Zeichnet 2.02
		c.drawText("Haus:2 Ebene: 02", 300, 200, new Paint());
	}

	private void draw_floor_18(Canvas c){ 		// Zeichnet 2.03
		c.drawText("Haus:2 Ebene: 03", 300, 200, new Paint());
	}

	private void draw_floor_19(Canvas c){ 		// Zeichnet 2.04
		c.drawText("Haus:2 Ebene: 04", 300, 200, new Paint());
	}

	private void draw_floor_20(Canvas c){ 		// Zeichnet 3.-01
		c.drawText("Haus:3 Ebene: -1", 300, 200, new Paint());
	}

	private void draw_floor_21(Canvas c){ 		// Zeichnet 3.00
		c.drawText("Haus:3 Ebene: 00", 300, 200, new Paint());
	}

	private void draw_floor_22(Canvas c){ 		// Zeichnet 3.01
		c.drawText("Haus:3 Ebene: 01", 300, 200, new Paint());
	}

	private void draw_floor_23(Canvas c){ 		// Zeichnet 3.02
		c.drawText("Haus:3 Ebene: 02", 300, 200, new Paint());
	}

	private void draw_floor_24(Canvas c){ 		// Zeichnet 3.03
		c.drawText("Haus:3 Ebene: 03", 300, 200, new Paint());
	}

	private void draw_floor_25(Canvas c){ 		// Zeichnet 4.-01
		c.drawText("Haus:4 Ebene: -1", 300, 200, new Paint());
	}

	private void draw_floor_26(Canvas c){ 		// Zeichnet 4.00
		c.drawText("Haus:4 Ebene: 00", 300, 200, new Paint());
	}

	private void draw_floor_27(Canvas c){ 		// Zeichnet 4.01
		c.drawText("Haus:4 Ebene: 01", 300, 200, new Paint());
	}

	private void draw_floor_28(Canvas c){ 		// Zeichnet 4.02
		c.drawText("Haus:4 Ebene: 02", 300, 200, new Paint());
	}

	private void draw_floor_29(Canvas c){ 		// Zeichnet 4.03
		c.drawText("Haus:4 Ebene: 03", 300, 200, new Paint());
	}


}
