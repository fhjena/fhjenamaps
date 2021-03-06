package org.database;

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
	private double h_factor = 1.0; // um Heuristik weniger oder st�rker in die Wegfindung einzubeziehen
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
	public void compute_Path(int Start_ID, int Ziel_ID) {
		Node n;						// Knoten f�r Eintrag in Gesamtliste
		Cursor c;					// Cursor zum Datenbankzugriff
		c = myDB.getDatafromNodeId(Start_ID);			// Datenbankabfrage nach Startknoten
		n = new Node(c);								// Startknoten initialisieren
		TotalList.add(n);								// Startknoten der Gesamtliste hinzuf�gen
		SN = TotalList.get(GetIndexOfElement(TotalList, Start_ID)); // Startknoten aus Gesamtliste holen
		SN.setG(0.0f); 												// G auf 0 setzen
		SN.setH(0.0f); 												// H auf 0 setzen
		
		c = myDB.getDatafromNodeId(Ziel_ID);			// Datenbankabfrage nach Zielknoten
		n = new Node(c);								// Zielknoten initialisieren
		TotalList.add(n);								// Zielknoten der Gesamtliste hinzuf�gen
		DN = TotalList.get(GetIndexOfElement(TotalList, Ziel_ID));	// Zielknoten aus Gesamtliste holen
		
		open_L.add(SN);												// Startknoten zu offener Liste hinzuf�gen
		
		while ((!closed_L.contains(DN)) && (!open_L.isEmpty()) ) {
			Current_N = open_L.pollFirst(); 						// aktueller Knoten = Knoten mit niedrigstem f-Wert
			closed_L.add(Current_N); 								// aktueller Knoten in geschlossene Liste hinzugef�gt
			for (int i = 0; i <= Current_N.getNeigbour_ID().size() - 1; i++) { // F�r alle angrenzenden Knoten
				if(Current_N.getNeigbour_ID().get(i) != -1){		// Wenn eingetragene NachbarID eine eine g�ltige ID ist
					c = myDB.getDatafromNodeId(Current_N.getNeigbour_ID().get(i));	// Datenbankabfrage
					n = new Node(c);								// Knoteninitialisierung
					if (GetIndexOfElement(TotalList, n.getID()) == -1) // Wenn Knoten noch nicht in Gesamtliste
						TotalList.add(n);								// Knoten hinzuf�gen
					Parent_N = TotalList.get(GetIndexOfElement(TotalList, Current_N.getNeigbour_ID().get(i))); 			// Angrenzenden Knoten festsetzen
					if (closed_L.contains(Parent_N)) {/* nichts */} 	// wenn schon in geschlossener Liste, dann zue nichts
					else { 												// ansonsten (wenn nicht in geschlossener Liste)
						if (open_L.contains(Parent_N)) { 				// wenn schon in offener Liste
							if ((Current_N.getG()+calculate_Distance(Parent_N.getX(), Parent_N.getY(), Parent_N.getZ(),Current_N.getX(), Current_N.getY(), Current_N.getZ())) < Parent_N.getG()){ // Wenn Strecke zwischen angrenzendem Knoten �ber aktuellen Knoten < als bisheriger Weg zu angrenzendem Knoten
								Parent_N.setG(Current_N.getG()+calculate_Distance(Parent_N.getX(), Parent_N.getY(), Parent_N.getZ(),Current_N.getX(), Current_N.getY(), Current_N.getZ())); // aktualsiere G (Strecke von Start bis zu diesem Knoten
								Parent_N.setParent(Current_N); // setze aktuellen Knoten als Vorg�ngerknoten f�r angrenzden Knoten (alter angrenzender Knoten wird �beschrieben)
							}
						}
	
						else { 	// wenn noch nicht in offener Liste
	
							Parent_N.setParent(Current_N); // aktuellen Knoten als Vorg�nger von angrenzenden Knoten eintragen + Werte aktualisieren
							Parent_N.setG(Parent_N.getParent().getG()+calculate_Distance(Parent_N.getX(), Parent_N.getY(), Parent_N.getZ(),Parent_N.getParent().getX(), Parent_N.getParent().getY(),Parent_N.getParent().getZ()));
							Parent_N.setH((float) (calculate_H(Parent_N.getX(), Parent_N.getY(), Parent_N.getZ()) * h_factor));
							open_L.add(Parent_N); // zu offener Liste hinzuf�gen
						}
					}
				}
			}
		}
		
		// Wegstreckenl�nge speichern
		distance = DN.getG();
		// speichert Knoten in Array
		ArrayList<Node> l = new ArrayList<Node>();
		if (!open_L.isEmpty()) { // Wenn Zielknoten gefunden
			l.clear();
			Node Node1 = DN;
			while (Node1 != SN) { // 2D-array f�llen. Array mit Arrays von Knoten. Immer Knoten mit gleicher Ebene kommen in ein Unterarray
				l.add(0, Node1);
				if(Node1.getFloorID() != Node1.getParent().getFloorID()){		// Wenn Ebene zwischen Knoten verschieden
					Path.add(0,new ArrayList<Node>(l));										// Knotenarray zu 2D-Array hinzuf�gen
					l.clear();										// und l�schen, um mit Knoten neuer Ebene zu f�llen
				}
					Node1 = Node1.getParent();
			}
			l.add(0, SN);					// Zuletzt noch Startknoten hinzuf�gen
			Path.add(0,new ArrayList<Node>(l));
			l.clear();
		} 
		else{
			// ansonsten
			l.clear(); // Liste leeren
			Path.clear();
		}
		closed_L.clear(); // alle Listen l�schen
		open_L.clear();
		TotalList.clear();
	}
	
	/**
	 * Gibt die L�nge des Gesamtweges zur�ck (erst nach Durchf�hrung einer Wegberechnung mit n�tzlichem Wert gef�llt)
	 * @return Wegstrecke
	 */
	public float getDistance() { 
		return distance;
	}

	/**
	 * Gibt den gefundenen Weg als 2D-Array zur�ck
	 * @return Weg
	 */
	public ArrayList<ArrayList<Node>> getPath() {		
		return Path;
	}

	/**
	 * dient dem extrahieren von Knoten aus der Gesamtliste... wird sp�ter durch DB anfrage ersetzt
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
