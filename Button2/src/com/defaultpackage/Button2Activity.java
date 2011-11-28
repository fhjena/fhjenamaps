package com.defaultpackage;

import java.util.Vector;

import org.w3c.dom.Text;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.*;
import android.view.View.*;
import android.widget.*;

public class Button2Activity extends Activity {
	
	private Vector<TableLayout> user_interface = new Vector<TableLayout>();
	private int stat = 1;
	private int display_width;
	private int display_height;
	
	TextView show5_1;
	TextView show5_2;
	Routenberechnung Route;

	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		
		// enable Fullscreen mode:
		requestWindowFeature(Window.FEATURE_NO_TITLE); // hide Title of App while running
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // hide Statusbar while running

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); // set Orientation to vertical
		
		// get Displaydimensions:
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		display_width = metrics.widthPixels;
		display_height = metrics.heightPixels;
		
		show5_1 = new TextView(this);
		show5_2 = new TextView(this);
		
		user_interface.add(initiateUI1());
		user_interface.add(initiateUI2());
		user_interface.add(initiateUI3());
		user_interface.add(initiateUI4());
		user_interface.add(initiateUI5());
		user_interface.add(initiateUI6());
		user_interface.add(initiateUI7());
		
		refreshUI();
	}
	
	private void refreshUI() {
		setContentView(user_interface.elementAt(stat-1));
	}

	private TableLayout initiateUI1() {
		
		// creat four Buttons with OnClickListener:
		Button testbutton1 = new Button(this);
		testbutton1.setText("Show Position");
		testbutton1.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				stat = 2;
				refreshUI();
			}
		});
		
		Button testbutton2 = new Button(this);
		testbutton2.setText("Routing");
		testbutton2.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				stat = 4;
				refreshUI();
			}
		});
		
		Button testbutton3 = new Button(this);
		testbutton3.setText("Campus");
		testbutton3.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				stat = 7;
				refreshUI();
			}
		});
		
		Button testbutton4 = new Button(this);
		testbutton4.setText("Options");
		testbutton4.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				stat = 6;
				refreshUI();
			}
		});
		
		TableLayout tl = new TableLayout(this); // contains Columns of menu
		TableRow tr1 = new TableRow(this); // Row 1 of menu
		TableRow tr2 = new TableRow(this); // Row 2 of menu
		tr1.addView(testbutton1,display_width/2,display_height/2);
		tr1.addView(testbutton2,display_width/2,display_height/2);
		tr2.addView(testbutton3,display_width/2,display_height/2);
		tr2.addView(testbutton4,display_width/2,display_height/2);
		tl.addView(tr1); // add Row in Column
		tl.addView(tr2); // add Row in Column
		

		testbutton1.setEnabled(false);
		testbutton3.setEnabled(false); // TODO erase
		testbutton4.setEnabled(false);
		
		
		return tl;
	}
	
	private TableLayout initiateUI2() {
		// TODO
		return new TableLayout(this);
	}
	
	private TableLayout initiateUI3() {
		// TODO
		return new TableLayout(this);
	}
	
	private TableLayout initiateUI4() {
		final EditText et1 = new EditText(this);
		final EditText et2 = new EditText(this);
		Button testbutton = new Button(this);
		TableLayout tl = new TableLayout(this);
		
		testbutton.setText("Go!");
		testbutton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Route = new Routenberechnung();
				show5_1.setText("" + Integer.parseInt(et1.getText().toString()));
//				show5_2.setText(Integer.parseInt(et2.getText().toString()));
				Route.Berechne_Weg(Integer.parseInt(et1.getText().toString()), Integer.parseInt(et2.getText().toString()));
				show5_2.setText(Route.getWeg().toString());
				
				stat = 5;
				refreshUI();
			}
		});

		tl.addView(et1);
		tl.addView(et2);
		tl.addView(testbutton);
		
		return tl;
	}
	
	private TableLayout initiateUI5() {
		TableLayout tl = new TableLayout(this);
		tl.addView(show5_1);
		tl.addView(show5_2);
		return tl;
	}
	
	private TableLayout initiateUI6() {
		// TODO
		return new TableLayout(this);
	}
	
	private TableLayout initiateUI7() {
		// TODO
		return new TableLayout(this);
	}
}