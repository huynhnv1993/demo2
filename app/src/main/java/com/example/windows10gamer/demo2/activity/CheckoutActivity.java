package com.example.windows10gamer.demo2.activity;

import android.content.Intent;
import android.media.MediaCodec;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.windows10gamer.demo2.R;
import com.example.windows10gamer.demo2.ultil.CheckConnection;
import com.example.windows10gamer.demo2.ultil.Server;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CheckoutActivity extends AppCompatActivity {
    EditText edtname,edtmobile,edtemail,edtstreet;
    Button btnconfirm,btnback;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        try {
            Mapping();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if (CheckConnection.haveNetworkConnection(getApplicationContext())){
            EventButton();
        }else {
            CheckConnection.ShowToast_Short(getApplicationContext(),"Bạn hãy kiểm tra lại kết nối INTERNET");
        }
    }

    private boolean isValidName(String name){
        boolean check = false;
        if (name.length()>0){
            check=true;
        }else {
            edtname.setError("Vui lòng nhập họ và tên");
        }
        return check;
    }

    private boolean isValidStreet(String street){
        boolean check = false;
        if (street.length()>0){
            check=true;
        }else {
            edtstreet.setError("Vui lòng nhập địa chỉ liên lạc");
        }
        return check;
    }

    private boolean isValidMail(String email) {
        boolean check=false;
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (email.length()>0){
            check = email.matches(emailPattern);
            if(!check) {
                edtemail.setError("Địa chỉ email chưa đúng! Vui lòng nhập lại");
            }
        }else {
            edtemail.setError("Vui lòng nhập địa chỉ email");
        }

        return check;
    }

    private boolean isValidMobile(String PhoneNo) {
        boolean check=false;
//        String PhoneNo = "+123-456 7890";
        String Regex = "[^\\d]";
        String PhoneDigits = PhoneNo.replaceAll(Regex, "");
        if (PhoneDigits.length()<10 || PhoneDigits.length()>11)
        {
            if (PhoneDigits.length()>0){
                edtmobile.setError("Số điện thoại chưa đúng! Vui lòng nhập lại");
            }else {
                edtmobile.setError("Vui lòng nhập số điện thoại");
            }
            check=false;
        }
        else
        {
            PhoneNo = "+";
            PhoneNo = PhoneNo.concat(PhoneDigits); // adding the plus sign
            check=true;
            // validation successful
        }
        return check;
    }

    private void EventButton() {
        btnconfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = edtname.getText().toString().trim();
                final String mobile = edtmobile.getText().toString().trim();
                final String email = edtemail.getText().toString().trim();
                final String street = edtstreet.getText().toString().trim();
                if (isValidName(name) && isValidMobile(mobile) && isValidMail(email) && isValidStreet(street)){
                    RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, Server.Urlrespartner, new Response.Listener<String>() {
                        @Override
                        public void onResponse(final String partner_id) {
                            Log.d("respartner id",partner_id);
                            if (Integer.parseInt(partner_id) > 0){
                                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                                StringRequest request = new StringRequest(Request.Method.POST, Server.Urlsaleorder, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        if (response.equals("1")){
//                                            HomeActivity.arrCart.clear();
                                            Intent intent = new Intent(getApplicationContext(),PaymentActivity.class);
                                            intent.putExtra("partner_id",partner_id);
                                            Log.d("partner_id",partner_id);
                                            startActivity(intent);
                                        }else{
                                            CheckConnection.ShowToast_Short(getApplicationContext(),response + "Dữ liệu giỏ hàng đã bị lỗi");
                                            Log.d("error",response);

//                                                    Error:
//                                            1: thanh cong
//                                            2: Dữ liệu gửi đi rỗng
//                                            3: Có lỗi khi tạo đơn hàng
//                                            4: Có lỗi khi lấy thông tin giá và thuế
//                                            5: Có lỗi khi tạo order_line
//                                            6: Có lỗi khi thêm thuế
//                                            7: Có lỗi khi thay đổi tên
//
                                        }
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {

                                    }
                                }){
                                    @Override
                                    protected Map<String, String> getParams() throws AuthFailureError {
                                        JSONArray jsonArray = new JSONArray();
                                        for (int i = 0 ; i < HomeActivity.arrCart.size() ; i++){
                                            JSONObject jsonObject = new JSONObject();
                                            try {
                                                jsonObject.put("partner_id",partner_id);
                                                jsonObject.put("product_id",HomeActivity.arrCart.get(i).getProduct_id());
                                                jsonObject.put("qty",HomeActivity.arrCart.get(i).getQty());
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            jsonArray.put(jsonObject);
                                        }
                                        HashMap<String,String> hashMap = new HashMap<String, String>();
                                        hashMap.put("json",jsonArray.toString());
                                        return hashMap;
                                    }
                                };
                                queue.add(request);
                            }else {
                                CheckConnection.ShowToast_Short(getApplicationContext(),"Bạn hãy kiểm tra lại thông tin");
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    }){
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            HashMap<String,String> hashMap = new HashMap<String, String>();
                            hashMap.put("name",name);
                            hashMap.put("mobile",mobile);
                            hashMap.put("email",email);
                            hashMap.put("street",street);
                            return hashMap;
                        }
                    };
                    requestQueue.add(stringRequest);
                }else {
                    CheckConnection.ShowToast_Short(getApplicationContext(),"Bạn hãy kiểm tra lại thông tin");
                }
            }
        });
    }

    private void Mapping() throws IOException, ClassNotFoundException {
        edtname = (EditText) findViewById(R.id.edittext_name);
        edtmobile = (EditText) findViewById(R.id.edittext_mobile);
        edtemail = (EditText) findViewById(R.id.edittext_email);
        edtstreet = (EditText) findViewById(R.id.edittext_street);
        btnconfirm = (Button) findViewById(R.id.button_confirm);
        btnback = (Button) findViewById(R.id.button_back);


    }
}
