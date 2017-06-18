package com.example.windows10gamer.demo2.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.windows10gamer.demo2.R;
import com.example.windows10gamer.demo2.adapter.CartAdapter;
import com.example.windows10gamer.demo2.ultil.CheckConnection;
import com.samilcts.sdk.mpaio.MpaioManager;
import com.samilcts.sdk.mpaio.ext.dialog.RxConnectionDialog;

import java.text.DecimalFormat;

public class CartActivity extends AppCompatActivity {
    private RxConnectionDialog connectionDialog;
    private MpaioManager mpaioManager;

    ListView lvcart;
    TextView txtnotif;
    static TextView txttotal;
    Button btncheckout, btncontinue;
    Toolbar toolbarcart;
    CartAdapter cartAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        Mapping();
        ActionToolbar();
        CheckData();
        EvenUltil();
        CatchOnItemListView();
        EventButton();
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

    private void EventButton() {
        btncontinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
                startActivity(intent);
            }
        });
        btncheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (HomeActivity.arrCart.size()>0){
                    Intent intent = new Intent(getApplicationContext(),CheckoutActivity.class);
                    startActivity(intent);
                }else {
                    CheckConnection.ShowToast_Short(getApplicationContext(),"Giỏ hàng của bạn chưa có sản phẩm");
                }
            }
        });
    }

    private void CatchOnItemListView() {
        lvcart.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
                builder.setTitle("Xác nhận xóa sản phẩm");
                builder.setMessage("Bạn có chắc muốn xóa sản phẩm này");
                builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (HomeActivity.arrCart.size() <=0){
                            txtnotif.setVisibility(View.VISIBLE);
                        }else {
                            HomeActivity.arrCart.remove(position);
                            cartAdapter.notifyDataSetChanged();
                            EvenUltil();
                            if (HomeActivity.arrCart.size()<=0){
                                txtnotif.setVisibility(View.VISIBLE);
                            }else{
                                txtnotif.setVisibility(View.INVISIBLE);
                                cartAdapter.notifyDataSetChanged();
                                EvenUltil();
                            }
                        }
                    }
                });
                builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();
                return true;
            }
        });
    }

    public static void EvenUltil() {
        long total = 0;
        for (int i = 0 ; i <HomeActivity.arrCart.size() ; i++){
            total += HomeActivity.arrCart.get(i).getList_price() * HomeActivity.arrCart.get(i).getQty();
        }
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        txttotal.setText(decimalFormat.format(total) + " đ");
    }

    private void CheckData() {
        if (HomeActivity.arrCart.size()<=0){
            cartAdapter.notifyDataSetChanged();
            txtnotif.setVisibility(View.VISIBLE);
            lvcart.setVisibility(View.INVISIBLE);
        }else {
            cartAdapter.notifyDataSetChanged();
            txtnotif.setVisibility(View.INVISIBLE);
            lvcart.setVisibility(View.VISIBLE);
        }
    }

    private void ActionToolbar() {
        setSupportActionBar(toolbarcart);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarcart.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void Mapping() {
        mpaioManager = new MpaioManager(getApplicationContext());
        connectionDialog = new RxConnectionDialog(this, mpaioManager);

        lvcart = (ListView) findViewById(R.id.listview_cart);
        txtnotif = (TextView) findViewById(R.id.textview_notif);
        txttotal = (TextView) findViewById(R.id.textview_total);
        btncheckout = (Button) findViewById(R.id.button_checkout);
        btncontinue = (Button) findViewById(R.id.button_continue);
        toolbarcart = (Toolbar) findViewById(R.id.toolbar_cart);
        cartAdapter = new CartAdapter(CartActivity.this,HomeActivity.arrCart);
        lvcart.setAdapter(cartAdapter);
    }
}
