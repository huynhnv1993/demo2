package com.example.windows10gamer.demo2.activity;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.example.windows10gamer.demo2.R;
import com.example.windows10gamer.demo2.adapter.CategAdapter;
import com.example.windows10gamer.demo2.adapter.ProductHomeAdapter;
import com.example.windows10gamer.demo2.model.Cart;
import com.example.windows10gamer.demo2.model.Product;
import com.example.windows10gamer.demo2.model.ProductCategory;
import com.example.windows10gamer.demo2.ultil.CheckConnection;
import com.example.windows10gamer.demo2.ultil.Server;
import com.samilcts.media.State;
import com.samilcts.sdk.mpaio.MpaioManager;
import com.samilcts.sdk.mpaio.command.MpaioCommand;
import com.samilcts.sdk.mpaio.error.ResponseError;
import com.samilcts.sdk.mpaio.ext.dialog.RxConnectionDialog;
import com.samilcts.sdk.mpaio.message.MpaioMessage;
import com.samilcts.util.android.Converter;
import com.samilcts.util.android.Logger;
import com.samilcts.util.android.ToastUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import rx.Subscriber;


public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "HomeActivity";
    private RxConnectionDialog connectionDialog;
    private MpaioManager mpaioManager;

    private EditText etCmd;
    private EditText etParam;
    private Button btnSend;
    private CheckBox cbHex;
    private Button btnBalance,btnRecharge,btnRefund,btnPurchase, btnLog, btnLength, btnInterval;
    private AppCompatSpinner spinner;

    private Logger logger = new Logger();
    private EditText etPrepaidAmount;
    private EditText etConfig;

    private Context mContext;

    private TextView datahex, datastring;

    Toolbar toolbarHome;
    DrawerLayout drawerLayoutHome;
    NavigationView navigationView;
    ListView listViewHome;
    RecyclerView recyclerViewP_H1;
    SliderLayout sliderLayoutHome;
    HashMap<String,String> Hash_file_maps;
    ArrayList<ProductCategory> arrCateg;
    CategAdapter categAdapter;
    ArrayList<Product> arrP_H1;
    ProductHomeAdapter p_h1_adapter;
    int id =0;
    String namecateg = "";
    String imgcateg = "";
    public static ArrayList<Cart> arrCart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Mapping();
        if (CheckConnection.haveNetworkConnection(getApplicationContext())){
            GetDataCategProduct();
            GetDataProductH1();
            ActionBarHome();
            CatchOnItemListView();

        }else {
            CheckConnection.ShowToast_Short(getApplicationContext(),"Bạn hãy kiểm tra lại kết nối INTERNET");
        }

        mContext = getApplicationContext();
        mpaioManager = new MpaioManager(getApplicationContext());
        connectionDialog = new RxConnectionDialog(this, mpaioManager);

        datahex = (TextView) findViewById(R.id.textview_datahex_MSCARD);
        datastring = (TextView) findViewById(R.id.textview_datastring_MSCARD);

        btnSend = (Button) findViewById(R.id.btn_send);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mpaioManager.isConnected()) {
                    connectionDialog.show();
                    return;
                }
                String cmdString = "0003";
                try {
                    short cmdShort = Short.parseShort(cmdString, 16);
                    byte[] cmd =  Converter.toBigEndianBytes(cmdShort);

                    String paramStr = "";
                    byte[] param;
                    param = paramStr.getBytes();

                    logger.i("param", " cmd : " + Converter.toHexString(cmd));
                    logger.i("param", " param : " + Converter.toHexString(param));
                    mpaioManager.rxSyncRequest(mpaioManager.getNextAid(), cmd, param)
                            .subscribe(getMessageSubscriber());
                }catch (NumberFormatException e) {
                    ToastUtil.show(getApplicationContext(), "Input valid number");
                    e.printStackTrace();
                }
                InputMethodManager imm = (InputMethodManager) getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });

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

        mpaioManager.onBarcodeRead()
                .mergeWith(mpaioManager.onReadMsCard())
                .mergeWith(mpaioManager.onReadEmvCard())
                .mergeWith(mpaioManager.onReadRfidCard())
                .mergeWith(mpaioManager.onPressPinPad())
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
                        byte[] data1 = data;
                        logger.i(TAG, "AID : " + Converter.toInt(mpaioMessage.getAID())
                                + " CMD : " + Converter.toHexString(mpaioMessage.getCommandCode())
                                + " Data : " + Converter.toHexString(mpaioMessage.getData()));

                        ToastUtil.show(mContext, "notify. data part : " + Converter.toHexString(data) );
                        ToastUtil.show(mContext, "(string) : " + (data == null ? "" : new String(data)));
                        datahex.setText(Converter.toHexString(data1));
                        datastring.setText("(string) : " + (data1 == null ? "" : new String(data1)));

                    }
                });
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
                Log.d("code", new MpaioCommand(MpaioCommand.READ_MS_CARD).getCode().toString());
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

    private void GetDataCategProduct() {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplication());
        final JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Server.Urlcategproduct, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (response != null){
                    for (int i = 0 ; i < response.length() ; i++){
                        try {
                            JSONObject jsonObject = response.getJSONObject(i);
                            id = jsonObject.getInt("id");
                            namecateg = jsonObject.getString("name");
                            imgcateg = jsonObject.getString("image");
                            arrCateg.add(new ProductCategory(id,namecateg,imgcateg));
                            categAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CheckConnection.ShowToast_Short(getApplicationContext(),error.toString());
            }
        });
        requestQueue.add(jsonArrayRequest);
    }

    private void CatchOnItemListView() {
        listViewHome.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (CheckConnection.haveNetworkConnection(getApplicationContext())){
                    Intent intent = new Intent(HomeActivity.this,ProductActivity.class);
                    intent.putExtra("cateid",arrCateg.get(position).getId());
                    intent.putExtra("catename",arrCateg.get(position).getName());
                    startActivity(intent);
                }else{
                    CheckConnection.ShowToast_Short(getApplicationContext(),"Bạn hãy kiểm tra lại kết nối");
                }
                drawerLayoutHome.closeDrawer(GravityCompat.START);
            }
        });
    }

    private void ActionBarHome() {
        setSupportActionBar(toolbarHome);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarHome.setNavigationIcon(android.R.drawable.ic_menu_sort_by_size);
        toolbarHome.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayoutHome.openDrawer(GravityCompat.START);
            }
        });
    }

    private void GetDataProductH1() {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Server.Urlproductnew, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (response !=null){
                    int product_id = 0;
                    String active ="";
                    String description_sale="";
                    String website_description="";
                    String image="";
                    int list_price=0;
                    String name="";
                    String sale_ok="";
                    int warranty=0;
                    for (int i = 0 ; i<response.length() ; i++){
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = response.getJSONObject(i);
                            product_id = jsonObject.getInt("id");
                            active = jsonObject.getString("active");
                            description_sale = jsonObject.getString("description_sale");
                            name = jsonObject.getString("name");
                            list_price = jsonObject.getInt("list_price");
                            image = (String) jsonObject.get("image");
                            website_description = (String) jsonObject.get("website_description");
                            sale_ok = "";
                            warranty = jsonObject.getInt("warranty");
                            arrP_H1.add(new Product(product_id,name,list_price,description_sale,website_description,image,warranty,active,sale_ok));
                            p_h1_adapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(jsonArrayRequest);
    }

    private void Mapping() {
        toolbarHome = (Toolbar) findViewById(R.id.toolbar_home);
        drawerLayoutHome = (DrawerLayout) findViewById(R.id.drawerlayout_home);
        listViewHome = (ListView) findViewById(R.id.listview_home);
        navigationView = (NavigationView) findViewById(R.id.navigation_home);
        recyclerViewP_H1 = (RecyclerView) findViewById(R.id.recyclerview_home1);
        arrP_H1 = new ArrayList<>();
        p_h1_adapter = new ProductHomeAdapter(getApplicationContext(),arrP_H1);
        recyclerViewP_H1.setAdapter(p_h1_adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        recyclerViewP_H1.setLayoutManager(linearLayoutManager);
        arrCateg = new ArrayList<>();
        categAdapter = new CategAdapter(arrCateg,getApplicationContext());
        listViewHome.setAdapter(categAdapter);
        if (arrCart != null){

        }else {
            arrCart = new ArrayList<>();
        }

    }
}
