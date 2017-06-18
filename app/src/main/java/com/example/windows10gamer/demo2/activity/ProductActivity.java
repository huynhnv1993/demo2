package com.example.windows10gamer.demo2.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.windows10gamer.demo2.R;
import com.example.windows10gamer.demo2.adapter.ProductAdapter;
import com.example.windows10gamer.demo2.model.Product;
import com.example.windows10gamer.demo2.ultil.CheckConnection;
import com.example.windows10gamer.demo2.ultil.Server;
import com.samilcts.sdk.mpaio.MpaioManager;
import com.samilcts.sdk.mpaio.ext.dialog.RxConnectionDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProductActivity extends AppCompatActivity {
    private RxConnectionDialog connectionDialog;
    private MpaioManager mpaioManager;

    Toolbar toolbar;
    ListView listView;
    ProductAdapter productAdapter;
    ArrayList<Product> arrProduct;
    int cateid = 0;
    int page =1;
    String catename = "";
    View footerview;
    boolean isLoading = false;
    boolean limitdata = false;
    mHandler mHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        Mapping();
        if (CheckConnection.haveNetworkConnection(getApplicationContext())){
            GetCateid();
            ActionToolbar();
            GetDataProduct(page);
            LoadMoreDataProduct();
        }else {
            CheckConnection.ShowToast_Short(getApplicationContext(),"Bạn hãy kiểm tra lại kết nối INTERNET");
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

    private void LoadMoreDataProduct() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(),ProductDetailActivity.class);
                intent.putExtra("detailproduct",arrProduct.get(position));
                startActivity(intent);
            }
        });
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount !=0 && isLoading ==false && limitdata == false){
                    isLoading = true;
                    ThreadDataProduct threadDataProduct = new ThreadDataProduct();
                    threadDataProduct.start();
                }
            }
        });
    }

    private void GetDataProduct(final int Page) {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        String Url = Server.UrlproductActivity+String.valueOf(Page);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("response ",response);
                int product_id = 0;
                String active ="";
                String description_sale = "";
                String website_description ="";
                String image ="";
                int list_price=0;
                String name = "";
                String sale_ok = "";
                int warranty = 0;
                if (response != null && response.length()!= 2){
                    listView.removeFooterView(footerview);
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        for (int i = 0 ; i<jsonArray.length() ; i++){
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            product_id = jsonObject.getInt("id");
                            active = jsonObject.getString("active");
                            description_sale = jsonObject.getString("description_sale");
                            name = jsonObject.getString("name");
                            list_price = jsonObject.getInt("list_price");
                            image = (String) jsonObject.get("image");
                            website_description = jsonObject.getString("website_description");
                            sale_ok = "";
                            warranty = jsonObject.getInt("warranty");
                            arrProduct.add(new Product(product_id,name,list_price,description_sale,website_description,image,warranty,active,sale_ok));
                            productAdapter.notifyDataSetChanged();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else {
                    limitdata = true;
                    listView.removeFooterView(footerview);
                    if (Page == 1){
                        ProgressBar progressbar = (ProgressBar)findViewById(R.id.progressbar_first);

                        progressbar.setVisibility(View.INVISIBLE) ;
                        CheckConnection.ShowToast_Short(getApplicationContext(),"Không có dữ liệu");
                    }else {
                        CheckConnection.ShowToast_Short(getApplicationContext(),"Đã hết dữ liệu");
                    }

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> param = new HashMap<String, String>();
                param.put("cateid",String.valueOf(cateid));
                return param;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void ActionToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle(catename);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void GetCateid() {
        cateid = getIntent().getIntExtra("cateid",-1);
        catename = getIntent().getStringExtra("catename");
        Log.d("gia tri cateid",cateid+"");
        Log.d("gia tri catename", catename +"!!!");
    }

    private void Mapping() {
        mpaioManager = new MpaioManager(getApplicationContext());
        connectionDialog = new RxConnectionDialog(this, mpaioManager);

        toolbar = (Toolbar) findViewById(R.id.toolbar_product);
        listView = (ListView) findViewById(R.id.listview_product);
        arrProduct = new ArrayList<>();
        productAdapter = new ProductAdapter(getApplicationContext(),arrProduct);
        listView.setAdapter(productAdapter);
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        footerview = inflater.inflate(R.layout.progressbar,null);
        mHandler = new mHandler();
    }

    public class mHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    listView.addFooterView(footerview);
                    break;
                case 1:
                    GetDataProduct(++page);
                    isLoading = false;
                    break;
            }
            super.handleMessage(msg);
        }
    }

    public class ThreadDataProduct extends Thread{
        @Override
        public void run() {
            mHandler.sendEmptyMessage(0);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Message message = mHandler.obtainMessage(1);
            mHandler.sendMessage(message);
            super.run();
        }

    }
}
