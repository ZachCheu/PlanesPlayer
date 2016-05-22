package genius.rifatrashid.planes;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class GameActivity extends AppCompatActivity implements SurfaceHolder.Callback {
    private Handler handlerApplication;
    private SurfaceHolder _surfaceHolder;
    private SurfaceView _surfaceView;
    private GameLoopThread thread;
    public int timer = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //set view to full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        getWindow().setFlags(0xFFFFFFFF, WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED, WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        //inflate layout
        setContentView(R.layout.activity_main);
        _surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        _surfaceHolder = _surfaceView.getHolder();
        _surfaceHolder.addCallback(this);
        Display device_display = getWindowManager().getDefaultDisplay();
        VAR.screenHeight = device_display.getHeight();
        VAR.screenWidth = device_display.getWidth();

        _surfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        VAR.pressX = (int)event.getX();
                        VAR.pressY = (int)event.getY();
                        VAR.rotateGoal = (int) (Math.toDegrees(Math.atan2(-VAR.screenHeight / 2 + VAR.pressY, -VAR.screenWidth/2 + VAR.pressX)));
                        VAR.pressDown = true;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        VAR.pressX = (int)event.getX();
                        VAR.pressY= (int)event.getY();
                        VAR.rotateGoal = (int) (Math.toDegrees(Math.atan2(-VAR.screenHeight / 2 + VAR.pressY, -VAR.screenWidth/2 + VAR.pressX)));
                        break;
                    case MotionEvent.ACTION_UP:
                        VAR.pressDown = false;
                        VAR.rotateGoal = VAR.currentRotate;
                        break;
                }
                return true;
            }
        });
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread = new GameLoopThread(_surfaceHolder, new Handler());
        thread.setRunning(true);
        thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        thread.setSurfaceSize(width, height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        thread.setRunning(false);
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    class GameLoopThread extends Thread {
        static final long FPS_GAME = 60;
        private boolean run;
        private GamePhysicsThread gamePhysicsThread;
        Bitmap bgScroller;
        Bitmap backgroundDraw;
        BackgroundBitmap backgroundBitmap;

        public GameLoopThread(SurfaceHolder surfaceHolder, Handler handler) {
            _surfaceHolder = surfaceHolder;
            handlerApplication = handler;
            gamePhysicsThread = new GamePhysicsThread();
            run = true;
        }

        public void doStart() {
            synchronized (_surfaceHolder) {
                VAR.startTime = System.currentTimeMillis();
                VAR.player = BitmapFactory.decodeResource(getResources(),R.drawable.player);
                VAR.plane = new player(VAR.screenWidth / 2, VAR.screenHeight / 2,VAR.player);
                VAR.missile = BitmapFactory.decodeResource(getResources(), R.drawable.missile);
                VAR.m1 = new missile(VAR.missile,VAR.screenWidth,450,4,true);
                VAR.m2 = new missile(VAR.missile,-200,-200,6,false);
                VAR.m3 = new missile(VAR.missile,VAR.screenWidth,900,3,false);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inScaled = false;
                bgScroller = BitmapFactory.decodeResource(getResources(), R.drawable.background, options);
                backgroundBitmap = new BackgroundBitmap(bgScroller, VAR.screenWidth, VAR.screenHeight);
            }
        }

        public void run() {
            long ticksFPS = 1000 / FPS_GAME;
            long startTime;
            long sleepTime;

            while (run) {
                Canvas c = null;
                try {
                    c = _surfaceHolder.lockCanvas(null);
                    synchronized (_surfaceHolder) {
                        gamePhysicsThread.update();
                        doDraw(c);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (c != null) {
                        _surfaceHolder.unlockCanvasAndPost(c);
                    }
                }
            }
        }

        /*
         @Param boolean b
         set thread running
         */
        public void setRunning(boolean b) {
            run = b;
        }

        /*
         @Param screen width, screen height
         Force set surfaceView height and width
         */
        public void setSurfaceSize(int width, int height) {
            synchronized (_surfaceHolder) {
                doStart();
            }
        }

        /*
        @Param Canvas
        function: draws all objects to screen
         */
        private void doDraw(Canvas canvas) {
            if (run) {
                canvas.save();
                canvas.drawColor(Color.parseColor("#FFFFFF"));
                backgroundBitmap.Draw(canvas);
                canvas.drawARGB(0, 183, 241, 255);
                VAR.plane.Draw(canvas);
                VAR.m1.Draw(canvas);
                VAR.m2.Draw(canvas);
                VAR.m3.Draw(canvas);

            }
            canvas.restore();
        }

        class GamePhysicsThread {

            public GamePhysicsThread() {

            }

            public void update() {
                backgroundDraw = Bitmap.createBitmap(bgScroller, bgScroller.getWidth()/2 - VAR.screenWidth/2, bgScroller.getHeight()/2 - VAR.screenHeight/2, VAR.screenWidth, VAR.screenHeight);
                timer++;
                if(timer>=180){
                    timer = 0;
                    if(!VAR.m1.getRun()){
                        VAR.m1.run();
                        VAR.m1run = true;
                    }else if(!VAR.m2.getRun()){
                        VAR.m2.run();
                        VAR.m2run = true;
                    }else if(!VAR.m3.getRun()){
                        VAR.m3.run();
                        VAR.m3run = true;
                    }
                }
                if(!VAR.isDead){
                    if(VAR.m1.hitDetect(VAR.plane)){
                        System.out.println("hit");
                    }
                }
            }
        }
    }
}
