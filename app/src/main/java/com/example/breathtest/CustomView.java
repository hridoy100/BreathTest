package com.example.breathtest;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.view.View;


public class CustomView extends View {
    Paint paint;
    public int radius = 0;
    int val=0;

    public CustomView(Context context) {
        super(context);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.rgb(0, 133, 119));
        //paint.setShader(new LinearGradient(0, 0, 0, radius, Color.parseColor("#008577"), Color.parseColor("#00baa7"), Shader.TileMode.MIRROR));
        paint.setAntiAlias(true);
    }

    public void updateView(int radius) {
        this.radius = radius;
//        paint.setColor(Color.rgb(0, 133+this.val, 119+this.val));
        invalidate();
    }
    public void updateColor(int val){
        paint.setColor(Color.rgb(0, 133+val/2, 119+val/2));
        invalidate();
    }

    public void resetColor(int r, int g, int b) {
        this.paint.setColor(Color.rgb(r,g,b));
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, radius, paint);
    }
}