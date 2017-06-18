package com.example.windows10gamer.demo2.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.windows10gamer.demo2.R;
import com.example.windows10gamer.demo2.activity.CartActivity;
import com.example.windows10gamer.demo2.activity.HomeActivity;
import com.example.windows10gamer.demo2.model.Cart;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by Windows 10 Gamer on 12/06/2017.
 */

public class CartAdapter extends BaseAdapter {
    Context context;
    ArrayList<Cart> arrCart;

    public CartAdapter(Context context, ArrayList<Cart> arrCart) {
        this.context = context;
        this.arrCart = arrCart;
    }

    @Override
    public int getCount() {
        return arrCart.size();
    }

    @Override
    public Object getItem(int position) {
        return arrCart.get(position);
    }
    public class CartViewHolder{
        public TextView txtname,txtcost;
        public ImageView imageView;
        public Button btnminus,btnvalues,btnplus;
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        CartViewHolder cartViewHolder = null;
        if (view == null){
            cartViewHolder = new CartViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.line_cart,null);
            cartViewHolder.txtname = (TextView) view.findViewById(R.id.textview_name_c);
            cartViewHolder.txtcost = (TextView) view.findViewById(R.id.textview_cost_c);
            cartViewHolder.imageView = (ImageView) view.findViewById(R.id.imageview_cart);
            cartViewHolder.btnminus = (Button) view.findViewById(R.id.button_minus);
            cartViewHolder.btnvalues = (Button) view.findViewById(R.id.button_values);
            cartViewHolder.btnplus = (Button) view.findViewById(R.id.button_plus);
            view.setTag(cartViewHolder);
        }else {
            cartViewHolder = (CartViewHolder) view.getTag();
        }
        Cart cart = (Cart) getItem(position);
        cartViewHolder.txtname.setText(cart.getName());
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        cartViewHolder.txtcost.setText("Đơn giá : " + decimalFormat.format(cart.getList_price()) + " đ");
        byte[] decodedString = Base64.decode(cart.getImage(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        cartViewHolder.imageView.setImageBitmap(decodedByte);
        cartViewHolder.btnvalues.setText(cart.getQty() + "");
        final int qty1 = Integer.parseInt(cartViewHolder.btnvalues.getText().toString());
        if (qty1<=1){
            cartViewHolder.btnminus.setVisibility(View.INVISIBLE);
        }else {
            cartViewHolder.btnminus.setVisibility(View.VISIBLE);
            cartViewHolder.btnplus.setVisibility(View.VISIBLE);
        }

        final CartViewHolder finalCartViewHolder = cartViewHolder;
        finalCartViewHolder.btnplus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int qtynew = Integer.parseInt(finalCartViewHolder.btnvalues.getText().toString())+1;
                HomeActivity.arrCart.get(position).setQty(qtynew);
                finalCartViewHolder.btnvalues.setText(String.valueOf(qtynew));
                CartActivity.EvenUltil();
                if (qtynew>1){
                    finalCartViewHolder.btnminus.setVisibility(View.VISIBLE);
                    finalCartViewHolder.btnplus.setVisibility(View.VISIBLE);
                }
            }
        });
        finalCartViewHolder.btnminus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int qtynew = Integer.parseInt(finalCartViewHolder.btnvalues.getText().toString())-1;
                HomeActivity.arrCart.get(position).setQty(qtynew);
                finalCartViewHolder.btnvalues.setText(String.valueOf(qtynew));
                CartActivity.EvenUltil();
                if (qtynew<2){
                    finalCartViewHolder.btnminus.setVisibility(View.INVISIBLE);
                    finalCartViewHolder.btnplus.setVisibility(View.VISIBLE);
                }
            }
        });
        return view;
    }
}
