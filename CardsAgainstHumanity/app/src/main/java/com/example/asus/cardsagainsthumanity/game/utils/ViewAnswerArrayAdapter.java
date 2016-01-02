package com.example.asus.cardsagainsthumanity.game.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.asus.cardsagainsthumanity.R;

import java.util.ArrayList;

public class ViewAnswerArrayAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final String[] answers1;
    private final String[] answers2;
    private int clickedItem = -1;

    public ViewAnswerArrayAdapter(Context context, String[] answers1, String[] answers2) {
        super(context, R.layout.content_answers, answers1);
        this.context = context;
        this.answers1 = answers1;
        this.answers2 = answers2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView;
        if(position == clickedItem){
            rowView = inflater.inflate(R.layout.content_view_answers1, parent, false);
        }
        else {
            rowView = inflater.inflate(R.layout.content_view_answers, parent, false);
        }
        TextView textView = (TextView) rowView.findViewById(R.id.text1);
        textView.setText(answers1[position]);

        TextView textView2 = (TextView) rowView.findViewById(R.id.text2);
        if(answers2 != null) {
            textView2.setText(answers2[position]);
        }
        else {
            textView2.setVisibility(View.GONE);
        }

        return rowView;
    }

    public void itemClicked(int position) {
        clickedItem = position;
        notifyDataSetChanged();
    }
}
