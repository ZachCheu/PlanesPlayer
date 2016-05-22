package genius.rifatrashid.planes;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

/**
 * Created by rifatrashid on 5/10/16.
 */
public class BackgroundBitmap {
    private int x, y, width, height;
    private Bitmap BigBackground;
    //private Bitmap screenBackground;
    private double deltaX = 0;
    private double deltaY = 0;

    public BackgroundBitmap() {

    }

    public BackgroundBitmap(Bitmap BigBackground, int width, int height) {
        this.BigBackground = BigBackground;
        this.width = width;
        this.height = height;
        this.x = BigBackground.getWidth() / 2 - width / 2;
        this.y = BigBackground.getHeight() / 2 - height / 2;
        deltaX = 0;
    }

    public void resetCoordinates() {
        this.x = width;
        this.y = BigBackground.getHeight() / 2 - height / 2;
        System.out.println("Players X coordinate reset");
    }

    /**
     @Param Canvas
     used to draw objects
     */
    public void Draw(Canvas canvas) {
        //screenBackground = Bitmap.createBitmap(BigBackground, this.x, this.y, width, height);
        //calculate the distance between device top right X coordinate and the larger background
        deltaX = 25*Math.cos((double)VAR.currentRotate* (Math.PI/180));
        deltaY = 25*Math.sin((double) VAR.currentRotate * (Math.PI / 180));
        this.x += deltaX;
        this.y += deltaY;
        /*System.out.println("Current Rotate: "+VAR.currentRotate);
        System.out.println("Angle: "+(double)VAR.currentRotate* (Math.PI/180));
        System.out.println("Cos: "+ 5*Math.cos((double)VAR.currentRotate* (Math.PI/180)));
        System.out.println("dX:"+deltaX+"   dY:"+deltaY);*/
        int distanceFromBorderx = this.BigBackground.getWidth() - (this.x + this.width);
        int distanceFromBordery = this.BigBackground.getHeight()-(this.x+this.width);
        if(this.x >= this.BigBackground.getWidth()){
            this.x = 2;
            System.out.println("Reset Player Location");
        }
        if(this.x<=0){
            this.x=this.BigBackground.getWidth();
        }
        if(this.y>=this.BigBackground.getHeight()-VAR.screenHeight){
            this.y = 2;
        }
        System.out.println(this.x);
        if(this.y <=0){
            this.y=this.BigBackground.getHeight()-VAR.screenHeight;
        }
        //System.out.println(canvas.getWidth() + " " + canvas.getHeight() + " " + canvas.getDensity());
        if (distanceFromBorderx <= 0) {
            //draw 2 part bitmaps
            canvas.drawBitmap(BigBackground, new Rect(this.x, this.y, this.x + this.width - Math.abs(distanceFromBorderx), this.y + this.height), new Rect(0, 0, this.width - Math.abs(distanceFromBorderx), this.height), null);
            canvas.drawBitmap(BigBackground, new Rect(0, this.y, Math.abs(distanceFromBorderx), this.y + this.height), new Rect(this.width - Math.abs(distanceFromBorderx), 0, this.width, this.height), null);
        } else if (distanceFromBordery <= 0) {
            //draw 2 part bitmaps
            canvas.drawBitmap(BigBackground, new Rect(this.x, this.y, this.x + this.width - Math.abs(distanceFromBordery), this.y + this.height), new Rect(0, 0, this.width - Math.abs(distanceFromBordery), this.height), null);
            canvas.drawBitmap(BigBackground, new Rect(0, this.y, Math.abs(distanceFromBordery), this.y + this.height), new Rect(this.width - Math.abs(distanceFromBordery), 0, this.width, this.height), null);
        }
        else {
            canvas.drawBitmap(BigBackground, new Rect(this.x, this.y, this.x + this.width, this.y + this.height), new Rect(0, 0, this.width, this.height), null);
        }
    }


    public Bitmap getBigBackground() {
        return this.BigBackground;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
