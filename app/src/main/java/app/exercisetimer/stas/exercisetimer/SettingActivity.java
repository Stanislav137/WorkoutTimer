package app.exercisetimer.stas.exercisetimer;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Locale;

public class SettingActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener, View.OnClickListener{

    AlertDialog.Builder builder;

    final int DIALOG_LANG = 1;

    SharedPreferences sPref;

    Context context;

    private Locale mNewLocale;

    private int CHECK_LANG = 1;

    private LinearLayout linearLayout_lang;

    SeekBar seekBarLaps, seekBarWorkMinutes, seekBarWorkSeconds, seekBarBreakMinutes, seekBarBreakSeconds;

    TextView amountLaps, amountWorkMinutes, amountWorkSeconds, amountBreakMinutes, amountBreakSeconds, tvChangeLang, tvLang;

    private int MIN_SEC_WORK = 0, MIN_SEC_BREAK = 0;

    private int AMOUNT_LAPS = 3, AMOUNT_MINUTES_WORK = 0, AMOUNT_SECONDS_WORK = 30, AMOUNT_MINUTES_BREAK = 0, AMOUNT_SECONDS_BREAK = 15;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);

        Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();

        if(width==1440 && height==2392) {
            setContentView(R.layout.setting_nexus6);
        }
        if(width<=600 && height<=1000) {
            setContentView(R.layout.setting_nexus_s);
        }

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        context = SettingActivity.this;

        linearLayout_lang = (LinearLayout) findViewById(R.id.linearLayout_lang);

        seekBarLaps = (SeekBar) findViewById(R.id.seekBarLaps);
        seekBarWorkMinutes = (SeekBar) findViewById(R.id.seekBar_minutes);
        seekBarWorkSeconds = (SeekBar) findViewById(R.id.seekBar_seconds);
        seekBarBreakMinutes = (SeekBar) findViewById(R.id.seekBarMinutes);
        seekBarBreakSeconds = (SeekBar) findViewById(R.id.seekBarSeconds);

        amountLaps = (TextView) findViewById(R.id.amountLaps);
        amountWorkMinutes = (TextView) findViewById(R.id.amount_minutes);
        amountWorkSeconds = (TextView) findViewById(R.id.amount_seconds);
        amountBreakMinutes = (TextView) findViewById(R.id.break_amount_minutes);
        amountBreakSeconds = (TextView) findViewById(R.id.break_amount_seconds);
        tvChangeLang = (TextView) findViewById(R.id.tvChangeLang);
        tvLang = (TextView) findViewById(R.id.tvLang);

        linearLayout_lang.setOnClickListener(this);
        seekBarWorkMinutes.setOnSeekBarChangeListener(this);
        seekBarWorkSeconds.setOnSeekBarChangeListener(this);
        seekBarBreakMinutes.setOnSeekBarChangeListener(this);
        seekBarBreakSeconds.setOnSeekBarChangeListener(this);
        seekBarLaps.setOnSeekBarChangeListener(this);
    }

    // start menu

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.back) {
            Intent intent = new Intent();
            intent.putExtra("laps", AMOUNT_LAPS);
            intent.putExtra("minutes_work", AMOUNT_MINUTES_WORK);
            intent.putExtra("seconds_work", AMOUNT_SECONDS_WORK);
            intent.putExtra("minutes_break", AMOUNT_MINUTES_BREAK);
            intent.putExtra("seconds_break", AMOUNT_SECONDS_BREAK);
            intent.putExtra("check_lang", CHECK_LANG);
            /* main func */
            formula_result();
            intent.putExtra("result_work", MIN_SEC_WORK);
            intent.putExtra("result_break", MIN_SEC_BREAK);
            /* end main func */
            setResult(RESULT_OK, intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // end menu

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {
            case R.id.seekBarLaps:
                AMOUNT_LAPS = Integer.valueOf(seekBarLaps.getProgress());
                if (AMOUNT_LAPS == 0) {
                    AMOUNT_LAPS = 1;
                    amountLaps.setText(AMOUNT_LAPS + "");
                }
                amountLaps.setText(AMOUNT_LAPS + "");
                break;
            case R.id.seekBar_minutes:
                AMOUNT_MINUTES_WORK = Integer.valueOf(seekBarWorkMinutes.getProgress());
                amountWorkMinutes.setText(AMOUNT_MINUTES_WORK + "");
                break;
            case R.id.seekBar_seconds:
                AMOUNT_SECONDS_WORK = Integer.valueOf(seekBarWorkSeconds.getProgress());
                amountWorkSeconds.setText(AMOUNT_SECONDS_WORK + "");
                break;
            case R.id.seekBarMinutes:
                AMOUNT_MINUTES_BREAK = Integer.valueOf(seekBarBreakMinutes.getProgress());
                amountBreakMinutes.setText(AMOUNT_MINUTES_BREAK + "");
                break;
            case R.id.seekBarSeconds:
                AMOUNT_SECONDS_BREAK = Integer.valueOf(seekBarBreakSeconds.getProgress());
                amountBreakSeconds.setText(AMOUNT_SECONDS_BREAK + "");
                break;
        }
        saveText();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    public void formula_result() {
         /* WORK */
        AMOUNT_SECONDS_WORK = AMOUNT_SECONDS_WORK * 1000;
        AMOUNT_MINUTES_WORK = AMOUNT_MINUTES_WORK * 60;
        AMOUNT_MINUTES_WORK = AMOUNT_MINUTES_WORK * 1000;
        MIN_SEC_WORK = AMOUNT_MINUTES_WORK + AMOUNT_SECONDS_WORK;

        /* BREAK */
        AMOUNT_SECONDS_BREAK = AMOUNT_SECONDS_BREAK * 1000;
        AMOUNT_MINUTES_BREAK = AMOUNT_MINUTES_BREAK * 60;
        AMOUNT_MINUTES_BREAK = AMOUNT_MINUTES_BREAK * 1000;
        MIN_SEC_BREAK = AMOUNT_MINUTES_BREAK + AMOUNT_SECONDS_BREAK;
    }

    void saveText() {
        sPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putInt("laps_amount", AMOUNT_LAPS);
        ed.putInt("work_minutes",AMOUNT_MINUTES_WORK);
        ed.putInt("work_seconds", AMOUNT_SECONDS_WORK);
        ed.putInt("break_minutes",AMOUNT_MINUTES_BREAK);
        ed.putInt("break_seconds", AMOUNT_SECONDS_BREAK);
        ed.putInt("lang",CHECK_LANG);
        ed.commit();
    }

    void loadText() {
        sPref = getPreferences(MODE_PRIVATE);
        AMOUNT_LAPS = sPref.getInt("laps_amount", AMOUNT_LAPS);
        AMOUNT_SECONDS_WORK = sPref.getInt("work_seconds", AMOUNT_SECONDS_WORK);
        AMOUNT_MINUTES_WORK  = sPref.getInt("work_minutes", AMOUNT_MINUTES_WORK);
        AMOUNT_MINUTES_BREAK = sPref.getInt("break_minutes", AMOUNT_MINUTES_BREAK);
        AMOUNT_SECONDS_BREAK = sPref.getInt("break_seconds", AMOUNT_SECONDS_BREAK);
        CHECK_LANG = sPref.getInt("lang",CHECK_LANG);

        amountWorkMinutes.setText(AMOUNT_MINUTES_WORK + "");
        amountWorkSeconds.setText(AMOUNT_SECONDS_WORK + "");
        amountBreakMinutes.setText(AMOUNT_MINUTES_BREAK + "");
        amountBreakSeconds.setText(AMOUNT_SECONDS_BREAK + "");
        amountLaps.setText(AMOUNT_LAPS + "");
        seekBarWorkMinutes.setProgress(AMOUNT_MINUTES_WORK);
        seekBarWorkSeconds.setProgress(AMOUNT_SECONDS_WORK);
        seekBarBreakMinutes.setProgress(AMOUNT_MINUTES_BREAK);
        seekBarBreakSeconds.setProgress(AMOUNT_SECONDS_BREAK);
        seekBarLaps.setProgress(AMOUNT_LAPS);
        if(CHECK_LANG==0) {
            setLocale("en");
        }
        if(CHECK_LANG==1) {
            setLocale("ru");
        }
        updateTextView();

    }

    protected void onStart() {
        super.onStart();
        loadText();
    }

    protected void onStop() {
        super.onStop();
        //    saveText();
    }

    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent();
            intent.putExtra("laps", AMOUNT_LAPS);
            intent.putExtra("minutes_work", AMOUNT_MINUTES_WORK);
            intent.putExtra("seconds_work", AMOUNT_SECONDS_WORK);
            intent.putExtra("minutes_break", AMOUNT_MINUTES_BREAK);
            intent.putExtra("seconds_break", AMOUNT_SECONDS_BREAK);
            intent.putExtra("check_lang", CHECK_LANG);
            /* main func */
            formula_result();
            intent.putExtra("result_work", MIN_SEC_WORK);
            intent.putExtra("result_break", MIN_SEC_BREAK);
            /* end main func */
            setResult(RESULT_OK, intent);
            finish();
            return false;
        }
        return true;
    }

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
                            tvLang.setText(R.string.lang_rus);
                            CHECK_LANG = 1;
                        }
                        if(item==1) {
                            setLocale("en");
                            tvLang.setText(R.string.lang_en);
                            CHECK_LANG = 0;
                        }

                        tvLang.setTextColor(Color.rgb(255,111,1));
                        tvChangeLang.setTextColor(Color.rgb(255,111,1));
                        saveText();
                    }
                });
                builder.setCancelable(false);
                return builder.create();

        }
        return super.onCreateDialog(id);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.linearLayout_lang:
                tvLang.setTextColor(Color.WHITE);
                tvChangeLang.setTextColor(Color.WHITE);
                showDialog(DIALOG_LANG);
                break;
        }
    }

    void updateTextView() {
        if(CHECK_LANG==0) {
            tvLang.setText(getResources().getString(R.string.lang_en));
        }
        if(CHECK_LANG==1) {
            tvLang.setText(getResources().getString(R.string.lang_rus));
        }
        TextView tvLaps, tvWork, tvBreak, tvWorkMinutes, tvWorkSeconds, tvBreakMinutes, tvBreakSeconds, tvChangeLang;
        tvLaps = (TextView) findViewById(R.id.tvLaps);
        tvWork = (TextView) findViewById(R.id.tvWork);
        tvBreak = (TextView) findViewById(R.id.tvBreak);
        tvWorkMinutes = (TextView) findViewById(R.id.tvWorkMinutes);
        tvWorkSeconds = (TextView) findViewById(R.id.tvWorkSeconds);
        tvBreakMinutes = (TextView) findViewById(R.id.tvBreakMinutes);
        tvBreakSeconds = (TextView) findViewById(R.id.tvBreakSeconds);
        tvChangeLang = (TextView) findViewById(R.id.tvChangeLang);

        tvWork.setText(getResources().getString(R.string.work_amount_string));
        tvWorkMinutes.setText(getResources().getString(R.string.minutes));
        tvWorkSeconds.setText(getResources().getString(R.string.seconds));

        tvBreak.setText(getResources().getString(R.string.break_amount_string));
        tvBreakMinutes.setText(getResources().getString(R.string.minutes));
        tvBreakSeconds.setText(getResources().getString(R.string.seconds));

        tvChangeLang.setText(getResources().getString(R.string.change_lang));

        tvLaps.setText(getResources().getString(R.string.laps));

    }

    void setLocale(String mLang) {
        mNewLocale = new Locale(mLang);
        Locale.setDefault(mNewLocale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.locale = mNewLocale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
        updateTextView();
    }
/*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_LANG:
                    CHECK_LANG = data.getIntExtra("check_lang", CHECK_LANG);
                    if(CHECK_LANG==0) {
                        setLocale("en");
                    }
                    if(CHECK_LANG==1) {
                        setLocale("ru");
                    }
                    updateTextView();
                    break;
            }

        }
    }
*/

}

