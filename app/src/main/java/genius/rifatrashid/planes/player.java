package genius.rifatrashid.planes;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Matrix;

public class player {
    public int rate = 10;
    private float x = 0;
    private float y = 0;
    private float radius = 100;
    private Matrix matrix ;
    Paint paint;
    private Bitmap player;

    public player(float x, float y, Bitmap player){
        this.x = x;
        this.y = y;
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
        this.player = player;
        matrix = new Matrix();
    }

    public void Draw(Canvas canvas){
        matrix.reset();
        //if(VAR.rotateGoal<)
        //matrix.setTranslate(x, y);
        //Testing
        if(VAR.rotateGoal < 0){
            VAR.rotateGoal = 360+VAR.rotateGoal;
        }
        if(VAR.currentRotate < 0){
            VAR.currentRotate = 360+VAR.currentRotate;
        }
        if(Math.abs(VAR.currentRotate-VAR.rotateGoal)<=3){
            VAR.shouldTurn = false;
        }
        else{
            VAR.shouldTurn = true;
        }
        if(VAR.currentRotate<90&&VAR.rotateGoal>270){
            VAR.currentRotate -=rate;
        }
        else if(VAR.rotateGoal<90&&VAR.currentRotate>270){
            VAR.currentRotate +=rate;
        }
        else if(VAR.rotateGoal<VAR.currentRotate && VAR.shouldTurn){
            if(Math.abs(VAR.rotateGoal-VAR.currentRotate)>180)
                VAR.currentRotate+=rate;
            else
                VAR.currentRotate-=rate;
        }else if(VAR.rotateGoal>VAR.currentRotate && VAR.shouldTurn){
            if(Math.abs(VAR.rotateGoal-VAR.currentRotate)>180)
                VAR.currentRotate-=rate;
            else
                VAR.currentRotate+=rate;
        }
        if(VAR.pressX!=0) {
            matrix.postTranslate(-player.getWidth()/2, -player.getHeight() / 2);
            if(VAR.currentRotate>180){
                VAR.currentRotate=VAR.currentRotate-360;
            }
            matrix.postRotate(VAR.currentRotate);
            matrix.postTranslate(x+player.getWidth()/2,y+player.getHeight()/2);
            //matrix.preRotate((int) (180*(Math.atan(VAR.pressY / VAR.pressX))/Math.PI));
            //System.out.println((int) (180*(Math.atan(VAR.pressY / VAR.pressX))/Math.PI));
        }
        else{
            matrix.postTranslate(x,y);
        }
        canvas.drawBitmap(this.player, matrix, null);
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getHeight(){
        return this.player.getHeight();
    }

    public float getWidth(){
        return this.player.getWidth();
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }


}
