package com.example.asus.cardsagainsthumanity.game.utils;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.asus.cardsagainsthumanity.R;

import java.util.List;

public class ScoreTableArrayAdapter extends ArrayAdapter<String> {
    private final Context context;
    private List<String> names;
    private List<Integer> points;

    public ScoreTableArrayAdapter(Context context, List<String> names, List<Integer> points) {
        super(context, R.layout.content_score_table, names);
        this.context = context;
        this.names = names;
        this.points = points;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView;
        if(Game.deviceName.equals(names.get(position)))
            rowView = inflater.inflate(R.layout.content_score_table_self, parent, false);
        else
            rowView = inflater.inflate(R.layout.content_score_table, parent, false);

        ImageView imageView = (ImageView) rowView.findViewById(R.id.contentImage);
        if(position == 0)
            imageView.setImageResource(R.drawable.btn_rating_star_on_selected);
        else if(position == 1)
            imageView.setImageResource(R.drawable.btn_rating_star_on_pressed);
        else if(position == 2)
            imageView.setImageResource(R.drawable.btn_rating_star_on_normal);
        else
            imageView.setImageResource(android.R.color.transparent);

        Log.wtf("WTF", names.size()+" "+points.size());

        TextView textView = (TextView) rowView.findViewById(R.id.contentPlayerName);
        textView.setText(names.get(position));

        TextView textView2 = (TextView) rowView.findViewById(R.id.contentPlayerPoints);
        textView2.setText(points.get(position)+" pts");

        return rowView;
    }
}
