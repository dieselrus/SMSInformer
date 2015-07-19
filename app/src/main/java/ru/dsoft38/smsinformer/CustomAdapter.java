package ru.dsoft38.smsinformer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by diesel on 19.07.15.
 */
public class CustomAdapter extends BaseAdapter {
    ArrayList time;
    ArrayList data;
    Context context;

    private static LayoutInflater inflater=null;
    public CustomAdapter(MainActivity mainActivity, ArrayList _time, ArrayList _data) {
        time=_time;
        context=mainActivity;
        data=_data;
        inflater = (LayoutInflater)context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class Holder
    {
        TextView tv;
        TextView tv2;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder=new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.loglistviewe, null);

        holder.tv = (TextView) rowView.findViewById(R.id.log_time);
        holder.tv2 = (TextView) rowView.findViewById(R.id.log_text);
        holder.tv.setText(time.get(position).toString());
        holder.tv2.setText(data.get(position).toString());

        /*
        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "You Clicked " + data.get(position), Toast.LENGTH_LONG).show();
            }
        });
        */
        return rowView;
    }

}
