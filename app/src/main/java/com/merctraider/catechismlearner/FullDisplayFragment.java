package com.merctraider.catechismlearner;

import android.content.Context;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class FullDisplayFragment extends Fragment {

    FullDisplayListener listener;
    TextView questionTextView;
    TextView answerTextView;
    Button buttonProceed;

    String mQuestion;
    String mAnswer;

    public interface FullDisplayListener{
        void onFullDisplayFinish();
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        View v = inflater.inflate(R.layout.fragment_fulldisplay, container, false);

        questionTextView = v.findViewById(R.id.questionTextView);
        answerTextView = v.findViewById(R.id.answerTextView);
        buttonProceed = v.findViewById(R.id.button_proceed);
        questionTextView.setText(mQuestion);
        answerTextView.setText(mAnswer);
        if(mQuestion.length() > 200){
            questionTextView.setMovementMethod(new ScrollingMovementMethod());
        }
        if(mAnswer.length() > 500){
            answerTextView.setMovementMethod(new ScrollingMovementMethod());
        }

        buttonProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onFullDisplayFinish();
            }
        });


        // Inflate the layout for this fragment
        return v;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof FullDisplayListener){
            listener = (FullDisplayListener) context;
        } else {
            throw new RuntimeException(context.toString() + " implement FullDisplayListener!");

        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }


    public void getQuestionsAndAnswers(String question, String answer){
        if(questionTextView != null && answerTextView != null){
            questionTextView.setText(question);
            answerTextView.setText(answer);
        }
        mQuestion = question;
        mAnswer = answer;
    }
}
