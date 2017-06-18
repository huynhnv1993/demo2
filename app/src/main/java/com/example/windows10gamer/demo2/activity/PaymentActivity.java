package com.example.windows10gamer.demo2.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

import com.example.windows10gamer.demo2.ultil.CheckConnection;
import com.samilcts.sdk.mpaio.MpaioManager;

import com.samilcts.sdk.mpaio.ext.dialog.RxConnectionDialog;

import com.example.windows10gamer.demo2.R;

public class PaymentActivity extends AppCompatActivity {
    private RxConnectionDialog connectionDialog;
    private MpaioManager mpaioManager;

    Toolbar toolbar;
    Spinner spinner;
    Button btnpayment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        Mapping();
        if (CheckConnection.haveNetworkConnection(getApplicationContext())){
            ActionToolbar();
        }else {
            CheckConnection.ShowToast_Short(getApplicationContext(),"Bạn hãy kiểm tra lại kết nối INTERNET");
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
        mpaioManager = new MpaioManager(getApplicationContext());
        connectionDialog = new RxConnectionDialog(this, mpaioManager);

        toolbar = (Toolbar) findViewById(R.id.toolbar_payment);
        spinner = (Spinner) findViewById(R.id.spinner_payment);
        btnpayment = (Button) findViewById(R.id.button_pay);


    }

}
