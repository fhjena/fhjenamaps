package org.database;

import static android.provider.BaseColumns._ID;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBase {
	 
	private static final String DATABASE_NAME = "database.db";	//Name der Datenbank-Datei
	private static final int DATABASE_VERSION = 1;				//Datenbankversion
	
	public static final String TABLE_NAME = "database";			//Tabellenname

	   // Spaltennamen der Tabelle
	public static final String NODE_ID = "node_id";			//Knoten ID
	public static final String ROOM = "room";				//Raum
	public static final String FLOOR = "floor";				//Ebene
	public static final String X_VALUE = "x_value";			//x-Wert
	public static final String Y_VALUE = "y_value";			//y-Wert
	public static final String Z_VALUE = "z_value";			//z-Wert
	public static final String NEIGHBOUR_1 = "neighbour_1";	//Nachbar 1
	public static final String NEIGHBOUR_2 = "neighbour_2";	//Nachbar 2
	public static final String NEIGHBOUR_3 = "neighbour_3"; //Nachbar 3
	public static final String NEIGHBOUR_4 = "neighbour_4";	//Nachbar 4
	
 
	private Context context;
	private DataBaseOpenHelper openHelper;	
	private static String[] FROM = { _ID, NODE_ID, ROOM,FLOOR, X_VALUE,Y_VALUE, Z_VALUE, NEIGHBOUR_1, NEIGHBOUR_2, NEIGHBOUR_3, NEIGHBOUR_4 };
	SQLiteDatabase db;
	
	
	public DataBase(Context context){
		this.context = context;
		openHelper = new DataBaseOpenHelper(this.context); 	//Instanz DataBaseOpenHelper
			
 	}
	
  //csv-File einlesen und Datenbank füllen
  public String[] WriteCSVintoDataBase(InputStream sourcePath)
 	{
	  	
	  	db= openHelper.getWritableDatabase();					//Schreibzugriff holen
  		db.delete(TABLE_NAME, null, null);						//alle Spalteninhalte löschen
		
 		String temp[] = null;		//String						
 		String returnValue[];		//String
 		try
 		{
 			
 			BufferedReader br = null;	//Instanz BufferedReader
 			br = new BufferedReader(new InputStreamReader(sourcePath)); //Quellpfad als InputStreamReader übergeben
 			
 			String strLine;			
 			while ((strLine = br.readLine()) != null)	//Zeile für Zeile auslesen
 			{

 				temp = strLine.split(";");				//String bei ; splitten
 			
 				//Zeile in Datenbank schreiben
 				try {
 					addData(temp[0],temp[1],temp[2],temp[3],temp[4],temp[5],temp[6],temp[7],temp[8],temp[9]); 

 				} finally {
 					db.close(); 	//Datenbank schließen
 				}
 			}
 			// alle Streams und Reader schließen
 			if (br != null)
 			{
 				br.close();
 			}
 		}
 		catch (FileNotFoundException e)
 		{
 			e.getMessage();
 			e.printStackTrace();
 		}
 		catch (IOException e)
 		{
 			e.getMessage();
 			e.printStackTrace();
 		}
 		
 		returnValue=temp;
 		
 		return returnValue;
 	}
  
  //Datenbank füllen
  	public void addData(String node_id, String room, String floor, String x_value, String y_value, String z_value, String neighbour_1, String neighbour_2, String neighbour_3, String neighbour_4) {
   	  		
	   		db = openHelper.getWritableDatabase();					//Schreibzugriff holen
	       	ContentValues values = new ContentValues();				//Instanz ContentValues

	   	      final int temp_node_id = Integer.parseInt(node_id);	//String in INT casten
	  	  	  values.put(NODE_ID, temp_node_id );					//Knoten ID einfügen
	  	  	  
	  	      values.put(ROOM, room);								//Raumnummer einfügen
	  	      
	  	      final int temp_floor = Integer.parseInt(floor);		//String in INT casten
	  	      values.put(FLOOR, temp_floor);						//Ebene einfügen
	  	      
	  	      final int temp_x_value = Integer.parseInt(x_value);	//String in INT casten
	  	      values.put(X_VALUE, temp_x_value);					//x-Wert einfügen
	  	      
	  	      final int temp_y_value = Integer.parseInt(y_value);	//String in INT casten
	  	      values.put(Y_VALUE, temp_y_value);					//y-Wert einfügen
	  	      
	  	      final int temp_z_value = Integer.parseInt(z_value);	//String in INT casten
	  	      values.put(Z_VALUE, temp_z_value );					//z-Wert einfügen
	  	      
	  	      final int temp_neighbour_1 = Integer.parseInt(neighbour_1);	//String in INT casten
	  	      values.put(NEIGHBOUR_1, temp_neighbour_1);					//Nachbar 1 einfügen	
	  	      
	  	      final int temp_neighbour_2 = Integer.parseInt(neighbour_2);	//String in INT casten
	  	      values.put(NEIGHBOUR_2, temp_neighbour_2);					//Nachbar 2 einfügen
	  	      
	  	      final int temp_neighbour_3 = Integer.parseInt(neighbour_3);	//String in INT casten
	  	      values.put(NEIGHBOUR_3, temp_neighbour_3);					//Nachbar 3 einfügen
	  	      
	  	      final int temp_neighbour_4 = Integer.parseInt(neighbour_4);	//String in INT casten
	  	      values.put(NEIGHBOUR_4, temp_neighbour_4);					//Nachbar 4 einfügen
	  	      
	  	      db.insertOrThrow(TABLE_NAME, null, values);	  		//Werte in Datenbank schreiben	      
	  	     
	}

	//Cursordaten aus Knoten ID beziehen
	public Cursor getDatafromNodeId(int node_id) {
  	
		db = openHelper.getReadableDatabase();				//Lesezugriff holen
		Cursor c = db.query(TABLE_NAME, FROM,  
              "NODE_ID like " + node_id, null, null, null, null);	//Abfrage auf Knoten ID

      return c;
   }
  
	//Cursordaten aus Raumnummer beziehen
	public Cursor getDatafromRoom(String room) {
  	
		db = openHelper.getReadableDatabase();				//Lesezugriff holen
		Cursor c = db.query(TABLE_NAME, FROM,  
              "ROOM like " + room, null, null, null, null);	//Abfrage auf Raumnummer
   
     return c;
   }
  
//-------------------------------------------------------------------------------------------
//OpenHelper-Klasse
//-------------------------------------------------------------------------------------------
 
   	private class DataBaseOpenHelper extends SQLiteOpenHelper {	
   
   public DataBaseOpenHelper(Context context) {	  
		super(context, DATABASE_NAME, null, DATABASE_VERSION); //Konstruktor
	}

   @Override
   public void onCreate(SQLiteDatabase db) { 				//Datenbank mit allen Spalten erzeugen
      db.execSQL("CREATE TABLE " + TABLE_NAME + " (" + _ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, "
    		+ NODE_ID + " INTEGER," 
            + ROOM + " TEXT NOT NULL," 
            + FLOOR + " INTEGER,"
            + X_VALUE + " INTEGER,"
            + Y_VALUE + " INTEGER,"
            + Z_VALUE + " INTEGER,"
            + NEIGHBOUR_1 + " INTEGER,"
            + NEIGHBOUR_2 + " INTEGER,"
            + NEIGHBOUR_3 + " INTEGER,"
            + NEIGHBOUR_4 + " INTEGER);");
   }

   @Override
   public void onUpgrade(SQLiteDatabase db, int oldVersion, //Upgrade Datenbank, falls nötig 
         int newVersion) {
      db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
      onCreate(db);
   		}
	}
   
}

