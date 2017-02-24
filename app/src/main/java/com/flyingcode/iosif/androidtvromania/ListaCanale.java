package com.flyingcode.iosif.androidtvromania;

import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by iosif on 10/3/15.
 */
public class ListaCanale extends ArrayAdapter<Canal>  {
    private final ArrayList<Canal> canal;
    private final HomeScreen.Channel_list_callback callback;

    public ListaCanale(Context context,int resource, ArrayList<Canal> canal,HomeScreen.Channel_list_callback callback) {
        super(context, resource);
        this.canal = canal;
        this.callback = callback;

    }

    @Override
    public Canal getItem(int position) {
        return canal.get(position);
    }

    @Override
    public int getCount() {
        return canal.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.canal_row, null);
        }

        Canal canal = getItem(position);
        TextView channel_name = (TextView) v.findViewById(R.id.channel_name);
        channel_name.setText(canal.getName());
        //v.setOnClickListener(onclick(canal));


        return v;
    }

    private View.OnKeyListener onkeylistener(final Canal canal) {
        return new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode==23){
                    callback.play(canal);
                    return true;
                }
                return false;
            }
        };
    }

    private View.OnClickListener onclick(final Canal canal) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.play(canal);
            }
        };
    }

}
