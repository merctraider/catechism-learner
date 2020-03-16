package com.merctraider.catechismlearner;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputLayout;

import static com.merctraider.catechismlearner.DrillFragment.checkAnswer;
import static com.merctraider.catechismlearner.MainActivity.SECTION_PARTS;
import static com.merctraider.catechismlearner.MainActivity.SHARED_PREFS;

public class ReviewActivity extends AppCompatActivity {

    TextView questionTextView;
    TextInputLayout blankFillInputLayout;
    Button buttonProceed;
    Button buttonSkip;

    String mAnswer;
    int partIndex = 0;
    int sectionIndex;

    SoundPool soundPool;
    int dingSound;

    String[] reviewQuestions;
    String[] reviewAnswers;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        String[] sections = getResources().getStringArray(R.array.sections);

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        soundPool = new SoundPool.Builder()
                .setMaxStreams(10)
                .setAudioAttributes(audioAttributes)
                .build();
        //soundPool = new SoundPool(6, AudioManager.STREAM_MUSIC, 0);
        dingSound = soundPool.load(this, R.raw.ding, 1);




        questionTextView = findViewById(R.id.questionTextView);
        blankFillInputLayout = findViewById(R.id.blankFillInputLayout);
        buttonProceed = findViewById(R.id.buttonProceed);
        buttonSkip = findViewById(R.id.buttonSkip);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            sectionIndex = bundle.getInt("SectionIndex");
            toolbar.setTitle(sections[sectionIndex]);
        }

        getQnASet(sectionIndex);

        setQnA(reviewQuestions[partIndex], reviewAnswers[partIndex]);
        //final MediaPlayer ring = MediaPlayer.create(this, R.raw.ding);


        buttonProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userInput = blankFillInputLayout.getEditText().getText().toString();
                if(checkAnswer(userInput, mAnswer)){
                    if(partIndex < reviewAnswers.length){
                        String keyToUpdate = "SECTION" + sectionIndex + "PART" + partIndex;
                        updateEntry(keyToUpdate, 1);

                        navigateQuestions(true);
                        playSound(dingSound);

                    } else  {
                        endActivity();
                    }


                } else {
                    blankFillInputLayout.setHint("");
                    blankFillInputLayout.getEditText().setHint(mAnswer);
                    if(mAnswer.length() > 500){
                        blankFillInputLayout.getEditText().setTextSize(14);
                    }
                    blankFillInputLayout.getEditText().setText("");
                    vibrateDevice(200);

                }
            }
        });

        buttonSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateQuestions(true);
            }
        });


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    public void playSound(int sound){
        Log.d("Sound", dingSound + " should be playing");
        soundPool.play(dingSound, 1, 1, 1, 0, 1);
    }

    void endActivity(){
        String progressString = updateProgress();
        Log.d("Save", progressString);
        String[] sectionKeys = getResources().getStringArray(R.array.sections);
        //You have come to the end of the review
        Intent intent = new Intent(ReviewActivity.this, ResultActivity.class);
        intent.putExtra("SectionName", sectionKeys[sectionIndex]);
        intent.putExtra("SectionProgress", progressString);
        intent.putExtra("SectionIndex", sectionIndex);
        startActivity(intent);

    }

    void navigateQuestions(boolean next){
        if(next){
            partIndex++;

        } else if(partIndex > 0){
            partIndex--;
        }
        if(partIndex < reviewAnswers.length){
            buttonSkip.setVisibility(View.VISIBLE);
            setQnA(reviewQuestions[partIndex], reviewAnswers[partIndex]);
            blankFillInputLayout.getEditText().setHint("");
            blankFillInputLayout.getEditText().setText("");
        } else {
            endActivity();
            buttonSkip.setVisibility(View.INVISIBLE);
            partIndex--;
        }


    }
    void getQnASet(int section){
        Resources res = getResources();
        switch(section){
            case 0:
                reviewQuestions = res.getStringArray(R.array.section1_part_question);
                reviewAnswers = res.getStringArray(R.array.section1_part_answer);
                break;
            case 1:
                reviewQuestions = res.getStringArray(R.array.section2_part_question);
                reviewAnswers = res.getStringArray(R.array.section2_part_answer);
                break;
            case 2:
                reviewQuestions = res.getStringArray(R.array.section3_part_question);
                reviewAnswers = res.getStringArray(R.array.section3_part_answer);

                break;
            case 3:
                reviewQuestions = res.getStringArray(R.array.section4_part_question);
                reviewAnswers = res.getStringArray(R.array.section4_part_answer);

                break;
            case 4:
                reviewQuestions = res.getStringArray(R.array.section5_part_question);
                reviewAnswers = res.getStringArray(R.array.section5_part_answer);
                break;
            case 5:
                reviewQuestions = res.getStringArray(R.array.section6_part_question);
                reviewAnswers = res.getStringArray(R.array.section6_part_answer);
                break;
        }

    }

    void setQnA(String question, String answer){
        questionTextView.setText(question);
        mAnswer = answer;
    }


    //0 = not clear, 1 = clear
    public void updateEntry(String key, int value){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor sharedPrefsEditor = sharedPreferences.edit();

        sharedPrefsEditor.putInt(key, value); //Individual entry is now updated, time to update the progress
        sharedPrefsEditor.apply();
        Log.d("Save", "Saved "+ key + " :" + value + "in " + SHARED_PREFS);


    }

    public String updateProgress(){
        int progress = 0;
        String baseKey = "SECTION" + sectionIndex + "PART";
        for(int i = 0; i<SECTION_PARTS[sectionIndex]; i++){
            String keyToCheck = baseKey + i;
            int value = getSaveData(keyToCheck);
            progress += value;
            Log.d("Saved", "Checked " + keyToCheck + "=" + value);
        }


        String[] sectionKeys = getResources().getStringArray(R.array.sections);

        updateEntry(sectionKeys[sectionIndex], progress);

        String display = progress + "/" + SECTION_PARTS[sectionIndex];

        return display;
    }

    public int getSaveData(String key){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        return sharedPreferences.getInt(key, 0);
    }


    public void vibrateDevice(long duration){
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
// Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(duration);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        String[] sectionKeys = getResources().getStringArray(R.array.sections);
        String progressString = updateProgress();

        if(partIndex == 0){
            Intent intent = new Intent(ReviewActivity.this, LearnMenuActivity.class);
            intent.putExtra("SectionName", sectionKeys[sectionIndex]);
            intent.putExtra("SectionProgress", progressString);
            intent.putExtra("SectionIndex", sectionIndex);
            startActivity(intent);
            overridePendingTransition(R.anim.nav_default_enter_anim, R.anim.nav_default_exit_anim);
        } else {
            navigateQuestions(false);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        soundPool.release();
        soundPool = null;
    }
}
