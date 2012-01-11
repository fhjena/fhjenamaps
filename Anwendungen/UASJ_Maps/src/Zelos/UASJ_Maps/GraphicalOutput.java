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

	public boolean isStateCampus(){
		if(state==2)
			return true;
		else return false;
	}
	
	//TODO: mache diese Funktion
	// berechnet ob unter angegebenen koordinaten Haus liegt. (welches)haus getroffen? und setzt current floor
	public boolean performClickOnCampus(int X, int Y){
		return true; // wenn Haus getroffen
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
	
	/**
	 * Gibt die Pfadbeschreibung zurück. Muss nach jedem Ebenenwechsel neu abgefragt werden.
	 * @return
	 */
	public String get_PathDescription(){
		if(state==0){					// gibt nur gültigen String aus, wenn in Status Routenberechnung
			String str;
			if(var_way_contains_floor(current_floor)!=-1){			// wenn die aktuelle Ebene des Pfades angezeigt wird
				if((var_way.size()-(check_counter+1)) == 0){// wenn auf letzter Ebene{
					return "Follow path to the final destination";
				}
				else{
					if(get_HouseNumber(1)=="Campus")			// Wenn man auf den Campus wechseln soll
						str = "At the end of the path: use the exit to campus";
					else 	
						if(get_HouseNumber(0)=="Campus" && get_HouseNumber(1)!= "Campus")		// Wenn man aus dem Campus in ein Haus wechseln soll
							str = "At the end of the path: use entrance to House " + get_HouseNumber(1) + "floor: " + get_floorNumber(1);
						else		// Wenn man Ebenen in einem Haus wechseln soll
							str = "At the end of the path: use the elevator/stairs to floor " + get_floorNumber(1);
					return str;		// Gesetzten String zurückgeben
				}
			}
			else{		// Wenn man in freier Navigation ist und eine andere Ebene anzeigt, als die aktuelle abzuschreitende Ebene
				str = "For further instructions: please return to the path via \"show route\"";
				return str;			// gesetzen String zurückgeben
			}
		}
		else
			return "";		// leeren String zurückgeben, wenn man nicht in Routenberechnung ist
	}
/**
 * gibt die reale Hausummer der aktuellen Eben zurück
 * @param offset (0 wenn aktuelle Ebene, 1 wenn nächste nächste Ebene)
 * @return
 */
	public String get_HouseNumber(int offset){
		if(((current_floor+offset) > 0) && ((current_floor+offset) <=7)) return "5";
		else
			if(((current_floor+offset) > 7) && ((current_floor+offset) <=13)) return "3;2;1";
			else return "Campus";
	}
	
	/**
	 * gibt die reale Ebenenbezeichnung der aktuellen Eben zurück
	 * @param offset (0 wenn aktuelle Ebene, 1 wenn nächste nächste Ebene)
	 * @return
	 */
	public String get_floorNumber(int offset){
		switch((current_floor+offset)){
		case 1: return "-2";
		case 2: return "-1";
		case 3: return "0";
		case 4: return "1";
		case 5: return "2";
		case 6: return "3";
		case 7: return "3Z";
		case 8: return "-1";
		case 9: return "0";
		case 10: return "1";
		case 11: return "2";
		case 12: return "3";
		case 13: return "4";
		default: return "Campus";
		}
	}
	
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
				draw_route_or_position(canvas, var_way.get(var_way_contains_floor(current_floor))); // zeichen Route für folgenden 
			}
//			draw_route(ArrayList<Node>);
			break;
		case 1: // Wenn in Positionsanzeige(keine Routenanzeige)
			if(current_floor == position.getFloorID()){
				ArrayList<Node> list = new ArrayList<Node>();
				list.add(position);
				draw_route_or_position(canvas, list);
			}
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
	 * Zeichnet die Route/Position auf das Canvas
	 * @param c : canvas auf das gezeichnet wird
	 * @param list : Abzugehende Knoten auf einer Ebene (Bei Position nur ein Knoten)
	 */
	private void draw_route_or_position(Canvas c, ArrayList<Node> list){
		var_paint.setColor(Color.BLUE); // Farbe setzen
		c.drawLine(list.get(0).getX()+20, list.get(0).getY()+20, list.get(0).getX()-20, list.get(0).getY()-20, var_paint); // Startkreuz setzen(strich1)
		c.drawLine(list.get(0).getX()+20, list.get(0).getY()-20, list.get(0).getX()-20, list.get(0).getY()+20, var_paint); // Startkreuz Strich2
		
		for (int i = 0; i < list.size() - 1; i++) {
			c.drawLine(list.get(i).getX(),list.get(i).getY(),				// Startpunkt der Linie
					list.get(i + 1).getX(), list.get(i + 1).getY(), var_paint); // Zielpunkt der Linie
		}
		
		c.drawLine(list.get(list.size()-1).getX()+20, list.get(list.size()-1).getY()+20, list.get(list.size()-1).getX()-20, list.get(list.size()-1).getY()-20, var_paint); // Zielkreuz (strich1)
		c.drawLine(list.get(list.size()-1).getX()+20, list.get(list.size()-1).getY()-20, list.get(list.size()-1).getX()-20, list.get(list.size()-1).getY()+20, var_paint); // Zielkreuz (strich2)
		//TODO: eventuell noch Farben anpassen (Startkreuz z.b. andere Farbe als Zielkreuz usw)
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
	
	private void draw_floor_01(Canvas canvas){ 		// Zeichnet 5.-02
		canvas.drawText("Haus:5 Ebene: -2", 300, 200, new Paint());
		//Farbe auf Schwarz setzen
				var_paint.setColor(Color.BLACK);
				//Style auf Umrandung setzen
				var_paint.setStyle(Paint.Style.STROKE);
				//Element_01		
				canvas.drawRect(382,54,404,94,var_paint);
				//Element_02
				canvas.drawRect(382,24,424,54,var_paint);
				//Element_03
				canvas.drawRect(18,94,462,152,var_paint);
				//Element_04		
				canvas.drawRect(200,46,222,94,var_paint);
				//Element_05
				canvas.drawRect(404,54,462,94,var_paint);
				//Element_06
				canvas.drawRect(392,94,370,100,var_paint);
				//Element_07
				canvas.drawRect(392,100,370,106,var_paint);
				//Element_08
				canvas.drawRect(392,106,370,116,var_paint);
				//Element_09
				canvas.drawRect(370,116,358,94,var_paint);
				//Element_10
				canvas.drawRect(358,116,347,94,var_paint);
				//Element_11
				canvas.drawRect(347,116,336,94,var_paint);
				//Element_12
				canvas.drawRect(336,116,325,94,var_paint);
				//Element_13
				canvas.drawRect(325,116,314,94,var_paint);
				//Element_14
				canvas.drawRect(314,116,294,94,var_paint);
				//Element_15
				canvas.drawRect(294,116,250,94,var_paint);
				//Element_16
				canvas.drawRect(250,152,236,94,var_paint);
				//Element_17
				canvas.drawRect(420,124,250,152,var_paint);
				//Element_18
				canvas.drawRect(462,138,420,152,var_paint);
				//Element_19
				canvas.drawRect(462,138,420,106,var_paint);
				//Element_20
				canvas.drawRect(462,106,420,94,var_paint);
				//Element_21
				canvas.drawRect(420,94,404,64,var_paint);
				//Element_22
				canvas.drawLine(420,64,430,64,var_paint); 
				//Element_23
				canvas.drawLine(430,64,430,54,var_paint); 
				var_paint.setColor(Color.WHITE);
				//Element_24		
				canvas.drawLine(404,64,404,55,var_paint);
				var_paint.setColor(Color.BLACK);
				//Element_25
				canvas.drawRect(420,94,404,64,var_paint);
				//Element_26
				canvas.drawRect(382,24,403,39,var_paint);
				//Element_27
				canvas.drawRect(403,24,424,39,var_paint);
				//Element_28
				canvas.drawRect(398,39,382,54,var_paint);
				//Element_29
				canvas.drawRect(408,39,424,54,var_paint);
		
	}
	
	private void draw_floor_02(Canvas canvas){ 		// Zeichnet 5.-01
		canvas.drawText("Haus:5 Ebene: -1", 300, 200, new Paint());
		//Farbe auf Schwarz setzen
				var_paint.setColor(Color.BLACK);
				//Style auf Umrandung setzen
				var_paint.setStyle(Paint.Style.STROKE);		
				//Element_01
				canvas.drawRect(0,0,48,48,var_paint);
				//Element_02
				canvas.drawRect(18,48,40,94,var_paint);
				//Element_03
				canvas.drawRect(18,94,462,152,var_paint);
				//Element_04		
				canvas.drawRect(200,46,222,94,var_paint);
				//Element_05		
				canvas.drawRect(382,54,404,94,var_paint);
				//Element_06
				canvas.drawRect(382,24,424,54,var_paint);
				//Element_07
				canvas.drawRect(-6,74,18,152,var_paint);
				//Element_08
				canvas.drawRect(404,54,462,94,var_paint);
				//Element_09
				canvas.drawLine(436,152,436,68,var_paint);
				//Element_10
				canvas.drawRect(392,94,370,100,var_paint);
				//Element_11
				canvas.drawRect(392,100,370,106,var_paint);
				//Element_12
				canvas.drawRect(392,106,370,116,var_paint);
				//Element_13
				canvas.drawRect(370,116,358,94,var_paint);
				//Element_14
				canvas.drawRect(358,116,347,94,var_paint);
				//Element_15
				canvas.drawRect(347,116,336,94,var_paint);
				//Element_16
				canvas.drawRect(336,116,313,94,var_paint);
				//Element_17
				canvas.drawLine(336,152,336,126,var_paint);
				//Element_18
				canvas.drawRect(313,116,290,94,var_paint);
				//Element_19
				canvas.drawRect(290,116,246,94,var_paint);
				//Element_20
				canvas.drawRect(222,116,234,94,var_paint);
				//Element_21
				canvas.drawLine(290,152,290,126,var_paint);
				//Element_22
				canvas.drawRect(222,126,246,152,var_paint);
				//Element_23
				canvas.drawRect(178,134,222,152,var_paint);
				//Element_24
				canvas.drawRect(123,134,178,152,var_paint);
				//Element_25
				canvas.drawRect(123,134,111,152,var_paint);
				//Element_26
				canvas.drawRect(111,134,98,152,var_paint);
				//Element_27
				canvas.drawRect(98,134,54,152,var_paint);
				//Element_28
				canvas.drawLine(54, 134, 18, 134,var_paint);
				//Element_29
				var_paint.setColor(Color.WHITE);
				canvas.drawLine(18, 134, 18, 152,var_paint);
				var_paint.setColor(Color.BLACK);
				//Element_30		
				canvas.drawRect(200,94,189,110,var_paint);	
				//Element_31		
				canvas.drawRect(200,110,189,126,var_paint);	
				//Element_32		
				canvas.drawRect(189,94,156,126,var_paint);	
				//Element_33		
				canvas.drawRect(156,94,138,126,var_paint);	
				//Element_34		
				canvas.drawRect(134,110,123,126,var_paint);	
				//Element_35		
				canvas.drawRect(123,94,111,126,var_paint);	
				//Element_36		
				canvas.drawRect(111,94,68,126,var_paint);	
				//Element_37		
				canvas.drawRect(68,110,28,126,var_paint);	
				//Element_38		
				canvas.drawRect(52,94,28,110,var_paint);	
				var_paint.setColor(Color.WHITE);
				//Element_39		
				canvas.drawLine(68, 110, 52, 110,var_paint);
				var_paint.setColor(Color.BLACK);
				//Element_40		
				canvas.drawRect(-6,94,18,126,var_paint);
				//Element_41		
				canvas.drawLine(222, 126, 222, 116,var_paint);
				//Element_42		
				canvas.drawRect(20,0,0,16,var_paint);
				//Element_43		
				canvas.drawRect(20,16,0,32,var_paint);
				//Element_44		
				canvas.drawRect(20,32,0,48,var_paint);
				//Element_45		
				canvas.drawRect(48,0,28,16,var_paint);
				//Element_46		
				canvas.drawRect(48,16,28,32,var_paint);
				//Element_47		
				canvas.drawRect(48,32,28,48,var_paint);
	}
	
	private void draw_floor_03(Canvas canvas){ 		// Zeichnet 5.00
		canvas.drawText("Haus:5 Ebene: 00", 300, 200, new Paint());
		//Farbe auf Schwarz setzen
				var_paint.setColor(Color.BLACK);
				//Style auf Umrandung setzen
				var_paint.setStyle(Paint.Style.STROKE);		
				//Element_01
				canvas.drawRect(0,0,48,48,var_paint);
				//Element_02
				canvas.drawRect(18,48,40,94,var_paint);
				//Element_03
				canvas.drawRect(18,94,462,152,var_paint);
				//Element_04		
				canvas.drawRect(200,46,222,94,var_paint);
				//Element_05		
				canvas.drawRect(382,54,404,94,var_paint);
				//Element_06
				canvas.drawRect(382,24,424,54,var_paint);
				//Element_07
				canvas.drawRect(-6,74,18,152,var_paint);
				//Element_08
				canvas.drawRect(-6,74,18,112,var_paint);
				//Element_09
				canvas.drawRect(-6,112,18,128,var_paint);
				//Element_10
				canvas.drawRect(39,152,177,128,var_paint);
				//Element_11
				canvas.drawRect(62,152,85,128,var_paint);
				//Element_12
				canvas.drawRect(108,152,131,128,var_paint);
				//Element_13
				canvas.drawRect(154,152,177,128,var_paint);
				//Element_14
				canvas.drawRect(177,94,50,114,var_paint);
				//Element_15
				canvas.drawRect(165,94,154,114,var_paint);
				//Element_16
				canvas.drawRect(119,94,131,114,var_paint);
				//Element_17
				canvas.drawRect(108,94,96,114,var_paint);
				//Element_18
				canvas.drawRect(85,94,73,114,var_paint);
				//Element_19
				canvas.drawRect(50,94,30,101,var_paint);
				//Element_20
				canvas.drawRect(50,101,30,108,var_paint);
				//Element_21
				canvas.drawRect(50,108,40,114,var_paint);		
				//Element_22
				canvas.drawRect(20,0,0,48,var_paint);		
				//Element_23
				canvas.drawRect(48,0,20,26,var_paint);		
				//Element_24
				canvas.drawRect(48,48,30,26,var_paint);		
				//Element_25
				canvas.drawRect(200,114,177,94,var_paint);		
				//Element_26
				canvas.drawRect(222,114,255,94,var_paint);		
				//Element_27
				canvas.drawLine(255,94,255,152, var_paint);		
				//Element_28
				canvas.drawRect(222,114,255,94,var_paint);		
				//Element_29
				canvas.drawRect(382,54,394,24,var_paint);		
				//Element_30
				canvas.drawRect(404,40,424,54,var_paint);		
				//Element_31
				canvas.drawRect(394,40,424,24,var_paint);	
				var_paint.setColor(Color.RED);		
				//Element_32
				canvas.drawRect(30,84,40,94,var_paint);
				//Element_33
				canvas.drawRect(30,72,40,84,var_paint);
				//Element_34
				canvas.drawRect(382,80,392,94,var_paint);
				//Element_35
				canvas.drawRect(190,72,200,82,var_paint);
				//Element_36	
				canvas.drawRect(222,72,232,82,var_paint);
	}
	
	private void draw_floor_04(Canvas canvas){ 		// Zeichnet 5.01
		canvas.drawText("Haus:5 Ebene: 01", 300, 200, new Paint());
		//Farbe auf Schwarz setzen
				var_paint.setColor(Color.BLACK);
				//Style auf Umrandung setzen
				var_paint.setStyle(Paint.Style.STROKE);		
				//Element_01
				canvas.drawRect(0,0,48,48,var_paint);
				//Element_02
				canvas.drawRect(18,48,40,94,var_paint);
				//Element_03
				canvas.drawRect(-6,94,462,152,var_paint);
				//Element_04		
				canvas.drawRect(200,46,222,94,var_paint);
				//Element_05		
				canvas.drawRect(382,54,404,94,var_paint);
				//Element_06
				canvas.drawRect(382,24,424,54,var_paint);
				//Element_07
				canvas.drawRect(0,0,20,30,var_paint);
				//Element_08
				canvas.drawRect(20,0,48,30,var_paint);
				//Element_09
				canvas.drawRect(20,48,0,30,var_paint);
				//Element_10
				canvas.drawRect(48,48,30,30,var_paint);
				//Element_11
				canvas.drawRect(-6,94,18,114,var_paint);
				//Element_12
				canvas.drawRect(-6,124,7,152,var_paint);
				//Element_13
				canvas.drawRect(7,124,18,152,var_paint);
				//Element_14
				canvas.drawRect(18,124,40,152,var_paint);
				//Element_15
				canvas.drawRect(40,124,51,152,var_paint);
				//Element_16
				canvas.drawRect(51,124,62,152,var_paint);
				//Element_17
				canvas.drawRect(62,124,73,152,var_paint);
				//Element_18
				canvas.drawRect(73,124,84,152,var_paint);
				//Element_19
				canvas.drawRect(84,124,95,152,var_paint);
				//Element_20
				canvas.drawRect(95,124,106,152,var_paint);
				//Element_21
				canvas.drawRect(106,124,117,152,var_paint);
				//Element_22
				canvas.drawRect(117,124,128,152,var_paint);
				//Element_23
				canvas.drawRect(128,124,139,152,var_paint);
				//Element_24
				canvas.drawRect(139,124,150,152,var_paint);
				//Element_25
				canvas.drawRect(150,124,161,152,var_paint);
				//Element_26
				canvas.drawRect(161,124,172,152,var_paint);
				//Element_27
				canvas.drawRect(172,134,200,152,var_paint);
				//Element_28
				canvas.drawRect(222,140,250,152,var_paint);
				//Element_29
				canvas.drawRect(250,140,261,152,var_paint);
				//Element_30
				canvas.drawRect(261,140,272,152,var_paint);
				//Element_31
				canvas.drawRect(272,140,283,152,var_paint);
				//Element_32
				canvas.drawRect(283,140,294,152,var_paint);
				//Element_33
				canvas.drawRect(294,140,305,152,var_paint);
				//Element_34
				canvas.drawRect(305,140,316,152,var_paint);
				//Element_35
				canvas.drawRect(316,140,327,152,var_paint);
				//Element_36
				canvas.drawRect(327,140,338,152,var_paint);
				//Element_37
				canvas.drawRect(338,140,349,152,var_paint);
				//Element_38
				canvas.drawRect(349,140,360,152,var_paint);
				//Element_39
				canvas.drawRect(360,140,371,152,var_paint);
				//Element_40
				canvas.drawRect(371,140,382,152,var_paint);
				//Element_41
				canvas.drawRect(382,140,393,152,var_paint);
				//Element_42
				canvas.drawRect(393,140,404,152,var_paint);
				//Element_43
				canvas.drawRect(404,140,412,152,var_paint);
				//Element_44
				canvas.drawRect(412,140,420,152,var_paint);		
				//Element_45
				canvas.drawRect(420,140,431,152,var_paint);
				//Element_46
				canvas.drawRect(431,140,442,152,var_paint);
				//Element_47
				canvas.drawRect(442,123,462,152,var_paint);
				//Element_48
				canvas.drawRect(442,123,462,113,var_paint);	
				//Element_49
				canvas.drawRect(442,113,462,94,var_paint);		
				//Element_50
				canvas.drawRect(404,106,412,94,var_paint);
				//Element_51
				canvas.drawRect(412,106,420,94,var_paint);		
				//Element_52
				canvas.drawRect(420,106,431,94,var_paint);
				//Element_53
				canvas.drawRect(431,106,442,94,var_paint);
				//Element_54
				canvas.drawRect(404,114,420,132,var_paint);
				//Element_55
				canvas.drawRect(420,114,430,132,var_paint);
				//Element_56
				canvas.drawRect(392,94,372,100,var_paint);
				//Element_57
				canvas.drawRect(392,100,372,106,var_paint);
				//Element_58
				canvas.drawRect(392,106,372,122,var_paint);
				//Element_59
				canvas.drawRect(372,122,360,94,var_paint);
				//Element_60
				canvas.drawRect(360,122,338,94,var_paint);
				//Element_61
				canvas.drawRect(338,122,316,94,var_paint);
				//Element_62
				canvas.drawRect(316,122,283,94,var_paint);
				//Element_63
				canvas.drawRect(283,122,250,94,var_paint);
				//Element_64
				canvas.drawRect(250,110,222,94,var_paint);
				//Element_65
				canvas.drawRect(236,110,222,122,var_paint);
				//Element_66
				canvas.drawRect(236,110,250,122,var_paint);
				//Element_67
				canvas.drawRect(382,24,406,36,var_paint);
				//Element_68
				canvas.drawRect(382,54,394,36,var_paint);
				//Element_69
				canvas.drawLine(404,36,404,54,var_paint);
				//Element_70
				canvas.drawRect(200,94,172,102,var_paint);
				//Element_71
				canvas.drawRect(200,114,172,102,var_paint);
				//Element_72
				canvas.drawRect(172,114,150,94,var_paint);
				//Element_73
				canvas.drawRect(150,114,128,94,var_paint);
				//Element_74
				canvas.drawRect(128,114,106,94,var_paint);
				//Element_75
				canvas.drawRect(106,114,84,94,var_paint);
				//Element_76
				canvas.drawRect(84,114,62,94,var_paint);
				//Element_77
				canvas.drawRect(51,114,62,94,var_paint);
				//Element_78
				canvas.drawRect(51,94,30,101,var_paint);
				//Element_79
				canvas.drawRect(51,101,30,108,var_paint);
				//Element_80
				canvas.drawRect(51,108,40,114,var_paint);
				var_paint.setColor(Color.RED);		
				//Element_81
				canvas.drawRect(30,84,40,94,var_paint);
				//Element_82
				canvas.drawRect(30,72,40,84,var_paint);
				//Element_83
				canvas.drawRect(382,80,392,94,var_paint);
				//Element_84
				canvas.drawRect(190,72,200,82,var_paint);
				//Element_85	
				canvas.drawRect(222,72,232,82,var_paint);
	}
	
	private void draw_floor_05(Canvas canvas){ 		// Zeichnet 5.02
		canvas.drawText("Haus:5 Ebene: 02", 300, 200, new Paint());
		//Farbe auf Schwarz setzen
				var_paint.setColor(Color.BLACK);
				//Style auf Umrandung setzen
				var_paint.setStyle(Paint.Style.STROKE);		
				//Element_01
				canvas.drawRect(0,0,48,48,var_paint);
				//Element_02
				canvas.drawRect(18,48,40,94,var_paint);
				//Element_03
				canvas.drawRect(-6,94,462,152,var_paint);
				//Element_04		
				canvas.drawRect(200,46,222,94,var_paint);
				//Element_05		
				canvas.drawRect(382,54,404,94,var_paint);
				//Element_06
				canvas.drawRect(382,24,424,54,var_paint);
				//Element_07
				canvas.drawRect(0,0,18,48,var_paint);
				//Element_08				
				canvas.drawRect(40,48,28,56,var_paint);
				//Element_09				
				canvas.drawRect(-6,94,18,126,var_paint);
				//Element_10				
				canvas.drawRect(-6,126,18,152,var_paint);
				//Element_11		
				canvas.drawRect(54,136,18,152,var_paint);
				//Element_12		
				canvas.drawRect(66,136,54,152,var_paint);
				//Element_13		
				canvas.drawRect(77,136,66,152,var_paint);
				//Element_14		
				canvas.drawRect(88,136,77,152,var_paint);
				//Element_15		
				canvas.drawRect(111,136,88,152,var_paint);
				//Element_16		
				canvas.drawRect(134,136,111,152,var_paint);
				//Element_17		
				canvas.drawRect(145,136,134,152,var_paint);
				//Element_18		
				canvas.drawRect(156,136,145,152,var_paint);
				//Element_19		
				canvas.drawRect(167,136,156,152,var_paint);
				//Element_20
				canvas.drawRect(178,136,167,152,var_paint);
				//Element_21		
				canvas.drawRect(189,136,178,152,var_paint);
				//Element_22		
				canvas.drawRect(200,136,189,152,var_paint);
				//Element_23		
				canvas.drawRect(222,136,245,152,var_paint);
				//Element_24		
				canvas.drawRect(245,136,256,152,var_paint);
				//Element_25		
				canvas.drawRect(256,136,267,152,var_paint);
				//Element_26		
				canvas.drawRect(267,136,290,152,var_paint);
				//Element_27
				canvas.drawRect(290,136,301,152,var_paint);
				//Element_28
				canvas.drawRect(301,136,312,152,var_paint);
				//Element_29
				canvas.drawRect(312,136,335,152,var_paint);
				//Element_30
				canvas.drawRect(335,136,358,152,var_paint);
				//Element_31
				canvas.drawRect(358,136,381,152,var_paint);
				//Element_32
				canvas.drawRect(381,136,404,152,var_paint);
				//Element_33
				canvas.drawRect(404,136,427,152,var_paint);
				//Element_34
				canvas.drawRect(427,126,462,152,var_paint);
				//Element_35
				canvas.drawRect(427,94,462,152,var_paint);
				//Element_36
				canvas.drawRect(404,94,427,118,var_paint);
				//Element_37
				canvas.drawRect(392,94,372,100,var_paint);
				//Element_38
				canvas.drawRect(392,100,372,106,var_paint);
				//Element_39
				canvas.drawRect(392,106,372,122,var_paint);
				//Element_40
				canvas.drawRect(372,94,335,122,var_paint);
				//Element_41
				canvas.drawRect(335,94,301,122,var_paint);
				//Element_42
				canvas.drawRect(301,94,267,122,var_paint);
				//Element_43
				canvas.drawRect(267,94,245,122,var_paint);
				//Element_44
				canvas.drawRect(245,94,222,122,var_paint);
				//Element_45
				canvas.drawRect(200,94,189,108,var_paint);
				//Element_46
				canvas.drawRect(200,108,189,122,var_paint);
				//Element_47
				canvas.drawRect(189,94,156,122,var_paint);
				//Element_48
				canvas.drawRect(156,94,134,122,var_paint);
				//Element_49
				canvas.drawRect(134,94,111,122,var_paint);
				//Element_50
				canvas.drawRect(111,94,54,122,var_paint);
				//Element_51
				canvas.drawRect(54,94,40,100,var_paint);
				//Element_52
				canvas.drawRect(54,100,40,106,var_paint);
				//Element_53
				canvas.drawRect(54,106,40,122,var_paint);
				//Treppen und Aufzuege werden gezeichnet
				//Farbe auf Rot setzen
				var_paint.setColor(Color.RED);		
				//Element_54
				canvas.drawRect(30,84,40,94,var_paint);
				//Element_55
				canvas.drawRect(30,72,40,84,var_paint);
				//Element_56
				canvas.drawRect(382,80,392,94,var_paint);
				//Element_57
				canvas.drawRect(190,72,200,82,var_paint);
				//Element_58
				canvas.drawRect(222,72,232,82,var_paint);	
	}
	
	private void draw_floor_06(Canvas canvas){ 		// Zeichnet 5.03
		canvas.drawText("Haus:5 Ebene: 03", 300, 200, new Paint());
		//Farbe auf Schwarz setzen
				var_paint.setColor(Color.BLACK);
				//Style auf Umrandung setzen
				var_paint.setStyle(Paint.Style.STROKE);		
				//Element_01
				canvas.drawRect(0,0,48,48,var_paint);
				//Element_02
				canvas.drawRect(18,48,40,94,var_paint);
				//Element_03
				canvas.drawRect(-6,94,462,152,var_paint);
				//Element_04		
				canvas.drawRect(200,46,222,94,var_paint);
				//Element_05		
				canvas.drawRect(382,54,404,94,var_paint);
				//Element_06
				canvas.drawRect(382,24,424,54,var_paint);
				//Element_06
				canvas.drawRect(382,24,424,54,var_paint);
				//Element_07
				canvas.drawRect(0,40,18,48,var_paint);
				//Element_08
				canvas.drawRect(24,40,48,48,var_paint);
				//Element_09
				canvas.drawRect(-6,124,18,152,var_paint);
				//Element_10
				canvas.drawRect(18,124,40,152,var_paint);
				//Element_11
				canvas.drawRect(40,124,62,152,var_paint);
				//Element_12
				canvas.drawRect(62,124,84,152,var_paint);
				//Element_13
				canvas.drawRect(84,124,106,152,var_paint);
				//Element_14
				canvas.drawRect(106,124,132,152,var_paint);
				//Element_15
				canvas.drawRect(132,116,176,152,var_paint);		
				//Element_16
				canvas.drawRect(176,124,200,152,var_paint);
				//Element_17
				canvas.drawRect(222,114,268,152,var_paint);
				//Element_18
				canvas.drawRect(268,124,290,152,var_paint);
				//Element_19
				canvas.drawRect(290,114,336,152,var_paint);
				//Element_20
				canvas.drawRect(336,124,358,152,var_paint);
				//Element_21
				canvas.drawRect(358,114,404,152,var_paint);
				//Element_22
				canvas.drawRect(404,124,436,152,var_paint);
				//Element_23
				canvas.drawRect(446,134,436,152,var_paint);
				//Element_24
				canvas.drawRect(446,122,462,152,var_paint);
				//Element_25
				canvas.drawRect(446,94,462,122,var_paint);
				//Element_26
				canvas.drawRect(446,94,436,112,var_paint);	
				//Element_27
				canvas.drawRect(390,94,374,108,var_paint);
				//Element_28
				canvas.drawLine(336,94,336,108,var_paint);
				//Element_29
				canvas.drawLine(268,94,268,108,var_paint);
				//Element_30
				canvas.drawRect(176,94,200,108,var_paint);
				//Element_31
				canvas.drawRect(106,94,54,114,var_paint);
				//Element_32
				canvas.drawRect(54,94,30,101,var_paint);
				//Element_33
				canvas.drawRect(54,101,30,108,var_paint);
				//Element_34
				canvas.drawRect(54,108,40,114,var_paint);		
				//Element_35
				canvas.drawRect(396,54,404,46,var_paint);		
				//Element_36
				canvas.drawLine(400, 46, 400, 24, var_paint);		
				var_paint.setColor(Color.RED);		
				//Element_37
				canvas.drawRect(-6,108,10,116,var_paint);
				//Element_38
				canvas.drawRect(30,84,40,94,var_paint);
				//Element_39
				canvas.drawRect(30,72,40,84,var_paint);
				//Element_40
				canvas.drawRect(382,80,392,94,var_paint);
				//Element_41
				canvas.drawRect(190,72,200,82,var_paint);
				//Element_42	
				canvas.drawRect(222,72,232,82,var_paint);		
				//Element_43
				canvas.drawRect(176,116,190,124,var_paint);
				//Element_44
				canvas.drawRect(404,114,414,124,var_paint);
				//Element_45
				canvas.drawRect(268,114,278,124,var_paint);
				//Element_46
				canvas.drawRect(336,114,346,124,var_paint);
	}
	
	private void draw_floor_07(Canvas canvas){ 		// Zeichnet 5.3Z
		canvas.drawText("Haus:5 Ebene: 3Z", 300, 200, new Paint());
		//Farbe auf Schwarz setzen
				var_paint.setColor(Color.BLACK);
				//Style auf Umrandung setzen
				var_paint.setStyle(Paint.Style.STROKE);		
				//Element_01
				canvas.drawRect(0,0,48,48,var_paint);
				//Element_02
				canvas.drawRect(18,48,40,94,var_paint);
				//Element_03
				canvas.drawRect(-6,94,462,152,var_paint);
				//Element_04		
				canvas.drawRect(200,46,222,94,var_paint);
				//Element_05		
				canvas.drawRect(382,54,404,94,var_paint);
				//Element_06
				canvas.drawRect(382,24,424,54,var_paint);
				//Element_07
				canvas.drawRect(-6,124,17,152,var_paint);
				//Element_08
				canvas.drawRect(17,124,40,152,var_paint);
				//Element_09
				canvas.drawRect(40,124,63,152,var_paint);
				//Element_10
				canvas.drawRect(63,124,86,152,var_paint);
				//Element_11
				canvas.drawRect(86,124,109,152,var_paint);
				//Element_12
				canvas.drawRect(109,124,132,152,var_paint);
				//Element_13
				canvas.drawRect(132,116,176,152,var_paint);		
				//Element_14
				canvas.drawRect(176,124,199,152,var_paint);
				//Element_15
				canvas.drawRect(176,94,199,108,var_paint);
				//Element_16
				canvas.drawLine(116, 104, 176, 104,var_paint);
				//Element_17
				canvas.drawRect(94,94,116,116,var_paint);
				//Element_18
				canvas.drawRect(72,94,94,116,var_paint);
				//Element_19
				canvas.drawRect(56,108,72,116,var_paint);
				//Element_20
				canvas.drawRect(35,94,72,108,var_paint);
				//Element_21
				canvas.drawRect(18,48,40,60,var_paint);
				//Element_22
				canvas.drawRect(446,94,462,122,var_paint);
				//Element_23
				canvas.drawRect(446,94,436,112,var_paint);
				//Element_24 
				canvas.drawRect(222,114,268,152,var_paint);
		    		//Element_25
				canvas.drawRect(268,124,290,152,var_paint);
				//Element_26
				canvas.drawRect(290,114,336,152,var_paint);
				//Element_27
				canvas.drawRect(336,124,358,152,var_paint);
				//Element_28
				canvas.drawRect(358,114,404,152,var_paint);
				//Element_29
				canvas.drawRect(404,124,436,152,var_paint);
				//Element_30
				canvas.drawRect(446,122,462,152,var_paint);
				//Element_31
				canvas.drawRect(390,94,374,108,var_paint);
				//Element_32
				canvas.drawLine(268,94,268,108,var_paint);
				//Element_33
				canvas.drawLine(346,94,346,108,var_paint);
				//Element_34		
				canvas.drawLine(222,104,268,104,var_paint);
				//Element_35
				canvas.drawLine(222,94,222,108,var_paint);
				//Element_36
				canvas.drawLine(268,104,374,104,var_paint);
				//Element_37
				canvas.drawLine(404,104,436,104,var_paint);
				//Element_38
				canvas.drawLine(404,94,404,104,var_paint);		
				//Element_39
				canvas.drawLine(2,94,2,108,var_paint);
				//Element_40		
				canvas.drawLine(2,116,2,124,var_paint);
				//Element_41
				canvas.drawRect(446,134,436,152,var_paint);
				//Element_42
				canvas.drawLine(268,104,346,104,var_paint);
				//Treppen und Aufzuege werden gezeichnet
				//Farbe auf Rot setzen
				var_paint.setColor(Color.RED);		
				//Element_43
				canvas.drawRect(-6,108,10,116,var_paint);
				//Element_44
				canvas.drawRect(30,84,40,94,var_paint);
				//Element_45
				canvas.drawRect(30,72,40,84,var_paint);
				//Element_46
				canvas.drawRect(382,80,392,94,var_paint);
				//Element_47
				canvas.drawRect(190,72,200,82,var_paint);
				//Element_48	
				canvas.drawRect(222,72,232,82,var_paint);		
				//Element_49
				canvas.drawRect(176,116,190,124,var_paint);
				//Element_50
				canvas.drawRect(404,114,414,124,var_paint);
				//Element_51
				canvas.drawRect(268,114,278,124,var_paint);
				//Element_52
				canvas.drawRect(336,114,346,124,var_paint);
	}
	
	private void draw_floor_08(Canvas c){ 		// Zeichnet 1.-01
		c.drawText("Haus:1,2,3 Ebene: -1", 300, 200, new Paint());
	}
	
	private void draw_floor_09(Canvas c){ 		// Zeichnet 1.00
		c.drawText("Haus:1,2,3 Ebene: 00", 300, 200, new Paint());
	}
	
	private void draw_floor_10(Canvas c){ 		// Zeichnet 1.01
		c.drawText("Haus:1,2,3 Ebene: 01", 300, 200, new Paint());
	}
	
	private void draw_floor_11(Canvas c){ 		// Zeichnet 1.02
		c.drawText("Haus:1,2,3 Ebene: 02", 300, 200, new Paint());
	}
	
	private void draw_floor_12(Canvas c){ 		// Zeichnet 1.03
		c.drawText("Haus:1,2,3 Ebene: 03", 300, 200, new Paint());
	}
	
	private void draw_floor_13(Canvas c){ 		// Zeichnet 1.04
		c.drawText("Haus:1,2,3 Ebene: 04", 300, 200, new Paint());
	}
}
