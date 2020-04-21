package com.example.magic8;

import android.animation.LayoutTransition;
import android.annotation.SuppressLint;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.TransactionTooLargeException;
import android.transition.TransitionManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity {

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;

    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    MagicDatabase db;
    View mContentView;




    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            /*mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);*/
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;

    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Shake

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {

            @Override
            public void onShake(int count) {
            handle();
            }
        });


        //DB
        db = Room.databaseBuilder(getApplicationContext(), MagicDatabase.class, "magicBase").fallbackToDestructiveMigration().build();
        super.onCreate(savedInstanceState);

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                if (db.answerDao().getRowCount() == 0) {
                    System.out.println("GENERATING ANSWERS");
                    List<Answer> defaultAnswers = new ArrayList<>();
                    defaultAnswers.add(new Answer("Se on varmaa."));
                    defaultAnswers.add(new Answer("Asia on ilmeinen."));
                    defaultAnswers.add(new Answer("Epäilemättä."));
                    defaultAnswers.add(new Answer("Kyllä, varmasti."));
                    defaultAnswers.add(new Answer("Voit luottaa siihen."));
                    defaultAnswers.add(new Answer("Uskoakseni kyllä."));
                    defaultAnswers.add(new Answer("Todennäköisesti."));
                    defaultAnswers.add(new Answer("Vaikuttaa hyvältä."));
                    defaultAnswers.add(new Answer("Kyllä."));
                    defaultAnswers.add(new Answer("Kaikki merkit viittaavat siihen."));

                    defaultAnswers.add(new Answer("Olen epävarma, kysy myöhemmin uudelleen."));
                    defaultAnswers.add(new Answer("Kysy myöhemmin uudelleen."));
                    defaultAnswers.add(new Answer("On parempi, etten kerro nyt."));
                    defaultAnswers.add(new Answer("Juuri nyt en osaa sanoa."));
                    defaultAnswers.add(new Answer("Keskity ja kysy uudelleen."));

                    defaultAnswers.add(new Answer("Älä luota siihen."));
                    defaultAnswers.add(new Answer("Vastaukseni on ei."));
                    defaultAnswers.add(new Answer("Tietojeni mukaan ei."));
                    defaultAnswers.add(new Answer("Ei vaikuta hyvältä."));
                    defaultAnswers.add(new Answer("Epäilen suuresti."));

                    db.answerDao().addDefaultAnswers(defaultAnswers);
                    System.out.println(db.answerDao().getRowCount() + " ROWS AFTER ADD");
                }
            }
        });


        setContentView(R.layout.activity_fullscreen);

        mVisible = true;
        //mControlsView = findViewById(R.id.fullscreen_content_controls);
        //mContentView = findViewById(R.id.fullscreen_content);


        // Set up the user interaction to manually show or hide the system UI.
        /*mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });*/

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        //findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);

        Button answerbutton = findViewById(R.id.vastausbtn);
        answerbutton.setOnClickListener(new View.OnClickListener() {
            boolean visible;

            @Override
            public void onClick(View view) {
            handle();

            }
        });
    }

    void handle() {
        TextView eight = findViewById(R.id.kasiText);
        eight.setVisibility(View.GONE);
        ViewGroup root = findViewById(R.id.root);
        TransitionManager.beginDelayedTransition(root);
        root.findViewById(R.id.answerText).setVisibility(View.GONE);
        new dbRunner().execute();
        root.findViewById(R.id.answerText).setVisibility(View.VISIBLE);
    }

    void setAnswerText(String result) {
        TextView answerView = findViewById(R.id.answerText);
        answerView.setText(result);
    }


    @Override
    public void onResume() {
        super.onResume();
        // Add the following line to register the Session Manager Listener onResume
        mSensorManager.registerListener(mShakeDetector, mAccelerometer,	SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        // Add the following line to unregister the Sensor Manager onPause
        mSensorManager.unregisterListener(mShakeDetector);
        super.onPause();
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        //mControlsView.setVisibility(View.GONE);
        //mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    private class dbRunner extends AsyncTask<Void, Void, Answer> {

        @Override
        protected Answer doInBackground(Void... voids) {
            return db.answerDao().findRandomAnswer();
        }

        @Override
        protected void onPostExecute(Answer result) {
            setAnswerText(result.answertext);
        }
    }


}

