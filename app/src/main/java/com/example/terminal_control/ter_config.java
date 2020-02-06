package com.example.terminal_control;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ter_config extends AppCompatActivity {
    Controllerdb controllerdb = new Controllerdb(this);
    SQLiteDatabase db;



    EditText edtIPAddress,edtPortNo,edtTerNo,edtcust,editTerName;
    Button btnUpdate,btnCancel,btnDelete;
    String Terminal="", Id ="",IPAddress="",TerNo="",CustID="",PortNo="",TerName="";

    public static final String TAG = "Terminal_Config";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ter_config);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        edtIPAddress= (EditText) findViewById(R.id.edtIpAddress);
        edtPortNo= (EditText) findViewById(R.id.edtPortNo);
        edtTerNo= (EditText) findViewById(R.id.edtTerno);
        btnUpdate=(Button) findViewById(R.id.btnupdate);
        btnCancel=(Button) findViewById(R.id.btnCancel);
        btnDelete=(Button) findViewById(R.id.btnDelete);
        edtcust=(EditText) findViewById(R.id.edtcust);
        editTerName=(EditText) findViewById(R.id.editTerName);

        Intent intent = getIntent();
        Terminal = intent.getStringExtra("Terminal");
       if(Terminal.equals("0"))
       {
           edtIPAddress.setText("");
           edtPortNo.setText("4001");
           edtTerNo.setText("");
           edtcust.setText("");
           editTerName.setText("");

       }
       else
       {
           ReadTerminalValue(Terminal);
       }


        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(TextUtils.isEmpty( edtTerNo.getText().toString())) {
                    Toast.makeText(ter_config.this,  getResources().getString(R.string.Please_enter_Terminal_No), Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty( edtIPAddress.getText().toString())) {
                    Toast.makeText(ter_config.this,  getResources().getString(R.string.Please_enter_IPAddress), Toast.LENGTH_SHORT).show();
                    return;
                }



                if(TextUtils.isEmpty( edtPortNo.getText().toString())) {
                    Toast.makeText(ter_config.this, getResources().getString(R.string.Please_enter_Port_No), Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty( edtcust.getText().toString())) {
                    Toast.makeText(ter_config.this,  getResources().getString(R.string.Please_enter_Customer_ID), Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty( editTerName.getText().toString())) {
                    Toast.makeText(ter_config.this,  getResources().getString(R.string.Please_enter_Terminal_Name), Toast.LENGTH_SHORT).show();
                    return;
                }
                if(Terminal.equals("0")) {
                    WriteTerminal();
                }
                else
                {
                    WriteTerminal(Terminal);
                }
                startActivity(new Intent(ter_config.this, MainActivity.class));
                finish();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ter_config.this, MainActivity.class));
                finish();

            }
        });


        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                        Integer res=0;
                db = controllerdb.getWritableDatabase();
                        try {
                            db = controllerdb.getWritableDatabase();
                            String[] params = new String[]{ Terminal };
                            db.execSQL("Delete from tblTerminals  where Id=?", params);// (date,BadgeNo,Name,Ter,direction,empid)VALUES('"+txtdatetime.getText()+"','"+badgeno+"','"+name+"' ,'"+compara.termo+"','"+s+"' ,"+empID+")" );
                            res=1;
                        }
                        catch (Exception ex) {
                            res=0;
                            Log.d(TAG, ex.getMessage());
                        }
                        if(res>0)
                        {
                            Toast.makeText(ter_config.this, "Data deleted " + TerName, Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(ter_config.this,
                                    "Fail to Delete", Toast.LENGTH_SHORT).show();
                        }

                finish();
            }
        });
    }


    public Integer WriteTerminal()//////CHANGE INTO COMMON FUNCTION LATTER
    {
        int res = 0;
        try {

            db = controllerdb.getWritableDatabase();
            db.execSQL("INSERT INTO tblTerminals(IPAddress,TerNo,CustNo,PortNo,TerName)VALUES('" + edtIPAddress.getText() + "',"  + edtTerNo.getText()  + ","  + edtcust.getText() + "," +edtPortNo.getText() +",'" + editTerName.getText() + "' )");
            res = 1;
        } catch (Exception ex) {
            res = 0;
            Log.d(TAG, ex.getMessage());
        }
        return res;
    }




    public Integer WriteTerminal(String  ID)//////CHANGE INTO COMMON FUNCTION LATTER
    {
        int res = 0;
        IPAddress= edtIPAddress.getText().toString();
        PortNo= edtPortNo.getText().toString();
        TerNo=  edtTerNo.getText().toString();
        CustID=  edtcust.getText().toString();
        TerName=  editTerName.getText().toString();
        try {
           // db = controllerdb.getReadableDatabase();
          //  String query="update tblTerminals set TerNo = ?, PortNo = ?, IPAddress = ?, CustNo = ?, TerName = ? where Id = ?";
           // String[] selections={TerNo, PortNo,"'"+IPAddress+"'",CustID,"'"+TerName+"'",Id};

            //Cursor cursor=db.rawQuery(query, selections);

             db = controllerdb.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("IPAddress",IPAddress);
            contentValues.put("TerNo",TerNo);
            contentValues.put("CustNo",CustID);
            contentValues.put("PortNo",PortNo);
            contentValues.put("TerName",TerName);

            db.update("tblTerminals", contentValues, "Id = ?",new String[] { ID });



          //  cursor.close();

            res = 1;
        } catch (Exception ex) {
            res = 0;
            Log.d(TAG, ex.getMessage());
        }
        return res;
    }


    public Integer ReadTerminalValue(String ID)//////CHANGE INTO COMMON FUNCTION LATTER
    {

        int res = 0;
        Id="";IPAddress="";TerNo="";CustID="";PortNo="";TerName="";
        try {
            db = controllerdb.getReadableDatabase();
            String[] params = new String[]{ ID };
            Cursor cursor = db.rawQuery("SELECT Id, IPAddress,TerNo,CustNo,PortNo,TerName  From   tblTerminals where Id=?", params);
            if (cursor.moveToFirst()) {
                do {
                    Id  = cursor.getString(cursor.getColumnIndex("Id")) ;
                    IPAddress = cursor.getString(cursor.getColumnIndex("IPAddress"));
                    TerNo = cursor.getString(cursor.getColumnIndex("TerNo"));
                    CustID = cursor.getString(cursor.getColumnIndex("CustNo"));
                    PortNo = cursor.getString(cursor.getColumnIndex("PortNo"));
                    TerName=cursor.getString(cursor.getColumnIndex("TerName"));
                } while (cursor.moveToNext());

                edtIPAddress.setText(IPAddress);
                edtPortNo.setText(PortNo);
                edtTerNo.setText(TerNo);
                edtcust.setText(CustID);
                editTerName.setText(TerName);

            }
            cursor.close();
            res = 1;
        } catch (Exception ex) {
            res = 0;
            Log.d(TAG, ex.getMessage());
        }
        return res;
    }


}
