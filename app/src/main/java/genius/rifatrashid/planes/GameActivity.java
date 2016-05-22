package genius.rifatrashid.planes;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.media.MediaPlayer;
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
import android.widget.TextView;

public class GameActivity extends AppCompatActivity implements SurfaceHolder.Callback {
    private Handler handlerApplication;
    private SurfaceHolder _surfaceHolder;
    private Typeface loading_text_font;
    private TextView loading_text;
    private SurfaceView _surfaceView;
    private GameLoopThread thread;
    public int timer = 0;
    public double timerscore = 0;
    private final Paint scorePaint = new Paint();
    private final Paint highScorePaint = new Paint();
    private final Paint continueGame = new Paint();
    public TextView t, a;
    public MediaPlayer music;
    public MediaPlayer boom;


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
        loading_text_font = Typeface.createFromAsset(getAssets(), "fonts/loader_font.TTF");
        t = (TextView)  findViewById(R.id.titleText);
        a = (TextView) findViewById(R.id.titleText2);
        t.setTypeface(loading_text_font);
        a.setTypeface(loading_text_font);
        music = MediaPlayer.create(this,R.raw.main_music);
        boom = MediaPlayer.create(this,R.raw.explosionsound);
        music.setVolume(0.7f,0.7f);
        //loading_text_font = Typeface.createFromAsset(getAssets(), "fonts/loader_font.TTF");

        _surfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        VAR.pressX = (int)event.getX();
                        VAR.pressY = (int)event.getY();
                        VAR.rotateGoal = (int) (Math.toDegrees(Math.atan2(-VAR.screenHeight / 2 + VAR.pressY, -VAR.screenWidth/2 + VAR.pressX)));
                        VAR.pressDown = true;
                        if(VAR.isDead){
                            VAR.isDead = false;
                            VAR.isDeadAnimation=false;
                            timer = 0;
                            VAR.currentScore = 0;
                            timerscore = System.currentTimeMillis();
                            VAR.m1Hit = false;
                            VAR.m2Hit = false;
                            VAR.m3Hit = false;
                        }
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
                VAR.explosion1 = BitmapFactory.decodeResource(getResources(), R.drawable.explosion1);
                VAR.explosion2 = BitmapFactory.decodeResource(getResources(), R.drawable.explosion2);
                VAR.explosion3 = BitmapFactory.decodeResource(getResources(), R.drawable.explosion3);
                VAR.explosion4 = BitmapFactory.decodeResource(getResources(), R.drawable.explosion4);
                VAR.player = BitmapFactory.decodeResource(getResources(), R.drawable.player);
                VAR.plane = new player(VAR.screenWidth / 2, VAR.screenHeight / 2,VAR.player,VAR.explosion1,VAR.explosion2,VAR.explosion3,VAR.explosion4);
                VAR.missile = BitmapFactory.decodeResource(getResources(), R.drawable.missile);
                VAR.m1 = new missile(VAR.missile,VAR.screenWidth,450,4,true);
                VAR.m2 = new missile(VAR.missile,-200,-200,6,false);
                VAR.m3 = new missile(VAR.missile,VAR.screenWidth,900,3,false);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inScaled = false;
                bgScroller = BitmapFactory.decodeResource(getResources(), R.drawable.background, options);
                backgroundBitmap = new BackgroundBitmap(bgScroller, VAR.screenWidth, VAR.screenHeight);
                scorePaint.setColor(Color.parseColor("#f4bcc4"));
                scorePaint.setTypeface(loading_text_font);
                scorePaint.setTextSize(75);
                highScorePaint.setColor(Color.parseColor(("#f4bcc4")));
                highScorePaint.setTypeface(loading_text_font);
                highScorePaint.setTextSize(125);
                continueGame.setColor(Color.GRAY);
                continueGame.setTypeface(loading_text_font);
                continueGame.setTextSize(50);
                timerscore = System.currentTimeMillis();
                music.start();
                music.setLooping(true);
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
                if(!VAR.isDeadAnimation) {
                    VAR.plane.Draw(canvas);
                }
                if(!VAR.m1Hit){
                    VAR.m1.Draw(canvas);
                }
                if(!VAR.m2Hit) {
                    VAR.m2.Draw(canvas);
                }
                if(!VAR.m3Hit){
                    VAR.m3.Draw(canvas);
                }
                if(!VAR.isDead) {
                    VAR.currentScore = (int)((System.currentTimeMillis()-timerscore)/1000);
                    canvas.drawText("Score: " + VAR.currentScore, VAR.screenWidth / 2 - 162, 2 * VAR.screenHeight / 3 + 250, scorePaint);
                }
                if(VAR.isDeadAnimation){
                    canvas.drawColor(Color.parseColor("#78000000"));
                    if (VAR.currentScore > VAR.globalHighScore) {
                        VAR.globalHighScore= VAR.currentScore;
                    }
                    canvas.drawText("Highscore: " + VAR.globalHighScore, VAR.screenWidth / 2 - 380, VAR.screenHeight / 3 - 100, highScorePaint);
                    canvas.drawText("Score: " + VAR.currentScore, VAR.screenWidth / 2 - 260, VAR.screenHeight / 3, highScorePaint);
                    canvas.drawText("Tap to Continue",VAR.screenWidth / 2-200,2*VAR.screenHeight / 3+300,continueGame);
                }
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
                    //System.out.println("test");
                    if(VAR.m1.hitDetect(VAR.plane)){
                        boom.start();
                        VAR.isDead = true;
                        VAR.m1Hit = true;
                        VAR.m1.ogPos();
                    }
                    if(VAR.m2.hitDetect(VAR.plane)){
                        boom.start();
                        VAR.isDead = true;
                        VAR.m2Hit = true;
                        VAR.m2.ogPos();
                    }
                    if(VAR.m3.hitDetect(VAR.plane)){
                        boom.start();
                        VAR.isDead = true;
                        VAR.m3Hit = true;
                        VAR.m3.ogPos();
                    }
                }
            }
        }
    }
}
