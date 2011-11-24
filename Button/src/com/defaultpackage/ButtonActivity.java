package com.defaultpackage;

import android.app.Activity;
import android.os.Bundle;
import android.view.*;
import android.view.View.*;
import android.widget.*;

public class ButtonActivity extends Activity {
	Button testbutton;
	
    public void onCreate(Bundle icicle) { 
        super.onCreate(icicle);
        testbutton = new Button(this);
        testbutton.setText("smart");
        setContentView(testbutton);
        testbutton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				testbutton.setText("stupid");
				
			}
		});

    }
}