package eu.AndroidTraining.Dashboard;

import android.graphics.Point;

public class Node implements Comparable<Node> {
	private int ID;
	private int F;
	private int G;
	private int H;
	private Node Parent; // Vorgängerknoten
	private int[] Neighbour_ID; // Nachbarknoten
	private int x; // x-,y- & z-Koordinaten(real)
	private int y;
	private int z;
	private Point PictureCoords; // x- & y- Koordinaten (Bild)

	public Node(int ID, int F, int G, int H) {
		this.ID = ID;
		this.F = F;
		this.G = G;
		this.H = H;
	}

	public Node(int ID, int X, int Y, int Z, Point p, int[] angrenzend) { //
		this.ID = ID;
		this.x = X;
		this.y = Y;
		this.z = Z;
		this.Neighbour_ID = angrenzend;
		this.PictureCoords = p;

		// this.AnzahlAngrenzderKnoten = Anzahl;
	}
	
	public Node getParent(){
		return Parent;
	}
	
	public int[] getNeigbour_ID(){
		return Neighbour_ID;
	}

	public Point getBildKoords() {
		return PictureCoords;
	}
	
	public int getID() {
		return ID;
	}
	
	public int getF() {
		return F;
	}
	
	public int getG() {
		return G;
	}
	
	public int getH() {
		return H;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getZ() {
		return z;
	}
	
	public void setG(int g){
		G=g;
	}
	
	public void setH(int h){
		H=h;
	}
	
	public void setF(int f){
		F=f;
	}
	
	public void setParent(Node P){
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
