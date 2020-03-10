package com.merctraider.catechismlearner;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputLayout;

import static com.merctraider.catechismlearner.DrillFragment.checkAnswer;


public class ProgressReviewFragment extends Fragment {

    String answer;
    String blankAnswer;
    String question;
    int progressIndex;

    Button buttonProceed;
    TextView questionTextView;
    TextView answerTextView;
    TextInputLayout blankFillInputLayout;

    ProgressReviewListener listener;


    public interface ProgressReviewListener{
        void onDrillVibrate();
        void onReviewCompleted(int progress);
    }


    public void getAnswer(String q, String a, String bA, int i){
        question = q;
        answer = a;
        blankAnswer = bA;
        progressIndex = i;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_progress_review, container, false);
        buttonProceed = v.findViewById(R.id.buttonProceed);
        questionTextView = v.findViewById(R.id.questionTextView);
        answerTextView = v.findViewById(R.id.answerTextView);
        blankFillInputLayout = v.findViewById(R.id.blankFillInputLayout);

        questionTextView.setText(question);
        answerTextView.setText(answer);

        if(question.length() > 200){
            questionTextView.setMovementMethod(new ScrollingMovementMethod());
        }
        if(answer.length() > 200){
            answerTextView.setMovementMethod(new ScrollingMovementMethod());
        }


        // Inflate the layout for this fragment
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        buttonProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userInput = blankFillInputLayout.getEditText().getText().toString();
                if(checkAnswer(userInput, blankAnswer)){
                    MediaPlayer ring = MediaPlayer.create(getContext(), R.raw.ding);
                    ring.start();
                    listener.onReviewCompleted(progressIndex);
                } else {
                    blankFillInputLayout.getEditText().setHint(blankAnswer);
                    blankFillInputLayout.getEditText().setText("");
                    listener.onDrillVibrate();
                }
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof ProgressReviewListener){
            listener = (ProgressReviewListener) context;
        } else {
            throw new RuntimeException(context.toString() + " implement DrillFragmentListener!");

        }

    }
}
