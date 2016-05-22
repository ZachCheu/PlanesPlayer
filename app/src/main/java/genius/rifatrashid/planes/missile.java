package genius.rifatrashid.planes;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;

/**
 * Created by ZachCheu on 5/21/16.
 */
public class missile {
    private Bitmap missileBit;
    private int x,y;
    private boolean shouldTurn = true;
    private double deltax,deltay;
    private int currentRotate;
    private int rotateGoal;
    private Matrix matrix;
    private int rate;
    private int timer = 0;
    private boolean run;
    public int playerhitlength, missilehitlength;
    public Point topLeft = new Point((VAR.screenWidth/2)-(playerhitlength/2),(VAR.screenHeight/2)-(playerhitlength/2));
    public Point topRight = new Point((VAR.screenWidth/2)+(playerhitlength/2),(VAR.screenHeight/2)-(playerhitlength/2));
    public Point botLeft = new Point((VAR.screenWidth/2)-(playerhitlength/2),(VAR.screenHeight/2)+(playerhitlength/2));
    public Point botRight = new Point((VAR.screenWidth/2)+(playerhitlength/2),(VAR.screenHeight/2)+(playerhitlength/2));

    public missile(Bitmap missleBit,int x, int y, int rate,boolean run){
        this.x= x;
        this.y= y;
        this.missileBit = missleBit;
        matrix = new Matrix();
        this.rate = rate;
        this.run = run;
    }
    public void Draw(Canvas canvas){
        if(run) {
            timer++;
            matrix.reset();
            if(!VAR.isDead) {
                this.x -= 30 * Math.cos((double) VAR.currentRotate * (Math.PI / 180));
                this.y -= 30 * Math.sin((double) VAR.currentRotate * (Math.PI / 180));
            }
            deltax = 35 * Math.cos((double) currentRotate * (Math.PI / 180));
            deltay = 35 * Math.sin((double) currentRotate * (Math.PI / 180));
            this.x += deltax;
            this.y += deltay;
            matrix.postTranslate(-missileBit.getWidth() / 2, -missileBit.getHeight() / 2);

            if(timer >=540 || VAR.isDead){
                rotateGoal = 0;
            } else if (timer < 540) {
                rotateGoal = (int) (Math.toDegrees(Math.atan2(-this.y + VAR.screenHeight / 2, -this.x + VAR.screenWidth / 2)));
            }
            if (timer > 700) {
                timer = 0;
                run = false;
            }
            if (rotateGoal < 0) {
                rotateGoal = 360 + rotateGoal;
            }
            if (currentRotate < 0) {
                currentRotate = 360 + currentRotate;
            }
            if (Math.abs(currentRotate - rotateGoal) <= 3) {
                shouldTurn = false;
            } else {
                shouldTurn = true;
            }
            if (currentRotate < 90 && rotateGoal > 270) {
                currentRotate -= rate;
            } else if (rotateGoal < 90 && currentRotate > 270) {
                currentRotate += rate;
            } else if (rotateGoal < currentRotate && shouldTurn) {
                if (Math.abs(rotateGoal - currentRotate) > 180)
                    currentRotate += rate;
                else
                    currentRotate -= rate;
            } else if (rotateGoal > currentRotate && shouldTurn) {
                if (Math.abs(rotateGoal - currentRotate) > 180)
                    currentRotate -= rate;
                else
                    currentRotate += rate;
            }
            matrix.postTranslate(-missileBit.getWidth() / 2, -missileBit.getHeight() / 2);
            if (currentRotate > 180) {
                currentRotate = currentRotate - 360;
            }
            matrix.postRotate(currentRotate);
            matrix.postTranslate(this.x, this.y);
            canvas.drawBitmap(this.missileBit, matrix, null);
        }
    }
    public float getWidth(){
        return this.missileBit.getWidth();
    }
    public float getHeight(){
        return this.missileBit.getHeight();
    }
    public void run(){
        this.run = true;
    }
    public boolean getRun(){
        return run;
    }
    public int getX(){
        return x;
    }
    public int getY(){
        return y;
    }
    public boolean hitDetect(player plane){
        playerhitlength = (int)(plane.getHeight()+plane.getWidth())/2;
        missilehitlength = (int)(getWidth()+getHeight())/2;
        System.out.println(playerhitlength);
        //System.out.println(x);
        //System.out.println(botLeft.y);
        //System.out.println(topLeft.y);
        if(x>650 && x< 790){
            System.out.println("x hit");
            if(y>1126 && y<1266){
                return true;
            }
        }
        /*if(x+missilehitlength>topLeft.x && x+missilehitlength< topRight.x){
            if(y>botLeft.y && y>topLeft.y){
                System.out.println("hit");
                return true;
            }
        }*/
        return false;
    }
}
