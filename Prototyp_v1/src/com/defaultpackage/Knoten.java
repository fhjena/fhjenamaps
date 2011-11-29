package com.defaultpackage;

import java.util.Comparator;

import android.graphics.Point;

public  class Knoten implements Comparable<Knoten>{
	public int ID;
	public int F;
	public int G;
	public int H;
	public Knoten Vorg; // Vorgängerknoten
	public int[] AngrKn_ID; // Angrenzende Knoten
	public int x;	// x-,y- & z-Koordinaten
	public int y;
	public int z;
	private Point BildKoords;
	
	
	
	public Knoten(int ID, int F, int G, int H){
		this.ID = ID;
		this.F = F;
		this.G = G;
		this.H = H;
	}
	
	public Knoten(int ID, int X, int Y, int Z, int[] angrenzend){ // 
		this.ID = ID;
		this.x = X;
		this.y = Y;
		this.z = Z;
		this.AngrKn_ID = angrenzend;
//		this.AnzahlAngrenzderKnoten = Anzahl;
	}
		
	public Point getBildKoords(){
		return BildKoords;
	}
	/* compare-Funktions damit Tree Set geornet werden kann */
		public int compareTo(Knoten another) {
		if (this.F< another.F)
			return -1;
			else if (this.F > another.F)
					return 1;
			else
				return -1; // nicht 0, damit unterschiedliche Knoten auch den gleichen F-Wert haben können
	}

	
	
	
}

	
	
	
	
	

