package com.merctraider.catechismlearner;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import static com.merctraider.catechismlearner.MainActivity.SECTION_PARTS;
import static com.merctraider.catechismlearner.MainActivity.SHARED_PREFS;

public class LearnMenuActivity extends AppCompatActivity {

    int sectionIndex;
    String sectionProgress;

    String[] sectionParts;
    String[] partContentQuestion;
    String[] partContentAnswer;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn_menu);



        Resources res = getResources();

        //TextView sectionTitleTextView = findViewById(R.id.sectionTitleTextView);
        //TextView progressTextView = findViewById(R.id.progressTextView);
        ListView partListView = findViewById(R.id.partListView);
        Toolbar toolbar = findViewById(R.id.toolbar);
        FloatingActionButton floatingActionButton = findViewById(R.id.floatingActionButton);

        Bundle bundle = getIntent().getExtras();
        String sectionName = "Null";
        sectionProgress = "0/0";
        sectionIndex = 0;
        if(bundle != null){
            sectionName = bundle.getString("SectionName");
            sectionProgress = bundle.getString("SectionProgress");
            sectionIndex = bundle.getInt("SectionIndex");

            switch (sectionIndex){
                case 0:
                    //Get the title of the Section Parts
                    sectionParts = res.getStringArray(R.array.section1);

                    //Get the questions and answers of each part
                    partContentQuestion = res.getStringArray(R.array.section1_part_question);
                    partContentAnswer = res.getStringArray(R.array.section1_part_answer);
                    break;
                case 1:
                    sectionParts = res.getStringArray(R.array.section2);

                    partContentQuestion = res.getStringArray(R.array.section2_part_question);
                    partContentAnswer = res.getStringArray(R.array.section2_part_answer);
                    break;
                case 2:
                    sectionParts = res.getStringArray(R.array.section3);

                    partContentQuestion = res.getStringArray(R.array.section3_part_question);
                    partContentAnswer = res.getStringArray(R.array.section3_part_answer);
                    break;
                case 3:
                    sectionParts = res.getStringArray(R.array.section4);

                    partContentQuestion = res.getStringArray(R.array.section4_part_question);
                    partContentAnswer = res.getStringArray(R.array.section4_part_answer);
                    break;
                case 4:
                    sectionParts = res.getStringArray(R.array.section5);

                    partContentQuestion = res.getStringArray(R.array.section5_part_question);
                    partContentAnswer = res.getStringArray(R.array.section5_part_answer);
                    break;
                case 5:
                    sectionParts = res.getStringArray(R.array.section6);

                    partContentQuestion = res.getStringArray(R.array.section6_part_question);
                    partContentAnswer = res.getStringArray(R.array.section6_part_answer);

                    break;
            }
            //progressTextView.setText(sectionProgress);
            //sectionTitleTextView.setText(sectionName);
            toolbar.setTitle(sectionName);
            toolbar.setSubtitle(sectionProgress);
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back);

            partListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent (LearnMenuActivity.this, InstructionActivity.class);
                    //Send over the questions and answers to display
                    intent.putExtra("Question", partContentQuestion[position]);
                    intent.putExtra("Answer", partContentAnswer[position]);
                    intent.putExtra("SectionIndex", sectionIndex);
                    intent.putExtra("PartIndex", position);
                    intent.putExtra("SectionProgress", sectionProgress);
                    startActivity(intent);

                }
            });

            floatingActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(LearnMenuActivity.this, ReviewActivity.class);
                    intent.putExtra("SectionIndex", sectionIndex);
                    startActivity(intent);
                }
            });

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });


        }



        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(LearnMenuActivity.this, R.layout.support_simple_spinner_dropdown_item, sectionParts);

        PartsAdapter adapter = new PartsAdapter(LearnMenuActivity.this, sectionParts, checkProgress());
        partListView.setAdapter(adapter);
    }

    public int[] checkProgress(){
        String baseKey = "SECTION" + sectionIndex + "PART";
        int[] progressData = new int[SECTION_PARTS[sectionIndex]];

        for(int i = 0; i<SECTION_PARTS[sectionIndex]; i++){
            String keyToCheck = baseKey + i;
            int value = getSaveData(keyToCheck);
            progressData[i] = value;

            Log.d("Saved", "Checked " + keyToCheck + "=" + value);
        }

        return progressData;
    }



    public int getSaveData(String key){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        return sharedPreferences.getInt(key, 0);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(LearnMenuActivity.this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.nav_default_enter_anim, R.anim.nav_default_exit_anim);
    }
}
