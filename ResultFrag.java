/*
This fragment displays whether the result was correct or not, then goes back to AnswerFrag when clicked
 */
package com.jdporras.assignment2;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ResultFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ResultFrag extends Fragment {
    Button results;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ResultFrag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ResultFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ResultFrag newInstance(String param1, String param2) {
        ResultFrag fragment = new ResultFrag();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_result, container, false);
        results = view.findViewById(R.id.results);

        //this section displays results of quiz depending on values from SecondaryActivity using ResultsHolder
        if (SecondaryActivity.ResultsHolder.isCorrect()) {
            results.setText("Correct!");
            results.setBackgroundColor(Color.GREEN);
        } else {
            results.setText("Answer: " + SecondaryActivity.ResultsHolder.getAnsString());
            results.setBackgroundColor(Color.RED);
        }

        results.setOnClickListener(new View.OnClickListener() {
            @Override
            //this clickListener calls SecondaryActivity to set a new question when clicked and brings back the previous fragment
            public void onClick(View v) {
                SecondaryActivity main = (SecondaryActivity)getActivity();
                main.setQuestion();
                AnswerFrag answer = new AnswerFrag();
                FragmentTransaction trans = getFragmentManager().beginTransaction();
                trans.replace(R.id.flfragment, answer);
                trans.commit();
            }
        });
        return view;
    }
}