package app.exercisetimer.stas.exercisetimer;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.io.IOException;
import java.util.Locale;

public class MainActivityTimer extends AppCompatActivity implements SoundPool.OnLoadCompleteListener {

    String log;

    private InterstitialAd interstitial;

    final int MAX_STREAMS = 3;

    long mills = 300L;

    private int CHECK_LANG = 1;

    SoundPool sp;
    int soundIdShot,soundIdSoundRound, soundGong, soundBreak;
    int soundIdExplosion;

    Typeface face;

    AnimationDrawable mAnimationDrawable, animationDrawableRound;

    private Chronometer mChronometer;

    private Locale mNewLocale;

    private  int AMOUNTS_LAPS = 3;

    Button btnContinueStop;

    private Boolean bool_start = false, bool_start_lang = false;

    private int CHANGE_LAPS = 0, CHANGE_LAPS_2 = 0, CHANGE_LAPS_START = 0;

    private long elapsedMillis = 0;
    private long baseTime;
    private long stopTime;

    final int REQUEST_CODE_LAPS = 1, DIALOG_LANG = 1;

    private Boolean CHECK_STOP_CONTINUE = true, CHECK_STOP_ACTIVE = false, SOUND_CHECK = false, SOUND_CHECK_WORK_BREAK = false;

    private int AMOUNT_WORK_MINUTES = 0, AMOUNT_WORK_SECONDS = 30, AMOUNT_BREAK_MINUTES = 0, AMOUNT_BREAK_SECONDS = 15, AMOUNT_LAPS_CURRENT = 0;
    private int CHANGE_WORK_BREAK = 0, CHANGE_WORK_BREAK_2 = 0, MIN_SEC_WORK = 0, MIN_SEC_BREAK = 0, MIN_SEC_WORK_START = 0, MIN_SEC_BREAK_START = 0;
    private int CHANGE_WORK_BREAK_START = 0, CHANGE_WORK_BREAK__START_BOOL = 0;

    TextView amountLaps, amountLapsGoal, workAmountGoal, breakAmountGoal, amountLapsCurrent, prepare;
    TextView tvHardWork, tvBreak;

    ImageView ivLine, animWorkBreak;

    ProgressBar progressBarRound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.logo_timer);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        super.onCreate(savedInstanceState);
        Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();
        setContentView(R.layout.main_timer);

        if(width==1080 && height==1794) {
            setContentView(R.layout.main_nexus5);
        }
        if(width==1440 && height==2392) {
            setContentView(R.layout.main_nexus6);
        }                                               // 1080 1794
        if(width==768 && height==1184) {
            setContentView(R.layout.main_nexus4);
        }
        if(width<=480 && height<=900) {
            setContentView(R.layout.main_nexus_s);
        }
        if(width==1536 && height==1952) {
            setContentView(R.layout.main_nexus9);
        }
        if(width==800 && height==1184) {
            setContentView(R.layout.main_nexus7);
        }
        if(width==540 && height==960) {
            setContentView(R.layout.special);
        }
  //      if(width==480 && height<900) {
  //          setContentView(R.layout.main_small);
  //      }

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        btnContinueStop = (Button) findViewById(R.id.btnStop);

        // ANIMATION MAIN

        tvBreak = (TextView) findViewById(R.id.textViewBreak);
        tvHardWork = (TextView) findViewById(R.id.textViewHardWork);
        ivLine = (ImageView) findViewById(R.id.imageViewLine);

        // END ANIMATION MAIN

        animWorkBreak = (ImageView) findViewById(R.id.anim_work_break);

        progressBarRound = (ProgressBar) findViewById(R.id.progressBarRound);

        mChronometer = (Chronometer) findViewById(R.id.chronometer);

        amountLaps = (TextView) findViewById(R.id.amountLaps);
        amountLapsGoal = (TextView) findViewById(R.id.laps_amount_goal_id);
        workAmountGoal = (TextView) findViewById(R.id.work_amount_id);
        breakAmountGoal = (TextView) findViewById(R.id.break_amount_id);
        amountLapsCurrent = (TextView) findViewById(R.id.amount_laps_current);
        prepare = (TextView) findViewById(R.id.prepare);

        sp = new SoundPool(MAX_STREAMS, AudioManager.STREAM_MUSIC, 0);
        sp.setOnLoadCompleteListener(this);

        soundIdSoundRound = sp.load(this, R.raw.sound_round, 1);
        soundIdShot = sp.load(this, R.raw.countdown_ready, 1);
        soundGong = sp.load(this, R.raw.sound_gong, 1);
        soundBreak = sp.load(this, R.raw.sound_break, 1);

        Log.d("log", SOUND_CHECK_WORK_BREAK + " MAIN");

        try {
            soundIdExplosion = sp.load(getAssets().openFd("explosion.ogg"), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }


        mChronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {

            @Override
            public void onChronometerTick(Chronometer chronometer) {

                Log.d("log", SOUND_CHECK_WORK_BREAK + " CHRON");

                elapsedMillis = SystemClock.elapsedRealtime() - mChronometer.getBase();          // GO

                if(CHANGE_WORK_BREAK__START_BOOL==0) {

                    if(elapsedMillis > MIN_SEC_WORK_START-3000 && CHANGE_LAPS_START == 1) {
                        sp.play(soundIdSoundRound, 1, 1, 0, 0, 1);
                        sp.play(soundIdExplosion, 1, 1, 0, 0, 1);
                        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        vibrator.vibrate(mills);
                    }

                    if (elapsedMillis > CHANGE_WORK_BREAK_START) {
                        start_func();
                        mChronometer.setBase(SystemClock.elapsedRealtime());
                    }
                    progressBarRound.setProgress((int) elapsedMillis);

                }

                if(CHANGE_WORK_BREAK__START_BOOL==1) {

                    if(elapsedMillis > MIN_SEC_WORK-3000 && CHANGE_LAPS== 1) {
                        sp.play(soundIdSoundRound, 1, 1, 0, 0, 1);
                        sp.play(soundIdExplosion, 1, 1, 0, 0, 1);
                        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        vibrator.vibrate(mills);
                    }

                    if (elapsedMillis > CHANGE_WORK_BREAK) {
                        formula_work_break();
                        mChronometer.setBase(SystemClock.elapsedRealtime());
                    }

                    if (elapsedMillis > CHANGE_WORK_BREAK_2) {
                        formula_work_break2();
                        mChronometer.setBase(SystemClock.elapsedRealtime());
                    }
                    progressBarRound.setProgress((int) elapsedMillis);

                }
            }
        });
    }

    public void start_func() {

        if(CHANGE_LAPS_START==0) {

            if(SOUND_CHECK_WORK_BREAK==false) {
                sp.play(soundGong, 1, 1, 0, 0, 1);
                sp.play(soundIdExplosion, 1, 1, 0, 0, 1);
            }
            SOUND_CHECK_WORK_BREAK = false;

            mChronometer.setTextColor(getResources().getColor(R.color.colorGreen));

            ivLine.setImageResource(R.drawable.line_work);

            progressBarRound.setProgressDrawable(getResources().getDrawable(R.drawable.circular_progress_bar_green));

            animWorkBreak.setBackgroundResource(R.drawable.circle_shape_green_flash);

            tvHardWork.setTextColor(Color.rgb(255,111,1));
            tvBreak.setTextColor(Color.rgb(194,194,194));
            /*
            AMOUNT_WORK_SECONDS = AMOUNT_WORK_SECONDS * 1000;
            AMOUNT_WORK_MINUTES = AMOUNT_WORK_MINUTES * 60;
            AMOUNT_WORK_MINUTES = AMOUNT_WORK_MINUTES * 1000;
            */
            MIN_SEC_WORK_START = AMOUNT_WORK_MINUTES + AMOUNT_WORK_SECONDS;
            CHANGE_WORK_BREAK_START = MIN_SEC_WORK_START;
            progressBarRound.setMax(CHANGE_WORK_BREAK_START);
            CHANGE_LAPS_START=1;

            formula_laps();
            return;
        }

        if(CHANGE_LAPS_START==1) {

            if(SOUND_CHECK_WORK_BREAK==false) {
                sp.play(soundBreak, 1, 1, 0, 0, 1);
                sp.play(soundIdExplosion, 1, 1, 0, 0, 1);
            }
            SOUND_CHECK_WORK_BREAK = false;

            mChronometer.setTextColor(getResources().getColor(R.color.colorBlue));

            ivLine.setImageResource(R.drawable.line);

            progressBarRound.setProgressDrawable(getResources().getDrawable(R.drawable.circular_progress_bar_blue));

            animWorkBreak.setBackgroundResource(R.drawable.circle_shape_blue_flash);

            tvBreak.setTextColor(Color.rgb(255,111,1));
            tvHardWork.setTextColor(Color.rgb(194,194,194));
            /*
            AMOUNT_BREAK_SECONDS = AMOUNT_BREAK_SECONDS * 1000;
            AMOUNT_BREAK_MINUTES = AMOUNT_BREAK_MINUTES * 60;
            AMOUNT_BREAK_MINUTES = AMOUNT_BREAK_MINUTES * 1000;
            */
            MIN_SEC_BREAK_START = AMOUNT_BREAK_MINUTES + AMOUNT_BREAK_SECONDS;
            CHANGE_WORK_BREAK_START = MIN_SEC_BREAK_START;
            progressBarRound.setMax(CHANGE_WORK_BREAK_START);
            CHANGE_LAPS_START = 0;

            return;
        }
    }

    public void formula_work_break() {
        /* WORK */
        if(CHANGE_LAPS == 0) {

            mChronometer.setTextColor(getResources().getColor(R.color.colorGreen));
            ivLine.setImageResource(R.drawable.line_work);
            animWorkBreak.setBackgroundResource(R.drawable.circle_shape_green_flash);
            progressBarRound.setProgressDrawable(getResources().getDrawable(R.drawable.circular_progress_bar_green));
            tvHardWork.setTextColor(Color.rgb(255,111,1));
            tvBreak.setTextColor(Color.rgb(194,194,194));
            CHANGE_WORK_BREAK = MIN_SEC_WORK;
            progressBarRound.setMax(CHANGE_WORK_BREAK);
            CHANGE_LAPS = 1;
            formula_laps();

            if(SOUND_CHECK_WORK_BREAK==false) {
                sp.play(soundGong, 1, 1, 0, 0, 1);
                sp.play(soundIdExplosion, 1, 1, 0, 0, 1);
                SOUND_CHECK_WORK_BREAK = false;
                return;
            }

            return;
        }
        /* BREAK */
        if(CHANGE_LAPS == 1) {

            mChronometer.setTextColor(getResources().getColor(R.color.colorBlue));
            ivLine.setImageResource(R.drawable.line);
            animWorkBreak.setBackgroundResource(R.drawable.circle_shape_blue_flash);
            if(MIN_SEC_BREAK>MIN_SEC_WORK) {
                CHANGE_WORK_BREAK_2 = MIN_SEC_BREAK;
                CHANGE_LAPS_2 = 1;
                formula_work_break2();
            }
            progressBarRound.setProgressDrawable(getResources().getDrawable(R.drawable.circular_progress_bar_blue));
            tvBreak.setTextColor(Color.rgb(255,111,1));
            tvHardWork.setTextColor(Color.rgb(194,194,194));
            CHANGE_WORK_BREAK = MIN_SEC_BREAK;
            progressBarRound.setMax(CHANGE_WORK_BREAK);
            CHANGE_LAPS = 0;

            if(SOUND_CHECK_WORK_BREAK==false) {
                sp.play(soundBreak, 1, 1, 0, 0, 1);
                sp.play(soundIdExplosion, 1, 1, 0, 0, 1);
                SOUND_CHECK_WORK_BREAK = false;
                return;
            }

            return;
        }
    }

    public void formula_work_break2() {
        /* WORK */
        if(CHANGE_LAPS_2 == 0) {

            mChronometer.setTextColor(getResources().getColor(R.color.colorGreen));
            ivLine.setImageResource(R.drawable.line_work);
            animWorkBreak.setBackgroundResource(R.drawable.circle_shape_green_flash);
            progressBarRound.setProgressDrawable(getResources().getDrawable(R.drawable.circular_progress_bar_green));
            tvHardWork.setTextColor(Color.rgb(255,111,1));
            tvBreak.setTextColor(Color.rgb(194,194,194));
            CHANGE_WORK_BREAK_2 = MIN_SEC_WORK;
            progressBarRound.setMax(CHANGE_WORK_BREAK_2);
            CHANGE_LAPS_2 = 1;

            if(SOUND_CHECK_WORK_BREAK==false) {
                sp.play(soundGong, 1, 1, 0, 0, 1);
                sp.play(soundIdExplosion, 1, 1, 0, 0, 1);
                SOUND_CHECK_WORK_BREAK = false;
                return;
            }
            return;
        }
        /* BREAK */
        if(CHANGE_LAPS_2 == 1) {

            mChronometer.setTextColor(getResources().getColor(R.color.colorBlue));
            ivLine.setImageResource(R.drawable.line);
            animWorkBreak.setBackgroundResource(R.drawable.circle_shape_blue_flash);
            progressBarRound.setProgressDrawable(getResources().getDrawable(R.drawable.circular_progress_bar_blue));
            tvBreak.setTextColor(Color.rgb(255,111,1));
            tvHardWork.setTextColor(Color.rgb(194,194,194));
            CHANGE_WORK_BREAK_2 = MIN_SEC_BREAK;
            progressBarRound.setMax(CHANGE_WORK_BREAK_2);
            CHANGE_LAPS_2 = 0;

            if(SOUND_CHECK_WORK_BREAK==false) {
                sp.play(soundBreak, 1, 1, 0, 0, 1);
                sp.play(soundIdExplosion, 1, 1, 0, 0, 1);
                SOUND_CHECK_WORK_BREAK = false;
                return;
            }
            return;
        }
    }

    public void formula_laps() {
        AMOUNT_LAPS_CURRENT = AMOUNT_LAPS_CURRENT + 1;
        amountLapsCurrent.setText(AMOUNT_LAPS_CURRENT + "");
        if(AMOUNT_LAPS_CURRENT>AMOUNTS_LAPS) {

            AMOUNT_LAPS_CURRENT = 1;
            amountLapsCurrent.setText(AMOUNT_LAPS_CURRENT + "");

            mChronometer.setBase(SystemClock.elapsedRealtime());
            mChronometer.stop();
            finish_work();
        }
    }

    public void onStartHardWork(View v) {
        // onStartWork
        if (bool_start == false) {
            new CountDownTimer(3100, 1000) {
                //Здесь обновляем текст счетчика обратного отсчета с каждой секундой
                public void onTick(long millisUntilFinished) {

                    if (SOUND_CHECK == false) {
                        sp.play(soundIdShot, 1, 1, 0, 0, 1);
                        sp.play(soundIdExplosion, 1, 1, 0, 0, 1);
                        SOUND_CHECK = true;
                    }

                    bool_start = true;
                    progressBarRound.setBackgroundResource(R.drawable.anim_round);
                    animationDrawableRound = (AnimationDrawable) progressBarRound.getBackground();
                    animationDrawableRound.stop();

                    face = Typeface.createFromAsset(getAssets(), "fonts/bold_italic.ttf");
                    prepare.setTypeface(face);
                    animWorkBreak.setBackgroundResource(R.drawable.anim_relax);
                    mAnimationDrawable = (AnimationDrawable) animWorkBreak.getBackground();
                    mAnimationDrawable.start();
                    tvHardWork.setVisibility(View.INVISIBLE);
                    tvBreak.setVisibility(View.INVISIBLE);
                    ivLine.setVisibility(View.INVISIBLE);
                    prepare.setVisibility(View.VISIBLE);
                    mChronometer.stop();
                    millisUntilFinished = millisUntilFinished / 1000;
                    if (millisUntilFinished == 0) {
                        onFinish();
                    }
                    prepare.setText(millisUntilFinished + "");
                }

                //Задаем действия после завершения отсчета (высвечиваем надпись "Бабах!"):
                public void onFinish() {
                    SOUND_CHECK_WORK_BREAK = false;              // HERE ERROR (нужно просто поставить на новый раунд и на старт один и тот же звук
                    SOUND_CHECK = false;
                    bool_start = false;
                    mAnimationDrawable.stop();
                    btnContinueStop.setEnabled(true);
                    CHECK_STOP_CONTINUE = true;
                    btnContinueStop.setText(R.string.stop_string);
                    ivLine.setImageResource(R.drawable.line);
                    tvHardWork.setVisibility(View.VISIBLE);
                    tvBreak.setVisibility(View.VISIBLE);
                    ivLine.setVisibility(View.VISIBLE);
                    prepare.setVisibility(View.INVISIBLE);
                    func_delete();
                    mChronometer.setBase(SystemClock.elapsedRealtime());
                    mChronometer.start();
                    progressBarRound.setBackgroundResource(R.drawable.circle_shape);
                    animationDrawableRound.stop();
                }
            }
                    .start();
        }
    }

    public void onStopHardWork(View v) {

        if(elapsedMillis==0) {

            btnContinueStop.setEnabled(false);
            return;
        }
        if(CHECK_STOP_CONTINUE==true) {

            CHECK_STOP_CONTINUE = false;
            btnContinueStop.setText(R.string.continue_string);
            stopTime = elapsedMillis;
            mChronometer.stop();
            return;
        }
        if(CHECK_STOP_CONTINUE==false) {

            CHECK_STOP_CONTINUE = true;
            btnContinueStop.setText(R.string.stop_string);
            baseTime = SystemClock.elapsedRealtime() - stopTime;
            mChronometer.setBase(baseTime);
            mChronometer.start();
            return;
        }
    }

    public void onResetHardWork(View v) {
        SOUND_CHECK_WORK_BREAK = true;
        func_delete();
        CHECK_STOP_CONTINUE = true;
        btnContinueStop.setText(R.string.stop_string);
        mChronometer.setTextColor(Color.WHITE);
        animWorkBreak.setBackgroundResource(R.drawable.circle_shape_orange_flash);
        amountLapsCurrent.setText(AMOUNT_LAPS_CURRENT + "");
        mChronometer.stop();
        mChronometer.setBase(SystemClock.elapsedRealtime());
        progressBarRound.setBackgroundResource(R.drawable.anim_round);
        animationDrawableRound = (AnimationDrawable) progressBarRound.getBackground();
        animationDrawableRound.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            displayInterstitial();
            Intent intent = new Intent();
            intent = new Intent(this, SettingActivity.class);
            startActivityForResult(intent, REQUEST_CODE_LAPS);
            AMOUNT_LAPS_CURRENT = 0;
            amountLapsCurrent.setText(AMOUNT_LAPS_CURRENT + "");
            mChronometer.stop();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_LAPS:

                    SOUND_CHECK_WORK_BREAK = true;
                    CHANGE_WORK_BREAK__START_BOOL = 1;
                    CHANGE_LAPS = 0;
                    CHANGE_LAPS_2 = 0;

                    CHECK_LANG = data.getIntExtra("check_lang", CHECK_LANG);

                    AMOUNTS_LAPS = data.getIntExtra("laps", AMOUNTS_LAPS);
                    AMOUNT_WORK_MINUTES = data.getIntExtra("minutes_work", AMOUNT_WORK_MINUTES);
                    AMOUNT_WORK_SECONDS = data.getIntExtra("seconds_work", AMOUNT_WORK_SECONDS);
                    AMOUNT_BREAK_MINUTES = data.getIntExtra("minutes_break", AMOUNT_BREAK_MINUTES);
                    AMOUNT_BREAK_SECONDS = data.getIntExtra("seconds_break", AMOUNT_BREAK_SECONDS);
                    saveText();
                    /* main formula */
                    MIN_SEC_WORK = data.getIntExtra("result_work", MIN_SEC_WORK);
                    MIN_SEC_BREAK = data.getIntExtra("result_break", MIN_SEC_BREAK);
                    formula_work_break();
                    formula_work_break2();

                    /* end main formula */
                    workAmountGoal.setText(AMOUNT_WORK_MINUTES+ " : " + AMOUNT_WORK_SECONDS);
                    breakAmountGoal.setText(AMOUNT_BREAK_MINUTES + " : " + AMOUNT_BREAK_SECONDS);
                    amountLaps.setText(AMOUNTS_LAPS + "");
                    amountLapsGoal.setText(AMOUNTS_LAPS + "");

                    animWorkBreak.setBackgroundResource(R.drawable.circle_shape_orange_flash);
                    tvHardWork.setTextColor(getResources().getColor(R.color.colorOrange));
                    ivLine.setImageResource(R.drawable.line);
                    mChronometer.setTextColor(getResources().getColor(R.color.colorWhite));
                    mChronometer.setBase(SystemClock.elapsedRealtime());

                    if(CHECK_LANG==1) {
                        setLocale("ru");
                    }
                    if(CHECK_LANG==0) {
                        setLocale("en");
                    }

                    btnContinueStop.setText(getResources().getString(R.string.stop_string));
                    break;
            }

        }
    }

    public void finish_work() {
        new CountDownTimer(3000, 800) {
            //Здесь обновляем текст счетчика обратного отсчета с каждой секундой
            public void onTick(long millisUntilFinished) {
                tvBreak.setVisibility(View.INVISIBLE);
                ivLine.setVisibility(View.INVISIBLE);
                tvHardWork.setText(R.string.great_work);
                millisUntilFinished = millisUntilFinished / 1000;
                if(millisUntilFinished==0) {
                    onFinish();
                }

            }

            public void onFinish() {
                tvBreak.setVisibility(View.VISIBLE);
                ivLine.setVisibility(View.VISIBLE);
                btnContinueStop.setEnabled(false);
                mChronometer.setTextColor(getResources().getColor(R.color.colorWhite));
                tvHardWork.setTextColor(getResources().getColor(R.color.colorOrange));
                ivLine.setImageResource(R.drawable.line);
                animWorkBreak.setBackgroundResource(R.drawable.circle_shape_orange_flash);
                tvHardWork.setText(R.string.main_string);
                progressBarRound.setBackgroundResource(R.drawable.anim_round);
                animationDrawableRound = (AnimationDrawable) progressBarRound.getBackground();
                animationDrawableRound.start();
            }
        }
                .start();
    }

    protected void onStart() {
        super.onStart();
        loadText();
    if(!bool_start_lang) {
        showDialog(DIALOG_LANG);
    }
        progressBarRound.setBackgroundResource(R.drawable.anim_round);
        animationDrawableRound = (AnimationDrawable) progressBarRound.getBackground();
        animationDrawableRound.start();
        progressBarRound.setVisibility(View.VISIBLE);
        progressBarRound.setProgress((int)elapsedMillis);

        tvBreak.setTextColor(Color.WHITE);
        animWorkBreak.setVisibility(View.VISIBLE);
        face = Typeface.createFromAsset(getAssets(), "fonts/original.ttf");
        tvHardWork.setTypeface(face);
        tvBreak.setTypeface(face);
// Создаём межстраничное объявление
        interstitial = new InterstitialAd(this);
        interstitial.setAdUnitId(getResources().getString(R.string.UnitId));
    //     Создаём запрос к AdMob
        AdRequest adRequesti = new AdRequest.Builder().build();
    //     Начинаем загружать объявление
        interstitial.loadAd(adRequesti);

        btnContinueStop.setText(getResources().getString(R.string.stop_string));

        if(CHECK_STOP_ACTIVE==true) {
            btnContinueStop.setEnabled(true);
            return;
            //    animationDrawableRound.stop();
        } else if(CHECK_STOP_ACTIVE==false) {
            btnContinueStop.setEnabled(false);
            return;
            //    animationDrawableRound.start();
        }
    }

    public void func_delete() {
        if(AMOUNT_LAPS_CURRENT==0) {
            return;
        }
        if(CHANGE_WORK_BREAK__START_BOOL==0) {
            CHANGE_LAPS_START = 0;
            AMOUNT_LAPS_CURRENT = 0;
            start_func();
        }
        if(CHANGE_WORK_BREAK__START_BOOL==1) {
            AMOUNT_LAPS_CURRENT = 0;
            CHANGE_LAPS = 0;
            formula_work_break();
            CHANGE_LAPS_2 = 0;
            formula_work_break2();
        }
    }

    void saveText() {
        SharedPreferences sPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putInt("laps_amount", AMOUNTS_LAPS);
        ed.putInt("work_minutes",AMOUNT_WORK_MINUTES);
        ed.putInt("work_seconds", AMOUNT_WORK_SECONDS);
        ed.putInt("break_minutes",AMOUNT_BREAK_MINUTES);
        ed.putInt("break_seconds", AMOUNT_BREAK_SECONDS);
        ed.putInt("lang",CHECK_LANG);
        ed.commit();
    }

    void loadText() {
        SharedPreferences sPref = getPreferences(MODE_PRIVATE);
        AMOUNTS_LAPS = sPref.getInt("laps_amount", AMOUNTS_LAPS);
        CHECK_LANG = sPref.getInt("lang", CHECK_LANG);
        AMOUNT_WORK_SECONDS = sPref.getInt("work_seconds", AMOUNT_WORK_SECONDS);
        AMOUNT_WORK_MINUTES  = sPref.getInt("work_minutes", AMOUNT_WORK_MINUTES);
        AMOUNT_BREAK_MINUTES = sPref.getInt("break_minutes", AMOUNT_BREAK_MINUTES);
        AMOUNT_BREAK_SECONDS = sPref.getInt("break_seconds", AMOUNT_BREAK_SECONDS);

        if(CHANGE_WORK_BREAK__START_BOOL==0) {
            workAmountGoal.setText(AMOUNT_WORK_MINUTES + " : " + AMOUNT_WORK_SECONDS);
            breakAmountGoal.setText(AMOUNT_BREAK_MINUTES + " : " + AMOUNT_BREAK_SECONDS);
            AMOUNT_WORK_MINUTES = (AMOUNT_WORK_MINUTES*60) * 1000;
            AMOUNT_WORK_SECONDS = AMOUNT_WORK_SECONDS * 1000;
            AMOUNT_BREAK_MINUTES = (AMOUNT_BREAK_MINUTES*60) * 1000;
            AMOUNT_BREAK_SECONDS = AMOUNT_BREAK_SECONDS * 1000;
        }
        if(CHANGE_WORK_BREAK__START_BOOL==1) {
            workAmountGoal.setText(AMOUNT_WORK_MINUTES + " : " + AMOUNT_WORK_SECONDS);
            breakAmountGoal.setText(AMOUNT_BREAK_MINUTES + " : " + AMOUNT_BREAK_SECONDS);
        }
        amountLaps.setText(AMOUNTS_LAPS + "");
        amountLapsGoal.setText(AMOUNTS_LAPS + "");

        if(CHECK_LANG==0) {
            setLocale("en");
        }
        if(CHECK_LANG==1) {
            setLocale("ru");
        }

    }

    protected void onStop() {
        super.onStop();
    //    saveText();

        animationDrawableRound.stop();
        workAmountGoal.setText(AMOUNT_WORK_MINUTES + " : " + AMOUNT_WORK_SECONDS);
        breakAmountGoal.setText(AMOUNT_BREAK_MINUTES + " :  " + AMOUNT_BREAK_SECONDS);
        AMOUNT_WORK_MINUTES = (AMOUNT_WORK_MINUTES*60) * 1000;
        AMOUNT_WORK_SECONDS = AMOUNT_WORK_SECONDS * 1000;
        AMOUNT_BREAK_MINUTES = (AMOUNT_BREAK_MINUTES*60) * 1000;
        AMOUNT_BREAK_SECONDS = AMOUNT_BREAK_SECONDS * 1000;

        CHECK_STOP_ACTIVE = true;
    }

    protected void onDestroy() {
        super.onDestroy();
        //   saveText();
    }

    protected void onResume() {
        super.onResume();
        //   saveText();
    }

    public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {

    }

    void updateTextView() {

        Button btnReset;
        TextView tvLap, tvGoal, tvLapsGoal, tvWorkGoal, tvBreakGoal;

        btnReset = (Button) findViewById(R.id.btnReset);

        tvLap = (TextView) findViewById(R.id.tvLap);
        tvGoal = (TextView) findViewById(R.id.tvGoal);
        tvLapsGoal = (TextView) findViewById(R.id.tvLapsGoal);
        tvWorkGoal = (TextView) findViewById(R.id.tvWorkGoal);
        tvBreakGoal = (TextView) findViewById(R.id.tvBreakGoal);

        tvLap.setText(R.string.lap);
        tvGoal.setText(R.string.goal_string);
        tvLapsGoal.setText(R.string.round_string);
        tvWorkGoal.setText(R.string.work_string);
        tvBreakGoal.setText(R.string.break_goal_string);
        tvBreak.setText(R.string.break_main_string);
        tvHardWork.setText(R.string.main_string);
        btnContinueStop.setText(getResources().getString(R.string.stop_string));
        btnReset.setText(R.string.reset_string);
        if(CHECK_STOP_ACTIVE==false) {
            btnContinueStop.setText(getResources().getString(R.string.stop_string));
        }
        if(CHECK_STOP_ACTIVE==true) {
            btnContinueStop.setText(getResources().getString(R.string.continue_string));
        }
    }

    void setLocale(String mLang) {
        mNewLocale = new Locale(mLang);
        Locale.setDefault(mNewLocale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.locale = mNewLocale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
        updateTextView();
    }

    public void displayInterstitial() {
        if (interstitial.isLoaded()) {
            interstitial.show();
        }
    }
/*
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return false;
        }
        return true;
    }
*/

    /*
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_LANG:
                final String[] arrScale = new String[]{"RUSSIAN", "ENGLISH"};

                builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.change_lang); // заголовок для диалога

                builder.setItems(arrScale, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {

                        if(item==0) {
                            setLocale("ru");
                         //   tvLang.setText(R.string.lang_rus);
                            CHECK_LANG = 1;
                        }
                        if(item==1) {
                            setLocale("en");
                          //  tvLang.setText(R.string.lang_en);
                            CHECK_LANG = 0;
                        }

                    //    tvLang.setTextColor(Color.rgb(255,111,1));
                     //   tvChangeLang.setTextColor(Color.rgb(255,111,1));
                        bool_start_lang = true;
                        saveText();
                    }
                });
                builder.setCancelable(false);
                return builder.create();

        }
        return super.onCreateDialog(id);
    }
    */

}
