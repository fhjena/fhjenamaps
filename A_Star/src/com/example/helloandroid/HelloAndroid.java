package com.example.helloandroid;

import java.util.LinkedList;
import java.util.ListIterator;
import java.util.TreeSet;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class HelloAndroid extends Activity {
   /** Called when the activity is first created. */
   @Override
   public void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
   
       TreeSet<Knoten> Liste_offen = new TreeSet<Knoten>(/*new F_Comparator()*/);
//       Knoten Knoten1 = new Knoten(1,3,3,3);
//       Liste_offen.add(new Knoten(1,3,3,3));
//       Liste_offen.add(new Knoten(6,3,3,3));
//       Liste_offen.add(new Knoten(4,4,5,5)); 
//       Liste_offen.add(new Knoten(2,5,5,5));
//       Liste_offen.add(new Knoten(5,1,1,1));
//       
//       //TreeSet<int> Liste_offen = new TreeSet<int>();
       
       
      
       
       Routenberechnung Weg = new Routenberechnung();
       Weg.Berechne_Weg(10, 7);
       String str  = Weg.Route.toString();
       
       
       
       TextView tv = new TextView(this);
//       tv.setText("String:" + Liste_offen.pollFirst().ID + "\n" + Liste_offen.pollFirst().ID+ "\n"+Liste_offen.pollFirst().ID + "\n");
       tv.setText("String:" + str +"\n");
	   setContentView(tv);
	   
	   
       
       
       
       
       
       
   }
   
   
}





