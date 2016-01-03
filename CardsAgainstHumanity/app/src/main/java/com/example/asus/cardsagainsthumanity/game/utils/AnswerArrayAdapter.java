package com.example.asus.cardsagainsthumanity.game.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.asus.cardsagainsthumanity.R;

import java.util.ArrayList;

public class AnswerArrayAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final String[] values;
    private ArrayList<Integer> clickedItens = new ArrayList<Integer>();
    private int maxTam;

    public AnswerArrayAdapter(Context context, String[] values, int maxTam) {
        super(context, R.layout.content_answers, values);
        this.context = context;
        this.values = values;
        this.maxTam = maxTam;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView;
        if(!clickedItens.isEmpty()) {
            if (position == clickedItens.get(0))
                rowView = inflater.inflate(R.layout.content_answers1, parent, false);
            else if (clickedItens.size() > 1 && position == clickedItens.get(1))
                rowView = inflater.inflate(R.layout.content_answers2, parent, false);
            else
                rowView = inflater.inflate(R.layout.content_answers, parent, false);
        }
        else
            rowView = inflater.inflate(R.layout.content_answers, parent, false);

        TextView textView = (TextView) rowView.findViewById(R.id.text1);
        textView.setText(values[position]);

        return rowView;
    }

    public void itemClicked(int position) {
        if(clickedItens.contains(position))
            clickedItens.remove(clickedItens.indexOf(position));
        else{
            if(clickedItens.size()>maxTam-1)
                clickedItens.remove(maxTam-1);

            clickedItens.add(position);
        }

        notifyDataSetChanged();
    }

    public int getClickedItensSize() {
        return clickedItens.size();
    }

    public ArrayList<String> getClickedItens() {
        ArrayList<String> result = new ArrayList<>();

        for(int i : clickedItens) {
            result.add(values[i]);
        }

        return result;
    }
}
