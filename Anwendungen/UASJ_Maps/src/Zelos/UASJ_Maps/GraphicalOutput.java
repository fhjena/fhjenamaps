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


/* mainly responsible: Nils Fabian, Eric Lagner */ 

package Zelos.UASJ_Maps;

import java.util.ArrayList;
import android.content.Context;
import android.graphics.*;
import android.graphics.Paint.Style;
import android.view.View;

public class GraphicalOutput extends View {
	private Paint var_paint;
	private ArrayList<ArrayList<Node>> route;		
	private Node position;
	private float degree = 0;
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
	private int dotSize = 4;			// Größe der Markierung

	// Konstruktor
	public GraphicalOutput(Context c_txt) {
		super(c_txt);
		var_paint = new Paint();
	}

	/** Wird zur Zeit der Campus angezeigt?
	 * @return true, wenn Campus angezeigt wird; ansonsten false
	 */
	public boolean isStateCampus(){
		if(0 == current_floor)
			return true;
		else return false;
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

		switch(floorID){				// gibt Bezeichnung der Ebene zurück
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
		dotSize = 2;
	}
	
	/**
	 * Setzt Status auf Positionsanzeige
	 * @param n : Knoten, der angezeigt werden soll
	 */
	public void set_state_location(Node n){
		state = 1;						// Status auf Positionsanzeige setzen
		position = n;
		current_floor = (short) position.getFloorID(); // EbenenID für Postion holen
		dotSize = 4;
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
				list.add(position);						// nur einen Knoten in die anzuzeigende Liste einfügen
				draw_route_or_position(canvas, list);	// Knoten in Liste anzeigen auf canvas
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
			if(route.get(i).get(0).getFloorID() == floorID)			// Wenn Ebene enthalten, dann
				return i;											// Index zurückliefern
		}
		return -1;													// Wenn Ebene nicht enthalten, dann -1 zurückgeben
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
			
		var_paint.setColor(Color.BLUE); // Farbe setzen
		var_paint.setStrokeWidth(2);
		var_paint.setStyle(Style.FILL);
		c.drawCircle(list.get(0).getX()-x_offset, list.get(0).getY()-y_offset, dotSize, var_paint); // Startpunkt setzen
		var_paint.setStyle(Style.STROKE);		
		
		var_paint.setColor(Color.BLUE); // Farbe setzen
		for (int i = 0; i < list.size() - 1; i++) {
			c.drawLine(list.get(i).getX()-x_offset,list.get(i).getY()-y_offset,				// Startpunkt der Linie
					list.get(i + 1).getX()-x_offset, list.get(i + 1).getY()-y_offset, var_paint); // Zielpunkt der Linie
		}
		var_paint.setColor(Color.BLUE); // Farbe setzen
		var_paint.setStyle(Style.FILL);		
		c.drawCircle(list.get(list.size()-1).getX()-x_offset, list.get(list.size()-1).getY()-y_offset, dotSize, var_paint); // Zielpunkt setzen
		var_paint.setStyle(Style.STROKE);
	}
	
	/**
	 * Zeigt die jeweilige Ebene an
	 * @param floorID
	 */
	private void draw_floor(int floorID, Canvas c){
		switch(floorID){
		case 0:
			draw_floor_00(c);	// campus
			break;
		case 1:
			draw_floor_01(c);	// Ebene 05.-2.x
			break;
		case 2:
			draw_floor_02(c);	// Ebene 05.-1.x
			break;
		case 3:
			draw_floor_03(c); 	// Ebene 05.00.x
			break;
		case 4:
			draw_floor_04(c);	// Ebene 05.01.x
			break;
		case 5:
			draw_floor_05(c);	// Ebene 05.02.x
			break;
		case 6:
			draw_floor_06(c);	// Ebene 05.03.x
			break;
		case 7:
			draw_floor_07(c);	// Ebene 05.3Z.x
			break;
		case 8:
			draw_floor_08(c);	// Ebene 01/02/03.-1.x
			break;
		case 9:
			draw_floor_09(c);	// Ebene 01/02/03.00.x
			break;
		case 10:
			draw_floor_10(c);	// Ebene 01/02/03.01.x
			break;
		case 11:
			draw_floor_11(c);	// Ebene 01/02/03.02.x
			break;
		case 12:
			draw_floor_12(c);	// Ebene 01/02/03.03.x
			break;
		case 13:
			draw_floor_13(c);	// Ebene 01/02/03.04.x
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
		// Textgroesse
		var_paint.setTextSize(5);			
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
		// Textgroesse
		var_paint.setTextSize(5);			
    }
    	
    	// Rahmen fuer Haus 5
		private void shape_house5(Canvas canvas){
		// Vorbereitung
        preparation(canvas);
		// Treppe
		// ----------------------------------------------
		// ----------------------------------------------
		// Dicke der Umrandung = 2
		var_paint.setStrokeWidth(2);
		// Farbe (Braun) definieren
		var_paint.setColor(Color.rgb(175, 112, 48));				
 
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
		canvas.drawLine(0*factor,0*factor,0*factor,372*factor,var_paint);
		canvas.drawLine(0*factor,372*factor,50*factor,372*factor,var_paint);
		canvas.drawLine(134*factor,372*factor,134*factor,12*factor,var_paint);
		canvas.drawLine(134*factor,12*factor,42*factor,12*factor,var_paint);
		canvas.drawLine(42*factor,76*factor,42*factor,0*factor,var_paint);	
		canvas.drawLine(0*factor,0*factor,42*factor,0*factor,var_paint);
		canvas.drawLine(0*factor,76*factor,42*factor,76*factor,var_paint);
		canvas.drawLine(66*factor,372*factor,134*factor,372*factor,var_paint);		
		// Haus 2
		var_paint.setColor(Color.BLACK);
		canvas.drawRect(410*factor, 470*factor, 134*factor, 372*factor, var_paint);
		// Schräge
		canvas.drawLine(factor*134,factor*470,factor*392,factor*545, var_paint);
		// Schraege
		canvas.drawLine(factor*392,factor*549,factor*404,factor*503, var_paint);
		canvas.drawLine(factor*404,factor*503,factor*338,factor*485, var_paint);
		canvas.drawLine(factor*338,factor*470,factor*338,factor*485, var_paint);
		// Haus 3
		canvas.drawRect(factor*410,factor*490, factor*1270,factor*334, var_paint);
		canvas.drawRect(factor*640,factor*334,factor*510,factor*39, var_paint);
		// Turm_1 an Haus 3
		canvas.drawRect(factor*1100,factor*490, factor*1170,factor*560, var_paint);
		// Turm_2 an Haus 3		
		canvas.drawRect(factor*600,factor*490, factor*670,factor*560, var_paint);
       //Nachbereitung
        postprocessing(canvas);   	
    }
    
		//---------------------------------------------------------------------
		//---------------------------------------------------------------------    
    	// Zeichnet Campus
		private void draw_floor_00(Canvas canvas){ 		
		// Vorbereitung
		preparation(canvas);
		// Dicke der Umrandung = 2
		var_paint.setStrokeWidth(2);
		// Haus 5
		// ----------------------------------------
		// ----------------------------------------		
		var_paint.setColor(Color.rgb(0, 151, 143));			
		var_paint.setStyle(Paint.Style.FILL);
		canvas.drawRect(0, 0, 50, 50, var_paint);
		canvas.drawRect(20, 50, 40, 75, var_paint);
		canvas.drawRect(-6, 75, 40, 155, var_paint);
		canvas.drawRect(40, 95, 460, 155, var_paint);
		canvas.drawRect(200, 95, 245, 75, var_paint);		
		canvas.drawRect(245, 0, 175, 75, var_paint);
		canvas.drawRect(380, 95, 405, 55, var_paint);
		canvas.drawRect(380, 55, 425, 25, var_paint);
		// Zusatzhaus
		canvas.drawRect(450,95,405,55,var_paint);
		// Straße		
		// ----------------------------------------
		// ----------------------------------------	
		var_paint.setStyle(Paint.Style.STROKE);
		var_paint.setColor(Color.BLACK);
		var_paint.setStrokeWidth(0);		
		canvas.drawLine(-100, 180, 600, 180, var_paint);
		canvas.drawLine(-100, 220, 600, 220, var_paint);
		// Streifen zeichen
		canvas.drawLine(-80, 200, -50, 200, var_paint);		
		canvas.drawLine(-20, 200, 10, 200, var_paint);
		canvas.drawLine(40, 200, 70, 200, var_paint);		
		canvas.drawLine(100, 200, 130, 200, var_paint);
		canvas.drawLine(160, 200, 190, 200, var_paint);	
		canvas.drawLine(220, 200, 250, 200, var_paint);
		canvas.drawLine(280, 200, 310, 200, var_paint);
		canvas.drawLine(340, 200, 370, 200, var_paint);
		canvas.drawLine(410, 200, 440, 200, var_paint);
		canvas.drawLine(470, 200, 500, 200, var_paint);
		canvas.drawLine(530, 200, 560, 200, var_paint);
		var_paint.setStrokeWidth(0);		
		var_paint.setTextSize(30);
		canvas.drawText(" > Carl Zeiss", 600, 210,var_paint);
		canvas.drawText("Center <", -220, 210,var_paint);		
		// Skywalk fuellen
		var_paint.setColor(Color.WHITE);	
		var_paint.setStyle(Style.FILL);
		canvas.drawRect(210, 170, 225, 220, var_paint);
		var_paint.setColor(Color.BLACK);	
		var_paint.setStyle(Style.STROKE);
		// Haus5 - Skywalk
		// ----------------------------------------
		// ----------------------------------------			
		var_paint.setColor(Color.rgb(0, 151, 143));	
		var_paint.setStyle(Style.FILL);		
		canvas.drawRect(210, 155, 225, 255, var_paint);
		// Haus  1
		// ----------------------------------------
		// ----------------------------------------		
		var_paint.setColor(Color.rgb(0, 151, 143));	
		var_paint.setStyle(Paint.Style.FILL);
		canvas.drawRect(210,255,255,380,var_paint);	
		var_paint.setStyle(Paint.Style.STROKE);
		var_paint.setColor(Color.BLACK);
		// Haus  2
		// ----------------------------------------
		// ----------------------------------------
		var_paint.setStyle(Paint.Style.FILL);		
		var_paint.setColor(Color.rgb(0, 151, 143));			
		canvas.drawRect(350,380,225,415,var_paint);	
		// Haus  3
		// ----------------------------------------
		// ----------------------------------------
		canvas.drawRect(350,370,650,425,var_paint);	
		canvas.drawRect(385,370,430,345,var_paint);		
		canvas.drawRect(385,345,445,265,var_paint);		
		// Hausausgaenge Quader
		canvas.drawRect(625,425,605,450,var_paint);			
		canvas.drawRect(510,425,530,450,var_paint);	
		// Haus  4
		// ----------------------------------------
		// ----------------------------------------
		canvas.drawRect(330,550,775,715,var_paint);		
		var_paint.setTextSize(22);
		// Dicke der Umrandung = 0
		var_paint.setStrokeWidth(0);
		var_paint.setColor(Color.WHITE);
		canvas.drawText("H 5",250,130,var_paint);		
		canvas.drawText("H 4",520,650,var_paint);
		canvas.drawText("H 3",450,405,var_paint);
		canvas.drawText("H 2",270,405,var_paint);
		canvas.drawText("H 1",220,300,var_paint);
		var_paint.setColor(Color.RED);
		var_paint.setStrokeWidth(2);
		// Human I/O
		// ----------------------------------------
		// ----------------------------------------
		// Haus 5 I/O Nähe EMV Labor
		canvas.drawLine(440, 55, 430, 55, var_paint);
		// Haus 1 I/O
		//canvas.drawLine(230, 255, 245, 255, var_paint);
		// Haus 2 I/O
		canvas.drawLine(290, 380, 315, 380, var_paint);
		// Haus 3 I/O 
		canvas.drawLine(625, 450, 605, 450, var_paint);
		// Haus 3 I/O
		canvas.drawLine(530, 450, 510, 450, var_paint);
		// Haus 3 Sporthalle
		canvas.drawLine(430, 350, 430, 365, var_paint);
		canvas.drawLine(385, 350, 385, 365, var_paint);
		// Haus 3 Post
		canvas.drawLine(400, 265, 415, 265, var_paint);
		// Haus 5 I/O
		canvas.drawLine(20, 55, 20, 65, var_paint);
		canvas.drawLine(40, 55, 40, 65, var_paint);
		// Treppe Mitte I/O
		canvas.drawLine(245, 55, 245, 65, var_paint);
		// Skywalk
		canvas.drawLine(210, 155, 225, 155, var_paint);
		canvas.drawLine(210, 255, 225, 255, var_paint);
		// EMV
		canvas.drawLine(460, 140, 460, 125, var_paint);
		// Haus 2
		canvas.drawLine(225, 390, 225, 405, var_paint);
		// Haus 2
		canvas.drawLine(260, 415, 275, 415, var_paint);
		// Nachbereitung
		postprocessing(canvas);
	}

		// Zeichnet Haus 5 Etage -2
		private void draw_floor_01(Canvas canvas){
			// Farbe
			// ----------------------------------------------
			// ----------------------------------------------
			var_paint.setColor(Color.rgb(0, 151, 143));		
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
			// Technikhaus fuellen
			canvas.drawRect(-6,74,18,94,var_paint);
			// Zusatzhaus fuellen
			canvas.drawRect(404, 54, 462, 94,var_paint);
			// Schablone Haus 5
			shape_house5(canvas);
			var_paint.setStrokeWidth(2);
			// Technikhaus
			canvas.drawRect(-6,74,18,152,var_paint);
			var_paint.setStrokeWidth(0);
			// Ueberzeichnet das Shape
			// ----------------------------------------------
			// ----------------------------------------------
			var_paint.setColor(Color.rgb(0, 151, 143));		
			var_paint.setStrokeWidth(2);
			// Element_2_Cleaner
			canvas.drawLine(19,94,30,94,var_paint);
			// Element_4_Cleaner
			canvas.drawLine(201,94,221,94,var_paint);
			// Element_5_Cleaner
			canvas.drawLine(392,94,403,94,var_paint);
			// Technikhaus Cleaner
			canvas.drawLine(-5,94,17,94,var_paint);
			// Stura Cleaner
			canvas.drawLine(18,151,18,75,var_paint);
			//ZusathausDing
			canvas.drawRect(404,54,462,94,var_paint);			
			var_paint.setColor(Color.BLACK);
			var_paint.setStrokeWidth(2);
			//ZusathausDing
			canvas.drawRect(404,54,462,94,var_paint);
			// Vorbereitung
			preparation(canvas);
			canvas.drawText("21",410, 80, var_paint);
			canvas.drawText("22",440, 80, var_paint);			
			// RudisResteRampe
			canvas.drawRect(392,94,370,106,var_paint);
			// WC
			canvas.drawRect(392,106,370,116,var_paint);
			// Raum 38
			canvas.drawRect(370,116,358,94,var_paint);
			canvas.drawText("38",360, 100, var_paint);
			// Raum 93
			canvas.drawRect(358,116,347,94,var_paint);
			canvas.drawText("93",350, 100, var_paint);
			// Raum 92
			canvas.drawRect(347,116,336,94,var_paint);
			canvas.drawText("92",340, 100, var_paint);			
			// Raum 91
			canvas.drawRect(336,116,325,94,var_paint);
			canvas.drawText("91",327, 100, var_paint);
			// Raum 90
			canvas.drawRect(325,116,314,94,var_paint);
			canvas.drawText("90",317, 100, var_paint);		
			// Raum 91
			canvas.drawRect(314,116,294,94,var_paint);
			canvas.drawText("44",300, 100, var_paint);			
			// RudisResteRampe
			canvas.drawRect(294,116,250,94,var_paint);	
			// Raum 43
			canvas.drawRect(250,124,236,94,var_paint);			
			canvas.drawText("43",340, 100, var_paint);			
			// RudisResteRRampe
			canvas.drawRect(250,152,236,124,var_paint);
			// Archiv
			canvas.drawRect(420,124,250,152,var_paint);
			for(int i=250;i<=420;i=i+3){
				canvas.drawLine(i,124, i, 152, var_paint);
			}
			// Raum 26
			canvas.drawRect(462,138,420,152,var_paint);
			canvas.drawText("26",440, 150, var_paint);			
			// EMV Labor
			canvas.drawRect(462,138,420,106,var_paint);
			canvas.drawText("EMV",440, 130, var_paint);	
			// Raum 23
			canvas.drawRect(462,106,420,94,var_paint);
			canvas.drawText("23",440, 100, var_paint);				
			// RudisResteRampe
			canvas.drawLine(404,64,440,64,var_paint); 
			// RudisResteRampe
			canvas.drawLine(440,64,440,54,var_paint); 
			// Raum 17
			canvas.drawRect(382,24,403,39,var_paint);
			canvas.drawText("17",390, 35, var_paint);
			// Raum 18
			canvas.drawRect(403,24,424,39,var_paint);
			canvas.drawText("18",410, 35, var_paint);
			// Raum 16
			canvas.drawRect(398,39,382,54,var_paint);
			canvas.drawText("16",390, 49, var_paint);
			// Raum 19
			canvas.drawRect(408,39,424,54,var_paint);
			canvas.drawText("19",410, 49, var_paint);
			// Line Raum 21 22
			canvas.drawLine(420,94,420 ,64,var_paint);
			// Zusatz
			var_paint.setColor(Color.rgb(0, 151, 143));
			var_paint.setStrokeWidth(2);
			var_paint.setStyle(Style.FILL);
			canvas.drawLine(403,64,403,55,var_paint);
			canvas.drawLine(404,63,404,55,var_paint);
			var_paint.setColor(Color.RED);
			canvas.drawRect(426, 55, 438, 53, var_paint);
			// Nachbereitung
			postprocessing(canvas);
	}
	
		// Zeichnet Haus 5 Etage -1	
		private void draw_floor_02(Canvas canvas){
			// Farbe
			// ----------------------------------------------
			// ----------------------------------------------
			var_paint.setColor(Color.rgb(0, 151, 143));		
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
			// Technikhaus fuellen
			canvas.drawRect(-6,74,18,94,var_paint);
			// ZusatzdingsHaus
			canvas.drawRect(404,94,462,54,var_paint);
			// Schablone Haus 5
			shape_house5(canvas);
			var_paint.setStrokeWidth(2);
			// Technikhaus
			canvas.drawRect(-6,74,18,152,var_paint);
			// ZusatzDingsHaus
			canvas.drawRect(404,94,462,54,var_paint);
			var_paint.setStrokeWidth(0);
			// Ueberzeichnet das Shape
			// ----------------------------------------------
			// ----------------------------------------------
			var_paint.setColor(Color.rgb(0, 151, 143));		
			var_paint.setStrokeWidth(2);
			// Element_2_Cleaner
			canvas.drawLine(19,94,30,94,var_paint);
			// Element_4_Cleaner
			canvas.drawLine(201,94,221,94,var_paint);
			// Element_5_Cleaner
			canvas.drawLine(392,94,403,94,var_paint);
			// Technikhaus Cleaner
			canvas.drawLine(-5,94,17,94,var_paint);
			// Stura Cleaner
			canvas.drawLine(18,151,18,75,var_paint);		
			// Vorbereitung
			preparation(canvas);
			// Technikhaus
			canvas.drawRect(-6,74,18,152,var_paint);
			// WC
			canvas.drawRect(392,94,370,106,var_paint);
			// RudisResteRampe
			canvas.drawRect(392,106,370,116,var_paint);
			// Raum 50
			canvas.drawRect(370,116,358,94,var_paint);
			canvas.drawText("50", 360, 104, var_paint);
			// Raum 49
			canvas.drawRect(358,116,347,94,var_paint);
			canvas.drawText("49",350, 104, var_paint);
			// Raum 48
			canvas.drawRect(347,116,336,94,var_paint);
			canvas.drawText("48",339, 104, var_paint);
			// Raum 47
			canvas.drawRect(336,116,313,94,var_paint);
			canvas.drawText("47",320, 104, var_paint);
			// Wand
			canvas.drawLine(336,152,336,126,var_paint);
			// Treppe
			canvas.drawRect(313,116,290,94,var_paint);
			// Luftraum
			canvas.drawRect(290,116,246,94,var_paint);
			// RudisResteRampe
			canvas.drawRect(222,116,234,94,var_paint);
			// Raum Treppe
			canvas.drawRect(234,116,246,94,var_paint);		
			// Wand
			canvas.drawLine(290,152,290,126,var_paint);
			// RudisResteRampe
			canvas.drawRect(222,126,246,152,var_paint);
			// Raum 203
			canvas.drawRect(178,134,222,152,var_paint);
			canvas.drawText("203",190, 145, var_paint);			
			// Raum 204			
			canvas.drawRect(123,134,178,152,var_paint);
			canvas.drawText("204",140, 145, var_paint);	
			// Raum 205
			canvas.drawRect(123,134,111,152,var_paint);
			// Raum 206
			canvas.drawRect(111,134,98,152,var_paint);
			// Raum 207			
			canvas.drawRect(98,134,54,152,var_paint);
			canvas.drawText("207",60, 145, var_paint);	
			// Raum 208
			canvas.drawLine(54, 134, 18, 134,var_paint);
			canvas.drawText("208",30, 145, var_paint);
			// RudisResteRampe		
			canvas.drawRect(200,94,189,110,var_paint);
			// RudisResteRampe
			canvas.drawRect(200,110,189,126,var_paint);
			// Raum 232	
			canvas.drawRect(189,94,156,126,var_paint);
			canvas.drawText("232",160, 110, var_paint);				
			// Raum 231		
			canvas.drawRect(156,94,138,126,var_paint);
			canvas.drawText("231",140, 110, var_paint);	
			// Raum 229		
			canvas.drawRect(134,110,123,126,var_paint);
			canvas.drawLine(133, 126, 139, 126,var_paint);
			// Raum 228		
			canvas.drawRect(123,94,111,126,var_paint);	
			// Raum 226	
			canvas.drawRect(111,94,68,126,var_paint);
			canvas.drawText("226",80, 110, var_paint);				
			// Raumkonstruckt
			canvas.drawRect(68,110,30,126,var_paint);	
			// RudisResteRampe	
			canvas.drawRect(52,94,30,110,var_paint);
			// WC	
			canvas.drawRect(-6,94,18,126,var_paint);
			// Abgrenzung Bibo Fachbereich	
			canvas.drawLine(222, 126, 222, 116,var_paint);
			// Raum 218	
			canvas.drawRect(20,0,0,16,var_paint);
			canvas.drawText("218",5, 10, var_paint);			
			// Raum 217	
			canvas.drawRect(20,16,0,32,var_paint);
			canvas.drawText("217",5, 26, var_paint);	
			// Raum 216		
			canvas.drawRect(20,32,0,48,var_paint);
			canvas.drawText("216",5, 46, var_paint);
			// Raum 219	
			canvas.drawRect(28,0,48,16,var_paint);
			canvas.drawText("219",32, 10, var_paint);	
			// Raum 220	
			canvas.drawRect(48,16,28,32,var_paint);
			canvas.drawText("220",32, 26, var_paint);				
			// Raum 221			
			canvas.drawRect(48,32,28,48,var_paint);
			canvas.drawText("221",32, 46, var_paint);					
			// Nachbereitung
			postprocessing(canvas);
	}

		// Zeichnet Haus 5 Etage 0		
		private void draw_floor_03(Canvas canvas){
		// Farbe
		// ----------------------------------------------
		// ----------------------------------------------
		var_paint.setColor(Color.rgb(0, 151, 143));		
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
		// Technikhaus fuellen
		canvas.drawRect(-6,74,18,94,var_paint);
		// Schablone Haus 5
		shape_house5(canvas);
		var_paint.setStrokeWidth(2);
		// Technikhaus
		canvas.drawRect(-6,74,18,152,var_paint);
		var_paint.setStrokeWidth(0);
		// Ueberzeichnet das Shape
		// ----------------------------------------------
		// ----------------------------------------------
		var_paint.setColor(Color.rgb(0, 151, 143));		
		var_paint.setStrokeWidth(2);
		// Element_2_Cleaner
		canvas.drawLine(19,94,30,94,var_paint);
		// Element_4_Cleaner
		canvas.drawLine(201,94,221,94,var_paint);
		// Element_5_Cleaner
		canvas.drawLine(392,94,403,94,var_paint);
		// Technikhaus Cleaner
		canvas.drawLine(-5,94,17,94,var_paint);
		// Stura Cleaner
		canvas.drawLine(18,151,18,75,var_paint);		
		// Vorbereitung
		preparation(canvas);
		// ----------------------------------------------
		// ----------------------------------------------
		canvas.drawText("9",0,100,var_paint);		
		// Raum 8
		canvas.drawRect(-6,74,18,112,var_paint);
		canvas.drawText("8",0,120,var_paint);		
		// Raum 7
		canvas.drawRect(-6,112,18,128,var_paint);
		canvas.drawRect(-6,128,18,152,var_paint);
		canvas.drawText("7",0,135,var_paint);		
		// Raumkonstruckt
		canvas.drawRect(42,152,177,128,var_paint);
		// Raum 6
		canvas.drawRect(62,152,85,128,var_paint);
		canvas.drawText("6",52,140,var_paint);		
		// Raum 5
		canvas.drawRect(108,152,131,128,var_paint);
		canvas.drawText("5",72,140,var_paint);		
		// Raum 4/3/2/1
		canvas.drawRect(154,152,177,128,var_paint);
		canvas.drawText("4",90,140,var_paint);		
		canvas.drawText("3",115,140,var_paint);
		canvas.drawText("2",135,140,var_paint);		
		canvas.drawText("1",165,140,var_paint);		
		// Raumkonstruckt
		canvas.drawRect(177,94,50,114,var_paint);
		// RudisResteRampe 
		canvas.drawLine(154,94,154,114,var_paint);
		canvas.drawText("29", 160, 104,var_paint);
		canvas.drawText("28", 140, 104,var_paint);		
		// Raum 27
		canvas.drawRect(119,94,131,114,var_paint);
		canvas.drawText("27", 120, 104,var_paint);		
		// Raum 26
		canvas.drawRect(108,94,96,114,var_paint);
		canvas.drawText("26",109, 104,var_paint);		
		// Raum 22/23/24/25
		canvas.drawRect(85,94,73,114,var_paint);
		canvas.drawText("22",55, 104,var_paint);
		canvas.drawText("23",74, 104,var_paint);
		canvas.drawText("24",86, 104,var_paint);		
		canvas.drawText("25",97, 104,var_paint);
		// WC
		canvas.drawRect(50,94,30,108,var_paint);
		// RudisResteRampe
		canvas.drawRect(50,108,40,114,var_paint);
		// Raum 10
		canvas.drawRect(19,0,0,48,var_paint);
		canvas.drawText("10",5,20,var_paint);
		// Raum 11
		canvas.drawRect(48,0,19,26,var_paint);
		canvas.drawText("11",30,10,var_paint);
		// Raum 12
		canvas.drawRect(48,48,30,26,var_paint);
		canvas.drawText("12",35,40,var_paint);
		// WC
		canvas.drawRect(200,114,177,94,var_paint);		
		// WC
		canvas.drawRect(221,114,255,94,var_paint);	
		// Abtrennung zur Bibo
		canvas.drawLine(255,94,255,152, var_paint);	
		//Element_29
		canvas.drawRect(382,54,394,24,var_paint);
		// Raum 64
		canvas.drawRect(404,40,424,54,var_paint);
		canvas.drawText("64",414,50,var_paint);
		// Raum 65
		canvas.drawRect(394,40,424,24,var_paint);	
		canvas.drawText("65",404,35,var_paint);
		canvas.drawText("51",387,35,var_paint);
		var_paint.setTextSize(14);
		canvas.drawText("Bibliothek", 360, 130,var_paint);
		// Ausgang Haus 5
		canvas.drawRect(204,152,204,165,var_paint);
		canvas.drawRect(218,152,218,165,var_paint);		
		// ----------------------------------------------
		// ----------------------------------------------
		// Ausgaenge
		var_paint.setStyle(Style.FILL);
		var_paint.setColor(Color.RED);
		// Ausgang_Left
		canvas.drawRect(17,70,19,60,var_paint);	
		// Ausgang_Mitte
		canvas.drawRect(204,151,218,153,var_paint);
		canvas.drawRect(39,70,41,60,var_paint);		
		var_paint.setColor(Color.BLACK);		
		var_paint.setStyle(Style.STROKE);
		// Nachbereitung
		postprocessing(canvas);
	}
			
		// Zeichnet Haus 5 Etage 1	
		private void draw_floor_04(Canvas canvas){
			// Farbe
			// ----------------------------------------------
			// ----------------------------------------------
			// FB SW
			var_paint.setColor(Color.rgb(247, 61, 41));
			// Style setzten
			var_paint.setStyle(Paint.Style.FILL);
			//Element_01
			canvas.drawRect(0,0,48,48,var_paint);
			//Element_02
			canvas.drawRect(18,48,40,94,var_paint);
			//Element_03
			canvas.drawRect(-6,94,201,152,var_paint);
			// FB BW
			var_paint.setColor(Color.rgb(247, 218, 22));
			//Element_03
			canvas.drawRect(221,94,462,152,var_paint);
			//Element_05
			canvas.drawRect(382,54,404,94,var_paint);
			//Element_06
			canvas.drawRect(382,24,424,54,var_paint);
			// Treppe_Mitte zeichen
			var_paint.setStrokeWidth(2);
			var_paint.setStyle(Paint.Style.STROKE);			
			// Farbe auf braun
			var_paint.setColor(Color.rgb(175, 112, 48));				
			for(int i=144;i<=150;i=i+4){
				canvas.drawLine(206, i, 218, i, var_paint);
			}
			canvas.drawRect(206, 151, 218, 144, var_paint);
			var_paint.setStrokeWidth(0);
			var_paint.setColor(Color.BLACK);
			// Ausgang Haus 5
			canvas.drawRect(204,152,204,165,var_paint);
			canvas.drawRect(218,152,218,165,var_paint);		
			// Schablone Haus 5
			shape_house5(canvas);
			// Ueberzeichnet das Shape
			// ----------------------------------------------
			// ----------------------------------------------
			// FB SW
			var_paint.setColor(Color.rgb(247, 61, 41));
			var_paint.setStrokeWidth(2);		
			// Element_2_Cleaner
			canvas.drawLine(19,94,30,94,var_paint);
			var_paint.setColor(Color.WHITE);
			// Element_4_Cleaner
			canvas.drawLine(201,94,221,94,var_paint);
			// FB BW
			var_paint.setColor(Color.rgb(247, 218, 22));
			// Element_5_Cleaner
			canvas.drawLine(392,94,403,94,var_paint);
			preparation(canvas);
			// Raum 20
			canvas.drawRect(0,0,20,30,var_paint);
			canvas.drawText("20", 5, 15, var_paint);
			// Raum 21
			canvas.drawRect(20,0,48,30,var_paint);		
			canvas.drawText("21", 25, 15, var_paint);		
			// Raum 19
			canvas.drawRect(0,48,20,30,var_paint);	
			canvas.drawText("19", 5, 40, var_paint);	
			// Raum 22
			canvas.drawRect(48,48,30,30,var_paint);
			canvas.drawText("22", 35, 40, var_paint);	
			// Raum 18
			canvas.drawRect(-6,94,18,114,var_paint);
			canvas.drawText("18",9,105, var_paint);	
			// Raum 17		
			canvas.drawRect(-6,94,6,114,var_paint);	
			canvas.drawText("17",-3,105, var_paint);		
			// Raum 16
			canvas.drawRect(-6,124,6,152,var_paint);		
			canvas.drawText("16",-3,135, var_paint);
			// Raum 15
			canvas.drawRect(6,124,18,152,var_paint);
			canvas.drawText("15",9,135, var_paint);	
			// Raum 14
			canvas.drawRect(18,124,42,152,var_paint);		
			canvas.drawText("14",21,135, var_paint);
			// Raum 13
			canvas.drawRect(42,124,51,152,var_paint);
			canvas.drawText("13",42,135, var_paint);		
			// Raum 12
			canvas.drawRect(51,124,62,152,var_paint);
			canvas.drawText("12",51,135, var_paint);		
			// Raum 11
			canvas.drawRect(62,124,73,152,var_paint);	
			canvas.drawText("11",62,135, var_paint);
			// Raum 10
			canvas.drawRect(73,124,84,152,var_paint);
			canvas.drawText("10",73,135, var_paint);			
			// Raum 09
			canvas.drawRect(84,124,95,152,var_paint);
			canvas.drawText("9",87,135, var_paint);		
			// Raum 08
			canvas.drawRect(95,124,106,152,var_paint);
			canvas.drawText("8",98,135, var_paint);			
			// Raum 07
			canvas.drawRect(106,124,117,152,var_paint);
			canvas.drawText("7",109,135, var_paint);			
			// Raum 06
			canvas.drawRect(117,124,128,152,var_paint);		
			canvas.drawText("6",120,135, var_paint);		
			// Raum 05
			canvas.drawRect(128,124,139,152,var_paint);
			canvas.drawText("5",131,135, var_paint);			
			// Raum 04
			canvas.drawRect(139,124,150,152,var_paint);
			canvas.drawText("4",142,135, var_paint);
			// Raum 03
			canvas.drawRect(150,124,161,152,var_paint);
			canvas.drawText("3",153,135, var_paint);		
			// Raum 02
			canvas.drawRect(161,124,172,152,var_paint);
			canvas.drawText("2",164,135, var_paint);		
			// Raum 01
			canvas.drawRect(172,134,200,152,var_paint);
			canvas.drawText("1",180,145, var_paint);		
			// Raum 85
			canvas.drawRect(222,140,244,152,var_paint);
			canvas.drawText("85",230,148, var_paint);		
			// Raum 84
			canvas.drawRect(244,140,255,152,var_paint);
			canvas.drawText("84",245,148, var_paint);
			// Raum 83	
			canvas.drawRect(255,140,266,152,var_paint);		
			canvas.drawText("83",256,148, var_paint);		
			// Raum 82
			canvas.drawRect(266,140,278,152,var_paint);
			canvas.drawText("82",267,148, var_paint);		
			// Raum 81
			canvas.drawRect(278,140,290,152,var_paint);
			canvas.drawText("81",279,148, var_paint);		
			// Raum 80
			canvas.drawRect(290,140,301,152,var_paint);
			canvas.drawText("80",291,148,var_paint);		
			// Raum 79
			canvas.drawRect(301,140,312,152,var_paint);
			canvas.drawText("79",302,148,var_paint);
			// Raum 78
			canvas.drawRect(312,140,323,152,var_paint);
			canvas.drawText("78",313,148,var_paint);
			// Raum 77
			canvas.drawRect(323,140,334,152,var_paint);
			canvas.drawText("77",324,148,var_paint);				
			// Raum 76
			canvas.drawRect(334,140,346,152,var_paint);	
			canvas.drawText("76",335,148,var_paint);
			// Raum 75
			canvas.drawRect(346,140,358,152,var_paint);	
			canvas.drawText("75",347,148,var_paint);
			// Raum 74
			canvas.drawRect(358,140,369,152,var_paint);	
			canvas.drawText("74",359,148,var_paint);
			// Raum 73	
			canvas.drawRect(369,140,380,152,var_paint);	
			canvas.drawText("73",370,148,var_paint);
			// Raum 72	
			canvas.drawRect(380,140,393,152,var_paint);	
			canvas.drawText("73",381,148,var_paint);		
			// Raum 71	
			canvas.drawRect(393,140,404,152,var_paint);	
			canvas.drawText("71",394,148,var_paint);		
			// Raum 70	
			canvas.drawRect(404,140,419,152,var_paint);	
			canvas.drawText("70",405,148,var_paint);		
			// Raum 69		
			canvas.drawRect(419,140,434,152,var_paint);	
			canvas.drawText("69",420,148,var_paint);		
			// Raum 68		
			canvas.drawRect(434,140,443,152,var_paint);	
			canvas.drawText("68",434,148,var_paint);	
			// Raum 67		
			canvas.drawRect(443,140,453,152,var_paint);	
			canvas.drawText("67",443,148,var_paint);		
			// Raum 66		
			canvas.drawRect(453,124,462,152,var_paint);	
			canvas.drawText("66",453,135,var_paint);		
			// Raum 65		
			canvas.drawRect(453,124,462,115,var_paint);	
			canvas.drawText("65",453,122,var_paint);	
			// Raum 64		
			canvas.drawRect(453,115,462,94,var_paint);	
			canvas.drawText("64",453,105,var_paint);	
			// Raum 63		
			canvas.drawRect(443,94,453,110,var_paint);	
			canvas.drawText("63",443,105,var_paint);		
			// Raum 62	
			canvas.drawRect(434,94,443,110,var_paint);	
			canvas.drawText("62",434,105,var_paint);	
			// Raum 61		
			canvas.drawRect(419,94,434,110,var_paint);	
			canvas.drawText("61",420,105,var_paint);		
			// Raum 60	
			canvas.drawRect(404,94,419,110,var_paint);	
			canvas.drawText("60",405,105,var_paint);		
			// Raum 58	
			canvas.drawRect(404,118,434,132,var_paint);
			canvas.drawText("58",420,130,var_paint);			
			// Raum 59	
			canvas.drawRect(434,118,440,132,var_paint);		
			// WC & RudisResteRampe
			canvas.drawRect(392,94,369,122,var_paint);
			// Raum 46	
			canvas.drawRect(369,94,358,122,var_paint);
			canvas.drawText("46",359,110,var_paint);				
			// Raum 45
			canvas.drawRect(358,94,334,122,var_paint);
			canvas.drawText("45",340,110,var_paint);
			// Raum 44
			canvas.drawRect(334,94,312,122,var_paint);
			canvas.drawText("44",318,110,var_paint);
			// Raum 43
			canvas.drawRect(334,94,278,122,var_paint);
			canvas.drawText("43",284,110,var_paint);		
			// Raum 42
			canvas.drawRect(278,94,244,122,var_paint);
			canvas.drawText("42",254,110,var_paint);
			// RudisResteRampe
			canvas.drawRect(244,108,222,122,var_paint);
			// Raum 39
			canvas.drawRect(244,94,222,108,var_paint);
			canvas.drawText("39",233,105,var_paint);	
			// RudisResteRampe
			canvas.drawRect(172,94,200,114,var_paint);
			// Raum 33
			canvas.drawRect(172,94,150,114,var_paint);
			canvas.drawText("33",152,105,var_paint);	
			// Raum 32
			canvas.drawRect(128,94,150,114,var_paint);
			canvas.drawText("32",130,105,var_paint);	
			// Raum 31
			canvas.drawRect(128,94,106,114,var_paint);
			canvas.drawText("31",108,105,var_paint);	
			// Raum 30
			canvas.drawRect(106,94,84,114,var_paint);
			canvas.drawText("30",86,105,var_paint);	
			// Raum 29
			canvas.drawRect(84,94,62,114,var_paint);
			canvas.drawText("29",64,105,var_paint);	
			// Raum 28
			canvas.drawRect(51,94,62,114,var_paint);
			canvas.drawText("28",53,105,var_paint);	
			// Raum 27
			canvas.drawRect(42,114,51,104,var_paint);
			canvas.drawText("27",43,112, var_paint);		
			// RudisResteRampe
			canvas.drawRect(30,104,51,94,var_paint);
			// ----------------------------------------------
			// ----------------------------------------------
			// Ausgaenge
			var_paint.setStyle(Style.FILL);
			var_paint.setColor(Color.RED);
			// Ausgang_Mitte
			canvas.drawRect(204,151,218,153,var_paint);
			// Nachbereitung
			postprocessing(canvas);
		}

		// Zeichnet Haus 5 Etage 2
		private void draw_floor_05(Canvas canvas){
			// Farbe
			// ----------------------------------------------
			// ----------------------------------------------
			var_paint.setColor(Color.rgb(179, 87, 113));
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
			// Schablone Haus 5
			shape_house5(canvas);
			// Ueberzeichnet das Shape
			// ----------------------------------------------
			// ----------------------------------------------
			var_paint.setColor(Color.rgb(179, 87, 113));	
			var_paint.setStrokeWidth(2);
			// Element_2_Cleaner Part1
			canvas.drawLine(19,94,30,94,var_paint);
			// Element_2_Cleaner Part2
			canvas.drawLine(19,48,25,48,var_paint);
			
			// Element_4_Cleaner
			canvas.drawLine(201,94,221,94,var_paint);
			// Element_5_Cleaner
			canvas.drawLine(392,94,403,94,var_paint);
			// Stura Cleaner
			canvas.drawLine(18,151,18,95,var_paint);		
			// Vorbereitung
			preparation(canvas);
			// Raum 15
			canvas.drawRect(0,0,18,48,var_paint);
			canvas.drawText("15",2,24,var_paint);		
			// Raum 16
			canvas.drawText("16",20,24,var_paint);
			canvas.drawLine(25,48,25,40,var_paint);		
			canvas.drawLine(25,40,18,40,var_paint);				
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
			canvas.drawRect(404,94,434,118,var_paint);
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
			// Nachbereitung
			postprocessing(canvas);
		}
	
		// Zeichnet Haus 5 Etage 3
		private void draw_floor_06(Canvas canvas){
			// Farbe
			// ----------------------------------------------
			// ----------------------------------------------
			var_paint.setColor(Color.rgb(0, 151, 143));	
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
			// Schablone Haus 5
			shape_house5(canvas);
			// Ueberzeichnet das Shape
			// ----------------------------------------------
			// ----------------------------------------------
			var_paint.setColor(Color.rgb(0, 151, 143));	
			var_paint.setStrokeWidth(2);
			// Element_2_Cleaner Part1
			canvas.drawLine(19,94,30,94,var_paint);
			// Element_2_Cleaner Part2
			canvas.drawLine(19,48,25,48,var_paint);
			// Element_4_Cleaner
			canvas.drawLine(201,94,221,94,var_paint);
			// Element_5_Cleaner
			canvas.drawLine(392,94,403,94,var_paint);
			// Stura Cleaner
			canvas.drawLine(18,151,18,95,var_paint);
			// Raum 28 / 29 Cleaner
			canvas.drawLine(404, 54, 396, 54, var_paint);
			// Raum 12 Cleaner
			canvas.drawLine(19, 48, 23, 48, var_paint);
			// Vorbereitung
			preparation(canvas);			
			// Raueme
			// ----------------------------------------------
			// ----------------------------------------------
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
			canvas.drawText("10",5,44,var_paint);		
			// Raum 13
			canvas.drawRect(24,40,48,48,var_paint);
			canvas.drawText("13",30,44,var_paint);	
			// Raum 12
			canvas.drawText("12",23,23,var_paint);	
			// Vorraum
			canvas.drawRect(396,54,404,46,var_paint);		
			// Trennwand zwischen 28 & 29
			canvas.drawLine(400, 46, 400, 24, var_paint);
			// Raum 29
			canvas.drawText("29",404,40,var_paint);	
			// Raum 28
			canvas.drawText("28",385,40,var_paint);	
			// Ueberzeichnet das Shape
			// ----------------------------------------------
			// ----------------------------------------------
			var_paint.setColor(Color.rgb(0, 151, 143));	
			var_paint.setStrokeWidth(2);
			// Raum 28 / 29 Cleaner
			canvas.drawLine(403, 54, 397, 54, var_paint);
			// Nachbereitung
		   	postprocessing(canvas);
	}
	
		// Zeichnet Haus 5 Etage 3Z
		private void draw_floor_07(Canvas canvas){
			// Farbe
			// ----------------------------------------------
			// ----------------------------------------------
			var_paint.setColor(Color.rgb(0, 151, 143));	
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
			var_paint.setColor(Color.WHITE);
			// ausfuellen
			canvas.drawRect(-6,95,2,123 ,var_paint);	
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
			// Cleaner
			// ----------------------------------------------
			// ----------------------------------------------
			// Farbe setzen
			var_paint.setColor(Color.WHITE);
			var_paint.setStyle(Paint.Style.FILL);				
			// ausfuellen
			canvas.drawRect(175,95,117,107,var_paint);
			// ausfuellen
			canvas.drawRect(223,95,265,107,var_paint);
			// ausfuellen
			canvas.drawRect(267,95,333,107,var_paint);	
			// ausfuellen
			canvas.drawRect(335,95,373,107 ,var_paint);	
			// ausfuellen
			canvas.drawRect(405,95,435,107 ,var_paint);	
			// Schablone Haus 5
			shape_house5(canvas);
			// Ueberzeichnet das Shape
			// ----------------------------------------------
			// ----------------------------------------------
			var_paint.setColor(Color.rgb(0, 151, 143));	
			var_paint.setStrokeWidth(2);
			// Element_2_Cleaner Part1
			canvas.drawLine(19,94,30,94,var_paint);
			// Element_4_Cleaner
			canvas.drawLine(201,94,221,94,var_paint);
			// Element_5_Cleaner
			canvas.drawLine(392,94,403,94,var_paint);
			// Stura Cleaner
			canvas.drawLine(18,151,18,95,var_paint);
			// Vorbereitung
			preparation(canvas);			
			// Raueme
			// ----------------------------------------------
			// ----------------------------------------------
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
			canvas.drawText("224",447,104,var_paint);
			// Raum 222/223 WC
			canvas.drawRect(446,94,436,114,var_paint);
			// Gelaender
			canvas.drawLine(404,108,436,108,var_paint);
			// Gelaender
			canvas.drawLine(404,94,404,108,var_paint);		
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
			// Nachbereitung
			postprocessing(canvas);
	}
			
		
		
		// Zeichnet Haus 123 Etage Minus 1		
		private void draw_floor_08(Canvas canvas){
			preparation(canvas);
			// Rundung zeichnen
			var_paint.setStyle(Style.FILL);
			var_paint.setColor(Color.WHITE);
			RectF ovalBounds = new RectF(218*factor, 470*factor, 35*factor,270*factor);
			canvas.drawOval(ovalBounds, var_paint);
			var_paint.setStyle(Style.STROKE);
			var_paint.setStrokeWidth(2);
			var_paint.setColor(Color.BLACK);
			canvas.drawOval(ovalBounds, var_paint);
			var_paint.setColor(Color.WHITE);
			var_paint.setStyle(Style.FILL);
			canvas.drawRect(200*factor, 370*factor, 0, 0, var_paint);
			canvas.drawRect(300*factor, 480*factor, 134*factor, 0, var_paint);
			var_paint.setStyle(Style.STROKE);			
			var_paint.setColor(Color.BLACK);			   
			// Haus 1 fuellen
			// --------------------------------------
			// --------------------------------------	
			var_paint.setColor(Color.rgb(99, 112, 133));
			var_paint.setStyle(Style.FILL);
			// Haus 1
			canvas.drawRect(0,12*factor,134*factor ,372*factor , var_paint);
			var_paint.setColor(Color.WHITE);
			// Haus 1 Treppe
			canvas.drawRect(0,0,42*factor,76*factor , var_paint);
			// Treppe Haus 1
			// ----------------------------------------------
			// ----------------------------------------------
			var_paint.setColor(Color.rgb( 175, 112, 48));			
			var_paint.setStyle(Style.STROKE);
			var_paint.setStrokeWidth(0);
			// Treppe Eingang von Haus 5 aus gesehen
			for(int i=0;i<=26;i=i+2){
				canvas.drawLine(0,i,15,i,var_paint);
			}
			shape_house123(canvas);
			// Haus 1 / Raueme
			// --------------------------------------
			// --------------------------------------		
			// Raum 41
			canvas.drawRect(0*factor,102*factor,43*factor,76*factor,var_paint);
			// Raum 08
			canvas.drawRect(0*factor,158*factor,43*factor,174*factor,var_paint);
			canvas.drawText("8",10*factor,170*factor,var_paint);		
			// Raum 09
			canvas.drawRect(0*factor,158*factor,43*factor,102*factor,var_paint);
			canvas.drawText("9",10*factor,125*factor,var_paint);		
			// Raum 07
			canvas.drawRect(0*factor,158*factor,43*factor,240*factor,var_paint);
			canvas.drawText("7 / 2",10*factor ,217*factor,var_paint);		
			// Raum 07
			canvas.drawRect(0*factor,240*factor,43*factor,272*factor,var_paint);
			canvas.drawText("7 / 1",10*factor ,260*factor,var_paint);
			// Raum 06/05 RudisResteRampe
			canvas.drawRect(0*factor,272*factor,43*factor,306*factor,var_paint);
			canvas.drawLine(0*factor,289*factor,43*factor,289*factor,var_paint);		
			// Raum WC
			canvas.drawRect(0*factor,306*factor,43*factor,372*factor,var_paint);
			canvas.drawLine(0*factor,339*factor,43*factor,339*factor,var_paint);
			// Raum 19
			canvas.drawLine(66*factor,339*factor,134*factor,339*factor,var_paint);
			canvas.drawText("19",76*factor,360*factor,var_paint);
			// Raum 18/2
			canvas.drawRect(66*factor,306*factor,134*factor,372*factor,var_paint);
			canvas.drawText("18 / 2",76*factor,330*factor,var_paint);
			// Raum 18/1
			canvas.drawRect(66*factor,306*factor,134*factor,272*factor,var_paint);
			canvas.drawText("18 / 1",76*factor,290*factor,var_paint);
			// Raum 17
			canvas.drawRect(66*factor,240*factor,134*factor,272*factor,var_paint);
			canvas.drawText("17",76*factor,260*factor,var_paint);		
			// Raum 16
			canvas.drawRect(66*factor,206*factor,134*factor,240*factor,var_paint);
			canvas.drawText("16",76*factor,217*factor,var_paint);
			// Raum 15	
			canvas.drawRect(66*factor,174*factor,134*factor,206*factor,var_paint);
			canvas.drawText("15",76*factor,200*factor,var_paint);
			// Raum 14	
			canvas.drawRect(66*factor,174*factor,134*factor,142*factor,var_paint);
			canvas.drawText("14",76*factor,160*factor,var_paint);
			// Raum 13	
			canvas.drawRect(66*factor,142*factor,134*factor,77*factor,var_paint);
			canvas.drawText("13",76*factor,120*factor,var_paint);
			// Raum 11
			canvas.drawLine(66*factor,77*factor,43*factor,77*factor,var_paint);
			canvas.drawText("11",86*factor,30*factor,var_paint);
			// Haus 2
			// --------------------------------------
			// --------------------------------------	
			for(float i= 48 ;i<=142 ;i=i+2){
				canvas.drawLine(i,163, i,130,var_paint);
			}
			// Haus 3
			// --------------------------------------
			// --------------------------------------		
			// WC, Duschen, Umkleide	
			canvas.drawRect(444*factor,334*factor,580*factor,392*factor,var_paint);
			// RudisResteRampe			
			canvas.drawRect(604*factor,334*factor,704*factor,392*factor,var_paint);		
			canvas.drawRect(704*factor,334*factor,777*factor,392*factor,var_paint);
			canvas.drawRect(478*factor,424*factor,410*factor,490*factor,var_paint);	
			canvas.drawRect(478*factor,424*factor,412*factor,490*factor,var_paint);
			canvas.drawRect(412*factor,424*factor,546*factor,490*factor,var_paint);		
			canvas.drawRect(546*factor,424*factor,588*factor,490*factor,var_paint);
			canvas.drawRect(588*factor,424*factor,630*factor,490*factor,var_paint);
			canvas.drawRect(630*factor,424*factor,672*factor,490*factor,var_paint);		
			canvas.drawRect(672*factor,424*factor,777*factor,490*factor,var_paint);
	
			for(int i=(int)(777*factor);i<=(int)(1270*factor);i=i+2){
				canvas.drawLine(i,334*factor, i, 490*factor,var_paint);
			}		
			// Sporthalle
			// Raum 3/4/5/6/7/48
			canvas.drawRect(580*factor,334*factor,510*factor,264*factor,var_paint);		
			// Treppenhaus
			canvas.drawRect(640*factor,334*factor,580*factor,264*factor,var_paint);	
			canvas.drawRect(640*factor,264*factor,600*factor,200*factor,var_paint);
			canvas.drawRect(640*factor,200*factor,600*factor,140*factor,var_paint);
			canvas.drawRect(640*factor,140*factor,600*factor,39*factor,var_paint);		
			canvas.drawRect(560*factor,334*factor,510*factor,100*factor,var_paint);		
			canvas.drawRect(560*factor,100*factor,510*factor,39*factor,var_paint);
			// Cleaner
		    // ----------------------------------------
		   	// ----------------------------------------      
			var_paint.setColor(Color.WHITE);
			var_paint.setStrokeWidth(2);
			// Line Haus 3 / Sporthalle
			canvas.drawLine(factor*581, factor*334, factor*603, factor*334, var_paint);
			// Ausgaenge
		    // ----------------------------------------
		   	// ----------------------------------------      
			var_paint.setColor(Color.RED);
			// Ausgang Sporthalle
			// Sporthalle Oben
			canvas.drawRect(factor*570,factor*38,factor*580,factor*40,var_paint);
			postprocessing(canvas);
	}

		// Zeichnet Haus 123 Etage 0
		private void draw_floor_09(Canvas canvas){
			preparation(canvas);
			// Rundung zeichnen
			var_paint.setStyle(Style.FILL);
			var_paint.setColor(Color.rgb(0, 151, 143));	
			RectF ovalBounds = new RectF(218*factor, 470*factor, 35*factor,270*factor);
			canvas.drawOval(ovalBounds, var_paint);
			var_paint.setStyle(Style.STROKE);
			var_paint.setStrokeWidth(2);
			var_paint.setColor(Color.BLACK);
			canvas.drawOval(ovalBounds, var_paint);
			var_paint.setColor(Color.WHITE);
			var_paint.setStyle(Style.FILL);
			canvas.drawRect(200*factor, 370*factor, 0, 0, var_paint);
			canvas.drawRect(300*factor, 480*factor, 134*factor, 0, var_paint);
			var_paint.setStyle(Style.STROKE);			
			var_paint.setColor(Color.BLACK);			   
			// Haus 1 fuellen
			// --------------------------------------
			// --------------------------------------	
			var_paint.setColor(Color.rgb(99, 112, 133));
			var_paint.setStyle(Style.FILL);
			// Haus 1
			canvas.drawRect(0,12*factor,134*factor ,372*factor , var_paint);
			var_paint.setColor(Color.WHITE);
			// Haus 1 Treppe
			canvas.drawRect(0,0,42*factor,76*factor , var_paint);
			// Treppe Haus 1
			// ----------------------------------------------
			// ----------------------------------------------
			var_paint.setColor(Color.rgb( 175, 112, 48));			
			var_paint.setStyle(Style.STROKE);
			var_paint.setStrokeWidth(0);
			// Treppe Eingang von Haus 5 aus gesehen
			for(int i=0;i<=26;i=i+2){
				canvas.drawLine(0,i,15,i,var_paint);
			}
			// Haus 2 fuellen
			//----------------------------------------
			//----------------------------------------	
			var_paint.setColor(Color.rgb(0, 151, 143));		
			int count = 0;
			float step = 0;
			for(int j=404;j>=150;j--){
				count++;
				if(count%10==1){
					step=step+1f;
					canvas.drawLine(factor*(j-8),factor*547-step,factor*j,factor*503-step, var_paint);
				}
				else
					canvas.drawLine(factor*(j-8),factor*547-step,factor*j,factor*503-step, var_paint);
			}
			
			for(int k=340;k>=250;k--){
				canvas.drawLine(factor*k,factor*470,factor*k,factor*485, var_paint);
			}
			var_paint.setStyle(Style.FILL);		
			canvas.drawRect(142, 163, 47, 130, var_paint);
			// Haus 3 fuellen
			// --------------------------------------
			// --------------------------------------
			var_paint.setColor(Color.rgb(99, 112, 133));			
			canvas.drawRect(factor*410,factor*490, factor*1270,factor*334, var_paint);
			canvas.drawRect(factor*640,factor*334,factor*510,factor*39, var_paint);
			var_paint.setStyle(Style.STROKE);	
			var_paint.setStrokeWidth(2);
			var_paint.setColor(Color.BLACK);			
			canvas.drawRect(410*factor,470*factor,346*factor,438*factor,var_paint);
			// Tuerme fuellen
			var_paint.setColor(Color.rgb(99, 112, 133));			
			var_paint.setStyle(Style.FILL);	
			// Turm_1 an Haus 3
			canvas.drawRect(factor*1100,factor*490, factor*1170,factor*560, var_paint);
			// Turm_2 an Haus 3		
			canvas.drawRect(factor*600,factor*490, factor*670,factor*560, var_paint);
			shape_house123(canvas);
			// Haus 1
			// Raum 41
			canvas.drawRect(0*factor,102*factor,43*factor,76*factor,var_paint);
			// Raum 08
			canvas.drawRect(0*factor,158*factor,43*factor,174*factor,var_paint);
			canvas.drawText("8",10*factor,170*factor,var_paint);		
			// Raum 09
			canvas.drawRect(0*factor,158*factor,43*factor,102*factor,var_paint);
			canvas.drawText("9",10*factor,125*factor,var_paint);		
			// Raum 07
			canvas.drawRect(0,158*factor,43*factor,240*factor,var_paint);
			canvas.drawText("7 / 2",10*factor ,217*factor,var_paint);		
			// Raum 07
			canvas.drawRect(0*factor,240*factor,43*factor,272*factor,var_paint);
			canvas.drawText("7 / 1",10*factor ,260*factor,var_paint);
			// Raum 06/05 RudisResteRampe
			canvas.drawRect(0*factor,272*factor,43*factor,306*factor,var_paint);
			canvas.drawLine(0*factor,289*factor,43*factor,289*factor,var_paint);		
			// Raum WC
			canvas.drawRect(0*factor,306*factor,43*factor,372*factor,var_paint);
			canvas.drawLine(0*factor,339*factor,43*factor,339*factor,var_paint);
			// Raum 19
			canvas.drawLine(66*factor,339*factor,134*factor,339*factor,var_paint);
			canvas.drawText("19",76*factor,360*factor,var_paint);
			// Raum 18/2
			canvas.drawRect(66*factor,306*factor,134*factor,372*factor,var_paint);
			canvas.drawText("18 / 2",76*factor,330*factor,var_paint);
			// Raum 18/1
			canvas.drawRect(66*factor,306*factor,134*factor,272*factor,var_paint);
			canvas.drawText("18 / 1",76*factor,285*factor,var_paint);
			// Raum 17
			canvas.drawRect(66*factor,240*factor,134*factor,272*factor,var_paint);
			canvas.drawText("17",76*factor,260*factor,var_paint);		
			// Raum 16
			canvas.drawRect(66*factor,206*factor,134*factor,240*factor,var_paint);
			canvas.drawText("16",76*factor,230*factor,var_paint);
			// Raum 15	
			canvas.drawRect(66*factor,174*factor,134*factor,206*factor,var_paint);
			canvas.drawText("15",76*factor,200*factor,var_paint);
			// Raum 14	
			canvas.drawRect(66*factor,174*factor,134*factor,142*factor,var_paint);
			canvas.drawText("14",76*factor,165*factor,var_paint);
			// Raum 13	
			canvas.drawRect(66*factor,142*factor,134*factor,102*factor,var_paint);
			canvas.drawText("13",76*factor,120*factor,var_paint);
			// Raum 12	
			canvas.drawRect(66*factor,102*factor,134*factor,77*factor,var_paint);
			canvas.drawText("12",76*factor,90*factor,var_paint);
			// Raum 11
			canvas.drawLine(66*factor,77*factor,43*factor,77*factor,var_paint);
			canvas.drawText("11",86*factor,30*factor,var_paint);
			// Raum 10	
			canvas.drawLine(66*factor,77*factor,66*factor,12*factor,var_paint);
			canvas.drawText("10",45*factor,30*factor,var_paint);
			// Haus 3
			// ---------------------------------------
			// ---------------------------------------		
			canvas.drawRect(410*factor,334*factor,444*factor,392*factor,var_paint);
			canvas.drawRect(444*factor,334*factor,510*factor,392*factor,var_paint);		
			canvas.drawRect(510*factor,334*factor,580*factor,392*factor,var_paint);		
			// ---------------------------------------
			// ---------------------------------------
			canvas.drawRect(604*factor,334*factor,640*factor,392*factor,var_paint);
			canvas.drawRect(640*factor,334*factor,672*factor,392*factor,var_paint);		
			// ---------------------------------------
			// ---------------------------------------
			for(int i=(int)(672*factor);i<=(int)(1270*factor);i=i+2){
				canvas.drawLine(i,334*factor, i, 490*factor,var_paint);
			}
			canvas.drawRect(478*factor,424*factor,410*factor,490*factor,var_paint);		
			canvas.drawRect(478*factor,424*factor,546*factor,490*factor,var_paint);		
			canvas.drawRect(546*factor,424*factor,672*factor,490*factor,var_paint);		
			for(int i=(int)(672*factor);i<=(int)(1270*factor);i=i+2){
				canvas.drawLine(i,424*factor, i, 490*factor,var_paint);
			}
			// Sporthalle
			// Raum 3/4/5/6/7/48
			canvas.drawRect(580*factor,334*factor,510*factor,264*factor,var_paint);		
			// Treppenhaus
			canvas.drawRect(640*factor,334*factor,580*factor,264*factor,var_paint);
			// Cleaner
		    // ----------------------------------------
		   	// ----------------------------------------      
			var_paint.setColor(Color.rgb(0, 151, 143));
			var_paint.setStrokeWidth(2);
			// Line Haus 3 zu Haus 2
			canvas.drawLine(134*factor, 433*factor, 134*factor, 468*factor, var_paint);
			// Line innerhalb von Haus 2
			canvas.drawLine(202*factor, 470*factor, 310*factor, 470*factor, var_paint);
			var_paint.setColor(Color.rgb(99, 112, 133));			
			// Line Haus 3 / Sporthalle
			canvas.drawLine(factor*581, factor*334, factor*603, factor*334, var_paint);
			// Ausgaenge
		    // ----------------------------------------
		   	// ----------------------------------------      
			var_paint.setColor(Color.RED);
			// Ausgang Haus 2
			canvas.drawLine(10*factor, 0, 30*factor, 0,var_paint);
			// Ausgang Haus 2 Schräge
			//canvas.drawLine(factor*85,factor*414,factor*100,factor*428, var_paint);
			// Ausgang Mitte
			canvas.drawRect(factor*227,factor*373,factor*294,factor*371,var_paint);
			// Sporthalle Unten			
			canvas.drawRect(factor*511,factor*310,factor*509,factor*290, var_paint);
			canvas.drawRect(factor*641,factor*310,factor*639,factor*290, var_paint);
			// Tuer Richtung Haus 4
			canvas.drawLine(factor*401,factor*518,factor*395,factor*534, var_paint);
			postprocessing(canvas);			
		}
	
		
		// Zeichnet Haus 123 Etage 1
		private void draw_floor_10(Canvas canvas){
			preparation(canvas);
			// Rundung zeichnen
			var_paint.setStyle(Style.FILL);
			var_paint.setColor(Color.rgb(95, 171, 92));	
			RectF ovalBounds = new RectF(218*factor, 470*factor, 35*factor,270*factor);
			canvas.drawOval(ovalBounds, var_paint);
			var_paint.setStyle(Style.STROKE);
			var_paint.setStrokeWidth(2);
			var_paint.setColor(Color.BLACK);
			canvas.drawOval(ovalBounds, var_paint);
			var_paint.setColor(Color.WHITE);
			var_paint.setStyle(Style.FILL);
			canvas.drawRect(200*factor, 370*factor, 0, 0, var_paint);
			canvas.drawRect(300*factor, 480*factor, 134*factor, 0, var_paint);
			var_paint.setStyle(Style.STROKE);			
			var_paint.setColor(Color.BLACK);			   
			// Haus 1 fuellen
			// --------------------------------------
			// --------------------------------------	
			var_paint.setColor(Color.rgb(95, 171, 92));		
			var_paint.setStyle(Style.FILL);
			// Haus 1
			canvas.drawRect(0,12*factor,134*factor ,372*factor , var_paint);
			var_paint.setColor(Color.WHITE);
			// Haus 1 Treppe
			canvas.drawRect(0,0,42*factor,76*factor , var_paint);
			// Treppe Haus 1
			// ----------------------------------------------
			// ----------------------------------------------
			var_paint.setColor(Color.rgb( 175, 112, 48));			
			var_paint.setStyle(Style.STROKE);
			var_paint.setStrokeWidth(0);
			// Treppe Eingang von Haus 5 aus gesehen
			for(int i=0;i<=26;i=i+2){
				canvas.drawLine(0,i,15,i,var_paint);
			}
			// Haus 2 fuellen
			//----------------------------------------
			//----------------------------------------	
			var_paint.setColor(Color.rgb(95, 171, 92));	
			var_paint.setStyle(Style.FILL);
			canvas.drawRect(142, 163, 47, 130, var_paint);
			int count = 0;
			float step = 0;
			for(int j=404;j>=150;j--){
				count++;
				if(count%10==1){
					step=step+1f;
					canvas.drawLine(factor*(j-8),factor*547-step,factor*j,factor*503-step, var_paint);
				}
				else
					canvas.drawLine(factor*(j-8),factor*547-step,factor*j,factor*503-step, var_paint);
			}
			
			for(int k=340;k>=250;k--){
				canvas.drawLine(factor*k,factor*470,factor*k,factor*485, var_paint);
			}			
			// Haus 3 fuellen
			// --------------------------------------
			// --------------------------------------
			var_paint.setColor(Color.rgb(95, 171, 92));				
			canvas.drawRect(factor*410,factor*490, factor*1270,factor*334, var_paint);
			canvas.drawRect(factor*640,factor*334,factor*510,factor*39, var_paint);
			var_paint.setColor(Color.WHITE);
			var_paint.setStyle(Style.FILL);
			canvas.drawRect(factor*227,factor*432,factor*294,factor*372,var_paint);
			var_paint.setColor(Color.rgb(95, 171, 92));	
			var_paint.setStyle(Style.FILL);	
			// Turm_1 an Haus 3
			canvas.drawRect(factor*1100,factor*490, factor*1170,factor*560, var_paint);
			// Turm_2 an Haus 3		
			canvas.drawRect(factor*600,factor*490, factor*670,factor*560, var_paint);
			shape_house123(canvas);
		   	// Haus 1
		   	// ----------------------------------------
		   	// ----------------------------------------        
			// Raum 41
			canvas.drawRect(factor*0,factor*102,factor*43,factor*76,var_paint);
			// Raum 10
			canvas.drawRect(factor*0,factor*158,factor*43,factor*174,var_paint);
			// Raum 09
			canvas.drawRect(factor*0,factor*174,factor*43,factor*206,var_paint);
			canvas.drawText("9",factor*10 ,factor*195,var_paint);
			// Raum 08
			canvas.drawRect(factor*0,factor*206,factor*43,factor*240,var_paint);
			canvas.drawText("8",factor*10 ,factor*217,var_paint);
			// Raum 07
			canvas.drawRect(factor*0,factor*240,factor*43,factor*272,var_paint);
			canvas.drawText("7",factor*10 ,factor*260,var_paint);
			// Raum 06/05 RudisResteRampe
			canvas.drawRect(factor*0,factor*272,factor*43,factor*306,var_paint);
			canvas.drawLine(factor*0,factor*289,factor*43,factor*289,var_paint);
			// Raum WC
			canvas.drawRect(factor*0,factor*306,factor*43,factor*372,var_paint);
			canvas.drawLine(factor*0,factor*339,factor*43,factor*339,var_paint);
			// Raum 13
			canvas.drawRect(factor*66,factor*174,factor*134,factor*206,var_paint);
			canvas.drawText("13",factor*76 ,factor*195,var_paint);
			// Raum 14
			canvas.drawRect(factor*66,factor*206,factor*134,factor*240,var_paint);
			canvas.drawText("14",factor*76 ,factor*230,var_paint);
			// Raum 15
			canvas.drawRect(factor*66,factor*240,factor*134,factor*272,var_paint);
			canvas.drawText("15",factor*76 ,factor*265,var_paint);
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
			// Cleaner
		    // ----------------------------------------
		   	// ----------------------------------------        
			var_paint.setColor(Color.rgb(95, 171, 92));	
			var_paint.setStrokeWidth(2);
			canvas.drawLine(factor*134, factor*433, factor*134,factor*468, var_paint);
			canvas.drawLine(factor*581, factor*334, factor*603, factor*334, var_paint);
		    // Nachbereitung
		    postprocessing(canvas);
	}
		
		
		// Zeichnet Haus 123 Etage 2
		private void draw_floor_11(Canvas canvas){
			preparation(canvas);
			// Rundung zeichnen
			var_paint.setStyle(Style.FILL);
			var_paint.setColor(Color.rgb(95, 171, 92));	
			RectF ovalBounds = new RectF(218*factor, 470*factor, 35*factor,270*factor);
			canvas.drawOval(ovalBounds, var_paint);
			var_paint.setStyle(Style.STROKE);
			var_paint.setStrokeWidth(2);
			var_paint.setColor(Color.BLACK);
			canvas.drawOval(ovalBounds, var_paint);
			var_paint.setColor(Color.WHITE);
			var_paint.setStyle(Style.FILL);
			canvas.drawRect(200*factor, 370*factor, 0, 0, var_paint);
			canvas.drawRect(300*factor, 480*factor, 134*factor, 0, var_paint);
			var_paint.setStyle(Style.STROKE);			
			var_paint.setColor(Color.BLACK);			   
			// Haus 1 fuellen
			// --------------------------------------
			// --------------------------------------	
			var_paint.setColor(Color.rgb(247, 201, 126));			
			var_paint.setStyle(Style.FILL);
			// Haus 1
			canvas.drawRect(0,12*factor,134*factor ,372*factor , var_paint);
			var_paint.setColor(Color.WHITE);
			// Haus 1 Treppe
			canvas.drawRect(0,0,42*factor,76*factor , var_paint);
			// Treppe Haus 1
			// ----------------------------------------------
			// ----------------------------------------------
			var_paint.setColor(Color.rgb( 175, 112, 48));			
			var_paint.setStyle(Style.STROKE);
			var_paint.setStrokeWidth(0);
			// Treppe Eingang von Haus 5 aus gesehen
			for(int i=0;i<=26;i=i+2){
				canvas.drawLine(0,i,15,i,var_paint);
			}
			// Haus 2 fuellen
			//----------------------------------------
			//----------------------------------------	
			var_paint.setColor(Color.rgb(95, 171, 92));		
			var_paint.setStyle(Style.FILL);
			canvas.drawRect(142, 163, 47, 130, var_paint);
			for(int k=340;k>=250;k--){
				canvas.drawLine(factor*k,factor*470,factor*k,factor*485, var_paint);
			}			
			int count = 0;
			float step = 0;
			for(int j=404;j>=150;j--){
				count++;
				if(count%10==1){
					step=step+1f;
					canvas.drawLine(factor*(j-8),factor*547-step,factor*j,factor*503-step, var_paint);
					if(count==101){
						var_paint.setColor(Color.BLACK);
						canvas.drawLine(factor*(j-8),factor*547-step,factor*j,factor*503-step, var_paint);
						var_paint.setColor(Color.rgb(95, 171, 92));						
					}
				}
				else
					canvas.drawLine(factor*(j-8),factor*547-step,factor*j,factor*503-step, var_paint);
			}
			// Haus 3 fuellen
			// --------------------------------------
			// --------------------------------------
			var_paint.setColor(Color.rgb(172, 222, 221));					
			canvas.drawRect(factor*410,factor*490, factor*1270,factor*334, var_paint);
			canvas.drawRect(factor*640,factor*334,factor*510,factor*39, var_paint);
			var_paint.setColor(Color.rgb(172, 222, 221));					
			var_paint.setStyle(Style.FILL);	
			// Turm_1 an Haus 3
			canvas.drawRect(factor*1100,factor*490, factor*1170,factor*560, var_paint);
			// Turm_2 an Haus 3		
			canvas.drawRect(factor*600,factor*490, factor*670,factor*560, var_paint);
			shape_house123(canvas);
			// Haus 1
	    	// ----------------------------------------
	    	// ----------------------------------------        
			// Raum 41
			canvas.drawRect(factor*0,factor*102,factor*43,factor*76,var_paint);
			// Raum 10
			canvas.drawRect(factor*0,factor*158,factor*43,factor*174,var_paint);
			// Raum 09	
			canvas.drawRect(factor*0,factor*174,factor*43,factor*206,var_paint);
			canvas.drawText("9",factor*10 ,factor*195,var_paint);
			// Raum 08
			canvas.drawRect(factor*0,factor*206,factor*43,factor*240,var_paint);
			canvas.drawText("8",factor*10 ,factor*217,var_paint);
			// Raum 07
			canvas.drawRect(factor*0,factor*240,factor*43,factor*272,var_paint);
			canvas.drawText("7",factor*10 ,factor*260,var_paint);
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
			// Anderer Raum
			canvas.drawLine(factor*0,factor*77,factor*134,factor*77,var_paint);			
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
			// Raum 6
			canvas.drawText("6",factor*290,factor*488,var_paint);		
			var_paint.setColor(Color.BLACK);
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
			canvas.drawText("40",factor*682,factor*460,var_paint);
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
			var_paint.setStrokeWidth(2);
			var_paint.setColor(Color.rgb(95, 171, 92));				
			canvas.drawLine(factor*134, factor*433, factor*134, factor*468, var_paint);
			var_paint.setColor(Color.rgb(172, 222, 221));			
			canvas.drawLine(factor*581, factor*334, factor*603, factor*334, var_paint);
		   	// Nachbereitung
		   	postprocessing(canvas);
	}
	
		
		// Zeichnet Haus 123 Etage 3
		private void draw_floor_12(Canvas canvas){
			preparation(canvas);
			// Rundung zeichnen
			var_paint.setStyle(Style.FILL);
			var_paint.setColor(Color.rgb(95, 171, 92));	
			RectF ovalBounds = new RectF(218*factor, 470*factor, 35*factor,270*factor);
			canvas.drawOval(ovalBounds, var_paint);
			var_paint.setStyle(Style.STROKE);
			var_paint.setStrokeWidth(2);
			var_paint.setColor(Color.BLACK);
			canvas.drawOval(ovalBounds, var_paint);
			var_paint.setColor(Color.WHITE);
			var_paint.setStyle(Style.FILL);
			canvas.drawRect(200*factor, 370*factor, 0, 0, var_paint);
			canvas.drawRect(300*factor, 480*factor, 134*factor, 0, var_paint);
			var_paint.setStyle(Style.STROKE);			
			var_paint.setColor(Color.BLACK);			   
			// Haus 1 fuellen
			// --------------------------------------
			// --------------------------------------	
			var_paint.setColor(Color.rgb(110, 169, 1));					
			var_paint.setStyle(Style.FILL);
			// Haus 1
			canvas.drawRect(0,12*factor,134*factor ,372*factor , var_paint);
			var_paint.setColor(Color.WHITE);
			// Haus 1 Treppe
			canvas.drawRect(0,0,42*factor,76*factor , var_paint);
			// Treppe Haus 1
			// ----------------------------------------------
			// ----------------------------------------------
			var_paint.setColor(Color.rgb( 175, 112, 48));			
			var_paint.setStyle(Style.STROKE);
			var_paint.setStrokeWidth(0);
			// Treppe Eingang von Haus 5 aus gesehen
			for(int i=0;i<=26;i=i+2){
				canvas.drawLine(0,i,15,i,var_paint);
			}
			// Haus 2 fuellen
			//----------------------------------------
			//----------------------------------------
			var_paint.setColor(Color.rgb(95, 171, 92));		
			for(int k=340;k>=250;k--){
				canvas.drawLine(factor*k,factor*470,factor*k,factor*485, var_paint);
			}			
			int count = 0;
			float step = 0;
			for(int j=404;j>=150;j--){
				count++;
				if(count%10==1){
					step=step+1f;
					canvas.drawLine(factor*(j-8),factor*547-step,factor*j,factor*503-step, var_paint);
					if(count==101 || count==131 || count==161){
						var_paint.setColor(Color.BLACK);
						canvas.drawLine(factor*(j-8),factor*547-step,factor*j,factor*503-step, var_paint);
						var_paint.setColor(Color.rgb(95, 171, 92));						
					}
				}
				else
					canvas.drawLine(factor*(j-8),factor*547-step,factor*j,factor*503-step, var_paint);
			}
			var_paint.setColor(Color.rgb(95, 171, 92));		
			var_paint.setStyle(Style.FILL);
			canvas.drawRect(142, 163, 47, 130, var_paint);
			// Haus 3 fuellen
			// --------------------------------------
			// --------------------------------------
			var_paint.setColor(Color.rgb(110, 169, 1));					
			canvas.drawRect(factor*410,factor*490, factor*1270,factor*334, var_paint);
			canvas.drawRect(factor*640,factor*334,factor*510,factor*39, var_paint);
			var_paint.setColor(Color.rgb(110, 169, 1));					
			var_paint.setStyle(Style.FILL);	
			// Turm_1 an Haus 3
			canvas.drawRect(factor*1100,factor*490, factor*1170,factor*560, var_paint);
			// Turm_2 an Haus 3		
			canvas.drawRect(factor*600,factor*490, factor*670,factor*560, var_paint);
			shape_house123(canvas);
			// Haus 1
		    // ----------------------------------------
		    // ----------------------------------------        
			// Raum 41
			canvas.drawRect(factor*0,factor*102,factor*43,factor*76,var_paint);
			// Raum 10
			canvas.drawRect(factor*0,factor*158,factor*43,factor*174,var_paint);
			// Raum 09	
			canvas.drawRect(factor*0,factor*174,factor*43,factor*206,var_paint);
			canvas.drawText("9",factor*10 ,factor*195,var_paint);
			// Raum 08
			canvas.drawRect(factor*0,factor*206,factor*43,factor*240,var_paint);
			canvas.drawText("8",factor*10 ,factor*217,var_paint);
			// Raum 07
			canvas.drawRect(factor*0,factor*240,factor*43,factor*272,var_paint);
			canvas.drawText("7",factor*10 ,factor*265,var_paint);
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
			canvas.drawText("13",factor*76 ,factor*220,var_paint);
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
			canvas.drawText("4",factor*390,factor*400,var_paint);
			// Raum 5
			canvas.drawText("5",factor*290,factor*488,var_paint);		
			// Raum 6
			canvas.drawText("6",factor*248,factor*488,var_paint);
			// Raum 7
			canvas.drawText("7",factor*218,factor*488,var_paint);		
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
			// Cleaner
		    // ----------------------------------------
		   	// ----------------------------------------      
			var_paint.setColor(Color.rgb(95, 171, 92));	
			var_paint.setStrokeWidth(2);
			// Line Haus 3 zu Haus 2
			canvas.drawLine(134*factor, 433*factor, 134*factor, 468*factor, var_paint);
			var_paint.setColor(Color.rgb(110, 169, 1));				
			// Line Haus 3 / Sporthalle
			canvas.drawLine(factor*581, factor*334, factor*603, factor*334, var_paint);
	    	// Nachbereitung
	    	postprocessing(canvas);
	}
	
		
		// Zeichnet Haus 123 Etage 4
		private void draw_floor_13(Canvas canvas){
			preparation(canvas);
			// Rundung zeichnen
			var_paint.setStyle(Style.FILL);
			var_paint.setColor(Color.rgb(0, 151, 143));	
			RectF ovalBounds = new RectF(218*factor, 470*factor, 35*factor,270*factor);
			canvas.drawOval(ovalBounds, var_paint);
			var_paint.setStyle(Style.STROKE);
			var_paint.setStrokeWidth(2);
			var_paint.setColor(Color.BLACK);
			canvas.drawOval(ovalBounds, var_paint);
			var_paint.setColor(Color.WHITE);
			var_paint.setStyle(Style.FILL);
			canvas.drawRect(200*factor, 370*factor, 0, 0, var_paint);
			canvas.drawRect(300*factor, 480*factor, 134*factor, 0, var_paint);
			var_paint.setStyle(Style.STROKE);			
			var_paint.setColor(Color.WHITE);
			// Haus 1 Treppe
			canvas.drawRect(0,0,42*factor,76*factor , var_paint);
			// Treppe Haus 1
			// ----------------------------------------------
			// ----------------------------------------------
			var_paint.setColor(Color.rgb( 175, 112, 48));			
			var_paint.setStyle(Style.STROKE);
			var_paint.setStrokeWidth(0);
			// Treppe Eingang von Haus 5 aus gesehen
			for(int i=0;i<=26;i=i+2){
				canvas.drawLine(0,i,15,i,var_paint);
			}
			// Haus 2 fuellen
			//----------------------------------------
			//----------------------------------------	
			var_paint.setColor(Color.rgb(0, 151, 143));		
			for(int k=340;k>=250;k--){
				canvas.drawLine(factor*k,factor*470,factor*k,factor*485, var_paint);
			}			
			int count = 0;
			float step = 0;
			for(int j=404;j>=150;j--){
				count++;
				if(count%10==1){
					step=step+1f;
					canvas.drawLine(factor*(j-8),factor*547-step,factor*j,factor*503-step, var_paint);
					if(count==101 || count==131){
						var_paint.setColor(Color.BLACK);
						canvas.drawLine(factor*(j-8),factor*547-step,factor*j,factor*503-step, var_paint);
						var_paint.setColor(Color.rgb(0, 151, 143));		
					}
				}
				else
					canvas.drawLine(factor*(j-8),factor*547-step,factor*j,factor*503-step, var_paint);
			}
			var_paint.setColor(Color.rgb(0, 151, 143));		
			var_paint.setStyle(Style.FILL);
			canvas.drawRect(142, 163, 47, 130, var_paint);
			shape_house123(canvas);
			// Raum 41		
			canvas.drawRect(0,102*factor,43*factor,76*factor,var_paint);
			canvas.drawRect(0,102*factor,43*factor,138*factor,var_paint);		
			// Raum 11		
			canvas.drawRect(0,102*factor,43*factor,138*factor,var_paint);		
			canvas.drawText("11",10*factor,120*factor,var_paint);		
			// RudisResteRampe
			canvas.drawRect(0,138*factor,43*factor,174*factor,var_paint);
			// Raum 8
			canvas.drawRect(0,158*factor,43*factor,240*factor,var_paint);
			canvas.drawText("8",10*factor ,217*factor,var_paint);		
			// Raum 7
			canvas.drawRect(0,240*factor,43*factor,272*factor,var_paint);
			canvas.drawText("7",10*factor ,260*factor,var_paint);
			// Raum 06/05 RudisResteRampe
			canvas.drawRect(0,272*factor,43*factor,306*factor,var_paint);
			canvas.drawLine(0,289*factor,43*factor,289*factor,var_paint);		
			// Raum WC
			canvas.drawRect(0,306*factor,43*factor,372*factor,var_paint);
			canvas.drawLine(0,339*factor,43*factor,339*factor,var_paint);		
			// Versammlungsraum
			canvas.drawRect(66*factor,372*factor,134*factor,272*factor,var_paint);
			canvas.drawText("18 / 2",76*factor,330*factor,var_paint);
			// Raum 18
			canvas.drawRect(66*factor,272*factor,134*factor,206*factor,var_paint);
			// Raum 15	
			canvas.drawRect(66*factor,174*factor,134*factor,206*factor,var_paint);
			canvas.drawText("15",76*factor,195*factor,var_paint);
			// Raum 14	
			canvas.drawRect(66*factor,174*factor,134*factor,142*factor,var_paint);
			canvas.drawText("14",76*factor,162*factor,var_paint);
			// Raum 13	
			canvas.drawRect(66*factor,142*factor,134*factor,102*factor,var_paint);
			canvas.drawText("13",76*factor,120*factor,var_paint);
			// Raum 12	
			canvas.drawRect(66*factor,102*factor,134*factor,77*factor,var_paint);
			canvas.drawText("12",76*factor,90*factor,var_paint);
			// Raum 11
			canvas.drawLine(66*factor,77*factor,43*factor,77*factor,var_paint);
			canvas.drawText("11",86*factor,30*factor,var_paint);
			// Raum 10	
			canvas.drawLine(66*factor,77*factor,66*factor,12*factor,var_paint);
			canvas.drawText("10",45*factor,30*factor,var_paint);
			// Haus 2
			// ------------------------------------
			// ------------------------------------		
			// Raumnummern
			// Raum 5
			canvas.drawText("7",factor*290,factor*488,var_paint);		
			// Raum 6
			canvas.drawText("8",factor*248,factor*488,var_paint);
			// Raum 01
			canvas.drawRect(factor*134,factor*432,factor*218,factor*372,var_paint);
			canvas.drawText("1",factor*160,factor*400,var_paint);
			// Raum 02
			canvas.drawRect(factor*218,factor*432,factor*270,factor*372,var_paint);
			canvas.drawText("2",factor*260,factor*400,var_paint);
			// Raum 03
			canvas.drawRect(factor*270,factor*432,factor*374,factor*372,var_paint);
			canvas.drawText("3",factor*278,factor*400,var_paint);
			// Raum 04
			canvas.drawRect(factor*374,factor*432,factor*410,factor*372,var_paint);
			canvas.drawText("6",factor*390,factor*400,var_paint);
						
			// Raum 04
			canvas.drawRect(factor*374,factor*432,factor*344,factor*372,var_paint);
			canvas.drawText("5",factor*354,factor*400,var_paint);
				
			// Raum 04
			canvas.drawRect(factor*344,factor*432,factor*314,factor*372,var_paint);
			canvas.drawText("4",factor*324,factor*400,var_paint);
			// Haus 3
			// ------------------------------------
			// ------------------------------------		
			for(int i= (int)(410*factor)  ;i<=(int)(1270*factor);i=i+2){
				canvas.drawLine(i,334*factor, i, 490*factor,var_paint);
			}

			for(int i= (int)(factor*39) ;i<=(int)(factor*334);i=i+2){
				canvas.drawLine(factor*640,i,factor*510, i,var_paint);
			}
			// Cleaner
		    // ----------------------------------------
		   	// ----------------------------------------      
			var_paint.setColor(Color.rgb(0, 151, 143));	
			var_paint.setStrokeWidth(2);
			// Line Haus 3 zu Haus 2
			canvas.drawLine(134*factor, 433*factor, 134*factor, 468*factor, var_paint);
			postprocessing(canvas);
		}
	

}