package com.example.windows10gamer.demo2.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import java.text.DecimalFormat;

import com.example.windows10gamer.demo2.R;
import com.example.windows10gamer.demo2.model.Cart;
import com.example.windows10gamer.demo2.model.Product;
import com.samilcts.sdk.mpaio.MpaioManager;
import com.samilcts.sdk.mpaio.ext.dialog.RxConnectionDialog;

public class ProductDetailActivity extends AppCompatActivity {
    private RxConnectionDialog connectionDialog;
    private MpaioManager mpaioManager;

    Toolbar toolbar;
    ImageView image;
    TextView txtname,txtcost,txtdescription;
    Spinner spinner;
    Button btnBuy;
    int product_id = 0;
    String active ="";
    String description_sale = "";
    String website_description ="";
    String imageproduct ="";
    int list_price=0;
    String nameproduct = "";
    String sale_ok = "";
    int warranty = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        Mapping();
        ActionToolbar();
        GetDataProductDetail();
        CatchEventSpinner();
        EventButtonBuy();
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

    private void EventButtonBuy() {
        btnBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (HomeActivity.arrCart.size()>0){
                    int qty1 = Integer.parseInt(spinner.getSelectedItem().toString());
                    boolean exists = false;
                    for ( int i = 0 ; i <HomeActivity.arrCart.size() ; i++){
                        if (HomeActivity.arrCart.get(i).getProduct_id() == product_id){
                            HomeActivity.arrCart.get(i).setQty(HomeActivity.arrCart.get(i).getQty() + qty1);
                            exists = true;
                        }
                    }
                    if (exists == false){
                        int qty = Integer.parseInt(spinner.getSelectedItem().toString());
                        HomeActivity.arrCart.add(new Cart(product_id,nameproduct,list_price,imageproduct,qty));
                    }
                }else {
                    int qty = Integer.parseInt(spinner.getSelectedItem().toString());
                    long subtotal = qty * list_price;
                    HomeActivity.arrCart.add(new Cart(product_id,nameproduct,list_price,imageproduct,qty));
                }
                Intent intent = new Intent(getApplicationContext(),CartActivity.class);
                startActivity(intent);
            }
        });
    }

    private void CatchEventSpinner() {
        Integer[] qty = new Integer[]{1,2,3,4,5,6,7,8,9,10};
        ArrayAdapter<Integer> arrayAdapter = new ArrayAdapter<Integer>(this,android.R.layout.simple_spinner_dropdown_item,qty);
        spinner.setAdapter(arrayAdapter);
    }

    private void GetDataProductDetail() {

        Product product = (Product) getIntent().getSerializableExtra("detailproduct");
        product_id = product.getProduct_id();
        active = product.getActive();
        description_sale = product.getDescription_sale();
        website_description = product.getWebsite_description();
        imageproduct = product.getImage();
        list_price = product.getList_price();
        nameproduct = product.getName();
        sale_ok = product.getSale_ok();
        warranty = product.getWarranty();
        txtname.setText(nameproduct);
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        txtcost.setText("Giá : " + decimalFormat.format(list_price) + " đ");
        txtdescription.setText(Html.fromHtml(website_description));
        byte[] decodedString = Base64.decode(imageproduct, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        image.setImageBitmap(decodedByte);
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

    private void Mapping() {
        mpaioManager = new MpaioManager(getApplicationContext());
        connectionDialog = new RxConnectionDialog(this, mpaioManager);

        toolbar = (Toolbar) findViewById(R.id.toolbar_product_detail);
        image = (ImageView) findViewById(R.id.imageview_img_p_d);
        txtname = (TextView) findViewById(R.id.textview_name_p_d);
        txtcost = (TextView) findViewById(R.id.textview_cost_p_d);
        txtdescription = (TextView) findViewById(R.id.textview_description_p_d);
        spinner = (Spinner) findViewById(R.id.spinner_p_d);
        btnBuy = (Button) findViewById(R.id.button_buy);

    }
}
