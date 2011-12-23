package usajmaps.prototyping;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.TreeSet;

import android.graphics.Point;

public class Pathfinding {

	/* ------------- A*-Algorithmus ------------- */
//	private int Start_ID = 0, Dest_ID = 0; 	// Start- und ZielID
	private Node SN, DN, Current_N, Parent_N; // Startknoten, Zielknoten, aktueller Knoten, angrenzender Knoten
	private TreeSet<Node> open_L = new TreeSet<Node>(); // TreeSet, da immer geordnet und immer kleinstes Element gesucht wird
	private LinkedList<Node> closed_L = new LinkedList<Node>(); // LinkedList, da nach diskreten Elementen gesucht werden muss
	private LinkedList<Node> TotalList = new LinkedList<Node>(); // Gesamtliste aller Knoten
	private ArrayList<Node> Path = new ArrayList<Node>(); // enthält nach Routenberechnung die Wegknoten
	private double h_factor = 1.0; // um Heuristik weniger oder stärker in die Wegfindung einzubeziehen
	private float distance = 0; // Distanz der ermittelten Route

	public Pathfinding() {
		// Knoten Ebene0
		int[] list0 = { 1 };												// Liste mit angrenzenden Knoten
		TotalList.add(new Node(0, 0, 0, 0, new Point(300, 220), list0));	// neuen Knoten mit Eigenschaften erstellen
		int[] list1 = { 0, 2, 3 };
		TotalList.add(new Node(1, 3, 0, 0, new Point(300, 280), list1));
		int[] list2 = { 1, 5 };
		TotalList.add(new Node(2, 3, 3, 0, new Point(220, 280), list2));
		int[] list3 = { 1, 4 };
		TotalList.add(new Node(3, 12, 0, 0, new Point(300, 360), list3));
		int[] list4 = { 3, 5 };
		TotalList.add(new Node(4, 15, 0, 0, new Point(300, 485), list4));
		int[] list5 = { 2, 6, 4 };
		TotalList.add(new Node(5, 16, 7, 0, new Point(170, 485), list5));
		int[] list6 = { 5, 7 };
		TotalList.add(new Node(6, 17, 10, 0, new Point(125, 485), list6));
		int[] list7 = { 6 };
		TotalList.add(new Node(7, 17, 13, 0, new Point(55, 485), list7));
	}

	public float calculate_Distance(int x1, int y1, int z1, int x2, int y2, int z2) { 		// Berechnung der Strecke zwischen zwei Knoten
		int i = (int) Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2)+ Math.pow(z1 - z2, 2));
		return i;
	}

	public float calculate_H(int x1, int y1, int z1) { 								// Berechnung der Luftlinie zu Zielknoten
		int i = (int) Math.sqrt(Math.pow(x1 - DN.getX(), 2) + Math.pow(y1 - DN.getY(), 2)+ Math.pow(z1 - DN.getZ(), 2));
		return i;
	}

	public void compute_Path(int Start_ID, int Ziel_ID) {			// berechnet den Weg von Start zu Zielknoten
		SN = TotalList.get(GetIndexOfElement(TotalList, Start_ID)); // Startknoten aus Gesamtliste holen; hier später: Datenbankabfrage + Erstellung eines neuen Knotens
		SN.setG(0.0f); 												// G auf 0 setzen
		SN.setH(0.0f); 												// H auf 0 setzen
		DN = TotalList.get(GetIndexOfElement(TotalList, Ziel_ID));	// Zielknoten aus Gesamtliste holen; hier später: Datenbankabfrage + Erstellung eines neuen Knotens
		open_L.add(SN);												// Startknoten zu offener Liste hinzufügen

		while ((!closed_L.contains(DN)) && (!open_L.isEmpty()) ) {
			Current_N = open_L.pollFirst(); 						// aktueller Knoten = Knoten mit niedrigstem f-Wert
			closed_L.add(Current_N); 								// aktueller Knoten in geschlossene Liste hinzugefügt
			for (int i = 0; i <= Current_N.getNeigbour_ID().length - 1; i++) { // Für alle angrenzenden Knoten
				Parent_N = TotalList.get(GetIndexOfElement(TotalList,
						Current_N.getNeigbour_ID()[i])); 			// hier später: Datenbankabfrage, Erstellung einer neuen Instanz vom Knoten
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
		// Wegstreckenlänge speichern
		distance = DN.getG();
		// speichert Knoten in Array
		if (!open_L.isEmpty()) { // Wenn Zielknoten gefunden
			Node Node1 = DN;
			while (Node1 != SN) { // "Gehe Weg Rückwärts" und speicher die zu begehenden Knoten
				this.Path.add(0, Node1);
				Node1 = Node1.getParent();
			}
			this.Path.add(0, SN);
		} else
			// ansonsten
			this.Path.clear(); // Liste leeren

	}

	public float getDistance() { // Gibt die Länge des Gesamtweges zurück (erst nach Durchführung einer Wegberechnung mit nützlichem Wert gefüllt)
		return distance;
	}

	public ArrayList<Node> getPath() {		// Gibt den gefundenen Weg als Liste zurück
		return Path;
	}

	// dient dem extrahieren von Knoten aus der Gesamtliste... wird später durch DB anfrage ersetzt.
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
