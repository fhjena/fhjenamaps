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

import android.database.Cursor;

public class Node implements Comparable<Node> {
	private int ID;					// Knoten-ID
	private float F;					// F-Wert
	private float G;					// G-Wert (Distanz zwischen zwei Knoten)
	private float H;					// Wert fü Distanz zwischen Knoten und Zielknoten
	private Node Parent; 			// Vorgängerknoten
	private ArrayList<Integer> Neighbour_ID; 	// Nachbarknoten
	private int x; 					// x-,y- & z-Koordinaten(real)
	private int y;
	private int z;
	private int floorID;			// EbenenID

	public Node(int ID, int X, int Y, int Z, ArrayList<Integer> adjacent) { // Konstruktor zum Initialisieren
		this.ID = ID;
		this.x = X;
		this.y = Y;
		this.z = Z;
		this.Neighbour_ID = adjacent;
	}
	
	public Node(Cursor cursor){		// Konstruktor zum Initialisieren eines Knotens nach DB-Abfrage
		this.Neighbour_ID = new ArrayList<Integer>();
		 while (cursor.moveToNext()) {
			//Einzelne Einträge auslesen
	           this.ID = cursor.getInt(1);
	           this.floorID = cursor.getInt(3);
	           this.x = cursor.getInt(4);
	           this.y = cursor.getInt(5);
	           this.z = cursor.getInt(6);
	           this.Neighbour_ID.add(cursor.getInt(7));
	           this.Neighbour_ID.add(cursor.getInt(8));
	           this.Neighbour_ID.add(cursor.getInt(9));
	           this.Neighbour_ID.add(cursor.getInt(10));
		 }
	}
	
	public Node(Node a){			// Copy-Konstruktor
		this.ID = a.ID;
		this.x = a.x;
		this.y = a.y;
		this.z = a.z;
		this.Neighbour_ID = new ArrayList<Integer>(a.Neighbour_ID);
		this.F = a.F;
		this.G = a.G;
		this.H = a.H;
		this.Parent = a.Parent;
		this.floorID = a.floorID;
	}
	
	public Node getParent(){				// gibt Vorgänergknoten zurück
		return Parent;
	}
	
	public ArrayList<Integer> getNeigbour_ID(){			// gibt Nachbarknoten-IDs zurück
		return Neighbour_ID;
	}

	public int getID() {					// gibt Knoten-ID zurück
		return ID;
	}
	
	public float getG() {						// gibt G-Wert zurück
		return G;
	}
	
	public float getH() {						// gibt H-Wert zurück
		return H;
	}
	
	public int getX() {						// gibt x-Wert des Knotens zurück
		return x;
	}
	
	public int getY() {						// gibt y-Wert des Knotens zurück
		return y;
	}
	
	public int getZ() {						// gibt z-Wert des Knotens zurück
		return z;
	}
	
	public int getFloorID(){
		return floorID;
	}
	
	public void setG(float g){				// setzt G-Wert
		G=g;
		F=G+H;								// und aktualisiert F-Wert
	}
	
	public void setH(float h){				// setzt H-Wert
		H=h;
		F=G+H;								// und aktualisiert F-Wert
	}
	
	public void setParent(Node P){			// setzt Vorgängerknoten
		Parent = P;
	}
	
	/* compare-Funktions damit Tree Set geornet werden kann */
	public int compareTo(Node another) {
		if (this.F < another.F)
			return -1;
		else if (this.F > another.F)
			return 1;
		else
			return -1; // nicht 0, damit unterschiedliche Knoten auch den
						// gleichen F-Wert haben können
	}

}
