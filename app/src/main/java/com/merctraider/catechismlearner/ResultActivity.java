package com.merctraider.catechismlearner;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import static com.merctraider.catechismlearner.MainActivity.SECTION_ICONS;

public class ResultActivity extends AppCompatActivity {

    ImageView sectionImageView;
    TextView progressTextView;
    Button proceedButton;

    String sectionName;
    String sectionProgress;
    int sectionIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        sectionImageView = findViewById(R.id.sectionImageView);
        progressTextView = findViewById(R.id.progressTextView);
        proceedButton = findViewById(R.id.proceedButton);


        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            sectionName = bundle.getString("SectionName");
            sectionProgress = bundle.getString("SectionProgress");
            sectionIndex = bundle.getInt("SectionIndex");
        }

        Resources res = getResources();
        sectionImageView.setImageDrawable(res.getDrawable(SECTION_ICONS[sectionIndex]));
        progressTextView.setText(sectionProgress);
        proceedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResultActivity.this, LearnMenuActivity.class);
                intent.putExtra("SectionName", sectionName);
                intent.putExtra("SectionProgress", sectionProgress);
                intent.putExtra("SectionIndex", sectionIndex);
                startActivity(intent);
            }
        });


    }
}
