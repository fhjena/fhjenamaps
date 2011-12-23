package eu.AndroidTraining.Dashboard;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.NavigableSet;
import java.util.TreeSet;

import android.graphics.Point;

public class Routenberechnung {
	
	
	
	
	
	/* ------------- A*-Algorithmus ------------- */
	public int Start_ID, Ziel_ID;	
	
    private Knoten SK, ZK, akt_K, angr_K;							// Startknoten, Zielknoten, aktueller Knoten, angrenzender Knoten
    
    private TreeSet<Knoten> open_L = new TreeSet<Knoten>(); 		// TreeSet, da immer geordnet und immer kleinstes Element durchsucht wird
    private LinkedList<Knoten> closed_L = new LinkedList<Knoten>(); // LinkedList, da nach diskreten Elementen gesucht werden muss
    private LinkedList<Knoten> Gesamtliste = new LinkedList<Knoten>();	// Gesamtliste aller Knoten
//    public ArrayList<Integer> Route = new ArrayList<Integer>(); 	// enthält nach Routenberechnung die IDs der Wegknoten
    private ArrayList<Knoten> Route = new ArrayList<Knoten>(); 	// enthält nach Routenberechnung die Wegknoten
    public double h_faktor=  1.0; 								// um Heuristik weniger oder stärker in die Wegfindung einzubeziehen
    private int Wegstrecke = 0;
    
    public Routenberechnung(){
    	//Knoten Ebene0
    	int[] list0 = {1};
    	Gesamtliste.add(new Knoten(0,0,0,0,new Point(300,220),list0));
    	int[] list1 = {0,2,3};
    	Gesamtliste.add(new Knoten(1,3,0,0,new Point(300,280),list1));
    	int[] list2 = {1,5};
    	Gesamtliste.add(new Knoten(2,3,3,0,new Point(220,280),list2));
    	int[] list3 = {1,4};
    	Gesamtliste.add(new Knoten(3,12,0,0,new Point(300,360),list3));
    	int[] list4 = {3,5};
    	Gesamtliste.add(new Knoten(4,15,0,0,new Point(300,485),list4));
    	int[] list5 = {2,6};
    	Gesamtliste.add(new Knoten(5,16,7,0,new Point(170,485),list5));
    	int[] list6 = {5,7};
    	Gesamtliste.add(new Knoten(6,17,10,0,new Point(125,485),list6));
    	int[] list7 = {6};
    	Gesamtliste.add(new Knoten(7,17,13,0,new Point(55,485),list7));
    	
    	
    }
    
    
    public int G_berechnen(int x1, int y1, int z1, int x2, int y2, int z2){ // Berechnung der Strecke zwischen zwei Knoten
    	int i = (int)Math.sqrt( Math.pow(x1-x2, 2) + Math.pow(y1-y2, 2) + Math.pow(z1-z2, 2) );
		return i;
	}
	
	public int H_berechnen(int x1, int y1, int z1){				// Berechnung der Luftlinie
		int i = (int)Math.sqrt( Math.pow(x1-ZK.x, 2) + Math.pow(y1-ZK.y, 2) + Math.pow(z1-ZK.z, 2) );
		return i;
	}
    
	public void Berechne_Weg(int Start_ID, int Ziel_ID){
		SK = Gesamtliste.get(GetIndexOfElement(Gesamtliste, Start_ID)); // Startknoten aus Gesamtliste holen; // hier später: Datenbankabfrage, Erstellung einer neuen Instanz vom Knoten
		SK.G=0;
		SK.F=0;
		ZK = Gesamtliste.get(GetIndexOfElement(Gesamtliste, Ziel_ID));	// Zielknoten aus Liste holen; // hier später: Datenbankabfrage, Erstellung einer neuen Instanz vom Knoten
		
		open_L.add(SK);
	    
	    while((!closed_L.contains(ZK)) && (!open_L.isEmpty())	/* Zielknoten nicht in geschlossener Liste && Liste_offen = empty*/){
	 	   akt_K = open_L.pollFirst();							// aktueller Knoten = Knoten mit niedrigstem f-Wert
	 	  closed_L.add(akt_K);									// aktueller Knoten in geschlossene Liste hinzugefügt
	 	   
	 	   for(int i = 0; i<= akt_K.AngrKn_ID.length-1; i++){	// Für alle angrenzenden Knoten tue:
	 		   
	 		   angr_K = Gesamtliste.get(GetIndexOfElement(Gesamtliste, akt_K.AngrKn_ID[i])); // hier später: Datenbankabfrage, Erstellung einer neuen Instanz vom Knoten
	 		   
	 		   if(closed_L.contains(angr_K)){/*nichts*/} 		// wenn schon in geschlossener Liste, dann zue nichts
	 		   else{											// ansonsten (wenn nicht in geschlossener Liste)
	 			   if(open_L.contains(angr_K)){					// wenn schon in offener Liste
	 				   
	 				   if(G_berechnen(akt_K.x,akt_K.y,akt_K.z,angr_K.x,angr_K.y,angr_K.z) < G_berechnen(angr_K.Vorg.x,angr_K.Vorg.y,angr_K.Vorg.z,angr_K.x,angr_K.y,angr_K.z)){ /* if(g von aktuellen Knoten zu angrenzen Knoten < angrenzenden zu engrenzenden.vorgänger */
	 					   angr_K.Vorg = akt_K;
	 				   }
	 			   }
	 			   
	 			   else{										// wenn noch nicht in offener Liste
	 				   
	 				   angr_K.Vorg = akt_K;						// aktuellen Knoten als Vorgänger von angrenzenden Knoten eintragen
	 				   angr_K.G = angr_K.Vorg.G + G_berechnen(angr_K.x,angr_K.y,angr_K.z,angr_K.Vorg.x,angr_K.Vorg.y,angr_K.Vorg.z);
	 				   angr_K.F = (int) ((int)angr_K.G + (int)H_berechnen(angr_K.x,angr_K.y,angr_K.z)*h_faktor);
	 				  open_L.add(angr_K);						// zu offener Liste hinzufügen
	 				  
	 			   }
	 		   }
	 	   }
	    }
//	    // speichert IDs in Array
//	    if(!open_L.isEmpty()){									// Wenn Zielknoten gefunden
//		    Knoten Knoten1 = ZK;
//		    int IDcount=-1;
//		    while(IDcount!=SK.ID){								// "Gehe Weg Rückwärts" und speicher die zu begehenden Knoten
//		    	IDcount = Knoten1.ID;
//		    	this.Route.add(0, IDcount);
//		    	Knoten1 = Knoten1.Vorg;
//		    	
//		    }
//		    this.Route.add(this.Route.size(),ZK.G); 			// fügt am Ende der Liste die Größe G (entspricht der Länge des Weges) ein
//	    }
//	    else //ansonsten
//	    	this.Route.clear(); 								// Liste leeren  
//	}
	
	 
	//Wegstrecke speichern
	 Wegstrecke = ZK.G;
	// speichert Knoten in Array
    if(!open_L.isEmpty()){									// Wenn Zielknoten gefunden
	    Knoten Knoten1 = ZK;
	    while(Knoten1!=SK){								// "Gehe Weg Rückwärts" und speicher die zu begehenden Knoten
	    	this.Route.add(0, Knoten1);
	    	Knoten1 = Knoten1.Vorg;
	    }
	    this.Route.add(0, SK);
    }
    else //ansonsten
    	this.Route.clear(); 								// Liste leeren  
    
     
}
	
	public int getWegstrecke(){
		return Wegstrecke;
	}
	
	public ArrayList<Knoten> getWeg(){
		return Route;
	}
	
// dient dem extrahieren von Knoten aus der Gesamtliste... wird später durch DB anfrage ersetzt.
	public int GetIndexOfElement(LinkedList<Knoten> Liste, int ID){
		   ListIterator<Knoten> it = Liste.listIterator();
		   while(it.hasNext()){
			   if(it.next().ID == ID){
				   return it.previousIndex();
			   }
		   }
		   return -1;	 
	   }
	
}

