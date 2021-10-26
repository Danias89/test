/*
This activity gets information from the Main Activity and is the driver class for the quiz program
 */
package com.jdporras.assignment2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;


public class SecondaryActivity extends AppCompatActivity {

    //Static class that holds all relevant info from results, used by AnswerFrag and ResultFrag
    public static class ResultsHolder {
        private static String[] quizStrings;
        private static int correctCounter = 0, recyclerCount;
        private static int userAnsIndex = -1, index = -1;
        private static boolean correct, isCreate = false, firstWrite = true;
        private static String ansString;
        private static int correctIndex = 0;
        private static boolean local = true, isArrListinit, editMode, modify = false, notInMain = false, deleteFlag = false, recyclerFlag = false;
        private static String fileName, quizSubject;
        private static ArrayList<String> arrListQuizStrings;

        public static String[] getQuizStrings() {return quizStrings;}

        public static void initArrListQuizStrings(){
            arrListQuizStrings = new ArrayList<>();
        }

        public static void setQuizStrings(String [] data) {quizStrings = data;}

        public static boolean isLocal() {return local;}

        public static void setLocal(boolean data){local = data;}

        public static int getCorrectCounter(){
            return correctCounter;
        }

        public static int getAns() {
            return userAnsIndex;
        }

        public static int getCorrect() {
            return correctIndex;
        }

        public static boolean isCorrect(){
            return correct;
        }

        public static String getAnsString() {
            return ansString;
        }

        public static String getQuizSubject() {return quizSubject;}

        public static void incCorrectCounter(){
            correctCounter++;
        }

        public static void setAns(int data) {
            ResultsHolder.userAnsIndex = data;
        }

        public static void setCorrectIndex(int data) {
            ResultsHolder.correctIndex = data;
        }

        public static void setQuizSubject(String data){
            ResultsHolder.quizSubject = data;
        }

        public static void setCorrectCounter(int data){
            correctCounter = data;
        }

        public static void setAnsString(String data) {
            ansString = data;
        }

        public static void setIsCorrect(boolean data){
            correct = data;
        }

        public static String getFileName() {
            return fileName;
        }

        public static void setFileName(String fileName) {
            ResultsHolder.fileName = fileName;
        }

        public static boolean isIsCreate() {
            return isCreate;
        }

        public static void setIsCreate(boolean isCreate) {
            ResultsHolder.isCreate = isCreate;
        }

        public static int getIndex() {
            return index;
        }

        public static void setIndex(int index) {
            ResultsHolder.index = index;
        }

        public static boolean isFirstWrite() {
            return firstWrite;
        }

        public static void setFirstWrite(boolean firstWrite) {
            ResultsHolder.firstWrite = firstWrite;
        }

        public static ArrayList<String> getArrListQuizStrings() {
            return arrListQuizStrings;
        }

        public static void setArrListQuizStrings(ArrayList<String> arrListQuizStrings) {
            ResultsHolder.arrListQuizStrings = arrListQuizStrings;
        }

        public static boolean isIsArrListinit() {
            return isArrListinit;
        }

        public static void setIsArrListinit(boolean isArrListinit) {
            ResultsHolder.isArrListinit = isArrListinit;
        }

        public static int getRecyclerCount() {
            return recyclerCount;
        }

        public static void setRecyclerCount() {
            ResultsHolder.recyclerCount++;
        }

        public static boolean isEditMode() {
            return editMode;
        }

        public static void setEditMode(boolean editMode) {
            ResultsHolder.editMode = editMode;
        }

        public static boolean isModify() {
            return modify;
        }

        public static void setModify(boolean modify) {
            Log.d("MODIFY", "CHANGING MODIFY TO " + modify);
            ResultsHolder.modify = modify;
        }

        public static boolean isNotInMain() {
            return notInMain;
        }

        public static void setNotInMain(boolean notInMain) {
            ResultsHolder.notInMain = notInMain;
        }

        public static boolean isDeleteFlag() {
            return deleteFlag;
        }

        public static void setDeleteFlag(boolean deleteFlag) {
            ResultsHolder.deleteFlag = deleteFlag;
        }

        public static boolean isRecyclerFlag() {
            return recyclerFlag;
        }

        public static void setRecyclerFlag(boolean recyclerFlag) {
            ResultsHolder.recyclerFlag = recyclerFlag;
        }
    }
    //initialization of relevant information
    String quizStrings[];
    int totalQuestions;
    int totalCounter = 0;
    int lineIndex = 0;
    int answerIndex = 0;

    Intent intent;
    Bundle bundle;

    TextView question;
    TextView ans1;
    TextView ans2;
    TextView ans3;
    TextView ans4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secondary);

        //this section retrieves data from MainActivity and initializes important data
        intent = getIntent();
        bundle = intent.getExtras();
        quizStrings = bundle.getStringArray("quizStrings");
        totalQuestions = (quizStrings.length / 5);
        question = findViewById(R.id.question);
        ans1 = findViewById(R.id.answer1);
        ans2 = findViewById(R.id.answer2);
        ans3 = findViewById(R.id.answer3);
        ans4 = findViewById(R.id.answer4);

        //this section sets the initial fragment to be ResultFrag
        FragmentManager fmanager = getSupportFragmentManager();
        Fragment answer = new AnswerFrag();
        fmanager.beginTransaction().replace(R.id.flfragment, answer, null)
                .commit();
        setQuestion();
    }

    //this function sets a new question by reading the next 5 strings from file and resetting background colors from
    //previous question
    public void setQuestion() {
        ans1.setBackgroundColor(Color.WHITE);
        ans2.setBackgroundColor(Color.WHITE);
        ans3.setBackgroundColor(Color.WHITE);
        ans4.setBackgroundColor(Color.WHITE);

        //if statement to know when to begin ThirdActivity
        if (totalCounter < totalQuestions){
            totalCounter++;
            //this for loop reads in next 5 strings to make a new question and answers
            for (int ix = 0; ix < 5; ix++) {
                //this if checks to see if a particular string is an answer and removes the asterisk if so
                if (quizStrings[lineIndex + ix].contains("*")) {
                    quizStrings[lineIndex + ix] = quizStrings[lineIndex + ix].replace("*", "");

                    //sets the correct answer index and string content for later use by fragments
                    ResultsHolder.setCorrectIndex(ix);
                    ResultsHolder.setAnsString(quizStrings[lineIndex + ix]);
                }
            }
        question.setText(quizStrings[lineIndex++]);
        ans1.setText(quizStrings[lineIndex++]);
        ans2.setText(quizStrings[lineIndex++]);
        ans3.setText(quizStrings[lineIndex++]);
        ans4.setText(quizStrings[lineIndex++]);
    }
        else{
            String userName = bundle.getString("name");
            Intent intent2 = new Intent(this, ThirdActivity.class);
            Bundle bundle2 = new Bundle();
            bundle2.putString("name", userName);
            bundle2.putInt("correct", ResultsHolder.getCorrectCounter());
            bundle2.putInt("total", totalQuestions);
            intent2.putExtras(bundle2);
            startActivity(intent2);
        }

    }


    //onClick function called by secondary activity xml when an answer option is selected
    //it sets the background of the selected option to light blue and the rest to white
    public void selected(View view) {
        answerIndex = 0;
        if (view == ans1) {
            colorSwap(ans1, ans2, ans3, ans4);
            answerIndex = 1;
        }
        if (view == ans2) {
            colorSwap(ans2, ans1, ans3, ans4);
            answerIndex = 2;
        }
        if (view == ans3) {
            colorSwap(ans3, ans2, ans1, ans4);
            answerIndex = 3;
        }
        if (view == ans4) {
            colorSwap(ans4, ans2, ans3, ans1);
            answerIndex = 4;
        }
        ResultsHolder.setAns(answerIndex);
    }

    //called by the selected function for readability
    public void colorSwap(View ans1, View ans2, View ans3, View ans4) {
        ans1.setBackgroundColor(Color.parseColor("#00FFFF"));
        ans2.setBackgroundColor(Color.parseColor("#FFFFFF"));
        ans3.setBackgroundColor(Color.parseColor("#FFFFFF"));
        ans4.setBackgroundColor(Color.parseColor("#FFFFFF"));
    }

}