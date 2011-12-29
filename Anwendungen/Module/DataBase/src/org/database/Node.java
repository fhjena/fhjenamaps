package org.database;


import java.util.ArrayList;

import android.database.Cursor;
import android.graphics.Point;

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
	private Point PictureCoords; // x- & y- Koordinaten (Bild)
	private int floorID;

	public Node(int ID, int X, int Y, int Z, Point p, ArrayList<Integer> adjacent) { // Konstruktor zum Initialisieren
		this.ID = ID;
		this.x = X;
		this.y = Y;
		this.z = Z;
		this.Neighbour_ID = adjacent;
		this.PictureCoords = p;
	}
	
	public Node(Cursor cursor){
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
	
	public Node(Node a){
		this.ID = a.ID;
		this.x = a.x;
		this.y = a.y;
		this.z = a.z;
		this.Neighbour_ID = new ArrayList<Integer>(a.Neighbour_ID);
		this.PictureCoords = a.PictureCoords;
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

	public Point getPictureCoords() {			// gibt Bildkoordinaten zurück
		return PictureCoords;
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
