package your.test.touch;

import android.R;
import android.app.Activity;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.FloatMath;
import android.util.Log;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
public class Touch extends Activity implements OnTouchListener {
   private static final String TAG = "Touch" ;
   @Override
   public void onCreate(Bundle savedInstanceState) {
	   super.onCreate(savedInstanceState);
	    setContentView(R.layout.main);
	    final TextView textView = (TextView)findViewById(R.id.textView);
	    // this is the view on which you will listen for touch events
	    final View touchView = findViewById(R.id.touchView);
	    touchView.setOnTouchListener(new View.OnTouchListener() {
//	        @Override
	        public boolean onTouch(View v, MotionEvent event) {
	            textView.setText("Touch coordinates : " +
	                String.valueOf(event.getX()) + "x" + String.valueOf(event.getY()));
	                return true;
	        }
	    });

   }
   public boolean onTouch(View v, MotionEvent event) {
      // Handle touch events here...
	   System.out.println(""+v.getScrollX());
	   return true;
   }
}