package template.gui.namespace;

import android.app.Activity;
import android.os.Bundle;

public class TemplateActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Gui my_gui = new Gui(this);
        
        setContentView(my_gui);
    }
}




    
    
    
