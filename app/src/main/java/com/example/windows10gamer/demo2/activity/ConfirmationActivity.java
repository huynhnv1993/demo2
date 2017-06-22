package com.example.windows10gamer.demo2.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.windows10gamer.demo2.R;

import java.text.DecimalFormat;

public class ConfirmationActivity extends AppCompatActivity {
    private String partner_id;
    private long total;
    private byte[] data;
    private String pincard;
    private TextView txtcontent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);
        partner_id = getIntent().getStringExtra("partner_id");
        data = getIntent().getByteArrayExtra("data");
        total = getIntent().getLongExtra("total",-1);
        String pincard_t = getIntent().getStringExtra("pincard");
        txtcontent = (TextView) findViewById(R.id.txt_content);
        String s = new String(data);
        String[] sArray = s.split("\\^")[1].split("\\s{2,}");
        pincard = stringToHex(pincard_t);
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        txtcontent.setText("Data string \n" +"ID khách hàng: " + partner_id + "\n" + "Số tài khoản ngân hàng: " + s.split("\\^")[0].split("B")[1] + "\n" + "Tên tài khoản: " + sArray[0] +"\n" + "PinCARD hex: " + new String(pincard) + "\n" + "Số tiền thanh toán: " + decimalFormat.format(total) + " đ");
    }

    static String stringToHex(String string) {
        StringBuilder buf = new StringBuilder(200);
        for (char ch: string.toCharArray()) {
            if (buf.length() > 0)
                buf.append(' ');
            buf.append(String.format("%04x", (int) ch));
        }
        return buf.toString();
    }
}
