package com.example.terminal_control;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.StrictMode;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ter_control extends AppCompatActivity {
    Thread Thread1 = null;

    private PrintWriter output;
    private BufferedReader input;

    String[] TerminalValue;
    String Terminal="",SwSRelaySts1="0",SwSRelaySts2="0",SwSRelaySts3="0",TerValue="",TerName="",GetTerValue;
    String TerminalID="";
    String PortNo="",TerNo="",IPAddress="",CustID="",CMD="";
    Switch SwRelay1, SwRelay2,SwRelay3;
    public static final String TAG = "Terminal_Control";
    private  static String SERVER_IP="";//+"/ElguardianService/Service1.svc/" +
    private static int SERVER_PORT=0;
    RadioGroup radRelay;
    TextView txtTerStus,txtdttime,txtLogcount,txttername;
    String hex ="";
    private ArrayAdapter<String> listAdapter;
    Controllerdb db =new Controllerdb(this);
    SQLiteDatabase database;
    public static int nCount = 0;
    static byte[] txBuff = new byte[32];
    static byte[] rxBuff = new byte[32];
    static byte[] diagTxBuff = new byte[32];
    Socket socket;
    Button Exicute,btnRefresh;
    String[] CmdValue;
  //  private ListView messageListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ter_control);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Exicute=(Button)findViewById(R.id.btnExecute);
        SwRelay1 = (Switch) findViewById(R.id.SwRelay1);
        SwRelay2 = (Switch) findViewById(R.id.SwRelay2);
        SwRelay3 = (Switch) findViewById(R.id.SwRelay3);
        radRelay = (RadioGroup)findViewById(R.id.radRelay);
        btnRefresh=(Button)findViewById(R.id.btnRefresh);
        txtTerStus=(TextView) findViewById(R.id.txtTerStus);
        txtdttime=(TextView) findViewById(R.id.txtdttime);
        txtLogcount=(TextView) findViewById(R.id.txtLgCnt);
        txttername=(TextView) findViewById(R.id.txttername);
        //messageListView = (ListView) findViewById(R.id.lstCmdView);
       // listAdapter = new ArrayAdapter<String>(this, R.layout.message_details);
       // messageListView.setAdapter(listAdapter);
       // messageListView.setDivider(null);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        Intent intent = getIntent();

        GetTerValue   = intent.getStringExtra("Terminal");

        if(null != GetTerValue && GetTerValue.length()>1) {
            TerminalValue = GetTerValue.split(",");
            Terminal = TerminalValue[0];
            TerName = TerminalValue[1] + "#" + TerminalValue[2];
            txttername.setText(TerName);
        }
        //   TerNo=TerminalValue[0];
        // IPAddress=TerminalValue[1];
        ReadTerminalValue();
        if(SwRelay1.isChecked()){
            /////  AutoPunch=1; //edit here
            SwRelay1.setText(R.string.ON);
            SwSRelaySts1="1";
        }else{
            SwRelay1.setText(R.string.OFF);
            SwSRelaySts1="0";
        }

        if(SwRelay2.isChecked()){
            SwSRelaySts2="1";
            SwRelay2.setText(R.string.ON);
        }else{
            SwRelay2.setText(R.string.OFF);
            SwSRelaySts2="0";

        }


        if(SwRelay3.isChecked()){
            SwSRelaySts3="1";
            SwRelay3.setText(R.string.ON);
        }else{
            SwRelay3.setText(R.string.OFF);
            SwSRelaySts3="0";

        }



        SwRelay1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                                @Override
                                                public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {

                                                    if(isChecked){
                                                        /////  AutoPunch=1; //edit here
                                                        SwRelay1.setText(R.string.ON);
                                                        SwSRelaySts1="1";
                                                    }else{
                                                        SwRelay1.setText(R.string.OFF);
                                                        SwSRelaySts1="0";
                                                    }

                                                }

                                            }

        );

        SwRelay2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {

                if(isChecked){
                    /////  AutoPunch=1; //edit here
                    SwRelay2.setText(R.string.ON);
                    SwSRelaySts2="1";
                }else{
                    SwRelay2.setText(R.string.OFF);
                    SwSRelaySts2="0";

                }


            }
        });


        SwRelay3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {

                if(isChecked){
                    /////  AutoPunch=1; //edit here
                    SwRelay3.setText(R.string.ON);
                    SwSRelaySts3="1";
                }else{
                    SwRelay3.setText(R.string.OFF);
                    SwSRelaySts3="0";

                }


            }
        });


        Exicute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                SendCommandCommon();




            }
        });
     //  PerformTest( 1,1);
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // PerformTest( 1,1);
                ter_control.AsyncTaskRunner runner = new ter_control.AsyncTaskRunner();
                runner.execute("1");

            }
        });



     ter_control.AsyncTaskRunner runner = new ter_control.AsyncTaskRunner();
     runner.execute("1");

    }





    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }




    private String getLogCount( byte rxBuff[])
    {
        int rxprIndex=9;
       short bytecnt = (short)((rxBuff[3] << 8) | rxBuff[2]);
       short cnt = (short)((rxBuff[rxprIndex++] << 8) | rxBuff[rxprIndex++]);
      short totalcnt = (short)((rxBuff[rxprIndex++] << 8) | rxBuff[rxprIndex++]);
       short flashcnt = (short)((rxBuff[rxprIndex++] << 8) | rxBuff[rxprIndex++]);

        String str = "";
        if (bytecnt > 0x0b)
        {
           str = totalcnt+"/"+flashcnt;
           // txtLogcount.setText(String.valueOf(totalcnt)+"/"+String.valueOf(flashcnt));
        }
        return str;
    }




    @Override
    public void onBackPressed() {

        this.finish();

    }




    private static String DisplayBytes(byte[] nBuff, int nDisplayBytes, int nTxRx, int nBytes)
    {
        nDisplayBytes = 1;
        String strBytes = "";
        if (nDisplayBytes == 1)
        {
            if (nTxRx == 1)
            {
                strBytes = "TX : ";
            }
            else
            {
                strBytes = "RX : ";
            }
            int x = 0;
            for (x = 0; x < nBytes; x++)
                strBytes += String.format("{0:X2}", nBuff[x]) + " ";

        }
        return strBytes;
    }
    private  void PrepareCommandBytes(int nTestNo,  int nByteCount,int nCustomerID)
    {
        int g_nElSmartFirmware=0,g_nCommType=1;
        int i = 0;

        for (i = 0; i < 32; i++)
            diagTxBuff[i] = 0;

        int diagTxBuffLength = 0;

        i = 0;

        byte output = 0;
        byte outState = 0;
        if (g_nElSmartFirmware == 1)
        {
            switch (nTestNo)
            {
                case 0xDA:
                    output = 1;
                    outState = 1;

                    txBuff[i++] = output;
                    txBuff[i++] = outState;
                    txBuff[i++] = 00;
                    txBuff[i++] = 10; //timeout 1 sec

                    break;
                case 0xDB:
                    output = 1;
                    outState = 0;

                    txBuff[i++] = output;
                    txBuff[i++] = outState;
                    txBuff[i++] = 00;
                    txBuff[i++] = 10; //timeout 1 sec

                    break;
                case 0xDC:
                    output = 2;
                    outState = 1;

                    txBuff[i++] = output;
                    txBuff[i++] = outState;
                    txBuff[i++] = 00;
                    txBuff[i++] = 10; //timeout 1 sec

                    break;
                case 0xDD:
                    output = 2;
                    outState = 0;

                    txBuff[i++] = output;
                    txBuff[i++] = outState;
                    txBuff[i++] = 00;
                    txBuff[i++] = 10; //timeout 1 sec

                    break;
                case 0xE0:
                    output = 3;
                    outState = 1;

                    txBuff[i++] = output;
                    txBuff[i++] = outState;
                    txBuff[i++] = 00;
                    txBuff[i++] = 10; //timeout 1 sec

                    break;
                case 0xE1:
                    output = 3;
                    outState = 0;

                    txBuff[i++] = output;
                    txBuff[i++] = outState;
                    txBuff[i++] = 00;
                    txBuff[i++] = 10; //timeout 1 sec

                    break;

                case 0x7B:
                    output = 3;
                    outState = 0;

                    txBuff[i++] = output;
                    txBuff[i++] = outState;
                    txBuff[i++] = 00;
                    txBuff[i++] = 10; //timeout 1 sec

                    break;

                case 0xDE:
                    output = 3;
                    outState = 0;

                    txBuff[i++] = output;
                    txBuff[i++] = outState;
                    txBuff[i++] = 00;
                    txBuff[i++] = 10; //timeout 1 sec

                    break;

                default:
                    txBuff[i++] = (byte)nTestNo;
                    break;
            }
        }
        else
        {
            txBuff[i++] = (byte)nTestNo;
        }


        nCount = i;
        int j = 0;
        for (j = 0; j < i; j++)
        {
            diagTxBuff[j] =(byte) txBuff[j];
        }
        diagTxBuffLength = j;

        /////////////////

        i = 0;
        short byteCount = 0;
        //Prepare header of packet
        txBuff[i++] = (byte)((int)elDefinitions.STX1 & 0xff);//;
        txBuff[i++] =  (byte)(int)elDefinitions.STX2;;//0x55;//
        txBuff[i++] = 0;	//Byte count lsb
        txBuff[i++] = 0;	//byte count msb
        txBuff[i++] =  (byte) Byte.valueOf(TerNo) ;//(byte)g_nSelectedTerNo;//PRADEEP
        txBuff[i++] = (byte)((int)elDefinitions.PollSourceID & 0xff);// (

        txBuff[i++] = (byte)(byte)(nCustomerID / 0x100);
        txBuff[i++] = (byte)(byte)(nCustomerID % 0x100);

        txBuff[i++] = (byte)(0xAE & 0xff);;//(int)elDefinitions.cmdOperateOutputControl;

        if (diagTxBuffLength == 1)
        {
            i--;
            txBuff[i++] = (byte)diagTxBuff[0];
        }
        else
        {
            for (int k = 0; k < diagTxBuffLength; k++) // Diagnostics Cmds
            {
                txBuff[i++] = (byte)diagTxBuff[k];
            }
        }

        i++;    //csum bytes
        i++;    //csum bytes
        txBuff[i++] = (byte)(0xF0  & 0xff);;//(int)elDefinitions.ETX;

        byteCount = (short) i;

        int checkSum = 0;

        for (i = 4; i < byteCount - 3; i++)
            checkSum += txBuff[i] & 0xff;

        i = (short)(byteCount - 3);
        txBuff[2] = (byte)i;
        txBuff[3] = (byte)(i >> 8);
        txBuff[byteCount - 3] = (byte)(checkSum / 0x100);
        txBuff[byteCount - 2] = (byte)(checkSum % 0x100);
        nByteCount = byteCount;
    }
    private  String  PerformTest(int nTestNo,int Read)
    {

        Exicute.setClickable(false);
        int g_nCommType=1;
        int   customerid=Integer.valueOf(CustID) ;
        String ReplyFromTer = "";
        InputStream inStream;
        SERVER_IP=IPAddress.trim();
        SERVER_PORT=Integer.valueOf(PortNo.trim()) ;
        byte[] array = new byte[100];
        int DateValue=15;
        int LogCount=0;
      String   requiredDate = "";
        String requiredTime = "";
     // Toast.makeText(ter_control.this,"Connected to Server...",Toast.LENGTH_LONG).show();
        OutputStream writerServer;
        try {
            int nByteCount = 0;

            txBuff[0] = (byte) (int)elDefinitions.STX1;
            txBuff[1] = (byte) (int)elDefinitions.STX2;
            txBuff[2] = 0x09;
            txBuff[3] = 0x00;
            txBuff[4] = 0x03;
            txBuff[5] = (byte) 0x83;
            txBuff[6] = (byte) (customerid / 0x100);
            txBuff[7] = (byte) (customerid % 0x100);
            txBuff[8] = (byte) (int)elDefinitions.cmdWhoAmI;
            txBuff[9] = 0x01;
            txBuff[10] = 0x0F;
            txBuff[11] = (byte) 0xF0;


            if (g_nCommType == 1) {
                txtTerStus.setText("Connecting..");

                socket = new Socket(SERVER_IP, SERVER_PORT);
                txtTerStus.setText("Connected");

                writerServer = socket.getOutputStream();
                    if(Read==0) {
                        writerServer.write(txBuff);
                         inStream = new BufferedInputStream(socket.getInputStream());

                        inStream.read(array, 0, 100);
                        // input = new BufferedReader(new InputStreamReader(ServerSocket.getInputStream()));
                        hex = byteArrayToHex(txBuff, 12);
                        //  final String message = input.readLine();
                        // byte[] dataInBytes = message.getBytes(StandardCharsets.UTF_8);
                        if (array.length > 13)
                            hex += "\n" + byteArrayToHex(array, 13);
                        else
                            hex += "\n" + byteArrayToHex(array, array.length);

                        PrepareCommandBytes(nTestNo,  nByteCount,Integer.valueOf(customerid) );
                        hex+="\n"+ byteArrayToHex(txBuff,12);
                        writerServer.write(txBuff);
                        PAlertDialog( getResources().getString(R.string.Command),hex);
                        Exicute.setClickable(true);
                    }
                    else {
                        nTestNo = (int) elDefinitions.cmdGetDateTime; //0x51;//getdatetime
                        PrepareCommandBytes(nTestNo, nByteCount, Integer.valueOf(customerid));
                        hex += "\n" + byteArrayToHex(txBuff, 12);
                        writerServer.write(txBuff);

                        inStream = new BufferedInputStream(socket.getInputStream());
                        inStream.read(array, 0, 100);
                        // input = new BufferedReader(new InputStreamReader(ServerSocket.getInputStream()));
                        hex = byteArrayToHex(txBuff, 12);
                        //  final String message = input.readLine();
                        // byte[] dataInBytes = message.getBytes(StandardCharsets.UTF_8);
                        if (array.length > 18)
                            hex += "\n" + byteArrayToHex(array, 18);
                        else
                            hex += "\n" + byteArrayToHex(array, array.length);

                        String strdate = BcdToHex(array[DateValue--]) + 2000 + "/" + BcdToHex(array[DateValue--]) + "/" + BcdToHex(array[DateValue--]) + " " +
                                "" + BcdToHex(array[--DateValue]) + ":" + BcdToHex(array[--DateValue]) + ":" + BcdToHex(array[--DateValue]);

                        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        try {
                            Date date1 = format.parse(strdate);
                            DateFormat df = new SimpleDateFormat("dd MMM yyyy");
                            requiredDate = df.format(date1).toString();
                            Date time1 = format.parse(strdate);
                            DateFormat tf = new SimpleDateFormat("hh:mm:ss a");
                            requiredTime = tf.format(time1).toString();
                            txtdttime.setText(requiredDate + ", " + requiredTime);

                        } catch (ParseException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        nTestNo =(int) elDefinitions.cmdGetLogCount;// 0x60;//get log counr
                        PrepareCommandBytes(nTestNo, nByteCount, Integer.valueOf(customerid));
                        hex += "\n" + byteArrayToHex(txBuff, 12);
                        writerServer.write(txBuff);
                        inStream = new BufferedInputStream(socket.getInputStream());
                        inStream.read(array, 0, 100);
                        // input = new BufferedReader(new InputStreamReader(ServerSocket.getInputStream()));
                        hex = byteArrayToHex(txBuff, 12);
                        //  final String message = input.readLine();
                        // byte[] dataInBytes = message.getBytes(StandardCharsets.UTF_8);
                        if (array.length > 18)
                            hex += "\n" + byteArrayToHex(array, 18);
                        else
                            hex += "\n" + byteArrayToHex(array, array.length);

                        txtLogcount.setText( getLogCount(array));

                    }
                writerServer.flush();
                inStream.close();
                socket.close();


            } else {
                if (g_nCommType == 0) {
                }
            }

        }
        catch (IOException e1) {
            Toast.makeText(ter_control.this,"Problem Connecting to server... Check your server IP and Port and try again",Toast.LENGTH_LONG).show();
            Thread.interrupted();
            e1.printStackTrace();
            txtTerStus.setText("Not Connected");
        }
        catch (Exception ex)
        {
            hex = ex.getMessage();

         //   PAlertDialog( getResources().getString(R.string.Unable_to_connect),strMsg);
        }
        finally
        {
            Exicute.setClickable(true);
        }

        return hex;
    }

    private  String  PerformTestThread(int Read)
    {
        int  nTestNo=0;
        Exicute.setClickable(false);
        int g_nCommType=1;
        int   customerid=Integer.valueOf(CustID) ;
        InputStream inStream;
        SERVER_IP=IPAddress.trim();
        SERVER_PORT=Integer.valueOf(PortNo.trim()) ;
        byte[] array = new byte[100];
        int DateValue=15;
        String   requiredDate = "";
        String requiredTime = "";
        OutputStream writerServer;
        try {
            int nByteCount = 0;

                socket = new Socket(SERVER_IP, SERVER_PORT);

                writerServer = socket.getOutputStream();
                    nTestNo =(int) elDefinitions.cmdGetDateTime; // 0x51;//getdatetime
                    PrepareCommandBytes(nTestNo, nByteCount, Integer.valueOf(customerid));
                    hex += "\n" + byteArrayToHex(txBuff, 12);
                    writerServer.write(txBuff);
                    inStream = new BufferedInputStream(socket.getInputStream());
                    inStream.read(array, 0, 100);
                    String strdate = BcdToHex(array[DateValue--]) + 2000 + "/" + BcdToHex(array[DateValue--]) + "/" + BcdToHex(array[DateValue--]) + " " +
                            "" + BcdToHex(array[--DateValue]) + ":" + BcdToHex(array[--DateValue]) + ":" + BcdToHex(array[--DateValue]);

                    SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    try {
                        Date date1 = format.parse(strdate);
                        DateFormat df = new SimpleDateFormat("dd MMM yyyy");
                        requiredDate = df.format(date1).toString();
                        Date time1 = format.parse(strdate);
                        DateFormat tf = new SimpleDateFormat("hh:mm:ss a");
                        requiredTime = tf.format(time1).toString();
                        //txtdttime.setText(requiredDate + ", " + requiredTime);
                        hex=requiredDate + ", " + requiredTime+";";
                    } catch (ParseException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    nTestNo =(int) elDefinitions.cmdGetLogCount; // 0x60;//get log counr
                    PrepareCommandBytes(nTestNo, nByteCount, Integer.valueOf(customerid));

                    writerServer.write(txBuff);
                    inStream = new BufferedInputStream(socket.getInputStream());
                    inStream.read(array, 0, 100);
                     hex +=  getLogCount(array);


                writerServer.flush();
                inStream.close();
                socket.close();




        }
        catch (IOException e1) {
         //   Toast.makeText(ter_control.this,"Problem Connecting to server... Check your server IP and Port and try again",Toast.LENGTH_LONG).show();
            Thread.interrupted();
            e1.printStackTrace();
            hex="`"+e1.getMessage()+ "`";
        }
        catch (Exception ex)
        {
            hex="`"+ex.getMessage()+ "`";

            //   PAlertDialog( getResources().getString(R.string.Unable_to_connect),strMsg);
        }


        finally
        {
            Exicute.setClickable(true);
        }

        return hex;
    }

    private int BcdToHex(byte  cBcd)
    {
        int decimal = (cBcd & 0xF) + (((int)cBcd & 0xF0) >> 4)*10;
           return  decimal;

    }



    public static String byteArrayToHex(byte[] a,int lenth) {
        StringBuilder sb = new StringBuilder(lenth * 2);
        for (int j = 0; j < lenth; j++)
        //for(byte b: a)
            sb.append(String.format("%02x", a[j]));
        return sb.toString();
    }

    public String SendCommandCommon()
    {
        String strTerDetails = "";
        String strTerminal = "";
        String ReplyFrmTer = "";
        int checkedRadioButtonId=0;
        String strRelay ="";
        checkedRadioButtonId = radRelay.getCheckedRadioButtonId();
        if (checkedRadioButtonId == R.id.rdRelay1) {
            strRelay="R1";

        }
        if (checkedRadioButtonId == R.id.rdRelay2) {
            strRelay="R2";

        }

        if (checkedRadioButtonId == R.id.rdRelay3) {
            strRelay="R3";

        }

        if (checkedRadioButtonId == R.id.radReset) {
            strRelay="RST";

        }


        try
        {
            //"R1"; //
            //  String strStatus = Relay; //"0"; //

            switch (strRelay)
            {
                case "R1":

                    if (SwSRelaySts1 == "1")
                    {
                        ReplyFrmTer= PerformTest(0xDA,0);
                    }
                    else if (SwSRelaySts1 == "0")
                    {
                        ReplyFrmTer= PerformTest(0xDB,0);
                    }
                    else
                    {
                        ReplyFrmTer= "Invalid";
                    }
                    break;
                case "R2":
                    if (SwSRelaySts2 == "1")
                    {
                        ReplyFrmTer= PerformTest(0xDC,0);
                    }
                    else if (SwSRelaySts2 == "0")
                    {
                        ReplyFrmTer= PerformTest(0xDD,0);
                    }
                    else
                    {
                        PAlertDialog(  getResources().getString(R.string.Command),  getResources().getString(R.string.Invalid_Command))  ;
                    }
                    break;
                case "R3":
                    if (SwSRelaySts3 == "1")
                    {
                        ReplyFrmTer= PerformTest(0xE0,0);
                    }
                    else if (SwSRelaySts3 == "0")
                    {
                        ReplyFrmTer= PerformTest(0xE1,0);
                    }
                    else
                    {
                        ReplyFrmTer = getResources().getString(R.string.Command);// Display_Invalid();
                    }
                    break;
                case "RST":

                    ReplyFrmTer = PerformTest(0x7B,0);

                    break;
                default:
                    ReplyFrmTer = getResources().getString(R.string.Command);// Display_Invalid();
                    break;
            }
            return ReplyFrmTer;
        }
        catch(Exception ex)
        {
            return ex.getMessage();
        }
        // return "ok";
    }







    private void PAlertDialog(String title, String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(ter_control.this);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();

    }



    private void PAlertDialog1(String title, String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(ter_control.this);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();

    }

    public Integer ReadTerminalValue()//////CHANGE INTO COMMON FUNCTION LATTER
    {

        int res = 0;
        try {
            database = db.getReadableDatabase();
            String[] params = new String[]{ Terminal };
            Cursor cursor = database.rawQuery("SELECT IPAddress,TerNo,CustNo,PortNo  From   tblTerminals where Id=?", params);
            if (cursor.moveToFirst()) {
                do {

                    IPAddress = cursor.getString(cursor.getColumnIndex("IPAddress"));
                    TerNo = cursor.getString(cursor.getColumnIndex("TerNo"));
                    CustID = cursor.getString(cursor.getColumnIndex("CustNo"));
                    PortNo = cursor.getString(cursor.getColumnIndex("PortNo"));
                } while (cursor.moveToNext());
            }
            cursor.close();
            res = 1;
        } catch (Exception ex) {
            res = 0;
            Log.d(TAG, ex.getMessage());
        }
        return res;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");

        try {
            if(null !=socket)
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class AsyncTaskRunner extends AsyncTask<String, String, String> {

        private String resp;
        ProgressDialog progressDialog;

        @Override
        protected String doInBackground(String... params) {
            publishProgress("Sleeping..."); // Calls onProgressUpdate()
            try {
                String Read = params[0];
               resp=   PerformTestThread(Integer.valueOf(Read) );
                //runOnUiThread(new Runnable() {
                   // public void run() {
                       // if(resp.contains("`"))
                          //  Toast.makeText(getBaseContext(),resp, Toast.LENGTH_SHORT).show();
                   // }
              //  });
            } catch (Exception e) {
                e.printStackTrace();
                resp = e.getMessage() ;
            }
            return resp;
        }


        @Override
        protected void onPostExecute(String result) {
            if(!result.contains("`")) {
                String[] res = result.split(";");
                txtdttime.setText(res[0]);
                txtLogcount.setText(res[1]);
                txtTerStus.setText("Connected");
            }
            else
            {
               // Toast.makeText(getBaseContext(),result, Toast.LENGTH_SHORT).show();
              PAlertDialog1("Error",result);

            }
            progressDialog.dismiss();
        }


        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(ter_control.this,
                    getResources().getString(R.string.Getting_Terminal_Data),
                    getResources().getString(R.string.Please_Wait));
          txtTerStus.setText("Connecting..");

            progressDialog.setProgressStyle(android.R.attr.progressBarStyleSmall);
        }


        @Override
        protected void onProgressUpdate(String... text) {

        }
    }
    public String SendCommandCommon(String strRelay,int Read)
    {
        String strTerDetails = "";
        String strTerminal = "";
        String ReplyFrmTer = "";
        int checkedRadioButtonId=0;



        try
        {
            //"R1"; //
            //  String strStatus = Relay; //"0"; //

            switch (strRelay)
            {
                case "INPUT":


                    ReplyFrmTer= PerformTest(0xDE,Read);

                    break;
                case "DateTime":


                    ReplyFrmTer= PerformTest(0x51,Read);

                    break;
                case "LogCount":


                    ReplyFrmTer= PerformTest(0x60,Read);

                    break;

                default:
                    ReplyFrmTer = getResources().getString(R.string.Command);// Display_Invalid();
                    break;
            }
            return ReplyFrmTer;
        }
        catch(Exception ex)
        {
            return ex.getMessage();
        }
        // return "ok";
    }


}



