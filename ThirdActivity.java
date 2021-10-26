/*
This activity displays results from secondary activity and returns to main activity when clicked
 */

package com.jdporras.assignment2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ThirdActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //this section retrieves data from the SecondaryActivity bundle
        setContentView(R.layout.activity_third);
        TextView name = findViewById(R.id.userName);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        name.setText(bundle.getString("name"));

        TextView quizName = findViewById(R.id.quizName);
        quizName.setText(SecondaryActivity.ResultsHolder.getQuizSubject());

        TextView score = findViewById(R.id.score);
        score.setText("Your Score Was: " + bundle.getInt("correct") + "/" + bundle.getInt("total"));

        Button returnToMain = findViewById(R.id.returnToMain);
    }

    //returns back to MainActivity when clicked
    public void returnToMain(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}