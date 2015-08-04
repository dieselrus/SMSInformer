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
public class CustomAdapterSMSQueuing extends BaseAdapter {
    ArrayList id;
    ArrayList num;
    ArrayList text;
    Context context;

    private static LayoutInflater inflater=null;
    public CustomAdapterSMSQueuing(SMSQueuing _activity, ArrayList _id, ArrayList _num, ArrayList _text) {
        id=_id;
        num=_num;
        text=_text;

        context=_activity;

        inflater = (LayoutInflater)context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return num.size();
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
        TextView tvId;
        TextView tvNum;
        TextView tvText;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder=new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.smslistviewe, null);

        holder.tvId = (TextView) rowView.findViewById(R.id.sms_id);
        holder.tvNum = (TextView) rowView.findViewById(R.id.sms_num);
        holder.tvText = (TextView) rowView.findViewById(R.id.sms_text);

        holder.tvId.setText(id.get(position).toString());
        holder.tvNum.setText(num.get(position).toString());
        holder.tvText.setText(text.get(position).toString());

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
