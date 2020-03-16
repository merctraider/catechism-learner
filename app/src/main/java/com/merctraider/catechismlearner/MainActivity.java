package com.merctraider.catechismlearner;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    public static final String SHARED_PREFS = "userPrefs";
    public static final Integer[] SECTION_ICONS = {
            R.drawable.ic_tencommandments,
            R.drawable.ic_creed,
            R.drawable.ic_lordsprayer,
            R.drawable.ic_baptism,
            R.drawable.ic_confession,
            R.drawable.ic_eucharist
    };
    public static final int[] SECTION_PARTS = {
            12,
            3,
            13,
            7,
            5,
            5

    };
    ListView mainListView;
    String[] sections;
    int[] sectionValues;

    String[] sectionProgress;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Resources res = getResources();
        mainListView = (ListView) findViewById(R.id.mainListView);
        //Variables to display in the list
        sections = res.getStringArray(R.array.sections);
        sectionValues = new int[sections.length];
        sectionProgress = new String[sections.length];
        loadSectionsSaveData();
        displaySectionProgress();

        //Send over SECTION_ICONS, sections, sectionProgress
        Drawable[] sectionIcons = new Drawable[SECTION_ICONS.length];
        for (int i = 0; i < SECTION_ICONS.length; i++)
        {
            sectionIcons[i] = res.getDrawable(SECTION_ICONS[i]);
        }
        SectionAdapter sectionAdapter = new SectionAdapter(this, sections, sectionProgress, sectionIcons);
        mainListView.setAdapter(sectionAdapter);
        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, LearnMenuActivity.class);
                intent.putExtra("SectionName", sections[position]);
                intent.putExtra("SectionProgress", sectionProgress[position]);
                intent.putExtra("SectionIndex", position);
                startActivity(intent);
            }
        });


    }


    void displaySectionProgress(){
        for(int i = 0; i <SECTION_PARTS.length; i++){
            sectionProgress[i] = sectionValues[i] + "/" + SECTION_PARTS[i];
        }
    }


    void loadSectionsSaveData(){
        for (int i = 0; i < sections.length; i++){
            sectionValues[i] = getSaveData(sections[i]);
        }
    }

    public void setSaveData(String key, int value){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor sharedPrefsEditor = sharedPreferences.edit();

        sharedPrefsEditor.putInt(key, value);
    }

    public int getSaveData(String key){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        Log.d("Save", key);
        return sharedPreferences.getInt(key, 0);
    }

    @Override
    public void onBackPressed() {
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory( Intent.CATEGORY_HOME );
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
    }
}
