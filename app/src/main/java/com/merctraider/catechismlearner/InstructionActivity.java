package com.merctraider.catechismlearner;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

import static com.merctraider.catechismlearner.MainActivity.SECTION_PARTS;
import static com.merctraider.catechismlearner.MainActivity.SHARED_PREFS;


public class InstructionActivity extends AppCompatActivity implements FullDisplayFragment.FullDisplayListener, DrillFragment.DrillFragmentListener, ProgressReviewFragment.ProgressReviewListener {



    int partIndex;
    int sectionIndex;


    FullDisplayFragment fullDisplayFragment;
    DrillFragment drillFragment;
    ProgressReviewFragment progressReviewFragment;

    String mQuestion;
    String mAnswer;

    String[] answerParts;
    int progressTracker = 0;


    SoundPool soundPool;
    int dingSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruction);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_close);

        fullDisplayFragment = new FullDisplayFragment();

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        soundPool = new SoundPool.Builder()
                .setMaxStreams(6)
                .setAudioAttributes(audioAttributes)
                .build();
        dingSound = soundPool.load(this, R.raw.ding, 1);


        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentHolder, fullDisplayFragment)
                .commit();

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            mQuestion = bundle.getString("Question");
            mAnswer = bundle.getString("Answer");
            partIndex = bundle.getInt("PartIndex");
            sectionIndex = bundle.getInt("SectionIndex");
        }


        fullDisplayFragment.getQuestionsAndAnswers(mQuestion, mAnswer);
        splitAnswer();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });



    }

    //Break answer down into phrases
    void splitAnswer(){

        String[] words = mAnswer.split(" ");
        int remainder = words.length% 3;

        List<String> phrases = new ArrayList<String>();
        for(int i=0; i< (words.length-remainder); i+=3){
            phrases.add(words[i] + " " + words[i+1] + " " + words[i+2]);
        }
        if(remainder == 1){
            phrases.add(words[words.length -1]);
        } else if(remainder == 2){
            phrases.add(words[words.length -2] + " " + words[words.length -1]);
        }
        answerParts = new String[phrases.size()];
        for(int w = 0; w<phrases.size(); w++){
            answerParts[w] = phrases.get(w);
            Log.d("AnswerSplit", answerParts[w]);


        }

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

    public void playSound(int sound){
        soundPool.play(sound, 1, 1, 0, 0, 1);
    }

    @Override
    public void onFullDisplayFinish() {

        drillFragment = new DrillFragment();
        drillFragment.getAnswer(answerParts[progressTracker], progressTracker);
        getSupportFragmentManager().beginTransaction()
                .remove(fullDisplayFragment)
                .replace(R.id.fragmentHolder, drillFragment)
                .commit();
    }


    @Override
    public void onDrillVibrate() {
        vibrateDevice(100);
    }

    @Override
    public void onReviewCompleted(int progress) {
        progressTracker = progress;
        playSound(dingSound);

        if(progressTracker < answerParts.length){
            drillFragment = new DrillFragment();
            drillFragment.getAnswer(answerParts[progressTracker], progressTracker);
            getSupportFragmentManager().beginTransaction()
                    .remove(progressReviewFragment)
                    .replace(R.id.fragmentHolder, drillFragment)
                    .commit();
        } else {
            Log.d("Progress", "All of it has been covered");

            String keyToUpdate = "SECTION" + sectionIndex + "PART" + partIndex;

            updateEntry(keyToUpdate, 1);
            String progressString = updateProgress();
            Log.d("Save", progressString);
            String[] sectionKeys = getResources().getStringArray(R.array.sections);

            Intent intent = new Intent(InstructionActivity.this, ResultActivity.class);
            intent.putExtra("SectionName", sectionKeys[sectionIndex]);
            intent.putExtra("SectionProgress", progressString);
            intent.putExtra("SectionIndex", sectionIndex);
            startActivity(intent);
            /*intent.putExtra("SectionIndex", sectionIndex);
                    intent.putExtra("PartIndex", position);
                    intent.putExtra("SectionProgress", position);
                    */
        }

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





    @Override
    public void onDrillCompleted(int progress) {

        progressTracker = progress + 1;
        String progressAnswer = new String();

        String remainingChunk = new String();

        progressAnswer += answerParts[0];
        for(int i = 1; i < progressTracker; i++){
            progressAnswer += (" " + answerParts[i]); //Assemble the phrases the user has learned so far
        }

        //Put them as underscores

        for(int c = 0; c < progressAnswer.length(); c++){
            remainingChunk += "_";
        }
        Log.d("Replaced", remainingChunk);
        Log.d("Replaced", progressAnswer);



        //Assemble the remaining sentence
        for(int w = progressTracker; w < answerParts.length; w++){
            remainingChunk += (" " + answerParts[w]);
        }
        progressReviewFragment = new ProgressReviewFragment();

        getSupportFragmentManager().beginTransaction()
                .remove(drillFragment)
                .replace(R.id.fragmentHolder, progressReviewFragment)

                .commit();

        progressReviewFragment.getAnswer(mQuestion, remainingChunk, progressAnswer, progressTracker);


    }

    @Override
    public void onBackPressed() {
        String[] sectionKeys = getResources().getStringArray(R.array.sections);
        String progressString = updateProgress();
        Intent intent = new Intent(InstructionActivity.this, LearnMenuActivity.class);
        intent.putExtra("SectionName", sectionKeys[sectionIndex]);
        intent.putExtra("SectionProgress", progressString);
        intent.putExtra("SectionIndex", sectionIndex);
        startActivity(intent);
        overridePendingTransition(R.anim.nav_default_enter_anim, R.anim.nav_default_exit_anim);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        soundPool.release();
        soundPool = null;
    }
}
