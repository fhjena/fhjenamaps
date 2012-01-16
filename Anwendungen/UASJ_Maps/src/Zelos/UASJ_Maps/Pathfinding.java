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
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.TreeSet;

import android.content.Context;
import android.database.Cursor;

public class Pathfinding {

	/* ------------- A*-Algorithmus ------------- */
	private Node SN, DN, Current_N, Parent_N; // Startknoten, Zielknoten, aktueller Knoten, angrenzender Knoten
	private TreeSet<Node> open_L = new TreeSet<Node>(); // TreeSet, da immer geordnet und immer kleinstes Element gesucht wird
	private LinkedList<Node> closed_L = new LinkedList<Node>(); // LinkedList, da nach diskreten Elementen gesucht werden muss
	private LinkedList<Node> TotalList = new LinkedList<Node>(); // Gesamtliste aller Knoten
	private ArrayList<ArrayList<Node>> Path = new ArrayList<ArrayList<Node>>();
	private double h_factor = 1.0; // um Heuristik weniger oder stärker in die Wegfindung einzubeziehen
	private float distance = 0; // Distanz der ermittelten Route
	private DataBase myDB; // Datenbank zur Abfrage
	
	public Pathfinding(Context c) {
		myDB = new DataBase(c);
	}
	
	/**
	 * Berechnung der Strecke zwischen zwei Knoten
	 * @params: Koordinaten
	 * @return: Strecke zwischen zwei Knoten
	 */
	public float calculate_Distance(int x1, int y1, int z1, int x2, int y2, int z2) {
		float i = (float) Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2)+ Math.pow(z1 - z2, 2));
		return i;
	}

	/**
	 * Berechnung der Luftlinie zu Zielknoten
	 * @params: Koordinaten
	 * @return: Luftlinie
	 */
	public float calculate_H(int x1, int y1, int z1) { 								// 
		float i = (float) Math.sqrt(Math.pow(x1 - DN.getX(), 2) + Math.pow(y1 - DN.getY(), 2)+ Math.pow(z1 - DN.getZ(), 2));
		return i;
	}

	/**
	 * berechnet den Weg von Start zu Zielknoten
	 * @param Start_ID
	 * @param Ziel_ID
	 */
	public boolean compute_Path(String Start, String Destination) {
		//TODO: noch Rückgabewert einfügen? Z.b. ob Datenbankarbeit geklappt hat etc.
		Node n;						// Knoten für Eintrag in Gesamtliste
		Cursor c;					// Cursor zum Datenbankzugriff
		
		c = myDB.getDatafromRoom(Start);			// Datenbankabfrage nach Startknoten
		if(c.getCount() == 0)
			return false;
		n = new Node(c);								// Startknoten initialisieren
		TotalList.add(n);								// Startknoten der Gesamtliste hinzufügen
		SN = TotalList.get(GetIndexOfElement(TotalList, n.getID())); // Startknoten aus Gesamtliste holen
		SN.setG(0.0f); 												// G auf 0 setzen
		SN.setH(0.0f); 												// H auf 0 setzen
		
		c = myDB.getDatafromRoom(Destination);			// Datenbankabfrage nach Zielknoten
		if(c.getCount() == 0)
			return false;
		n = new Node(c);								// Zielknoten initialisieren
		TotalList.add(n);								// Zielknoten der Gesamtliste hinzufügen
		DN = TotalList.get(GetIndexOfElement(TotalList, n.getID()));	// Zielknoten aus Gesamtliste holen
		
		open_L.add(SN);												// Startknoten zu offener Liste hinzufügen
		
		while ((!closed_L.contains(DN)) && (!open_L.isEmpty()) ) {
			Current_N = open_L.pollFirst(); 						// aktueller Knoten = Knoten mit niedrigstem f-Wert
			closed_L.add(Current_N); 								// aktueller Knoten in geschlossene Liste hinzugefügt
			for (int i = 0; i <= Current_N.getNeigbour_ID().size() - 1; i++) { // Für alle angrenzenden Knoten
				if(((Current_N.getNeigbour_ID().get(i) > -1) && (Current_N.getNeigbour_ID().get(i) != 120)  && (Current_N.getNeigbour_ID().get(i) != 108) && (Current_N.getNeigbour_ID().get(i) != 103) && (Current_N.getNeigbour_ID().get(i) != 98))||(Current_N.getNeigbour_ID().get(i)==DN.getID())){		// Wenn eingetragene NachbarID eine eine gültige ID ist
					c = myDB.getDatafromNodeId(Current_N.getNeigbour_ID().get(i));	// Datenbankabfrage
					n = new Node(c);								// Knoteninitialisierung
					if (GetIndexOfElement(TotalList, n.getID()) == -1) // Wenn Knoten noch nicht in Gesamtliste
						TotalList.add(n);								// Knoten hinzufügen
					Parent_N = TotalList.get(GetIndexOfElement(TotalList, Current_N.getNeigbour_ID().get(i))); 			// Angrenzenden Knoten festsetzen
					if (closed_L.contains(Parent_N)) {/* nichts */} 	// wenn schon in geschlossener Liste, dann zue nichts
					else { 												// ansonsten (wenn nicht in geschlossener Liste)
						if (open_L.contains(Parent_N)) { 				// wenn schon in offener Liste
							if ((Current_N.getG()+calculate_Distance(Parent_N.getX(), Parent_N.getY(), Parent_N.getZ(),Current_N.getX(), Current_N.getY(), Current_N.getZ())) < Parent_N.getG()){ // Wenn Strecke zwischen angrenzendem Knoten über aktuellen Knoten < als bisheriger Weg zu angrenzendem Knoten
								Parent_N.setG(Current_N.getG()+calculate_Distance(Parent_N.getX(), Parent_N.getY(), Parent_N.getZ(),Current_N.getX(), Current_N.getY(), Current_N.getZ())); // aktualsiere G (Strecke von Start bis zu diesem Knoten
								Parent_N.setParent(Current_N); // setze aktuellen Knoten als Vorgängerknoten für angrenzden Knoten (alter angrenzender Knoten wird übeschrieben)
							}
						}
	
						else { 	// wenn noch nicht in offener Liste
	
							Parent_N.setParent(Current_N); // aktuellen Knoten als Vorgänger von angrenzenden Knoten eintragen + Werte aktualisieren
							Parent_N.setG(Parent_N.getParent().getG()+calculate_Distance(Parent_N.getX(), Parent_N.getY(), Parent_N.getZ(),Parent_N.getParent().getX(), Parent_N.getParent().getY(),Parent_N.getParent().getZ()));
							Parent_N.setH((float) (calculate_H(Parent_N.getX(), Parent_N.getY(), Parent_N.getZ()) * h_factor));
							open_L.add(Parent_N); // zu offener Liste hinzufügen
						}
					}
				}
			}
		}
		
		// Wegstreckenlänge speichern
		distance = DN.getG();
		Path.clear(); // alte Wege löschen
		// speichert Knoten in Array
		ArrayList<Node> l = new ArrayList<Node>();
		if (!open_L.isEmpty()) { // Wenn Zielknoten gefunden
			l.clear();
			Node Node1 = DN;
			while (Node1 != SN) { // 2D-array füllen. Array mit Arrays von Knoten. Immer Knoten mit gleicher Ebene kommen in ein Unterarray
				l.add(0, Node1);
				if(Node1.getFloorID() != Node1.getParent().getFloorID()){		// Wenn Ebene zwischen Knoten verschieden
					Path.add(0,new ArrayList<Node>(l));										// Knotenarray zu 2D-Array hinzufügen
					l.clear();										// und löschen, um mit Knoten neuer Ebene zu füllen
				}
					Node1 = Node1.getParent();
			}
			l.add(0, SN);					// Zuletzt noch Startknoten hinzufügen
			Path.add(0,new ArrayList<Node>(l));
			l.clear();
		} 
		else{
			// ansonsten
			l.clear(); // Liste leeren
			Path.clear();
			return false;
		}
		if(Path.size()>1){ // Löscht alle Ebenen, die nur einen Knoten beinhalten (außer, wenn es insgesamt nur eine abzuschreitende Ebene gibt)
			for(int i=0;i<Path.size();i++){
				if(Path.get(i).size()==1 && !(Path.get(i).get(0) == DN || Path.get(i).get(0) == SN))		{	// Array-Länge vergleichen
					Path.remove(i--);				// Array löschen
					
				}
				
			}
		}
		
		closed_L.clear(); // alle Listen löschen
		open_L.clear();
		TotalList.clear();
		return true;
	}
	
	/**
	 * Gibt die Länge des Gesamtweges zurück (erst nach Durchführung einer Wegberechnung mit nützlichem Wert gefüllt)
	 * @return Wegstrecke in m
	 */
	public float getDistance() { 
		return (distance*6.276f/20);
	}

	/**
	 * Gibt den gefundenen Weg als 2D-Array zurück
	 * @return Weg
	 */
	public ArrayList<ArrayList<Node>> getPath() {		
		return Path;
	}

	/**
	 * dient dem extrahieren von Knoten aus der Gesamtliste... wird später durch DB anfrage ersetzt
	 * @param List
	 * @param ID
	 * @return Index des gesuchten Elements
	 */
	private int GetIndexOfElement(LinkedList<Node> Liste, int ID) {
		ListIterator<Node> it = Liste.listIterator();
		while (it.hasNext()) {
			if (it.next().getID() == ID) {
				return it.previousIndex();
			}
		}
		return -1;
	}

}
