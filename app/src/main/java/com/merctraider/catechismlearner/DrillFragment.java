package com.merctraider.catechismlearner;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputLayout;


public class DrillFragment extends Fragment {

    TextInputLayout practiceTextInputLayout;
    TextView countTextView;
    Button buttonProceed;

    DrillFragmentListener listener;

    int practiceCount;
    String correctAnswer;

    int progressTracker;


    public interface DrillFragmentListener{

        void onDrillVibrate();
        void onDrillCompleted(int progress);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        View v = inflater.inflate(R.layout.fragment_drill, container, false);
        practiceTextInputLayout = v.findViewById(R.id.practiceTextInputLayout);
        practiceTextInputLayout.setHint(correctAnswer);
        countTextView = v.findViewById(R.id.countTextView);
        buttonProceed = v.findViewById(R.id.button_proceed);
        buttonProceed.setVisibility(View.INVISIBLE);
        countTextView.setVisibility(View.INVISIBLE);



        // Inflate the layout for this fragment
        return v;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final MediaPlayer ring = MediaPlayer.create(getContext(), R.raw.ding);

        practiceTextInputLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String debugLog = s + " " + correctAnswer;
                boolean matching = false;





                Log.d("Input", debugLog);

                if(checkAnswer(s.toString(), correctAnswer)){
                    Log.d("Input", "MATCH FOUND!");
                    practiceTextInputLayout.getEditText().setText("");
                    practiceCount++;
                    countTextView.setVisibility(View.VISIBLE);
                    countTextView.setText(practiceCount + "x");
                    listener.onDrillVibrate();
                    if(practiceCount >= 3){
                        //Enable proceed button
                        buttonProceed.setVisibility(View.VISIBLE);

                        ring.start();

                    }

                } else {
                    Log.d("Input", "Not a match");
                }


            }

            @Override
            public void afterTextChanged(Editable s) {


            }
        });

        buttonProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onDrillCompleted(progressTracker);
            }
        });



    }

    public static boolean checkAnswer(String input, String rightAnswer){
        input = input.replaceAll("[^a-zA-Z ]", "");
        rightAnswer = rightAnswer.replaceAll("[^a-zA-Z ]", "");
        input = input.replaceAll(" ", "");
        rightAnswer = rightAnswer.replaceAll(" ", "");
        Log.d("Input", "Comparing " + input + " with " + rightAnswer);

        return input.equalsIgnoreCase(rightAnswer);
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof DrillFragmentListener){
            listener = (DrillFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString() + " implement DrillFragmentListener!");

        }

    }

    public void getAnswer(String answer, int progress ){
        correctAnswer = answer;
        progressTracker = progress;

    }
}
