package com.example.terminal_control;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class CustomAdapter_Ter extends BaseAdapter {

    private Context mContext;
    Controllerdb controldb;
    SQLiteDatabase db;

   // private ArrayList<String> Id = new ArrayList<String>();
    private ArrayList<String> TerNo = new ArrayList<String>();
    private ArrayList<String> IPAddress = new ArrayList<String>();
    private ArrayList<String> Delete = new ArrayList<String>();
    private ArrayList<String> TerName = new ArrayList<String>();
    public static final String TAG = "ElSmart";
    TextView Ter_Name;
    public CustomAdapter_Ter(Context  context, ArrayList<String> TerNo,ArrayList<String> IPAddress,ArrayList<String> TerName,ArrayList<String> Delete)
    {
        this.mContext = context;
       // this.Id = Id;
        this.TerNo = TerNo;
        this.IPAddress=IPAddress;
        this.Delete=Delete;
        this.TerName=TerName;

    }

    @Override
    public int getCount() {
        return TerName.size();
    }
    @Override
    public Object getItem(int position) {
        return null;
    }
    @Override
    public long getItemId(int position) {
        return 0;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final CustomAdapter_Ter.viewHolder holder;
        controldb =new Controllerdb(mContext);
        LayoutInflater layoutInflater;
        if (convertView == null) {
            layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.layout_ter, null);
            holder = new CustomAdapter_Ter.viewHolder();
            holder.TerName = (TextView) convertView.findViewById(R.id.TerName);
            holder.TerNo = (TextView) convertView.findViewById(R.id.TerNo);
            holder.IPAddress = (TextView) convertView.findViewById(R.id.IPAddress);
            holder.Delete = (Button) convertView.findViewById(R.id.delete_btn);



            Ter_Name=(TextView) convertView.findViewById(R.id.TerName);
            Ter_Name.setPaintFlags(Ter_Name.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            Ter_Name.setTextColor(Color.BLUE);
            holder.TerName = Ter_Name;



            convertView.setTag(holder);
        } else {
            holder = (CustomAdapter_Ter.viewHolder) convertView.getTag();
        }
        holder.TerName.setText(TerName.get(position));
        holder.TerNo.setText(TerNo.get(position));
        holder.IPAddress.setText(IPAddress.get(position));
       // holder.Delete.setText("");
        holder.Delete.setText(Delete.get(position));




        holder.TerName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ter_control.class);

                Bundle b = new Bundle();
                b.putString("Terminal",String.valueOf( holder.Delete.getText()+","+holder.TerName.getText()+","+holder.TerNo.getText()));
                intent.putExtras(b);
                mContext.startActivity(intent);

            }
        });

        holder.Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ter_config.class);

                Bundle b = new Bundle();
                b.putString("Terminal",String.valueOf( holder.Delete.getText()));
                intent.putExtras(b);
                mContext.startActivity(intent);

            }
        });

        return convertView;
    }
    public class viewHolder {
        TextView TerNo;
        TextView TerName;
        TextView IPAddress;
        Button Delete;
    }

}
