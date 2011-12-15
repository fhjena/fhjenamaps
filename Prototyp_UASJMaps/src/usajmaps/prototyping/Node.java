package usajmaps.prototyping;


import android.graphics.Point;

public class Node implements Comparable<Node> {
	private int ID;					// Knoten-ID
	private int F;					// F-Wert
	private int G;					// G-Wert (Distanz zwischen zwei Knoten)
	private int H;					// Wert fü Distanz zwischen Knoten und Zielknoten
	private Node Parent; 			// Vorgängerknoten
	private int[] Neighbour_ID; 	// Nachbarknoten
	private int x; 					// x-,y- & z-Koordinaten(real)
	private int y;
	private int z;
	private Point PictureCoords; // x- & y- Koordinaten (Bild)

	public Node(int ID, int X, int Y, int Z, Point p, int[] adjacent) { // Konstruktor zum Initialisieren
		this.ID = ID;
		this.x = X;
		this.y = Y;
		this.z = Z;
		this.Neighbour_ID = adjacent;
		this.PictureCoords = p;
	}
	
	public Node getParent(){				// gibt Vorgänergknoten zurück
		return Parent;
	}
	
	public int[] getNeigbour_ID(){			// gibt Nachbarknoten-IDs zurück
		return Neighbour_ID;
	}

	public Point getPictureCoords() {			// gibt Bildkoordinaten zurück
		return PictureCoords;
	}
	
	public int getID() {					// gibt Knoten-ID zurück
		return ID;
	}
	
	public int getF() {						// gibt F-Wert zurück
		return F;
	}
	
	public int getG() {						// gibt G-Wert zurück
		return G;
	}
	
	public int getH() {						// gibt H-Wert zurück
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
	
	public void setG(int g){				// setzt G-Wert
		G=g;
	}
	
	public void setH(int h){				// setzt H-Wert
		H=h;
	}
	
	public void setF(int f){				// setzt F-Wert
		F=f;
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
