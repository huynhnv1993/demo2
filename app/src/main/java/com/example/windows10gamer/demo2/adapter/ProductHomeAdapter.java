package com.example.windows10gamer.demo2.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.windows10gamer.demo2.R;
import com.example.windows10gamer.demo2.activity.ProductDetailActivity;
import com.example.windows10gamer.demo2.model.Product;
import com.example.windows10gamer.demo2.ultil.CheckConnection;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by Windows 10 Gamer on 06/06/2017.
 */

public class ProductHomeAdapter extends RecyclerView.Adapter<ProductHomeAdapter.ProductHomeViewHolder> {
    Context context;
    ArrayList<Product> arrP_H;

    public ProductHomeAdapter(Context context, ArrayList<Product> arrP_H) {
        this.context = context;
        this.arrP_H = arrP_H;
    }

    @Override
    public ProductHomeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_recyclerview_home1,null);
        ProductHomeViewHolder productHomeViewHolder = new ProductHomeViewHolder(v);
        return productHomeViewHolder;
    }

    @Override
    public void onBindViewHolder(ProductHomeViewHolder holder, int position) {
        Product product = arrP_H.get(position);
        holder.txtname.setMaxLines(2);
        holder.txtname.setEllipsize(TextUtils.TruncateAt.END);
        holder.txtname.setText(product.getName());
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        holder.txtcost.setText("Giá : " + decimalFormat.format(product.getList_price()) + " đ");
        byte[] decodedString = Base64.decode(product.getImage(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        holder.imgproduct.setImageBitmap(decodedByte);
    }

    @Override
    public int getItemCount() {
        return arrP_H.size();
    }

    public class ProductHomeViewHolder extends RecyclerView.ViewHolder{
        public ImageView imgproduct;
        public TextView txtname,txtcost;

        public ProductHomeViewHolder(View itemView) {
            super(itemView);
            imgproduct = (ImageView) itemView.findViewById(R.id.imageview_img_p_h);
            txtcost = (TextView) itemView.findViewById(R.id.textview_cost_p_h);
            txtname = (TextView) itemView.findViewById(R.id.textview_name_p_h);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ProductDetailActivity.class);
                    intent.putExtra("detailproduct",arrP_H.get(getPosition()));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    CheckConnection.ShowToast_Short(context,arrP_H.get(getPosition()).getName());
                    context.startActivity(intent);
                }
            });
        }
    }
}
