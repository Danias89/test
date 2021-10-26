/*
This class is the driver behind the dynamic recycler View used in main activity
 */
package com.jdporras.assignment2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class recyclerAdapter extends RecyclerView.Adapter<recyclerAdapter.MyViewHolder> {
    FourthActivity main;
    private int selectedPos = RecyclerView.NO_POSITION;
    public static class DataHolder {
        private static int index;
        private static boolean editMode = false;
        public static boolean isEditMode() { return editMode;}
        public static void setEditMode(boolean data) {editMode = data;}
        private static boolean quizSelected = false;
        public static boolean isQuizSelected() {return quizSelected;}
        public static void setSelected(boolean data) {DataHolder.quizSelected = data;}
        public static int getIndex() {return index;}
        public static void setIndex(int data) {DataHolder.index = index;}
    }

    ArrayList<String> quizNames;
    Context con;

    public recyclerAdapter(ArrayList<String> quizNames){
        this.quizNames = quizNames;
    }
    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView quizTxt;
        int choice = -1;
        public MyViewHolder(View view){
            super(view);
            quizTxt = view.findViewById(R.id.textView2);
        }

    }

    @NonNull
    @Override
    public recyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.quizlist, parent, false);
        main = new FourthActivity();
        con = itemView.getContext();
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull recyclerAdapter.MyViewHolder holder, int position) {
        holder.quizTxt.setText(quizNames.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.quizTxt.setText(quizNames.get(position) + "<-------");
                Context context = view.getContext();
                CharSequence text = "You have selected " + quizNames.get(position);
                int duration = Toast.LENGTH_LONG;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                DataHolder.setSelected(true);
                SecondaryActivity.ResultsHolder.setIndex(position);
                Log.d("INDEX", "SETTING INDEX TO " + position);
                Intent intent = new Intent(view.getContext(), FourthActivity.class);
                SecondaryActivity.ResultsHolder.setRecyclerCount();
                if(SecondaryActivity.ResultsHolder.isEditMode() && SecondaryActivity.ResultsHolder.isNotInMain()) {
                    SecondaryActivity.ResultsHolder.setRecyclerFlag(true);
                    SecondaryActivity.ResultsHolder.setModify(true);
                    view.getContext().startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return quizNames.size();
    }

}
