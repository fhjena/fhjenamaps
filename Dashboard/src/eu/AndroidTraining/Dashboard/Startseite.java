package eu.AndroidTraining.Dashboard;

import android.app.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Startseite<Ziel, Start> extends Activity {
	
	private int state;
	private int start;
	private int destination;
	EditText start_view;
	EditText destination_view;
	TextView textoutput_start;
	TextView textoutput_destination;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.state_0);
        
    }
    
    public void state0_Routing(final View view){
    	 setContentView(R.layout.state_1);
    	
    }
    public void state1_go(final View view){
    	
    	start_view = (EditText) findViewById(R.id.tx_start);
    	start = Integer.parseInt(start_view.getText().toString());
    	
    	destination_view = (EditText)findViewById(R.id.tx_destination);
    	destination = Integer.parseInt(destination_view.getText().toString());
    	  	
    	textoutput_start = (TextView) findViewById(R.id.Text1);
    	textoutput_start.setText(String.valueOf(start));
    	    
    	textoutput_destination = (TextView) findViewById(R.id.Text2);
    	textoutput_destination.setText(String.valueOf(destination));
    	
 //   	   	 getContentView(R.layout.);
   	
   }
    
 /*   public void wechsleActivity(final View view) {
    	startActivity(new Intent(this, Unteractivity.class));
    	//setContentView(R.layout.unteractivity);
    }*/
    
    

	
}