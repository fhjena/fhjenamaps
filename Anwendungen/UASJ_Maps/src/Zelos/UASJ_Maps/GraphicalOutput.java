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

package Zelos.UASJ_Maps;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.View;

public class GraphicalOutput extends View {
	private Paint var_paint;
	private ArrayList<ArrayList<Node>> route;		
	private Node position;
	private float degree = 0;
	private int x_ref = 150;
	private int y_ref = -160;
	private short check_counter=0; // Zählvariable wie oft der Checkbutton gedrückt wurde
	private short current_floor=-1; // Speichert ID der aktuellen Ebene
	private int state=-1; // 0: Routenberechnung, 1: Positionsanzeige, 2: Campusanzeige
	private float zoom = 1.f;	//Zoom bzw. Skalierungsfaktor Canvas
	private float x_zoom = 0.f;	//Zoompunkt x
	private float y_zoom = 0.f;	//Zoompunkt y
	private float mPosX = 250.f;	//Canvasposition x (für Singletouch)     
	private float mPosY = -130.f;	//Canvasposition y (für Singletouch)
	private float MidX = 150.f;     		//Mittelpunkt x (für rotate)
	private float MidY = 130.f;      		//Mittelpunkt y (für rotate)
	private float factor = 0.347222f;
	
	//TODO: x_ref & y_ref entferen, dafür Canvas.Translate(x,y) nutzen
	// Konstruktor
	public GraphicalOutput(Context c_txt) {
		super(c_txt);
		var_paint = new Paint();
//		var_way = route;
	}

	/** Wird zur Zeit der Campus angezeigt?
	 * @return true, wenn Campus angezeigt wird; ansonsten false
	 */
	public boolean isStateCampus(){
		if(0 == current_floor)
			return true;
		else return false;
	}
	
	/** Berechnet, ob unter angegebenen Koordinaten ein Haus liegt. Wenn ein Haus getroffen wurde, wird entsprechend set_floor ausgeführt.
	 * @param X X-Koordinate vom Klick
	 * @param Y Y-Koordinate vom Klick
	 * @return true, wenn ein Haus getroffen wurde; ansonsten false
	 */
	public boolean performClickOnCampus(int X, int Y) { // TODO David: überarbeiten
		// Umrechnung der View-Koordinaten in Canvas-Koordinaten
		Point inputCoords = new Point((int) (X + mPosX), (int) (Y + mPosY)); // Verschub zu Koordinaten addieren
		convertCartesianToPolar(inputCoords); // Kovertierung in Polarkoordinaten
		inputCoords.set(inputCoords.x, (int) (inputCoords.y + degree)); // Drehwinkel hinzuaddieren
		convertPolarToCartesian(inputCoords); // Zurückkonvertierung in Kartesische Koordinaten
		
		if (inputCoords.x>=-6 && inputCoords.x<=460 && inputCoords.y>=0 && inputCoords.y<=155)
			set_floor(6); // Haus 5 getroffen
		else if (inputCoords.x>=210 && inputCoords.x<=650 && inputCoords.y>=255 && inputCoords.y<=450)
			set_floor(4); // Haus 1/2/3 getroffen
		else if (inputCoords.x>=330 && inputCoords.x<=775 && inputCoords.y>=550 && inputCoords.y<=715)
			set_floor(5); // Haus 4 getroffen
		else return false; // kein Haus getroffen
		return true; // irgendein Haus getroffen
	}
	
	/** Konvertierung von Kartesischen Koordinaten in Polarkoordinaten 
	 * @param P .x wird von x-Wert in Betrag der Polarkoordinaten gewandelt
	 * @param P .y wird von y-Wert in Winkel der Polarkoordinaten gewandelt
	 */
	private void convertCartesianToPolar(Point P) {
		P.set((int) Math.sqrt((P.x * P.x) + (P.y * P.y)), (int) Math.atan(P.y/P.x));
	}
	
	/** Kovertierung von Polarkoordinaten in Kartesischen Koordinaten
	 * @param P .x wird von Betrag in x-Wert der Kartesischen Koordinaten gewandelt
	 * @param P .y wird von Winkel in y-Wert der Kartesischen Koordinaten gewandelt
	 */
	private void convertPolarToCartesian(Point P) {
		P.set((int) (P.x * Math.cos(P.y)), (int) (P.x * Math.sin(P.y)));
	}

	// Setzt den Wert der Gradzahl vom Sensor
	public void set_degree(float f) {
		degree = f;
	}
	
	// Setzt den Wert für Zoom
	public void set_zoom(float z, float x, float y) {
		zoom = z;
		x_zoom = x;
		y_zoom = y;
	}
	
	// Setzt Wert für Verschiebung
	public void set_position(float x, float y) {
		mPosX = x;
		mPosY = y;
	}
	// Setzt Wert für Mittelpunkt Rotation
		public void set_midpoint(float x, float y) {
			MidX = x;
			MidY = y;
				}
	/**
	 * Gibt die Pfadbeschreibung zurück. Muss nach jedem Ebenenwechsel neu abgefragt werden.
	 * @return
	 */
	public String get_RouteDescription(){
		if(state==0){					// gibt nur gültigen String aus, wenn in Status Routenberechnung
			String str;
			if(route.get(check_counter).get(0).getFloorID() == current_floor){			// wenn die aktuelle Ebene des Pfades angezeigt wird
				if((route.size()-(check_counter+1)) == 0){// wenn auf letzter Ebene{
					return "Follow path to the final destination";
				}
				else{
					if(get_HouseNumber(false)=="Campus")			// Wenn man auf den Campus wechseln soll
						str = "At the end of the path: use the exit to campus";
					else 	
						if(get_HouseNumber(true)=="Campus" && get_HouseNumber(false)!= "Campus")		// Wenn man aus dem Campus in ein Haus wechseln soll
							str = "At the end of the path: use entrance to House " + get_HouseNumber(false) + "floor: " + get_floorNumber(false);
						else		// Wenn man Ebenen in einem Haus wechseln soll
							str = "At the end of the path: use the elevator/stairs to floor " + get_floorNumber(false);
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
 * @param current (true: gibt aktuelle Ebene zurück, false: gibt nächste abzuschreitende Ebene zurück)
 * @return
 */
	public String get_HouseNumber(boolean current){
		int floorID=0;
		if(current == true)
			floorID = current_floor;		// Ausgabe für freie Navigation
		else
			floorID = route.get(check_counter+1).get(0).getFloorID(); //Ausgabe für Textausgabe in Wegbeschreibung
		
		if(((floorID) > 0) && ((floorID) <=7)) return "05";
		else
			if(((floorID) > 7) && ((floorID) <=13)) return "01/02/03";
			else return "Campus";
	}
	
	/**
	 * gibt die reale Ebenenbezeichnung der aktuellen Eben zurück
	 * @param current (true: gibt aktuelle Ebene zurück, false: gibt nächste abzuschreitende Ebene zurück)
	 * @return
	 */
	public String get_floorNumber(boolean current){
		int floorID=0;
		if(current == true)
			floorID = current_floor;
		else
			floorID = route.get(check_counter+1).get(0).getFloorID();

		switch(floorID){
			case 1: return "-2";
			case 2: return "-1";
			case 3: return "00";
			case 4: return "01";
			case 5: return "02";
			case 6: return "03";
			case 7: return "3Z";
			case 8: return "-1";
			case 9: return "00";
			case 10: return "01";
			case 11: return "02";
			case 12: return "03";
			case 13: return "04";
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
				current_floor = (short) route.get(check_counter).get(0).getFloorID(); // Current_floor aktualisieren (auf nächste abzuschreitende Ebene)
			else if(state==1)
				current_floor = (short) position.getFloorID(); // Current_floor aktualisieren (auf nächste abzuschreitende Ebene)
			break;
		case 4: // aus Campus Haus 1, 2 oder 3 anklicken
			current_floor = 9; // haus 1,2,3 Ebene 0
			break;
		case 5: // aus Campus Haus 4 anklicken
			current_floor = 14; // haus 4 Ebene 0
			break;
		case 6: // aus Campus Haus 5 anklicken
			current_floor = 3; // haus 5 Ebene 0
			break;
		default:
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
		current_floor = (short) route.get(check_counter).get(0).getFloorID(); // Current_floor aktualisieren (auf nächste abzuschreitende Ebene)
		return (short) (route.size()-(check_counter+1));		// restliche Ebendenanzahl zurückgeben 
	}
	
	/**
	 * Setzt Status auf Routenanzeige
	 * @param list : 2D-Array mit abzuschreitenden Ebenen und Knoten
	 */
	public void set_state_routing(ArrayList<ArrayList<Node>> list){
		state = 0;						// Status auf Routenanzeige setzen
		check_counter = 0;				// Check_Counter resetten
		route = list;					// Route speicher/aktualisieren
		current_floor = (short) list.get(0).get(0).getFloorID();		// erste Ebene ermitteln
	}
	
	/**
	 * Setzt Status auf Positionsanzeige
	 * @param n : Knoten, der angezeigt werden soll
	 */
	public void set_state_location(Node n){
		state = 1;						// Status auf Positionsanzeige setzen
		position = n;
		current_floor = (short) position.getFloorID(); // EbenenID für Postion holen
	}
	
	/**
	 * Setzt Status auf Campusanzeige(freie Navigation)
	 */
	public void set_state_free_navigation(){
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
		canvas.rotate(degree, MidX, MidY);
		canvas.drawPaint(var_paint);
	}
	/**
	 * onDraw wird von der Klasse View überschrieben,zeichnet im Endeffekt aus das Canvas
	 */
	protected void onDraw(Canvas canvas) {
		
		canvas.save();	
		canvas.translate(mPosX, mPosY);   			//Verschiebung
		draw_rotate(canvas);						//Rotation
		canvas.scale( zoom, zoom, x_zoom, y_zoom);	//Skalierung
		draw_background(canvas);
		draw_floor(current_floor, canvas); // aktuelle Ebene zeichnen. Egal in welchem Zustand man ist.
		switch(state){ // jenachdem in welchem Zustand und auf welcher Ebene man sich befindet wird nun noch eine Route oder eine Position in die Ebene gezeichnet
		case 0:			// Wenn in Routenberechnung, dann soll Weg angezeigt werden, falls dieser in aktueller Ebene vorhanden
			if(var_way_contains_floor(current_floor)!=-1){ // Wenn auf aktueller Ebene Route vorhanden
				draw_route_or_position(canvas, route.get(var_way_contains_floor(current_floor))); // zeichen Route für folgenden 
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
		default: break;
		}
		canvas.restore();
	}
	
	/**
	 * Durchsucht die Wegstrecke, ob angegebene Ebene enthalten ist
	 * @param floorID : EbenenID nach der gesucht werden soll
	 * @return 	-1, wenn Ebene nicht enthalten.
	 * 			Index, im array, wenn enthalten
	 */
	private int var_way_contains_floor(int floorID){
		for(int i =0; i < route.size();i++){
			if(route.get(i).get(0).getFloorID() == floorID)
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
		int x_offset, y_offset;				// Offset errechnen, weil bestimmte Häuser auf Basis eines neues Koordinatensystems gezeichnet werden
		if((list.get(0).getFloorID() >= 8) && (list.get(0).getFloorID() <= 13)){
			x_offset = 210;
			y_offset = 254;
		}
		else{
			x_offset = 0;
			y_offset = 0;
		}
			
		var_paint.setColor(Color.GREEN); // Farbe setzen
		var_paint.setStrokeWidth(2);
		c.drawLine(list.get(0).getX()+10-x_offset, list.get(0).getY()+10-y_offset, list.get(0).getX()-10-x_offset, list.get(0).getY()-10-y_offset, var_paint); // Startkreuz setzen(strich1)
		c.drawLine(list.get(0).getX()+10-x_offset, list.get(0).getY()-10-y_offset, list.get(0).getX()-10-x_offset, list.get(0).getY()+10-y_offset, var_paint); // Startkreuz Strich2
		
		var_paint.setColor(Color.BLUE); // Farbe setzen
		for (int i = 0; i < list.size() - 1; i++) {
			c.drawLine(list.get(i).getX()-x_offset,list.get(i).getY()-y_offset,				// Startpunkt der Linie
					list.get(i + 1).getX()-x_offset, list.get(i + 1).getY()-y_offset, var_paint); // Zielpunkt der Linie
		}
		var_paint.setColor(Color.RED); // Farbe setzen
		c.drawLine(list.get(list.size()-1).getX()+10-x_offset, list.get(list.size()-1).getY()+10-y_offset, list.get(list.size()-1).getX()-10-x_offset, list.get(list.size()-1).getY()-10-y_offset, var_paint); // Zielkreuz (strich1)
		c.drawLine(list.get(list.size()-1).getX()+10-x_offset, list.get(list.size()-1).getY()-10-y_offset, list.get(list.size()-1).getX()-10-x_offset, list.get(list.size()-1).getY()+10-y_offset, var_paint); // Zielkreuz (strich2)
		//TODO: Farben & Größe anpassen (Startkreuz z.b. andere Farbe als Zielkreuz usw)
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
	//---------------------------------------------------------------------
	//---------------------------------------------------------------------	
	
    // Vorbereitung zum Zeichen
	// Gewaehrleistet gleiche Vorraussetzungen zum zeichen auf das Canvas
    private void preparation(Canvas canvas){
   		// Farbe auf Schwarz setzen
		var_paint.setColor(Color.BLACK);
 		// Style auf Umrandung setzen
		var_paint.setStyle(Paint.Style.STROKE);	
        	// Dicke der Umrandung = 0
		var_paint.setStrokeWidth(0);
     }
    
    // Nachbereitung zum Zeichen
	// Gewaehrleistet gleiche Vorraussetzungen zum zeichen auf das Canvas    
    private void postprocessing(Canvas canvas){
   		//Farbe auf Schwarz setzen
		var_paint.setColor(Color.BLACK);
 		//Style auf Umrandung setzen
		var_paint.setStyle(Paint.Style.STROKE);	
        	// Dicke der Umrandung = 0
		var_paint.setStrokeWidth(0);
    }
    	
    // Rahmen fuer Haus 5
	private void shape_house5(Canvas canvas){// Zeichnet Haus 5 blanko
	       // Vorbereitung
        preparation(canvas);
		// Treppe
		// ----------------------------------------------
		// ----------------------------------------------
		// Dicke der Umrandung = 2
		var_paint.setStrokeWidth(2);
		// Farbe (Braun) definieren
		int brown = Color.argb(127, 175, 112, 48);
		// Farbe auf Braun setzen
		var_paint.setColor(brown);
		// Treppe_Mitte zeichen
		for(int i=48;i<=72;i=i+4){
			canvas.drawLine(200, i, 222, i, var_paint);
		}
		// Aufzuege
		// ----------------------------------------------
		// ----------------------------------------------
		//Farbe auf Rot	setzen
		var_paint.setColor(Color.RED);
		// Aufzug_Links
		canvas.drawRect(30,80,40,92,var_paint);
		canvas.drawLine(30,80,40,92,var_paint);
		canvas.drawLine(30,92,40,80,var_paint);
		// Aufzug_Rechts
		canvas.drawRect(382,80,392,92,var_paint);
		canvas.drawLine(382,80,392,92,var_paint);
		canvas.drawLine(382,92,392,80,var_paint);
		// Aufzug_Mitte_Links
		canvas.drawRect(190,72,200,84,var_paint);
		canvas.drawLine(190,72,200,84,var_paint);
		canvas.drawLine(190,84,200,72,var_paint);
		// Aufzug_Mitte_Rechts
		canvas.drawRect(222,72,232,84,var_paint);
		canvas.drawLine(222,72,232,84,var_paint);
		canvas.drawLine(222,84,232,72,var_paint);
		// Raueme
		// ----------------------------------------------
		// ----------------------------------------------
		// Farbe auf Schwarz setzen
		var_paint.setColor(Color.BLACK);
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
		// Cleaner
		// ----------------------------------------------
		// ----------------------------------------------
		// Teile der Rechtecke werden geweisst
		// Farbe auf Weiß setzen
		var_paint.setColor(Color.WHITE);
		// Element_2_Cleaner
		canvas.drawLine(19,94,30,94,var_paint);
		// Element_4_Cleaner
		canvas.drawLine(201,94,221,94,var_paint);
		// Element_5_Cleaner
		canvas.drawLine(392,94,403,94,var_paint);
        // Nachbereitung
        postprocessing(canvas);
}
    
	// Rahmen fuer Haus 123
    private void shape_house123(Canvas canvas){
        // Vorbereitung
        preparation(canvas);
    	// Dicke der Umrandung = 2
		var_paint.setStrokeWidth(2);
		// Umrandung Haus 1
		canvas.drawRect(0, 0, 48, 130, var_paint);
		var_paint.setColor(Color.WHITE);
		canvas.drawLine(15, 130, 33, 130, var_paint);
		// Treppe Haus 1
		// ----------------------------------------------
		// ----------------------------------------------
		// Farbe (Braun) definieren
		int brown = Color.argb(127, 175, 112, 48);
		// Farbe auf Braun setzen
		var_paint.setColor(brown);
		// Treppe Eingang von Haus 5 aus gesehen
		for(int i=0;i<=26;i=i+2){
			canvas.drawLine(0,i,15,i,var_paint);
		}
		// Haus 2
		var_paint.setColor(Color.BLACK);
		canvas.drawRect(142, 163, 47, 130, var_paint);
		canvas.drawLine(factor*50,factor*372,factor*134,factor*470, var_paint);
		canvas.drawLine(factor*134,factor*470,factor*134,factor*482, var_paint);
		canvas.drawLine(factor*134,factor*482,factor*392,factor*549, var_paint);
		canvas.drawLine(factor*392,factor*549,factor*404,factor*503, var_paint);
		canvas.drawLine(factor*404,factor*503,factor*338,factor*485, var_paint);
		canvas.drawLine(factor*338,factor*470,factor*338,factor*485, var_paint);
		// Haus 3
		canvas.drawRect(factor*410,factor*490, factor*1270,factor*334, var_paint);
		canvas.drawRect(factor*640,factor*334,factor*510,factor*39, var_paint);
        //Nachbereitung
        postprocessing(canvas);   	
    }
    
	//---------------------------------------------------------------------
	//---------------------------------------------------------------------    
    
    // Zeichnet Campus
	private void draw_floor_00(Canvas canvas){ 		
		//Farbe auf Schwarz setzen
		var_paint.setColor(Color.BLACK);
		//Style auf Umrandung setzen
		var_paint.setStyle(Paint.Style.STROKE);	
		// Dicke der Umrandung = 0
		var_paint.setStrokeWidth(2);
		// Haus5
		canvas.drawRect(0, 0, 50, 50, var_paint);
		canvas.drawRect(20, 50, 40, 75, var_paint);
		canvas.drawRect(-6, 75, 40, 155, var_paint);
		canvas.drawRect(-6, 95, 460, 155, var_paint);
		canvas.drawRect(200, 95, 245, 75, var_paint);		
		canvas.drawRect(245, 0, 175, 75, var_paint);
		canvas.drawRect(380, 95, 405, 55, var_paint);
		canvas.drawRect(380, 55, 425, 25, var_paint);
		var_paint.setStrokeWidth(0);
		canvas.drawRect(450,95,405,55,var_paint);
		var_paint.setStrokeWidth(2);
		var_paint.setColor(Color.WHITE);
		canvas.drawLine(21,50,39,50,var_paint);
		canvas.drawLine(21,75,39,75,var_paint);
		canvas.drawLine(-5,95,39,95,var_paint);	
		canvas.drawLine(40,96,40,154,var_paint);
		canvas.drawLine(201,95,244,95,var_paint);
		canvas.drawLine(201,75,244,75,var_paint);
		canvas.drawLine(381,95,404,95,var_paint);		
		canvas.drawLine(381,55,404,55,var_paint);	
		// Eigene Farbe (Hellgelb) definieren
		int hgelb = Color.argb(127,244,233,83);
		// Farbe = Hellgelb
		var_paint.setColor(hgelb);
		var_paint.setStyle(Paint.Style.FILL);
		canvas.drawRect(380, 55, 425, 25, var_paint);
		canvas.drawRect(0, 0, 50, 50, var_paint);
		canvas.drawRect(20, 50, 40, 75, var_paint);
		canvas.drawRect(-6, 75, 40, 155, var_paint);
		canvas.drawRect(40, 95, 460, 155, var_paint);
		canvas.drawRect(200, 95, 245, 75, var_paint);		
		canvas.drawRect(245, 0, 175, 75, var_paint);
		canvas.drawRect(380, 95, 405, 55, var_paint);
		var_paint.setStyle(Paint.Style.STROKE);		
		// Haus5 - Skywalk
		var_paint.setColor(Color.BLACK);		
		canvas.drawRect(210, 155, 225, 255, var_paint);
		// Haus  1
		canvas.drawRect(210,255,255,380,var_paint);
		// Eigene Farbe (HellBlau) definieren
		int hblau = Color.argb(127,87,206,240);
		// Farbe = HellBlau
		var_paint.setColor(hblau);
		var_paint.setStyle(Paint.Style.FILL);
		canvas.drawRect(210,255,255,380,var_paint);	
		var_paint.setStyle(Paint.Style.STROKE);
		var_paint.setColor(Color.BLACK);
		// Haus  2		
		canvas.drawRect(350,380,225,415,var_paint);	
		// Eigene Farbe (Bronze) definieren
		int bronze = Color.argb(127,215,139,106);
		// Farbe = Bronze
		var_paint.setColor(bronze);
		var_paint.setStyle(Paint.Style.FILL);
		canvas.drawRect(350,380,225,415,var_paint);	
		var_paint.setStyle(Paint.Style.STROKE);
		var_paint.setColor(Color.BLACK);
		// Haus  3
		canvas.drawRect(350,370,650,425,var_paint);	
		canvas.drawRect(385,370,430,345,var_paint);		
		canvas.drawRect(385,345,445,265,var_paint);		
		canvas.drawRect(625,425,605,450,var_paint);			
		canvas.drawRect(510,425,530,450,var_paint);	
		var_paint.setColor(Color.WHITE);
		canvas.drawLine(606,425,624,425,var_paint);
		canvas.drawLine(511,425,529,425,var_paint);
		canvas.drawLine(386,345,429,345,var_paint);
		canvas.drawLine(386,370,429,370,var_paint);
		// Eigene Farbe (Blau) definieren
		int blau = Color.argb(127,136,156,249);
		// Farbe = Blau
		var_paint.setColor(blau);
		var_paint.setStyle(Paint.Style.FILL);
		canvas.drawRect(350,370,650,425,var_paint);	
		canvas.drawRect(385,370,430,345,var_paint);		
		canvas.drawRect(385,345,445,265,var_paint);		
		canvas.drawRect(625,425,605,450,var_paint);			
		canvas.drawRect(510,425,530,450,var_paint);	
		var_paint.setStyle(Paint.Style.STROKE);	
		var_paint.setColor(Color.BLACK);		
		// Haus 4
		canvas.drawRect(330,550,775,715,var_paint);		
		// Eigene Farbe (Orange) definieren
		int orange = Color.argb(127,250,100,0);
		// Farbe = Orange
		var_paint.setColor(orange);
		var_paint.setStyle(Paint.Style.FILL);
		canvas.drawRect(330,550,775,715,var_paint);	
		var_paint.setColor(Color.BLACK);		
		var_paint.setTextSize(22);
		// Dicke der Umrandung = 0
		var_paint.setStrokeWidth(0);
		canvas.drawText("H 5",250,130,var_paint);		
		canvas.drawText("H 4",520,650,var_paint);
		canvas.drawText("H 3",395,410,var_paint);
		canvas.drawText("H 2",250,410,var_paint);
		canvas.drawText("H 1",220,350,var_paint);				
		
	}

	// Zeichnet Haus 5 Etage -2
	private void draw_floor_01(Canvas canvas){ 
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
	
	// Zeichnet Haus 5 Etage -1	
	private void draw_floor_02(Canvas canvas){ 		
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

	// Zeichnet Haus 5 Etage 0		
	private void draw_floor_03(Canvas canvas){ 		
		canvas.drawRect(0, 0, 50, 50, var_paint);
		canvas.drawRect(20, 50, 40, 75, var_paint);
		
		canvas.drawRect(-6, 75, 40, 155, var_paint);
		

		/*
		
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
				*/
	}

	// Zeichnet Haus 5 Etage 1	
	private void draw_floor_04(Canvas canvas){
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
	   
	// Zeichnet Haus 5 Etage 2
	private void draw_floor_05(Canvas canvas){
				// Vorbereitung
				preparation(canvas);
				// Schablone Haus 5
		shape_house5(canvas);
		// Schriftgroesse
		var_paint.setTextSize(8);
		// Raum 15
		canvas.drawRect(0,0,18,48,var_paint);
		canvas.drawText("15",2,24,var_paint);		
		// Raum 16
		canvas.drawText("16",20,24,var_paint);		
		// Raum 17 RudisResteRampe			
		canvas.drawRect(40,48,28,56,var_paint);	
		// Raum 14				
		canvas.drawRect(-6,94,18,126,var_paint);	
		canvas.drawText("14",0,110,var_paint);		
		// Raum 13				
		canvas.drawRect(-6,126,18,152,var_paint);
		canvas.drawText("13",0,148,var_paint);		
		// Raum 12				
		canvas.drawRect(54,136,18,152,var_paint);
		canvas.drawText("12",34,148,var_paint);		
		// Raum 11				
		canvas.drawRect(62,136,54,152,var_paint);
		canvas.drawText("11",54,148,var_paint);
		// Raum 10			
		canvas.drawRect(73,136,62,152,var_paint);
		canvas.drawText("10",62,148,var_paint);
		// Raum 9
		canvas.drawRect(84,136,73,152,var_paint);
		canvas.drawText("9",77,148,var_paint);
		// Raum 8		
		canvas.drawRect(106,136,84,152,var_paint);
		canvas.drawText("8",96,148,var_paint);
		// Raum 7		
		canvas.drawRect(132,136,106,152,var_paint);
		canvas.drawText("7",118,148,var_paint);
		// Raum 6
		canvas.drawRect(143,136,132,152,var_paint);
		canvas.drawText("6",135,148,var_paint);
		// Raum 5
		canvas.drawRect(154,136,143,152,var_paint);	
		canvas.drawText("5",146,148,var_paint);
		// Raum 4		
		canvas.drawRect(165,136,154,152,var_paint);
		canvas.drawText("4",157,148,var_paint);
		// Raum 3
		canvas.drawRect(176,136,165,152,var_paint);
		canvas.drawText("3",168,148,var_paint);
		// Raum 2		
		canvas.drawRect(187,136,176,152,var_paint);
		canvas.drawText("2",180,148,var_paint);		
		// Raum 1			
		canvas.drawRect(200,136,187,152,var_paint);
		canvas.drawText("1",190,148,var_paint);		
		// Raum 61	
		canvas.drawRect(222,136,244,152,var_paint);
		canvas.drawText("61",228,148,var_paint);		
		// Raum 60		
		canvas.drawRect(244,136,255,152,var_paint);
		canvas.drawText("60",246,148,var_paint);		
		// Raum 59		
		canvas.drawRect(266,136,255,152,var_paint);		
		canvas.drawText("59",257,148,var_paint);	
		// Raum 58		
		canvas.drawRect(290,136,266,152,var_paint);
		canvas.drawText("58",272,148,var_paint);
		// Raum 57
		canvas.drawRect(301,136,290,152,var_paint);
		canvas.drawText("57",293,148,var_paint);		
		// Raum 56
		canvas.drawRect(312,136,301,152,var_paint);
		canvas.drawText("56",303,148,var_paint);		
		// Raum 55
		canvas.drawRect(334,136,312,152,var_paint);
		canvas.drawText("55",320,148,var_paint);		
		// Raum 54
		canvas.drawRect(334,136,358,152,var_paint);	
		canvas.drawText("54",340,148,var_paint);		
		// Raum 53		
		canvas.drawRect(380,136,358,152,var_paint);	
		canvas.drawText("53",368,148,var_paint);		
		// Raum 52		
		canvas.drawRect(380,136,434,152,var_paint);	
		canvas.drawText("52",400,148,var_paint);		
		// Raum 51	
		canvas.drawRect(434,126,462,152,var_paint);
		canvas.drawText("51",440,148,var_paint);		
		// Raum 50	
		canvas.drawRect(434,94,462,152,var_paint);
		canvas.drawText("50",440,110,var_paint);		
		// Raum 49	
		canvas.drawRect(403,94,434,118,var_paint);
		canvas.drawText("49",412,110,var_paint);		
		// Raum 45/46/47 RudisResteRampe	
		canvas.drawRect(392,94,372,106,var_paint);
		// Raum 41/42/43/44 WC
		canvas.drawRect(392,106,372,126,var_paint);	
		// Raum 40	
		canvas.drawRect(372,94,334,126,var_paint);		
		canvas.drawText("40",345,110,var_paint);	
		// Raum 39
		canvas.drawRect(334,94,301,126,var_paint);
		canvas.drawText("39",315,110,var_paint);	
		// Raum 38
		canvas.drawRect(301,94,266,126,var_paint);	
		canvas.drawText("38",276,110,var_paint);	
		// Raum 37
		canvas.drawRect(266,94,246,126,var_paint);
		canvas.drawText("37",250,110,var_paint);		
		// Raum 36
		canvas.drawRect(222,94,246,126,var_paint);
		canvas.drawText("36",230,110,var_paint);	
		// Raum 35 RudisResteRampe
		canvas.drawRect(200,94,187,108,var_paint);
		// Raum 30/31/32/33/34 WC
		canvas.drawRect(200,108,187,126,var_paint);
		// Raum 29
		canvas.drawRect(187,94,152,126,var_paint);
		canvas.drawText("29",160,110,var_paint);		
		// Raum 28
		canvas.drawRect(152,94,132,126,var_paint);		
		canvas.drawText("28",136,110,var_paint);	
		// Raum 27
		canvas.drawRect(132,94,106,126,var_paint);
		canvas.drawText("27",114,110,var_paint);
		// Raum 26
		canvas.drawRect(106,94,54,126,var_paint);
		canvas.drawText("26",85,110,var_paint);			
		// Raum 18/19/20 RudisResteRampe
		canvas.drawRect(54,94,30,106,var_paint);
		// Raum 21/22/23/24 WC 
		canvas.drawRect(54,106,30,126,var_paint);
		// Raum 48
		canvas.drawText("48",404,40,var_paint);		
		// Treppen
		// ----------------------------------------------
		// ----------------------------------------------
		// Dicke der Umrandung = 2
		var_paint.setStrokeWidth(2);		
		// Farbe (Braun) definieren
		int brown = Color.argb(127, 175, 112, 48);
		// Farbe = Braun
		var_paint.setColor(brown);
		// Style = Umrandung		
		var_paint.setStyle(Paint.Style.STROKE);		
		// Treppe_Links		
		for(int i=63;i<=80;i=i+4){
			canvas.drawLine(30,i,40,i,var_paint);
		}
		canvas.drawRect(30,63,40,80,var_paint);
		// Treppe_Rechts
		for(int i=63;i<=80;i=i+4){
			canvas.drawLine(382,i,392,i,var_paint);
		}	
		canvas.drawRect(382,63,392,80,var_paint);
		// Farbe
		// ----------------------------------------------
		// ----------------------------------------------
		// Farbe definieren (Grau-Transparent)
		int gray = Color.argb(127, 255, 111, 111);
		// Farbe setzten
		var_paint.setColor(gray);
		// Style auf ausgefuellt setzten
		var_paint.setStyle(Paint.Style.FILL);		
		//Element_01 ausfuellen
		canvas.drawRect(0,0,48,48,var_paint);
		//Element_02 ausfuellen
		canvas.drawRect(18,48,40,94,var_paint);
		//Element_03 ausfuellen
		canvas.drawRect(-6,94,462,152,var_paint);
		//Element_04 ausfuellen	
		canvas.drawRect(200,46,222,94,var_paint);
		//Element_05 ausfuellen		
		canvas.drawRect(382,54,404,94,var_paint);
		//Element_06 ausfuellen
		canvas.drawRect(382,24,424,54,var_paint);
				// Nachbereitung
				postprocessing(canvas);
		}

	// Zeichnet Haus 5 Etage 3
	private void draw_floor_06(Canvas canvas){
    	// Vorbereitung
    	preparation(canvas);
    	// Schablone von Haus 5 zeichnen
    	shape_house5(canvas);		
    	// Treppen
	// ----------------------------------------------
	// ----------------------------------------------
	// Dicke der Umrandung = 2
	var_paint.setStrokeWidth(2);		
	// Farbe (Braun) definieren
	int brown = Color.argb(127, 175, 112, 48);
	// Farbe = Braun
	var_paint.setColor(brown);
	// Style = Umrandung		
	var_paint.setStyle(Paint.Style.STROKE);		
	// Treppe_Links		
	for(int i=63;i<=80;i=i+4){
		canvas.drawLine(30,i,40,i,var_paint);
	}
	canvas.drawRect(30,63,40,80,var_paint);
	// Treppe_Rechts
	for(int i=63;i<=80;i=i+4){
		canvas.drawLine(382,i,392,i,var_paint);
	}	
	canvas.drawRect(382,63,392,80,var_paint);
	// Treppe 3Z (Links waagerecht)
	for(int i=-6;i<=10;i=i+4){
		canvas.drawLine(i,108, i, 116, var_paint);
	}
	canvas.drawRect(-2,108,10,116,var_paint);
	// Treppe Hoersaal 5
	for(int i=176;i<=188;i=i+4){
		canvas.drawLine(i,116, i, 124, var_paint);
	}
	canvas.drawRect(176,116,188,124,var_paint);	
	// Treppe Hoersaal 4
	for(int i=266;i<=278;i=i+4){
		canvas.drawLine(i,116, i, 124, var_paint);
	}
	canvas.drawRect(266,116,278,124,var_paint);	
	// Treppe Hoersaal 3
	for(int i=334;i<=346;i=i+4){
		canvas.drawLine(i,116, i, 124, var_paint);
	}
	canvas.drawRect(334,116,346,124,var_paint);	
	// Treppe Hoersaal 2
	for(int i=404;i<=416;i=i+4){
		canvas.drawLine(i,116, i, 124, var_paint);
	}
	canvas.drawRect(404,116,416,124,var_paint);	
	// Raueme
	// ----------------------------------------------
	// ----------------------------------------------
	// Farbe auf Schwarz setzen
	var_paint.setColor(Color.BLACK);
	// Dicke der Umrandung = 0
	var_paint.setStrokeWidth(0);
	// Textgroesse setzen
	var_paint.setTextSize(8);	
	// Raum 09
	canvas.drawRect(-6,124,18,152,var_paint);		
	canvas.drawText("09",-4,144,var_paint);
	// Raum 08
	canvas.drawRect(18,124,40,152,var_paint);	
	canvas.drawText("08",20,144,var_paint);	
	// Raum 07
	canvas.drawRect(40,124,62,152,var_paint);
	canvas.drawText("07",42,144,var_paint);
	// Raum 06
	canvas.drawRect(62,124,84,152,var_paint);
	canvas.drawText("06",64,144,var_paint);
	// Raum 05
	canvas.drawRect(84,124,106,152,var_paint);
	canvas.drawText("05",86,144,var_paint);		
	// Raum 04
	canvas.drawRect(106,124,132,152,var_paint);
	canvas.drawText("04",108,144,var_paint);	
	// Hoersaal 5
	canvas.drawRect(132,116,176,152,var_paint);		
	canvas.drawText("HS - 5",134,144,var_paint);	
	// Raum 01
	canvas.drawRect(176,124,200,152,var_paint);
	canvas.drawText("01",178,144,var_paint);	
	// Hoersaal 4
	canvas.drawRect(222,116,266,152,var_paint);
	canvas.drawText("HS - 4",224,144,var_paint);	
	// Raum 42
	canvas.drawRect(266,124,290,152,var_paint);
	canvas.drawText("42",270,144,var_paint);
	// Hoersaal 3
	canvas.drawRect(290,116,334,152,var_paint);
	canvas.drawText("HS - 3",292,144,var_paint);	
	// Raum 229
	canvas.drawRect(334,124,358,152,var_paint);
	canvas.drawText("38",336,144,var_paint);
	// Hoersaal 2
	canvas.drawRect(358,116,404,152,var_paint);
	canvas.drawText("HS - 2",360,144,var_paint);	
	// Raum 36
	canvas.drawRect(404,124,436,152,var_paint);
	canvas.drawText("36",406,144,var_paint);		
	// WC
	canvas.drawRect(436,132,446,152,var_paint);
	// Raum 33
	canvas.drawRect(446,122,462,152,var_paint);
	canvas.drawText("33",447,144,var_paint);		
	// Raum 32
	canvas.drawRect(446,94,462,122,var_paint);
	canvas.drawText("32",447,106,var_paint);		
	// WC
	canvas.drawRect(446,94,436,114,var_paint);
	// Raum 25/26/27 RudisResteRampe
	canvas.drawRect(392,94,374,108,var_paint);
	// Gelaender
	canvas.drawLine(334,94,334,108,var_paint);
	// Gelaender
	canvas.drawLine(266,94,266,108,var_paint);
	// Raum 24 RudisResteRampe
	canvas.drawRect(176,94,200,108,var_paint);
	// WC
	canvas.drawRect(72,94,116,116,var_paint);
	// Raum 18 RudisResteRampe
	canvas.drawRect(40,108,72,116,var_paint);
	// Raum 15/16/17 RudisResteRampe
	canvas.drawRect(30,94,72,108,var_paint);
	// Raum 10
	canvas.drawRect(0,40,18,48,var_paint);
	canvas.drawText("10",2,48,var_paint);		
	// Raum 13
	canvas.drawRect(24,40,48,48,var_paint);
	canvas.drawText("13",26,48,var_paint);	
	// Raum 12
	canvas.drawText("12",23,23,var_paint);	
	// Durchgang ausfuellen
	var_paint.setColor(Color.WHITE);
	var_paint.setStrokeWidth(2);
	canvas.drawLine(19,48,24,48,var_paint);
	var_paint.setColor(Color.BLACK);
	var_paint.setStrokeWidth(0);
	// Vorraum
	canvas.drawRect(396,54,404,46,var_paint);		
	// Trennwand zwischen 28 & 29
	canvas.drawLine(400, 46, 400, 24, var_paint);
	// Raum 29
	canvas.drawText("29",404,40,var_paint);	
	// Raum 28
	canvas.drawText("28",385,40,var_paint);	
	// Farbe
	// ----------------------------------------------
	// ----------------------------------------------
	//  Farbe definieren (Grau)
	int gray = Color.argb(127, 200, 200, 200);
	// Farbe setzten
	var_paint.setColor(gray);
	// Style setzten
	var_paint.setStyle(Paint.Style.FILL);		
	//Element_01 ausfuellen
	canvas.drawRect(0,0,48,48,var_paint);
	//Element_02 ausfuellen
	canvas.drawRect(18,48,40,94,var_paint);
	//Element_03 ausfuellen
	canvas.drawRect(-6,94,462,152,var_paint);
	//Element_04 ausfuellen	
	canvas.drawRect(200,46,222,94,var_paint);
	//Element_05 ausfuellen		
	canvas.drawRect(382,54,404,94,var_paint);
	//Element_06 ausfuellen
	canvas.drawRect(382,24,424,54,var_paint);
	// Nachbereitung
   		postprocessing(canvas);
}

	// Zeichnet Haus 5 Etage 3Z
	private void draw_floor_07(Canvas canvas){ 
    	// Schablone von Haus 5 zeichnen
	shape_house5(canvas);
    	// Vorbereitung
    	preparation(canvas);
    	// Treppen
	// ----------------------------------------------
	// ----------------------------------------------
	// Dicke der Umrandung = 2
	var_paint.setStrokeWidth(2);		
	// Eigene Farbe (Braun) definieren
	int brown = Color.argb(127, 175, 112, 48);
	// Farbe = Braun
	var_paint.setColor(brown);
	// Style = Umrandung		
	var_paint.setStyle(Paint.Style.STROKE);		
	// Treppe 3Z (Links waagerecht)
	for(int i=-6;i<=10;i=i+4){
		canvas.drawLine(i,108, i, 116, var_paint);
	}
	canvas.drawRect(-2,108,10,116,var_paint);
	// Treppe_Links		
	for(int i=63;i<=80;i=i+4){
		canvas.drawLine(30,i,40,i,var_paint);
	}
	canvas.drawRect(30,63,40,80,var_paint);		
	// Treppe Hoersaal 5
	for(int i=176;i<=188;i=i+4){
		canvas.drawLine(i,116, i, 124, var_paint);
	}
	canvas.drawRect(176,116,188,124,var_paint);	
	// Treppe Hoersaal 4
	for(int i=266;i<=278;i=i+4){
		canvas.drawLine(i,116, i, 124, var_paint);
	}
	canvas.drawRect(266,116,278,124,var_paint);	
	// Treppe Hoersaal 3
	for(int i=334;i<=346;i=i+4){
		canvas.drawLine(i,116, i, 124, var_paint);
	}
	canvas.drawRect(334,116,346,124,var_paint);	
	// Treppe Hoersaal 2
	for(int i=404;i<=416;i=i+4){
		canvas.drawLine(i,116, i, 124, var_paint);
	}
	canvas.drawRect(404,116,416,124,var_paint);	
	// Raueme
	// ----------------------------------------------
	// ----------------------------------------------
	//Farbe auf Schwarz setzen
	var_paint.setColor(Color.BLACK);
	// Dicke der Umrandung auf 0 setzen
	var_paint.setStrokeWidth(0);
	// Textgroesse setzen
	int text_size = 8;
	var_paint.setTextSize(text_size);	
	// Raum 207
	canvas.drawRect(-6,124,18,152,var_paint);		
	canvas.drawText("207",-4,144,var_paint);
	// Raum 206
	canvas.drawRect(18,124,40,152,var_paint);	
	canvas.drawText("206",20,144,var_paint);	
	// Raum 205
	canvas.drawRect(40,124,62,152,var_paint);
	canvas.drawText("205",42,144,var_paint);
	// Raum 204
	canvas.drawRect(62,124,84,152,var_paint);
	canvas.drawText("204",64,144,var_paint);
	// Raum 203
	canvas.drawRect(84,124,106,152,var_paint);
	canvas.drawText("203",86,144,var_paint);		
	// Raum 202
	canvas.drawRect(106,124,132,152,var_paint);
	canvas.drawText("202",108,144,var_paint);	
	// Hoersaal 5
	canvas.drawRect(132,116,176,152,var_paint);		
	canvas.drawText("HS - 5",134,144,var_paint);	
	// Raum 201
	canvas.drawRect(176,124,200,152,var_paint);
	canvas.drawText("201",178,144,var_paint);	
	// Hoersaal 4
	canvas.drawRect(222,116,266,152,var_paint);
	canvas.drawText("HS - 4",224,144,var_paint);	
	// Raum 230
	canvas.drawRect(266,124,290,152,var_paint);
	canvas.drawText("230",270,144,var_paint);
	// Hoersaal 3
	canvas.drawRect(290,116,334,152,var_paint);
	canvas.drawText("HS - 3",292,144,var_paint);	
	// Raum 229
	canvas.drawRect(334,124,358,152,var_paint);
	canvas.drawText("229",336,144,var_paint);
	// Hoersaal 2
	canvas.drawRect(358,116,404,152,var_paint);
	canvas.drawText("HS - 2",360,144,var_paint);	
	// Raum 228
	canvas.drawRect(404,124,436,152,var_paint);
	canvas.drawText("228",406,144,var_paint);		
	// WC
	canvas.drawRect(436,132,446,152,var_paint);
	// Raum 225
	canvas.drawRect(446,122,462,152,var_paint);
	canvas.drawText("225",447,144,var_paint);		
	// Raum 224
	canvas.drawRect(446,94,462,122,var_paint);
	//	canvas.drawText("224",447,text_ypos_2,var_paint);
	// TODO text_ypos_2 !?!?!?
	// Raum 222/223 WC
	canvas.drawRect(446,94,436,114,var_paint);
	// Gelaender
	canvas.drawLine(403,108,436,108,var_paint);
	// Gelaender
	canvas.drawLine(403,94,403,108,var_paint);		
	// Raum 219/220/221 RudisResteRampe
	canvas.drawRect(392,94,374,108,var_paint);
	// Gelaender
	canvas.drawLine(334,94,334,108,var_paint);
	// Gelaender
	canvas.drawLine(266,94,266,108,var_paint);
	// Gelaender
	canvas.drawLine(222,94,222,108,var_paint);
	// Gelaender
	canvas.drawLine(222,108,374,108,var_paint);
	// Raum 218 RudisResteRampe
	canvas.drawRect(176,94,200,108,var_paint);
	// Gelaender
	canvas.drawLine(116,108,176,108,var_paint);
	// WC
	canvas.drawRect(72,94,116,116,var_paint);
	// Raum 212 RudisResteRampe
	canvas.drawRect(40,108,72,116,var_paint);
	// Raum 209/210/211 RudisResteRampe
	canvas.drawRect(30,94,72,108,var_paint);		
	// Raum 208
	canvas.drawRect(18,48,40,60,var_paint);
	canvas.drawText("208",20,58,var_paint);		
	// Gelaender
	canvas.drawLine(2,94,2,108,var_paint);
	// Gelaender
	canvas.drawLine(2,116,2,124,var_paint);
	// Farbe
	// ----------------------------------------------
	// ----------------------------------------------
	// Farbe definieren (Grau)
	int gray = Color.argb(127, 200, 200, 200);
	// Farbe setzten
	var_paint.setColor(gray);
	// Style setzten
	var_paint.setStyle(Paint.Style.FILL);		
	//Element_01 ausfuellen
	canvas.drawRect(0,0,48,48,var_paint);
	//Element_02 ausfuellen
	canvas.drawRect(18,48,40,94,var_paint);
	//Element_03 ausfuellen
	canvas.drawRect(-6,94,462,152,var_paint);
	//Element_04 ausfuellen	
	canvas.drawRect(200,46,222,94,var_paint);
	//Element_05 ausfuellen		
	canvas.drawRect(382,54,404,94,var_paint);
	//Element_06 ausfuellen
	canvas.drawRect(382,24,424,54,var_paint);
	// Cleaner
	// ----------------------------------------------
	// ----------------------------------------------
	// Farbe setzen
	var_paint.setColor(Color.WHITE);
	// ausfuellen
	canvas.drawRect(175,95,117,107,var_paint);
	// ausfuellen
	canvas.drawRect(223,95,265,107,var_paint);
	// ausfuellen
	canvas.drawRect(267,95,333,107,var_paint);	
	// ausfuellen
	canvas.drawRect(335,95,373,107 ,var_paint);	
        // Nachbereitung
    	postprocessing(canvas);
}
    
	// Zeichnet Haus 123 Etage -1
	private void draw_floor_08(Canvas canvas){
    	// TODO zeichnen        
}

	// Zeichnet Haus 123 Etage 0
	private void draw_floor_09(Canvas canvas){
		// Schablone Haus 123
		shape_house123(canvas);
    	// Vorbereitung
        preparation(canvas);
        // Textgroesse
		var_paint.setTextSize(5);			
		// Haus 1
		// ----------------------------------------
	    // ----------------------------------------        
		// Raum 41
		canvas.drawRect(factor*0,factor*102,factor*43,factor*76,var_paint);
		// Raum 08
		canvas.drawRect(factor*0,factor*158,factor*43,factor*174,var_paint);
		canvas.drawText("8",factor*10,factor*170,var_paint);		
		// Raum 09
		canvas.drawRect(factor*0,factor*158,factor*43,factor*102,var_paint);
		canvas.drawText("9",factor*10,factor*125,var_paint);		
		// Raum 07
		canvas.drawRect(factor*0,factor*158,factor*43,factor*240,var_paint);
		canvas.drawText("7 / 2",factor*10,factor*217,var_paint);		
		// Raum 07
		canvas.drawRect(factor*0,factor*240,factor*43,factor*272,var_paint);
		canvas.drawText("7 / 1",factor*10 ,factor*251,var_paint);
		// Raum 06/05 RudisResteRampe
		canvas.drawRect(factor*0,factor*272,factor*43,factor*306,var_paint);
		canvas.drawLine(factor*0,factor*289,factor*43,factor*289,var_paint);		
		// Raum WC
		canvas.drawRect(factor*0,factor*306,factor*43,factor*372,var_paint);
		canvas.drawLine(factor*0,factor*339,factor*43,factor*339,var_paint);
		// Raum 19
		canvas.drawLine(factor*66,factor*339,factor*134,factor*339,var_paint);
		canvas.drawText("19",factor*76,factor*350,var_paint);
		// Raum 18/2
		canvas.drawRect(factor*66,factor*306,factor*134,factor*372,var_paint);
		canvas.drawText("18 / 2",factor*76,factor*330,var_paint);
		// Raum 18/1
		canvas.drawRect(factor*66,factor*306,factor*134,factor*272,var_paint);
		canvas.drawText("18 / 1",factor*76,factor*285,var_paint);
		// Raum 17
		canvas.drawRect(factor*66,factor*240,factor*134,factor*272,var_paint);
		canvas.drawText("17",factor*76,factor*251,var_paint);		
		// Raum 16
		canvas.drawRect(factor*66,factor*206,factor*134,factor*240,var_paint);
		canvas.drawText("16",factor*76,factor*217,var_paint);
		// Raum 15	
		canvas.drawRect(factor*66,factor*174,factor*134,factor*206,var_paint);
		canvas.drawText("15",factor*76,factor*185,var_paint);
		// Raum 14	
		canvas.drawRect(factor*66,factor*174,factor*134,factor*142,var_paint);
		canvas.drawText("14",factor*76,factor*152,var_paint);
		// Raum 13	
		canvas.drawRect(factor*66,factor*142,factor*134,factor*102,var_paint);
		canvas.drawText("13",factor*76,factor*120,var_paint);
		// Raum 12	
		canvas.drawRect(factor*66,factor*102,factor*134,factor*77,var_paint);
		canvas.drawText("12",factor*76,factor*90,var_paint);
		// Raum 11
		canvas.drawLine(factor*66,factor*77,factor*43,factor*77,var_paint);
		canvas.drawText("11",factor*86,factor*30,var_paint);
		// Raum 10	
		canvas.drawLine(factor*66,factor*77,factor*66,factor*12,var_paint);
		canvas.drawText("10",factor*45,factor*30,var_paint);
		// Haus 2
	    // ----------------------------------------
	    // ----------------------------------------        
		canvas.drawRect(factor*410,factor*470,factor*346,factor*438,var_paint);		
		// Cleaner
	    // ----------------------------------------
	    // ----------------------------------------        
		var_paint.setColor(Color.WHITE);
		var_paint.setStrokeWidth(2);		
		canvas.drawLine(factor*134, factor*433, factor*134, factor*468, var_paint);
		canvas.drawLine(factor*581, factor*334, factor*603, factor*334, var_paint);
		canvas.drawLine(factor*202, factor*470, factor*310, factor*470, var_paint);		
		var_paint.setStrokeWidth(0);		
		var_paint.setColor(Color.BLACK);
		var_paint.setStrokeWidth(2);	
		canvas.drawRect(factor*208,factor*372,factor*312,factor*322,var_paint);		
		// Haus 3
	    // ----------------------------------------
	   	// ----------------------------------------        
	    // TODO
}

	// Zeichnet Haus 123 Etage 1
	private void draw_floor_10(Canvas canvas){
    	// Schablone Haus 123
    	shape_house123(canvas);
   		// Vorbereitung
    	preparation(canvas);
    	var_paint.setStrokeWidth(0);
    	var_paint.setColor(Color.BLACK);
    	var_paint.setTextSize(5);
	   	// Haus 1
	   	// ----------------------------------------
	   	// ----------------------------------------        
		// Raum 41
		canvas.drawRect(factor*0,factor*102,factor*43,factor*76,var_paint);
		// Raum 10
		canvas.drawRect(factor*0,factor*158,factor*43,factor*174,var_paint);
		// Raum 09
		canvas.drawRect(factor*0,factor*174,factor*43,factor*206,var_paint);
		canvas.drawText("9",factor*10 ,factor*185,var_paint);
		// Raum 08
		canvas.drawRect(factor*0,factor*206,factor*43,factor*240,var_paint);
		canvas.drawText("8",factor*10 ,factor*217,var_paint);
		// Raum 07
		canvas.drawRect(factor*0,factor*240,factor*43,factor*272,var_paint);
		canvas.drawText("7",factor*10 ,factor*251,var_paint);
		// Raum 06/05 RudisResteRampe
		canvas.drawRect(factor*0,factor*272,factor*43,factor*306,var_paint);
		canvas.drawLine(factor*0,factor*289,factor*43,factor*289,var_paint);
		// Raum WC
		canvas.drawRect(factor*0,factor*306,factor*43,factor*372,var_paint);
		canvas.drawLine(factor*0,factor*339,factor*43,factor*339,var_paint);
		// Raum 13
		canvas.drawRect(factor*66,factor*174,factor*134,factor*206,var_paint);
		canvas.drawText("13",factor*76 ,factor*185,var_paint);
		// Raum 14
		canvas.drawRect(factor*66,factor*206,factor*134,factor*240,var_paint);
		canvas.drawText("14",factor*76 ,factor*217,var_paint);
		// Raum 15
		canvas.drawRect(factor*66,factor*240,factor*134,factor*272,var_paint);
		canvas.drawText("15",factor*76 ,factor*251,var_paint);
		// Raum 12
		canvas.drawRect(factor*66,factor*174,factor*134,factor*77,var_paint);
		canvas.drawText("12",factor*76 ,factor*120,var_paint);
		// Raum 11
		canvas.drawLine(factor*66,factor*77,factor*42,factor*77,var_paint);
		canvas.drawText("11",factor*76,factor*30,var_paint);
		// Raum 16
		canvas.drawRect(factor*66,factor*272,factor*134,factor*372,var_paint);
		canvas.drawText("16",factor*76 ,factor*350,var_paint);
		// Haus 2
	    // ----------------------------------------
	    // ----------------------------------------        
		// Raum 01
		canvas.drawRect(factor*134,factor*432,factor*176,factor*372,var_paint);
		canvas.drawText("1",factor*144,factor*420,var_paint);
		// Raum 02
		canvas.drawRect(factor*176,factor*432,factor*208,factor*372,var_paint);
		canvas.drawText("2",factor*186,factor*420,var_paint);
		// Freier Blick auf die Etage darunter
		canvas.drawRect(factor*227,factor*432,factor*294,factor*372,var_paint);
		// Raum 03
		canvas.drawRect(factor*314,factor*432,factor*348,factor*372,var_paint);
		canvas.drawText("3",factor*324,factor*420,var_paint);
		// Raum 04
		canvas.drawRect(factor*348,factor*432,factor*380,factor*372,var_paint);
		canvas.drawText("4",factor*358,factor*420,var_paint);
		// Raum 05
		canvas.drawRect(factor*380,factor*432,factor*410,factor*372,var_paint);
		canvas.drawText("5",factor*390,factor*420,var_paint);
		// Cleaner
	    // ----------------------------------------
	   	// ----------------------------------------        
		var_paint.setColor(Color.WHITE);
		var_paint.setStrokeWidth(2);
		canvas.drawLine(factor*134, factor*433, factor*134,factor*468, var_paint);
		canvas.drawLine(factor*581, factor*334, factor*603, factor*334, var_paint);
		var_paint.setStrokeWidth(0);
		var_paint.setColor(Color.BLACK);
		// Haus 3
	    // ----------------------------------------
	    // ----------------------------------------        
		// Raum 1
		canvas.drawRect(factor*410,factor*334,factor*444,factor*392,var_paint);
		canvas.drawText("1",factor*420,factor*360,var_paint);
		// Raum 2
		canvas.drawRect(factor*444,factor*334,factor*542,factor*392,var_paint);
		canvas.drawText("2",factor*454,factor*360,var_paint);
		// Raum 3
		canvas.drawRect(factor*542,factor*334,factor*580,factor*392,var_paint);
		canvas.drawText("3",factor*552,factor*360,var_paint);
		// Raum 9
		canvas.drawRect(factor*604,factor*334,factor*704,factor*392,var_paint);
		canvas.drawText("9",factor*614,factor*360,var_paint);
		// Raum 10
		canvas.drawRect(factor*704,factor*334,factor*740,factor*384,var_paint);
		canvas.drawText("10",factor*714,factor*360,var_paint);
		// Raum 11
		canvas.drawRect(factor*740,factor*334,factor*772,factor*384,var_paint);
		canvas.drawText("11",factor*750,factor*360,var_paint);
		// Raum 12
		canvas.drawRect(factor*772,factor*334,factor*872,factor*392,var_paint);
		canvas.drawText("12",factor*782,factor*360,var_paint);
		// Raum 13
		canvas.drawRect(factor*872,factor*334,factor*970,factor*392,var_paint);
		canvas.drawText("13",factor*882,factor*360,var_paint);
		// Raum 14
		canvas.drawRect(factor*970,factor*334,factor*998,factor*392,var_paint);
		canvas.drawText("14",factor*980,factor*360,var_paint);
		// Raum 15
		canvas.drawRect(factor*998,factor*334,factor*1102,factor*392,var_paint);
		canvas.drawText("15",factor*1008,factor*360,var_paint);
		// Raum 16
		canvas.drawRect(factor*1102,factor*334,factor*1134,factor*392,var_paint);
		canvas.drawText("16",factor*1112,factor*360,var_paint);
		// Raum 17
		canvas.drawRect(factor*1134,factor*334,factor*1170,factor*392,var_paint);
		canvas.drawText("17",factor*1144,factor*360,var_paint);
		// Raum 18
		canvas.drawRect(factor*1170,factor*334,factor*1204,factor*392,var_paint);
		canvas.drawText("18",factor*1180,factor*360,var_paint);
		// Raum 19
		canvas.drawRect(factor*1204,factor*334,factor*1238,factor*392,var_paint);
		canvas.drawText("19",factor*1214,factor*360,var_paint);
		// Raum 20
		canvas.drawRect(factor*1238,factor*334,factor*1270,factor*392,var_paint);
		canvas.drawText("20",factor*1248,factor*360,var_paint);
		// Raum 21
		canvas.drawRect(factor*1238,factor*424,factor*1270,factor*490,var_paint);
		canvas.drawText("21",factor*1248,factor*460,var_paint);
		// Raum 22
		canvas.drawRect(factor*1238,factor*424,factor*1204,factor*490,var_paint);
		canvas.drawText("22",factor*1214,factor*460,var_paint);
		// Raum 23/24/25/26 WC
		canvas.drawRect(factor*1204,factor*414,factor*1170,factor*490,var_paint);
		// Raum 29
		canvas.drawRect(factor*1128,factor*424,factor*1028,factor*490,var_paint);
		canvas.drawText("29",factor*1038,factor*460,var_paint);
		// Raum 30
		canvas.drawRect(factor*1028,factor*424,factor*998,factor*490,var_paint);
		canvas.drawText("30",factor*1008,factor*460,var_paint);
		// Raum 31
		canvas.drawRect(factor*998,factor*424,factor*936,factor*490,var_paint);
		canvas.drawText("31",factor*946,factor*460,var_paint);
		// Raum 32/33/34 RudisResteRampe
		canvas.drawRect(factor*936,factor*414,factor*896,factor*490,var_paint);
		// Raum 35
		canvas.drawRect(factor*872,factor*424,factor*772,factor*490,var_paint);
		canvas.drawText("35",factor*782,factor*460,var_paint);
		// Raum 36
		canvas.drawRect(factor*772,factor*424,factor*740,factor*490,var_paint);
		canvas.drawText("36",factor*750,factor*460,var_paint);
		// Raum 37
		canvas.drawRect(factor*740,factor*424,factor*704,factor*490,var_paint);
		canvas.drawText("37",factor*714,factor*460,var_paint);
		// Raum 38
		canvas.drawRect(factor*704,factor*424,factor*682,factor*490,var_paint);
		canvas.drawText("38",factor*692,factor*460,var_paint);
		// Raum 39
		canvas.drawRect(factor*682,factor*424,factor*576,factor*490,var_paint);
		canvas.drawText("39",factor*586,factor*460,var_paint);
		// Raum 40
		canvas.drawRect(factor*576,factor*424,factor*544,factor*490,var_paint);
		canvas.drawText("40",factor*554,factor*460,var_paint);
		// Raum 41
		canvas.drawRect(factor*544,factor*424,factor*450,factor*490,var_paint);
		canvas.drawText("41",factor*460,factor*460,var_paint);
		// Sporthalle
		// Raum 4/5/6/7/8
		canvas.drawRect(factor*580,factor*334,factor*510,factor*264,var_paint);
		// Treppenhaus
		canvas.drawRect(factor*640,factor*334,factor*580,factor*264,var_paint);
	    // Nachbereitung
	    postprocessing(canvas);
}
	
	// Zeichnet Haus 123 Etage 2
	private void draw_floor_11(Canvas canvas){
    	// Schablone Haus 123
    	shape_house123(canvas);
    	// Vorbereitung
    	preparation(canvas);
    	// Textgroesse setzen
    	var_paint.setTextSize(5);			
		// Haus 1
    	// ----------------------------------------
    	// ----------------------------------------        
		// Raum 41
		canvas.drawRect(factor*0,factor*102,factor*43,factor*76,var_paint);
		// Raum 10
		canvas.drawRect(factor*0,factor*158,factor*43,factor*174,var_paint);
		// Raum 09	
		canvas.drawRect(factor*0,factor*174,factor*43,factor*206,var_paint);
		canvas.drawText("9",factor*10 ,factor*185,var_paint);
		// Raum 08
		canvas.drawRect(factor*0,factor*206,factor*43,factor*240,var_paint);
		canvas.drawText("8",factor*10 ,factor*217,var_paint);
		// Raum 07
		canvas.drawRect(factor*0,factor*240,factor*43,factor*272,var_paint);
		canvas.drawText("7",factor*10 ,factor*251,var_paint);
		// Raum 06/05 RudisResteRampe
		canvas.drawRect(factor*0,factor*272,factor*43,factor*306,var_paint);
		canvas.drawLine(factor*0,factor*289,factor*43,factor*289,var_paint);		
		// Raum WC
		canvas.drawRect(factor*0,factor*306,factor*43,factor*372,var_paint);
		canvas.drawLine(factor*0,factor*339,factor*43,factor*339,var_paint);
		// Raum 12
		canvas.drawRect(factor*66,factor*223,factor*134,factor*372,var_paint);
		canvas.drawText("12",factor*76,factor*300,var_paint);		
		// Raum 13
		canvas.drawRect(factor*66,factor*223,factor*134,factor*77,var_paint);
		canvas.drawText("13",factor*76,factor*150,var_paint);
		// Haus 2
	   	// ----------------------------------------
	   	// ----------------------------------------        
		// Raum 01
		canvas.drawRect(factor*134,factor*432,factor*238,factor*372,var_paint);
		canvas.drawText("1",factor*180,factor*400,var_paint);
		// Raum 02
		canvas.drawRect(factor*238,factor*432,factor*270,factor*372,var_paint);
		canvas.drawText("2",factor*248,factor*400,var_paint);
		// Raum 3
		canvas.drawRect(factor*270,factor*406,factor*310,factor*372,var_paint);
		canvas.drawText("3",factor*290,factor*389,var_paint);
		// Raum 4
		canvas.drawRect(factor*270,factor*432,factor*310,factor*406,var_paint);
		canvas.drawText("4",factor*290,factor*419,var_paint);		
		// Raum 5
		canvas.drawRect(factor*310,factor*432,factor*410,factor*372,var_paint);
		canvas.drawText("5",factor*360,factor*400,var_paint);
		// Haus 3
	   	// ----------------------------------------
	   	// ----------------------------------------        
		// Raum 1
		canvas.drawRect(factor*410,factor*334,factor*478,factor*392,var_paint);
		canvas.drawText("1",factor*420,factor*360,var_paint);
		// Raum 2
		canvas.drawRect(factor*478,factor*334,factor*580,factor*392,var_paint);
		canvas.drawText("2",factor*488,factor*360,var_paint);
		// Raum 8
		canvas.drawRect(factor*604,factor*334,factor*638,factor*392,var_paint);
		canvas.drawText("8",factor*614,factor*360,var_paint);
		// Raum 9
		canvas.drawRect(factor*638,factor*334,factor*672,factor*392,var_paint);
		canvas.drawText("9",factor*648,factor*360,var_paint);
		// Raum 10
		canvas.drawRect(factor*672,factor*334,factor*704,factor*392,var_paint);
		canvas.drawText("10",factor*682,factor*360,var_paint);
		// Raum 11
		canvas.drawRect(factor*704,factor*334,factor*772,factor*392,var_paint);
		canvas.drawText("11",factor*714,factor*360,var_paint);
		// Raum 12
		canvas.drawRect(factor*772,factor*334,factor*804,factor*392,var_paint);
		canvas.drawText("12",factor*782,factor*360,var_paint);
		// Raum 13
		canvas.drawRect(factor*804,factor*334,factor*872,factor*392,var_paint);
		canvas.drawText("13",factor*814,factor*360,var_paint);
		// Raum 14
		canvas.drawRect(factor*872,factor*334,factor*906,factor*392,var_paint);
		canvas.drawText("14",factor*882,factor*360,var_paint);
		// Raum 15
		canvas.drawRect(factor*906,factor*334,factor*970,factor*392,var_paint);
		canvas.drawText("15",factor*916,factor*360,var_paint);
		// Raum 16
		canvas.drawRect(factor*970,factor*334,factor*998,factor*392,var_paint);
		canvas.drawText("16",factor*980,factor*360,var_paint);
		// Raum 17
		canvas.drawRect(factor*998,factor*334,factor*1134,factor*392,var_paint);
		canvas.drawText("17",factor*1008,factor*360,var_paint);
		// Raum 18
		canvas.drawRect(factor*1134,factor*334,factor*1170,factor*392,var_paint);		
		canvas.drawText("18",factor*1144,factor*360,var_paint);
		// Raum 19
		canvas.drawRect(factor*1170,factor*334,factor*1204,factor*392,var_paint);		
		canvas.drawText("19",factor*1180,factor*360,var_paint);
		// Raum 20
		canvas.drawRect(factor*1204,factor*334,factor*1238,factor*392,var_paint);		
		canvas.drawText("20",1214,360,var_paint);
		// Raum 21
		canvas.drawRect(factor*1238,factor*334,factor*1270,factor*392,var_paint);		
		canvas.drawText("21",factor*1248,factor*360,var_paint);
		// Raum 22
		canvas.drawRect(factor*1238,factor*424,factor*1270,factor*490,var_paint);		
		canvas.drawText("22",factor*1248,factor*460,var_paint);		
		// Raum 23
		canvas.drawRect(factor*1238,factor*424,factor*1204,factor*490,var_paint);		
		canvas.drawText("23",factor*1214,factor*460,var_paint);
		// WC
		canvas.drawRect(factor*1204,factor*414,factor*1170,factor*490,var_paint);		
		// Raum 30
		canvas.drawRect(factor*1128,factor*424,factor*1064,factor*490,var_paint);		
		canvas.drawText("30",factor*1074,factor*460,var_paint);
		// Raum 31
		canvas.drawRect(factor*1064,factor*424,factor*1028,factor*490,var_paint);		
		canvas.drawText("31",factor*1038,factor*460,var_paint);
		// Raum 32
		canvas.drawRect(factor*1028,factor*424,factor*998,factor*490,var_paint);		
		canvas.drawText("32",factor*1008,factor*460,var_paint);
		// Raum 33
		canvas.drawRect(factor*998,factor*424,factor*936,factor*490,var_paint);		
		canvas.drawText("33",factor*946,factor*460,var_paint);
		// Raum 34/35/36 RudisResteRampe
		canvas.drawRect(factor*936,factor*414,factor*896,factor*490,var_paint);
		// Raum 37
		canvas.drawRect(factor*872,factor*424,factor*772,factor*490,var_paint);		
		canvas.drawText("37",factor*782,factor*460,var_paint);
		// Raum 38
		canvas.drawRect(factor*772,factor*424,factor*740,factor*490,var_paint);		
		canvas.drawText("38",factor*750,factor*460,var_paint);	
		// Raum 39
		canvas.drawRect(factor*740,factor*424,factor*704,factor*490,var_paint);		
		canvas.drawText("39",factor*714,factor*460,var_paint);		
		// Raum 40
		canvas.drawRect(factor*704,factor*424,factor*672,factor*490,var_paint);		
		canvas.drawText("40",factor*692,factor*460,var_paint);
		// Raum 41
		canvas.drawRect(factor*638,factor*424,factor*672,factor*490,var_paint);
		canvas.drawText("41",factor*648,factor*460,var_paint);
		// Raum 42
		canvas.drawRect(factor*604,factor*424,factor*638,factor*490,var_paint);
		canvas.drawText("42",factor*614,factor*460,var_paint);
		// Raum 43
		canvas.drawRect(factor*604,factor*424,factor*574,factor*490,var_paint);		
		canvas.drawText("43",factor*584,factor*460,var_paint);
		// Raum 44
		canvas.drawRect(factor*574,factor*424,factor*540,factor*490,var_paint);		
		canvas.drawText("44",factor*550,factor*460,var_paint);			
		// Raum 45
		canvas.drawRect(factor*540,factor*424,factor*510,factor*490,var_paint);		
		canvas.drawText("45",factor*520,factor*460,var_paint);	
		// Raum 43
		canvas.drawRect(factor*510,factor*424,factor*478,factor*490,var_paint);		
		canvas.drawText("46",factor*488,factor*460,var_paint);		
		// Raum 47
		canvas.drawRect(factor*478,factor*424,factor*450,factor*490,var_paint);		
		canvas.drawText("47",factor*460,factor*460,var_paint);	
		// Sporthalle
		// Raum 3/4/5/6/7/48
		canvas.drawRect(factor*580,factor*334,factor*510,factor*264,var_paint);		
		// Treppenhaus
		canvas.drawRect(factor*640,factor*334,factor*580,factor*264,var_paint);		
		// Cleaner
	    // ----------------------------------------
	    // ----------------------------------------        
		var_paint.setColor(Color.WHITE);
		var_paint.setStrokeWidth(2);		
		canvas.drawLine(factor*134, factor*433, factor*134, factor*468, var_paint);
		canvas.drawLine(factor*581, factor*334, factor*603, factor*334, var_paint);
		var_paint.setStrokeWidth(0);		
		var_paint.setColor(Color.BLACK);
	   	// Nachbereitung
	   	postprocessing(canvas);
}

	// Zeichnet Haus 123 Etage 3
	private void draw_floor_12(Canvas canvas){
    	// Schablone Haus 123
    	shape_house123(canvas);
    	// Vorbereitung
        preparation(canvas);
    	// Textgroesse setzen
		var_paint.setTextSize(5);			
		// Haus 1
	    // ----------------------------------------
	    // ----------------------------------------        
		// Raum 41
		canvas.drawRect(factor*0,factor*102,factor*43,factor*76,var_paint);
		// Raum 10
		canvas.drawRect(factor*0,factor*158,factor*43,factor*174,var_paint);
		// Raum 09	
		canvas.drawRect(factor*0,factor*174,factor*43,factor*206,var_paint);
		canvas.drawText("9",factor*10 ,factor*185,var_paint);
		// Raum 08
		canvas.drawRect(factor*0,factor*206,factor*43,factor*240,var_paint);
		canvas.drawText("8",factor*10 ,factor*217,var_paint);
		// Raum 07
		canvas.drawRect(factor*0,factor*240,factor*43,factor*272,var_paint);
		canvas.drawText("7",factor*10 ,factor*251,var_paint);
		// Raum 06/05 RudisResteRampe
		canvas.drawRect(factor*0,factor*272,factor*43,factor*306,var_paint);
		canvas.drawLine(factor*0,factor*289,factor*43,factor*289,var_paint);		
		// Raum WC
		canvas.drawRect(factor*0,factor*306,factor*43,factor*372,var_paint);
		canvas.drawLine(factor*0,factor*339,factor*43,factor*339,var_paint);
		// Raum 14
		canvas.drawRect(factor*66,factor*272,factor*134,factor*372,var_paint);
		canvas.drawText("14",factor*76 ,factor*350,var_paint);
		// Raum 13
		canvas.drawRect(factor*66,factor*272,factor*134,factor*158,var_paint);
		canvas.drawText("13",factor*76 ,factor*170,var_paint);
		// Raum 12
		canvas.drawRect(factor*66,factor*77,factor*134,factor*158,var_paint);
		canvas.drawText("12",factor*77 ,factor*120,var_paint);
		// Raum 11
		canvas.drawLine(factor*66,factor*77,factor*42,factor*77,var_paint);
		canvas.drawText("11",factor*76,factor*30,var_paint);	
		// Haus 2
	    // ----------------------------------------
	   	// ----------------------------------------        
		// Raum 01
		canvas.drawRect(factor*134,factor*432,factor*238,factor*372,var_paint);
		canvas.drawText("1",factor*180,factor*400,var_paint);
		// Raum 02
		canvas.drawRect(factor*238,factor*432,factor*270,factor*372,var_paint);
		canvas.drawText("2",factor*248,factor*400,var_paint);
		// Raum 03
		canvas.drawRect(factor*270,factor*432,factor*374,factor*372,var_paint);
		canvas.drawText("3",factor*320,factor*400,var_paint);
		// Raum 04
		canvas.drawRect(factor*374,factor*432,factor*410,factor*372,var_paint);
		canvas.drawText("4",factor*395,factor*400,var_paint);		
		// Cleaner
	    // ----------------------------------------
	    // ----------------------------------------        
		var_paint.setColor(Color.WHITE);
		var_paint.setStrokeWidth(2);		
		canvas.drawLine(factor*134,factor*433, factor*134, factor*468, var_paint);
		canvas.drawLine(factor*581,factor*334, factor*603, factor*334, var_paint);
		var_paint.setStrokeWidth(0);		
		var_paint.setColor(Color.BLACK);
		// Haus 3
	    // ----------------------------------------
	    // ----------------------------------------        
		// Raum 1
		canvas.drawRect(factor*410,factor*334,factor*518,factor*420,var_paint);
		canvas.drawText("HS-1",factor*460,factor*360,var_paint);
		// Raum 2
		canvas.drawRect(factor*518,factor*334,factor*564,factor*392,var_paint);
		canvas.drawText("2",factor*530,factor*360,var_paint);
		// Raum 10		
		canvas.drawRect(factor*603,factor*334,factor*804,factor*392,var_paint);
		canvas.drawText("10",factor*704,factor*360,var_paint);
		// Raum 11
		canvas.drawRect(factor*804,factor*334,factor*838,factor*392,var_paint);
		canvas.drawText("11",factor*816,factor*360,var_paint);
		// Raum 12
		canvas.drawRect(factor*838,factor*334,factor*872,factor*392,var_paint);
		canvas.drawText("12",factor*856,factor*360,var_paint);
		// Raum 13
		canvas.drawRect(factor*872,factor*334,factor*904,factor*392,var_paint);
		canvas.drawText("13",factor*888,factor*360,var_paint);
		// Raum 14
		canvas.drawRect(factor*904,factor*334,factor*936,factor*392,var_paint);
		canvas.drawText("14",factor*920,factor*360,var_paint);
		// Raum 15
		canvas.drawRect(factor*936,factor*334,factor*970,factor*392,var_paint);
		canvas.drawText("15",factor*950,factor*360,var_paint);
		// Raum 16
		canvas.drawRect(factor*970,factor*334,factor*998,factor*392,var_paint);
		canvas.drawText("16",factor*980,factor*360,var_paint);
		// Raum 17
		canvas.drawRect(factor*998,factor*334,factor*1030,factor*392,var_paint);
		canvas.drawText("17",factor*1014,factor*360,var_paint);
		// Raum 18
		canvas.drawRect(factor*1030,factor*334,factor*1062,factor*392,var_paint);
		canvas.drawText("18",factor*1045,factor*360,var_paint);
		// Raum 19
		canvas.drawRect(factor*1062,factor*334,factor*1094,factor*392,var_paint);
		canvas.drawText("19",factor*1076,factor*360,var_paint);		
		// Raum 20
		canvas.drawRect(factor*1094,factor*334,factor*1134,factor*392,var_paint);
		canvas.drawText("20",factor*1114,factor*360,var_paint);		
		// Raum 21
		canvas.drawRect(factor*1134,factor*334,factor*1170,factor*392,var_paint);		
		canvas.drawText("21",factor*1144,factor*360,var_paint);
		// Raum 22
		canvas.drawRect(factor*1170,factor*334,factor*1204,factor*392,var_paint);		
		canvas.drawText("22",factor*1180,factor*360,var_paint);
		// Raum 23
		canvas.drawRect(factor*1204,factor*334,factor*1238,factor*392,var_paint);		
		canvas.drawText("23",factor*1214,factor*360,var_paint);
		// Raum 24
		canvas.drawRect(factor*1238,factor*334,factor*1270,factor*392,var_paint);		
		canvas.drawText("24",factor*1248,factor*360,var_paint);
		// Raum 25
		canvas.drawRect(factor*1238,factor*424,factor*1270,factor*490,var_paint);		
		canvas.drawText("25",factor*1248,factor*460,var_paint);		
		// Raum 26
		canvas.drawRect(factor*1238,factor*424,factor*1204,factor*490,var_paint);		
		canvas.drawText("26",factor*1214,factor*460,var_paint);
		// WC
		canvas.drawRect(factor*1204,factor*414,factor*1170,factor*490,var_paint);	
		// Raum 33
		canvas.drawRect(factor*1128,factor*424,factor*970,factor*490,var_paint);		
		canvas.drawText("33",factor*1074,factor*460,var_paint);		
		// Raum 34
		canvas.drawRect(factor*936,factor*424,factor*970,factor*490,var_paint);
		canvas.drawText("34",factor*950,factor*460,var_paint);
		// Raum 35/36/37 RudisResteRampe
		canvas.drawRect(factor*936,factor*424,factor*896,factor*490,var_paint);
		// Raum 38
		canvas.drawRect(factor*838,factor*424,factor*872,factor*490,var_paint);
		canvas.drawText("38",factor*855,factor*460,var_paint);		
		// Raum 39
		canvas.drawRect(factor*804,factor*424,factor*838,factor*490,var_paint);
		canvas.drawText("39",factor*821,factor*460,var_paint);
		// Raum 40
		canvas.drawRect(factor*772,factor*424,factor*838,factor*490,var_paint);		
		canvas.drawText("40",factor*780,factor*460,var_paint);	
		// Raum 41
		canvas.drawRect(factor*772,factor*424,factor*740,factor*490,var_paint);		
		canvas.drawText("41",factor*750,factor*460,var_paint);	
		// Raum 42
		canvas.drawRect(factor*740,factor*424,factor*540,factor*490,var_paint);		
		canvas.drawText("42",factor*640,factor*460,var_paint);			
		// Sporthalle
		// Raum 3/4/5/6/7/48
		canvas.drawRect(factor*580,factor*334,factor*510,factor*264,var_paint);		
		// Treppenhaus
		canvas.drawRect(factor*640,factor*334,factor*580,factor*264,var_paint);		
    	// Nachbereitung
    	postprocessing(canvas);
}

	// Zeichnet Haus 123 Etage 4

	private void draw_floor_13(Canvas canvas){
    	// TODO zeichnen
	}

	//---------------------------------------------------------------------	
	//---------------------------------------------------------------------	
}
