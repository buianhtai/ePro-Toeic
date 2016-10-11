package com.mZone.epro.launch.fragment;

import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

public class MyDragShadowBuilder extends View.DragShadowBuilder {

	private static Drawable shadow;

	public MyDragShadowBuilder() {
		// TODO Auto-generated constructor stub
	}

	public MyDragShadowBuilder(View v) {

        // Stores the View parameter passed to myDragShadowBuilder.
        super(v);
        ImageView imgv = (ImageView)v;
        // Creates a draggable image that will fill the Canvas provided by the system.
        shadow = imgv.getDrawable();
    }
	
	@Override
    public void onProvideShadowMetrics (Point size, Point touch){
        // Defines local variables
        int width, height;

        // Sets the width of the shadow to half the width of the original View
        width = getView().getWidth()/3*2;

        // Sets the height of the shadow to half the height of the original View
        height = getView().getHeight()/3*2;

        // The drag shadow is a ColorDrawable. This sets its dimensions to be the same as the
        // Canvas that the system will provide. As a result, the drag shadow will fill the
        // Canvas.
        shadow.setBounds(0, 0, width, height);

        // Sets the size parameter's width and height values. These get back to the system
        // through the size parameter.
        size.set(width, height);

        // Sets the touch point's position to be in the middle of the drag shadow
        touch.set(width / 2, height / 2);
    }

    // Defines a callback that draws the drag shadow in a Canvas that the system constructs
    // from the dimensions passed in onProvideShadowMetrics().
    @Override
    public void onDrawShadow(Canvas canvas) {

        // Draws the ColorDrawable in the Canvas passed in from the system.
        shadow.draw(canvas);
    }



}
