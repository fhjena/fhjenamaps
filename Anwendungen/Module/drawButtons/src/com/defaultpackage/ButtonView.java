package com.defaultpackage;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;

public class ButtonView extends View {
	
	private final Button but;
	private float degrees = 0;
	private int l=0;
	private int t=0;
	private int r=0;
	private int b=0;

	public ButtonView(Context context) {
		super(context);
		but = new Button(context);
		l=-100;
		t=-100;
		r=100;
		b=100;
	}
	
	public void setDegree(float f) {
		degrees = f;
	}
	
	public float getDegree() {
		return degrees;
	}
	
	public void setText(CharSequence cs) {
		but.setText(cs);
	}
	
	public CharSequence getText() {
		return but.getText();
	}
	
	@Override
	public void setOnClickListener(OnClickListener l) {
		super.setOnClickListener(l);
		but.setOnClickListener(l);
	}

	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		but.layout(l, t, r, b);
//        canvas.translate((l-r)/2, (t-b)/2);
		canvas.rotate(degrees);
		but.draw(canvas);
		System.out.println("Lorem ipsum");
	}
}
