package org.database;

import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class CallDataBase extends Activity {
	
	InputStream Dateiname=null;					//Dateiname des einzulesenen Files
	private DataBase myDB;						//Datenbank
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        myDB = new DataBase(this);      					//Instanz Datenbank
        
    }
    
    //Datenbank aktualisieren
    public void button_DB(View view) {
    	
    	//Zugriff auf Assets-Ordner in dem CSV-Datei liegt
    	AssetManager assetManager = getAssets();		
		try {
			Dateiname = assetManager.open("DataBase.csv");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	
	myDB.WriteCSVintoDataBase(Dateiname);	//CSV-Datei in Datenbank �bertragen
		
    }
    
    
	 public void go(View view) {  	
	    
		EditText start_view = (EditText) findViewById(R.id.tx_start);	//Eingabefeld Startpunkt �ber ID festlegen
		String start = start_view.getText().toString();					//String Startraum
		
		EditText destination_view = (EditText) findViewById(R.id.tx_destination);	//Eingabefeld Startpunkt �ber ID festlegen
		String destination = destination_view.getText().toString();		//String Zielraum
		
	//	Cursor cursorNode = myDB.getDatafromNodeId(2); //Datenbankzugriff Knoten ID
		
		Cursor cursorRoom1 = myDB.getDatafromRoom(start);		//Datenbankzugriff Startraum
		Cursor cursorRoom2 = myDB.getDatafromRoom(destination);	//Datenbankzugriff Zielraum
		
//		Cursor cursorRoom1 = myDB.getDatafromRoom('05.02.01');	//Datenbankzugriff genau dieser Raum
//		Cursor cursorRoom1 = myDB.getDatafromRoom('%05.02.01%');//Datenbankzugriff dieser Raum kommt mit im String vor 
	
		
	//	showData(cursorRoom1); 
	//	showData(cursorRoom2); 
		
		 TextView test = (TextView) findViewById(R.id.test); 
		 test.setText(showData(cursorRoom1));					//zum Test Cursordaten Startraum ausgeben
	
		 TextView test2 = (TextView) findViewById(R.id.test2); 
		 test2.setText(showData(cursorRoom2));					//zum Test Cursordaten Zielraum ausgeben
		
	}
    public StringBuilder showData(Cursor cursor) {
        // alles in einen String schreiben
        StringBuilder builder = new StringBuilder( 
              "Saved Data:\n");
        while (cursor.moveToNext()) { 
          
        	//Einzelne Eintr�ge auslesen
           long node_id = cursor.getInt(1);
           String room = cursor.getString(2);
           long floor = cursor.getInt(3);
           long x_value = cursor.getInt(4);
           long y_value = cursor.getInt(5);
           long z_value = cursor.getInt(6);
           long neighbour_1 = cursor.getInt(7);
           long neighbour_2 = cursor.getInt(8);
           long neighbour_3 = cursor.getInt(9);
           long neighbour_4 = cursor.getInt(10);
         //  builder.append(id).append(": "); 
           builder.append(node_id).append(": ");
           builder.append(room).append(":");
           builder.append(floor).append(":");
           builder.append(x_value).append(":");
           builder.append(y_value).append(":");
           builder.append(z_value).append(":");
           builder.append(neighbour_1).append(":");
           builder.append(neighbour_2).append(":");
           builder.append(neighbour_3).append(":");
           builder.append(neighbour_4).append("\n");
         
        }
		return builder;
    }
}