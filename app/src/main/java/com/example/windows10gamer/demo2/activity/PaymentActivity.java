package com.example.windows10gamer.demo2.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.windows10gamer.demo2.ultil.CheckConnection;
import com.example.windows10gamer.demo2.ultil.Server;
import com.kyanogen.signatureview.SignatureView;
import com.samilcts.media.State;
import com.samilcts.sdk.mpaio.MpaioManager;

import com.samilcts.sdk.mpaio.command.MpaioCommand;
import com.samilcts.sdk.mpaio.error.ResponseError;
import com.samilcts.sdk.mpaio.ext.dialog.RxConnectionDialog;

import com.example.windows10gamer.demo2.R;
import com.samilcts.sdk.mpaio.message.MpaioMessage;
import com.samilcts.util.android.Converter;
import com.samilcts.util.android.Logger;
import com.samilcts.util.android.ToastUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import rx.Subscriber;


public class PaymentActivity extends AppCompatActivity {
    private static final String TAG = "PaymentActivity";
    private RxConnectionDialog connectionDialog;
    private MpaioManager mpaioManager;
    private Logger logger = new Logger();
    private Context mContext;
    private Dialog alertbox,alertboxsign;
    private byte[] savedata = null;
    private byte[] savepincard = null;
    boolean checkpin = false,checkdata = false;
    private String senddata = "";
    private String pincard ="";
    String partner_id = "0";
    Toolbar toolbar;
    Spinner spinner;
    private Button btnSend, btnBarcode, btnCard, btnPin;

    Bitmap bitmap;
    Button clear,save;
    SignatureView signatureView;
    String path;
    private static final String IMAGE_DIRECTORY = "/signdemo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        mContext = getApplicationContext();
        mpaioManager = new MpaioManager(getApplicationContext());
        connectionDialog = new RxConnectionDialog(this, mpaioManager);
        partner_id = getIntent().getStringExtra("partner_id");
        alertbox = showDialogcustom();
        alertboxsign = showDialogSign();
        EventButton();

        mpaioManager.onReceived()
                .subscribe(new Subscriber<byte[]>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(byte[] bytes) {

                    }
                });

        mpaioManager.onStateChanged()
                .subscribe(new Subscriber<State>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(State state) {
                        invalidateOptionsMenu();
                        ToastUtil.show(mContext, "state changed : " + state.getValue());
                    }
                });

        mpaioManager
                .onBarcodeRead()
                .mergeWith(mpaioManager.onReadMsCard())
                .mergeWith(mpaioManager.onReadEmvCard())
                .mergeWith(mpaioManager.onReadRfidCard())
//                .mergeWith(mpaioManager.onPressPinPad())
                .mergeWith(mpaioManager.onNotifyPrepaidTransaction())
                .mergeWith(mpaioManager.onReadPrepaidTransactionLog())
                .subscribe(new Subscriber<MpaioMessage>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        String msg = null == e.getMessage() ? e.toString() : e.getMessage();
                        ToastUtil.show(mContext, "ERROR : " + msg);
                    }

                    @Override
                    public void onNext(MpaioMessage mpaioMessage) {
                        byte[] data = mpaioMessage.getData();

                        logger.i(TAG, "AID : " + Converter.toInt(mpaioMessage.getAID())
                                + " CMD : " + Converter.toHexString(mpaioMessage.getCommandCode())
                                + " Data : " + Converter.toHexString(mpaioMessage.getData()));
                        ToastUtil.show(mContext, "received data part : " + Converter.toHexString(data));
                        ToastUtil.show(mContext, "(string) : " + (data == null ? "" : new String(data)));
                        if (data != null && data != savedata){
                            savedata = data;
                            senddata = Converter.toHexString(mpaioMessage.getData());
                            Log.d("senddata1" , senddata);
                            Log.d("senddata2" , new String(data));
                            if (alertbox.isShowing()){
                                alertbox.dismiss();
                            }
                        }
                        mpaioManager.rxSyncRequest(mpaioManager.getNextAid(), new MpaioCommand(MpaioCommand.STOP).getCode(), new byte[0])
                                .subscribe(getMessageSubscriber());
                    }
                });
        mpaioManager.onPressPinPad() //Receive notification of PIN PAD input data
                .subscribe(new Subscriber<MpaioMessage>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        String msg = null == e.getMessage() ? e.toString() : e.getMessage();
                        ToastUtil.show(mContext, "ERROR : " + msg);
                    }

                    @Override
                    public void onNext(MpaioMessage mpaioMessage) {
                        byte[] data = mpaioMessage.getData();

                        logger.i(TAG, "AID : " + Converter.toInt(mpaioMessage.getAID())
                                + " CMD : " + Converter.toHexString(mpaioMessage.getCommandCode())
                                + " Data : " + Converter.toHexString(mpaioMessage.getData()));
                        ToastUtil.show(mContext, "received data part : " + Converter.toHexString(data));
                        ToastUtil.show(mContext, "(string) : " + (data == null ? "" : new String(data)));

                        pincard = pincard.concat(new String(data));
                        Log.d("pincard" , pincard);
                        if (new String(data) == "E"){
                            mpaioManager.rxSyncRequest(mpaioManager.getNextAid(), new MpaioCommand(MpaioCommand.STOP).getCode(), new byte[0])
                                    .subscribe(getMessageSubscriber());
                            ToastUtil.show(getApplicationContext(),"Please Confirm",3000);
                        }else if(new String(data) == "C"){
                            mpaioManager.rxSyncRequest(mpaioManager.getNextAid(), new MpaioCommand(MpaioCommand.STOP).getCode(), new byte[0])
                                    .subscribe(getMessageSubscriber());
                            pincard = "";
                            ToastUtil.show(getApplicationContext(),"Please try again input pincard",3000);
                        }
                    }
                });

        Mapping();
        if (CheckConnection.haveNetworkConnection(getApplicationContext())){
            ActionToolbar();
        }else {
            CheckConnection.ShowToast_Short(getApplicationContext(),"Bạn hãy kiểm tra lại kết nối INTERNET");
        }
    }


    private Dialog showDialogSign() {
        Dialog aDialogSign = new Dialog(this);
        aDialogSign.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        aDialogSign.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        aDialogSign.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        aDialogSign.setContentView(R.layout.dialog_sign);
        aDialogSign.setCancelable(false);

        signatureView = (SignatureView) aDialogSign.findViewById(R.id.signature_view);
        clear = (Button) aDialogSign.findViewById(R.id.clear);
        save = (Button) aDialogSign.findViewById(R.id.save);

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signatureView.clearCanvas();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bitmap = signatureView.getSignatureBitmap();
                path = saveImage(bitmap);
            }
        });

        return aDialogSign;
    }

    public String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File wallpaperDirectory = new File(
                Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY /*iDyme folder*/);
        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
            Log.d("hhhhh",wallpaperDirectory.toString());
        }

        try {
            File f = new File(wallpaperDirectory, Calendar.getInstance()
                    .getTimeInMillis() + ".jpg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(PaymentActivity.this,
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Log.d("TAG", "File Saved::--->" + f.getAbsolutePath());

            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";

    }

    private Dialog showDialogcustom(){
        final Dialog aDialog = new Dialog(this);
        aDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        aDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        aDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        aDialog.setContentView(R.layout.dialog_custom);
        aDialog.setCancelable(false);
        Button btn_close = (Button) aDialog.findViewById(R.id.close_button);
        Button btn_OK = (Button) aDialog.findViewById(R.id.buttonOK);
        btn_OK.setVisibility(View.INVISIBLE);
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mpaioManager.rxSyncRequest(mpaioManager.getNextAid(), new MpaioCommand(MpaioCommand.STOP).getCode(), new byte[0])
                        .subscribe(getMessageSubscriber());
                aDialog.dismiss();
            }
        });
        btn_OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aDialog.dismiss();
            }
        });

        return aDialog;
    }

    private void EventButton() {
        btnCard = (Button) findViewById(R.id.button_readcard);
        btnCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mpaioManager.isConnected()) {
                    connectionDialog.show();
                    return;
                }
                String cmdString = "0003";
                TextView textView1 = (TextView) alertbox.findViewById(R.id.textview_insertcard);
                TextView textView2 = (TextView) alertbox.findViewById(R.id.title_dialog);
                textView1.setText("INSERT CARD");
                textView2.setText("CARD");
                try {
                    short cmdShort = Short.parseShort(cmdString, 16);
                    byte[] cmd =  Converter.toBigEndianBytes(cmdShort);

                    String paramStr = "";
                    byte[] param;
                    param = paramStr.getBytes();

                    logger.i("param", " cmd : " + Converter.toHexString(cmd));
                    logger.i("param", " param : " + Converter.toHexString(param));
                    mpaioManager.rxSyncRequest(mpaioManager.getNextAid(), cmd, param)
                            .subscribe();
                    mpaioManager.rxSyncRequest(mpaioManager.getNextAid(), new MpaioCommand(MpaioCommand.READ_EMV_CARD).getCode(), new byte[0])
                            .subscribe(); //Command for activating EMV card mode
                    mpaioManager.rxSyncRequest(mpaioManager.getNextAid(), new MpaioCommand(MpaioCommand.READ_RFID_CARD).getCode(),null)
                            .subscribe(); //Command for activating RFID card mode

                    alertbox.show();
                }catch (NumberFormatException e) {
                    ToastUtil.show(getApplicationContext(), "Input valid number");
                    e.printStackTrace();
                }
                InputMethodManager imm = (InputMethodManager) getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });

        btnBarcode = (Button) findViewById(R.id.button_barcode);
        btnBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mpaioManager.isConnected()) {
                    connectionDialog.show();
                    return;
                }
                TextView textView1 = (TextView) alertbox.findViewById(R.id.textview_insertcard);
                TextView textView2 = (TextView) alertbox.findViewById(R.id.title_dialog);
                textView1.setText("Barcode");
                textView2.setText("READ BARCODE");
                try {
                    mpaioManager.rxSyncRequest(mpaioManager.getNextAid(), new MpaioCommand(MpaioCommand.READ_BARCODE).getCode(), new byte[0])
                            .subscribe(); //Command for activating Barcode Mode
                    alertbox.show();
                }catch (NumberFormatException e) {
                    ToastUtil.show(getApplicationContext(), "Input valid number");
                    e.printStackTrace();
                }

                InputMethodManager imm = (InputMethodManager) getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

            }
        });

        btnPin = (Button) findViewById(R.id.button_pincard);
        btnPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mpaioManager.isConnected()) {
                    connectionDialog.show();
                    return;
                }
                TextView textView1 = (TextView) alertbox.findViewById(R.id.textview_insertcard);
                TextView textView2 = (TextView) alertbox.findViewById(R.id.title_dialog);
                textView1.setText("(Number : 0~9, F1 : ‘*’, F2 : ‘#’, Cancel : ‘C’, Enter : ‘E’, Back Space: ‘<’)");
                textView2.setText("READ PIN CARD");
                try {
                    mpaioManager.rxSyncRequest(mpaioManager.getNextAid(), new MpaioCommand(MpaioCommand.READ_PIN_PAD).getCode(), new byte[0])
                            .subscribe(); //Command for activating PIN PAD input mode
                    ToastUtil.show(getApplicationContext(),"Input pincard",5000);
//                    alertbox.show();
                }catch (NumberFormatException e) {
                    ToastUtil.show(getApplicationContext(), "Input valid number");
                    e.printStackTrace();
                }
                InputMethodManager imm = (InputMethodManager) getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });
        btnSend = (Button) findViewById(R.id.button_pay);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendData(senddata);
            }
        });
    }

    private void SendData(final String s){
        Log.d("pincard" , pincard);
        Log.d("senddata" , senddata);
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Server.Urlsenddata, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals("1")){
                    long total = 0;
                    for (int i = 0 ; i <HomeActivity.arrCart.size() ; i++){
                        total += HomeActivity.arrCart.get(i).getList_price() * HomeActivity.arrCart.get(i).getQty();
                    }
                    HomeActivity.arrCart.clear();
                    Intent intent = new Intent(getApplicationContext(),ConfirmationActivity.class);
                    intent.putExtra("partner_id",partner_id);
                    intent.putExtra("data",savedata);
                    intent.putExtra("pincard",pincard);
                    intent.putExtra("total",total);
                    startActivity(intent);
                }else{
                    Log.d("error",response);
                    CheckConnection.ShowToast_Short(getApplicationContext(),response + "Dữ liệu data đã bị lỗi");
                    Log.d("error",response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Log.d("partner_id ",String.valueOf(partner_id));
                Log.d("data", s);
                HashMap<String,String> hashMap = new HashMap<String, String>();
                hashMap.put("partner_id",String.valueOf(partner_id));
                hashMap.put("data",s);
                return hashMap;
            }
        };
        requestQueue.add(stringRequest);
    }

    private Subscriber<MpaioMessage> getMessageSubscriber() {

        return new Subscriber<MpaioMessage>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

                e.printStackTrace();
                String msg = null == e.getMessage() ? e.toString() : e.getMessage();
                ToastUtil.show(mContext, "ERROR : " + msg);
            }

            @Override
            public void onNext(MpaioMessage mpaioMessage) {

                byte[] data = mpaioMessage.getData();

                if (ResponseError.fromCode(data[0]) == ResponseError.NO_ERROR) {
                    //response ok
                }


                logger.i(TAG, "AID : " + Converter.toInt(mpaioMessage.getAID())
                        + " CMD : " + Converter.toHexString(mpaioMessage.getCommandCode())
                        + " Data : " + Converter.toHexString((byte[]) mpaioMessage.getData()));

                ToastUtil.show(mContext, "received data part : " + Converter.toHexString(data));
                ToastUtil.show(mContext, "(string) : " + (data == null ? "" : new String(data)));

            }
        };
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


        if ( null != connectionDialog && connectionDialog.isShowing()) {
            //this is for updating connection dialog state.
            connectionDialog.onRequestPermissionResult(requestCode, permissions, grantResults);
        }

    }


    private void ActionToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case  R.id.menu_cart:
                Intent intent = new Intent(getApplicationContext(),CartActivity.class);
                startActivity(intent);
            case R.id.action_connect:
                if ( mpaioManager.isConnected()) {

                    mpaioManager.disconnect();
                    return true;

                } else {

                    connectionDialog.show();

                }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if ( mpaioManager != null) {
            boolean isConnected = mpaioManager.isConnected();
            MenuItem item = menu.findItem(R.id.action_connect);
            item.setTitle(isConnected ? "disconnect" : "connect");
            return true;
        }

        return super.onPrepareOptionsMenu(menu);
    }

    private void Mapping() {


        toolbar = (Toolbar) findViewById(R.id.toolbar_payment);

    }

}
