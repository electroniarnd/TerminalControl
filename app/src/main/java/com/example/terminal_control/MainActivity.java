package com.example.terminal_control;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    Controllerdb controllerdb = new Controllerdb(this);
    SQLiteDatabase db;
    private ArrayList<String> TerNo = new ArrayList<String>();
    //private ArrayList<String> Id = new ArrayList<String>();
    private ArrayList<String> IPAddress = new ArrayList<String>();
    private ArrayList<String> Delete = new ArrayList<String>();
    private ArrayList<String> TerName = new ArrayList<String>();
    public static final String TAG = "Terminal_List";
    ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        lv = (ListView) findViewById(R.id.lstvw);

        ViewGroup headerView = (ViewGroup) getLayoutInflater().inflate(R.layout.header_ter, lv, false);
        // lv.addHeaderView(headerView);
        lv.setSmoothScrollbarEnabled(true);






        displayData();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    final int position, long id) {
                Log.i("List View Clicked", "**********");
                displayData();
                // Toast.makeText(Device.this, getResources().getString(R.string.List_View_Clicked) + position, Toast.LENGTH_LONG).show();
            }
        });



    }


    private void displayData() {
        db = controllerdb.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM  tblTerminals",null);
       // Id.clear();
        IPAddress.clear();
        TerNo.clear();
        Delete.clear();
        TerName.clear();
        if (cursor.moveToFirst()) {
            do {
                //Id.add(cursor.getString(cursor.getColumnIndex("Id")));
                IPAddress.add(cursor.getString(cursor.getColumnIndex("IPAddress")));
                TerNo.add(cursor.getString(cursor.getColumnIndex("TerNo")));
                Delete.add(cursor.getString(cursor.getColumnIndex("Id")));
                TerName.add(cursor.getString(cursor.getColumnIndex("TerName")));
            } while (cursor.moveToNext());
        }
        CustomAdapter_Ter ca = new CustomAdapter_Ter(MainActivity.this, TerNo,IPAddress,TerName,Delete);
        lv.setAdapter(ca);
        //code to set adapter to populate list
        cursor.close();
    }






    public Integer Deletedata()
    {
        Integer res=0;
        try {
            db = controllerdb.getWritableDatabase();
            db.execSQL("Delete from tblTerminals" );// (date,BadgeNo,Name,Ter,direction,empid)VALUES('"+txtdatetime.getText()+"','"+badgeno+"','"+name+"' ,'"+compara.termo+"','"+s+"' ,"+empID+")" );
            res=1;
        }
        catch (Exception ex) {
            res=0;
            Log.d(TAG, ex.getMessage());
        }
        return res;

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, ter_config.class);

            Bundle b = new Bundle();
            b.putString("Terminal","0");
            intent.putExtras(b);
            startActivity(intent);
          //  startActivity(new Intent(MainActivity.this, ter_config.class));
        }

        return super.onOptionsItemSelected(item);
    }
}
