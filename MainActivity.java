//Written by Juan Porras for CS4301.002, assignment 2, starting February 26, 2021.
//        NetID: jdp180003

/*
Main Activity, gets name of user and determines whether quiz list will be sourced online or offline
by utilizing a recyclerView along with multithreading
 */

package com.jdporras.assignment2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.Network;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {
    //Instantiation of variables to be used later
    File filesDir;
    File[] quizzes;
    ArrayList<String> quizNames;
    ArrayList<String> localQuizNames;
    ArrayList<String> onlineQuizNames;
    ArrayList<ArrayList<String>> onlineQuizStrings;
    String[] quizStrings;
    RecyclerView quizList;
    String name;
    EditText editText;
    RadioGroup radioGroup;
    RadioButton radioButton;
    Network net;
    boolean firstRead = true, create;
    String siteURL = "https://personal.utdallas.edu/~john.cole/Data/Quizzes.txt";
    EditText username;
    Button createQuiz, editQuiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerAdapter.DataHolder.setEditMode(false);

        radioGroup = findViewById(R.id.radioGroup);

        createQuiz = findViewById(R.id.createQuiz);
        editQuiz = findViewById(R.id.editQuiz);
        username = findViewById(R.id.nameField);
        username.addTextChangedListener(textWatcher);

        quizList = findViewById(R.id.quizList);
        filesDir = getFilesDir();

        //Resetting the number of correct questions to 0 from activity 2
        SecondaryActivity.ResultsHolder.setCorrectCounter(0);
        SecondaryActivity.ResultsHolder.setEditMode(false);
        SecondaryActivity.ResultsHolder.setIndex(-1);

        //This function filters out filenames to find only quiz files
        FilenameFilter ff = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if(name.lastIndexOf('.') > 0){
                    int lastIndex = name.lastIndexOf('.');
                    String str = name.substring(lastIndex);
                    if(str.equals(".txt") && name.startsWith("Quiz")){
                        return true;
                    }
                }
                return false;
            }
        };

        //Set the quizzes string array to the filtered files
        quizzes = filesDir.listFiles(ff);
        quizNames = new ArrayList<String>();
        try{
            //this section scans all quiz files to get the quiz name
            for (int ix = 0; ix < quizzes.length; ix++){
                int iLine = 0;
                Scanner quiz = new Scanner(quizzes[ix]);
                while(quiz.hasNext()){
                    String qn = quiz.nextLine();
                    if (iLine == 0){
                        quizNames.add(qn);
                        iLine++;
                    }
                }
            }
            localQuizNames = quizNames;
        }
        catch(Exception exc){
            Log.d("FILETRYCATCH", "EXCEPTION: " + exc);
        }
        setAdapter();
        editText = (EditText)findViewById(R.id.nameField);
    }

    //sets the recyclerView info and other miscellaneous details
    private void setAdapter() {
        recyclerAdapter adapter = new recyclerAdapter(quizNames);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        quizList.setLayoutManager(layoutManager);
        quizList.setItemAnimator(new DefaultItemAnimator());
        quizList.setAdapter(adapter);
    }

    //the onClick function for the "Start Quiz" button, takes the text from the user name EditText view and quiz selection to
    //pass to SecondaryActivity with an intent
    public void store_input(View view) throws FileNotFoundException {
        Log.d("INDEX", "INDEX IS " + SecondaryActivity.ResultsHolder.getIndex());
        name = editText.getText().toString();
        if (!name.isEmpty() && (SecondaryActivity.ResultsHolder.getIndex() != -1)) {
        //class from reyclerAdapter specifically made to store the index of quiz selected
        int index = SecondaryActivity.ResultsHolder.getIndex();
        int ix = 0;
        quizStrings = new String[99];
        if (SecondaryActivity.ResultsHolder.isLocal()) {
            Scanner quiz = new Scanner(quizzes[index]);
            //temporary string array to hold quiz strings
            //Read the subject and store for later use
            String quizSubject = quiz.nextLine();
            SecondaryActivity.ResultsHolder.setQuizSubject(quizSubject);

            //reads all strings from a file into string array
            while (quiz.hasNextLine()) {
                String qn = quiz.nextLine();
                //checks to make sure line isn't empty
                if (!qn.equals("")) {
                    quizStrings[ix] = qn;
                    ix++;
                }
            }
        } else {
            SecondaryActivity.ResultsHolder.setQuizSubject(onlineQuizNames.get(index));
            Log.d("QUIZSUBJECT", "QUIZ SUBJECT IS SET TO " + SecondaryActivity.ResultsHolder.getQuizSubject());
            for (String string : onlineQuizStrings.get(index)) {
                quizStrings[ix] = string;
                ix++;
            }
        }
        //creates new array with proper size
        String[] finalQuizStrings = new String[ix];
        //populates newly made array
        for (int i = 0; i < ix; i++)
            finalQuizStrings[i] = quizStrings[i];
        //this section stores all relevant info for SecondaryActivity and starts it
        Intent intent = new Intent(this, SecondaryActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        bundle.putStringArray("quizStrings", finalQuizStrings);
        bundle.putInt("index", index);
        intent.putExtras(bundle);
        startActivity(intent);
    }
        if (name.isEmpty()){
            Context context = view.getContext();
            CharSequence text = "Please enter a name";
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
        else if(SecondaryActivity.ResultsHolder.getIndex() < 0){
            Context context = view.getContext();
            CharSequence text = "Please select a quiz";
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }

    }

    public void localClick(View view) {
        SecondaryActivity.ResultsHolder.setLocal(true);
        Log.d("LOCAL", "LOCAL HAS BEEN CLICKED");
        quizNames = localQuizNames;
        setAdapter();
    }

    public void onlineClick(View view) {
        SecondaryActivity.ResultsHolder.setLocal(false);
        Log.d("ONLINE", "ONLINE HAS BEEN CLICKED");
        readOnlineFile readFile = new readOnlineFile();
        ArrayList<String> arg = new ArrayList<String>();
        if(firstRead) {
            arg.add(siteURL);
            readFile.execute(arg);
        }
        else{
            quizNames = onlineQuizNames;
            setAdapter();
        }
    }

    public void createClick(View view) throws FileNotFoundException {
        create = true;
        startFourth();
    }

    public void editClick(View view) throws FileNotFoundException {
        create = false;
        if(recyclerAdapter.DataHolder.isQuizSelected())
            startFourth();
        else{
            Context context = getApplicationContext();
            CharSequence text = "Please select a quiz first";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
    }

    public void startFourth() throws FileNotFoundException {
        Intent intent = new Intent(this, FourthActivity.class);
        SecondaryActivity.ResultsHolder.setIsCreate(create);
        SecondaryActivity.ResultsHolder.setEditMode(true);
        int index = SecondaryActivity.ResultsHolder.getIndex();
        Log.d("MAININDEX", "THIS IS INDEX IN MAIN: " + index);
        if(!create){
            int ix = 0;
            quizStrings = new String[99];
            if (SecondaryActivity.ResultsHolder.isLocal()) {
                Scanner quiz = new Scanner(quizzes[index]);
                //temporary string array to hold quiz strings
                //Read the subject and store for later use
                String quizSubject = quiz.nextLine();
                SecondaryActivity.ResultsHolder.setQuizSubject(quizSubject);

                //reads all strings from a file into string array
                while (quiz.hasNextLine()) {
                    String qn = quiz.nextLine();
                    //checks to make sure line isn't empty
                    if (!qn.equals("")) {
                        quizStrings[ix] = qn;
                        ix++;
                    }
                }
            }
            //creates new array with proper size
            String[] finalQuizStrings = new String[ix];
            //populates newly made array
            for (int i = 0; i < ix; i++)
                finalQuizStrings[i] = quizStrings[i];
            //this section stores all relevant info for SecondaryActivity and starts it
            SecondaryActivity.ResultsHolder.setFileName(quizzes[index].getName());
            Scanner quiz = new Scanner(quizzes[index]);
            //temporary string array to hold quiz strings
            //Read the subject and store for later use
            String quizSubject = quiz.nextLine();
            Log.d("SUBJECT", "SETTING QUIZ SUBJECT TO " + quizSubject);
            SecondaryActivity.ResultsHolder.setQuizSubject(quizSubject);
            Log.d("FINALQUIZSTRINGS", "HEAD IS " + finalQuizStrings[0] + " AND SIZE IS " + finalQuizStrings.length);
            ArrayList<String> temp = new ArrayList<>();
            for(String str : finalQuizStrings)
                temp.add(str);
            SecondaryActivity.ResultsHolder.setArrListQuizStrings(temp);
        }
        SecondaryActivity.ResultsHolder.setNotInMain(true);
        if(create)
            SecondaryActivity.ResultsHolder.initArrListQuizStrings();
        startActivity(intent);
    }

    private class readOnlineFile extends AsyncTask<ArrayList<String>, Integer, ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(ArrayList<String>... arrayLists) {
            Log.d("BACKGROUND", "WE IN THE BACKGROUND RN");
            Log.d("ARRALIST", "ARRAYLIST AT INDEX 0: " + arrayLists[0].get(0));
            URL url = null;
            ArrayList<String> quizFileNames = new ArrayList<String>();
            Scanner in = null;
            try {
                url = new URL(arrayLists[0].get(0));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) url.openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                connection.setRequestMethod("GET");
            } catch (ProtocolException e) {
                e.printStackTrace();
            }
            connection.setConnectTimeout(1000);
            int response = 0;
            try {
                response = connection.getResponseCode();
            } catch (IOException e) {
                e.printStackTrace();
            }
            InputStream ins = null;
            if(response == HttpURLConnection.HTTP_OK){
                try{
                    ins = connection.getInputStream();
                    in = new Scanner(ins);
                }
                catch(Exception ex){
                    System.out.println(ex.getMessage());
                }
                while(in.hasNext()){
                    quizFileNames.add(in.nextLine());
                }
                in.close();
            }
            connection.disconnect();

            onlineQuizNames = new ArrayList<String>();
            ArrayList<String> tempOnlineStrings = new ArrayList<>();
            onlineQuizStrings = new ArrayList<>(15);
            int sSize = 0;
            for(String s : quizFileNames){
                sSize++;
            }
            for(int i = 0; i < sSize; i++) {
                onlineQuizStrings.add(new ArrayList<>());
                try {
                    url = new URL("https://personal.utdallas.edu/~john.cole/Data/" + quizFileNames.get(i));
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                connection = null;
                try {
                    connection = (HttpURLConnection) url.openConnection();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    connection.setRequestMethod("GET");
                } catch (ProtocolException e) {
                    e.printStackTrace();
                }
                connection.setConnectTimeout(1000);
                response = 0;
                try {
                    response = connection.getResponseCode();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ins = null;
                if (response == HttpURLConnection.HTTP_OK) {
                    try {
                        ins = connection.getInputStream();
                        in = new Scanner(ins);
                    } catch (Exception ex) {
                        System.out.println(ex.getMessage());
                    }
                    onlineQuizNames.add(in.nextLine());
                    while(in.hasNext())
                        onlineQuizStrings.get(i).add(in.nextLine());
                    in.close();
                }

                connection.disconnect();
            }

            return onlineQuizNames;
        }

        @Override
        protected void onPostExecute(ArrayList<String> s){
            super.onPostExecute(s);
            quizNames = s;
            int sSize = 0;
            for(String str : s){
                sSize++;
            }
            if(firstRead) {
                firstRead = false;
                setAdapter();
            }
            Log.d("POSTEXECUTE", "THE ARRAYLIST AT POSTEXECUTION HAS " + s.get(0));
        }
    }

    // implement the TextWatcher callback listener
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // get the content of the edit text
            String nameInput = username.getText().toString();
            // check whether the name is Professor
            if(nameInput.equals("Professor") || nameInput.equals("professor")) {
                createQuiz.setVisibility(View.VISIBLE);
                editQuiz.setVisibility(View.VISIBLE);
            }
            else{
                createQuiz.setVisibility(View.INVISIBLE);
                editQuiz.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

}