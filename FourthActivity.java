package com.jdporras.assignment2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.telephony.ClosedSubscriberGroupInfo;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class FourthActivity extends AppCompatActivity {
    RecyclerView quizList;
    ArrayList<String> questionList, quizStrings;
    File filesDir, newQuiz;
    EditText newQuizName, newSubjectName, newQuestionName, newAns1, newAns2, newAns3, newAns4;
    Button saveButton, deleteButton;
    String quizName, questionName, quizSubject, ans1, ans2, ans3, ans4;
    RadioButton rad1, rad2, rad3, rad4;
    String [] rawQuizStrings;
    int correctMarker = 0;
    boolean isCreate, fromRecycle, firstWrite;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fourth);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        saveButton = findViewById(R.id.saveButton);
        deleteButton = findViewById(R.id.deleteButton);
        quizList = findViewById(R.id.newQuizList);
        newQuestionName = findViewById(R.id.questionName);
        newSubjectName = findViewById(R.id.subjectName);
        newAns1 = findViewById(R.id.newAnswer1);
        newAns2 = findViewById(R.id.newAnswer2);
        newAns3 = findViewById(R.id.newAnswer3);
        newAns4 = findViewById(R.id.newAnswer4);
        newQuizName = findViewById(R.id.newQuizName);
        rad1 = findViewById(R.id.correctAns1);
        rad2 = findViewById(R.id.correctAns2);
        rad3 = findViewById(R.id.correctAns3);
        rad4 = findViewById(R.id.correctAns4);


        if(SecondaryActivity.ResultsHolder.isRecyclerFlag()) {
            if(SecondaryActivity.ResultsHolder.getIndex() * 5 < SecondaryActivity.ResultsHolder.getArrListQuizStrings().size())
                setQuestion();
            SecondaryActivity.ResultsHolder.setRecyclerFlag(false);
        }
        filesDir = getFilesDir();

        if(!SecondaryActivity.ResultsHolder.isIsCreate()){
            quizStrings = SecondaryActivity.ResultsHolder.getArrListQuizStrings();
            questionList = new ArrayList<String>();
            newQuizName.setText(SecondaryActivity.ResultsHolder.getFileName());
            newQuizName.setEnabled(false);
            newSubjectName.setText(SecondaryActivity.ResultsHolder.getQuizSubject());
            newSubjectName.setEnabled(false);
            deleteButton.setEnabled(true);
            for(String str : quizStrings){
                if(str.endsWith("?"))
                    questionList.add(str);
            }
        }
        else
            questionList = new ArrayList<String>(Arrays.asList(""));
        setAdapter();
    }


    private void setAdapter() {
        recyclerAdapter adapter = new recyclerAdapter(questionList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        quizList.setLayoutManager(layoutManager);
        quizList.setItemAnimator(new DefaultItemAnimator());
        quizList.setAdapter(adapter);
    }

    public void saveClick(View view) throws IOException {
        quizName = newQuizName.getText().toString();
        quizSubject = newSubjectName.getText().toString();
        questionName = newQuestionName.getText().toString();
        ans1 = newAns1.getText().toString();
        ans2 = newAns2.getText().toString();
        ans3 = newAns3.getText().toString();
        ans4 = newAns4.getText().toString();


        if(quizName.isEmpty()){
            Context context = getApplicationContext();
            CharSequence text = "Please enter a name for the quiz";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
        else if(quizSubject.isEmpty()){
            Context context = getApplicationContext();
            CharSequence text = "Please enter a subject for the quiz";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
        else if(questionName.isEmpty()){
            Context context = getApplicationContext();
            CharSequence text = "Please enter a name for the question";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
        else if(ans1.isEmpty() || ans2.isEmpty() || ans3.isEmpty() || ans4.isEmpty()){
            Context context = getApplicationContext();
            CharSequence text = "Please ensure all answer fields are filled";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
        else if(correctMarker == 0){
            Context context = getApplicationContext();
            CharSequence text = "Please mark a correct answer";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
        else {
            if(!quizName.startsWith("Quiz"))
                quizName = "Quiz" + quizName + ".txt";
            else if(!quizName.contains(".txt"))
                quizName += ".txt";

            switch (correctMarker){
                case 1:
                    ans1 = "*" + ans1;
                    break;
                case 2:
                    ans2 = "*" + ans2;
                    break;
                case 3:
                    ans3 = "*" + ans3;
                    break;
                case 4:
                    ans4 = "*" + ans4;
                    break;
            }
            if (!questionName.endsWith("?"))
                questionName += "?";
            SecondaryActivity.ResultsHolder.setFileName(quizName);
            SecondaryActivity.ResultsHolder.setQuizSubject(quizSubject);
            saveData();
        }
    }

    public void backClick(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void clearClick(View view) {
        newQuestionName.getText().clear();
        newAns1.getText().clear();
        newAns2.getText().clear();
        newAns3.getText().clear();
        newAns4.getText().clear();
        SecondaryActivity.ResultsHolder.setIsCreate(false);
        rad1.setChecked(false);
        rad2.setChecked(false);
        rad3.setChecked(false);
        rad4.setChecked(false);
        correctMarker = 0;
        if(newQuizName.isEnabled())
            newQuizName.getText().clear();
        if(newSubjectName.isEnabled())
            newSubjectName.getText().clear();
    }

    public void saveData() throws IOException {
            newQuiz = new File(getApplicationContext().getFilesDir(), SecondaryActivity.ResultsHolder.getFileName());
            FileWriter writer = new FileWriter(newQuiz);

        if(!SecondaryActivity.ResultsHolder.isModify() && !SecondaryActivity.ResultsHolder.isDeleteFlag()) {
            Log.d("ADD", "ADDING NEW STRINGS");
            quizStrings = SecondaryActivity.ResultsHolder.getArrListQuizStrings();
            questionList.add(questionName);
            quizStrings.add(questionName);
            quizStrings.add(ans1);
            quizStrings.add(ans2);
            quizStrings.add(ans3);
            quizStrings.add(ans4);
            SecondaryActivity.ResultsHolder.setArrListQuizStrings(quizStrings);
        }

        else if (!SecondaryActivity.ResultsHolder.isDeleteFlag()){
            Log.d("WRITE", "WRITING OVER EXISTING STRINGS");
            quizStrings = SecondaryActivity.ResultsHolder.getArrListQuizStrings();
            int index = SecondaryActivity.ResultsHolder.getIndex() * 5;
            quizStrings.set(index++, questionName);
            quizStrings.set(index++, ans1);
            quizStrings.set(index++, ans2);
            quizStrings.set(index++, ans3);
            quizStrings.set(index, ans4);
            SecondaryActivity.ResultsHolder.setArrListQuizStrings(quizStrings);
        }


        if (SecondaryActivity.ResultsHolder.isFirstWrite()) {
            newQuizName.setEnabled(false);
            newSubjectName.setEnabled(false);
        }
            Log.d("WRITE", "NOT FIRST WRITE");
        Log.d("QUIZSTRINGS", "AT TIME OF WRITING QUIZSTRINGS HAS A SIZE OF " + quizStrings.size());
            ArrayList<String> finalQuizStrings = new ArrayList<String>();
            finalQuizStrings.add(SecondaryActivity.ResultsHolder.getQuizSubject());
            Log.d("SUBJECT", "SUBJECT AT WRITING TIME IS " + SecondaryActivity.ResultsHolder.getQuizSubject());
                for(String str : quizStrings) {
                    finalQuizStrings.add(str);
                }
                for(String str : finalQuizStrings)
                    writer.write(str + "\n");
//            writer.append(questionName);
//            writer.append("\n");
//            writer.append(ans1);
//            writer.append("\n");
//            writer.append(ans2);
//            writer.append("\n");
//            writer.append(ans3);
//            writer.append("\n");
//            writer.append(ans4);
//            writer.append("\n");

        SecondaryActivity.ResultsHolder.setArrListQuizStrings(quizStrings);
        //SecondaryActivity.ResultsHolder.setQuizStrings(quizStrings.toString().split(""));
        SecondaryActivity.ResultsHolder.setIsArrListinit(true);

        Context context = getApplicationContext();
        CharSequence text = "Changes saved successfully";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();

        writer.flush();
        writer.close();
        SecondaryActivity.ResultsHolder.setDeleteFlag(false);
        SecondaryActivity.ResultsHolder.setFirstWrite(false);
        newQuestionName.getText().clear();
        newAns1.getText().clear();
        newAns2.getText().clear();
        newAns3.getText().clear();
        newAns4.getText().clear();
        SecondaryActivity.ResultsHolder.setIsCreate(false);
        rad1.setChecked(false);
        rad2.setChecked(false);
        rad3.setChecked(false);
        rad4.setChecked(false);
        correctMarker = 0;
    }

    public void ans1Click(View view) {
        correctMarker = 1;
    }

    public void ans2Click(View view) {
        correctMarker = 2;
    }

    public void ans3Click(View view) {
        correctMarker = 3;
    }

    public void ans4Click(View view) {
        correctMarker = 4;
    }


    public void setQuestion(){
        int index = SecondaryActivity.ResultsHolder.getIndex() * 5;
        Log.d("INDEX", "INSIDE SETQUESTION IS " + index);
        quizStrings = SecondaryActivity.ResultsHolder.getArrListQuizStrings();
        Log.d("LENGTH", "QUIZSTRINGS LENGTH IS " + quizStrings.size());
        Log.d("QUIZSTRINGS", "CURRENTLY USING STRING OF QUIZSTRINGS FROM RESULTSHOLDER: " + quizStrings.get(index));
        newQuestionName.setText(quizStrings.get(index++));
        correctMarker = 0;
        Log.d("CORRECTMARKER", "BEFORE OPERATIONS CORRECT IS " + correctMarker);
        if(quizStrings.get(index).contains("*")) {
            rad1.setChecked(true);
            correctMarker = 1;
            quizStrings.set(index, quizStrings.get(index).substring(1));
        }
        newAns1.setText(quizStrings.get(index++));
        if(quizStrings.get(index).contains("*")) {
            rad2.setChecked(true);
            correctMarker = 2;
            quizStrings.set(index, quizStrings.get(index).substring(1));
        }
        newAns2.setText(quizStrings.get(index++));
        if(quizStrings.get(index).contains("*")) {
            rad3.setChecked(true);
            correctMarker = 3;
            quizStrings.set(index, quizStrings.get(index).substring(1));
        }
        newAns3.setText(quizStrings.get(index++));
        if(quizStrings.get(index).contains("*")) {
            rad4.setChecked(true);
            correctMarker = 4;
            quizStrings.set(index, quizStrings.get(index).substring(1));
        }
        newAns4.setText(quizStrings.get(index++));
        Log.d("CORRECTMARKER", "AFTER OPERATIONS CORRECT IS " + correctMarker);
    }

    public void deleteClick(View view) throws IOException {
        int index = SecondaryActivity.ResultsHolder.getIndex() * 5;
        quizStrings = SecondaryActivity.ResultsHolder.getArrListQuizStrings();
        questionList.remove(SecondaryActivity.ResultsHolder.getIndex());
        quizStrings.remove(index);
        quizStrings.remove(index);
        quizStrings.remove(index);
        quizStrings.remove(index);
        quizStrings.remove(index);
        SecondaryActivity.ResultsHolder.setArrListQuizStrings(quizStrings);
        SecondaryActivity.ResultsHolder.setDeleteFlag(true);

        newQuestionName.getText().clear();
        newAns1.getText().clear();
        newAns2.getText().clear();
        newAns3.getText().clear();
        newAns4.getText().clear();
        //SecondaryActivity.ResultsHolder.setIsCreate(false);
        rad1.setChecked(false);
        rad2.setChecked(false);
        rad3.setChecked(false);
        rad4.setChecked(false);
        correctMarker = 0;
        if(newQuizName.isEnabled())
            newQuizName.getText().clear();
        if(newSubjectName.isEnabled())
            newSubjectName.getText().clear();
        saveData();
    }
    }