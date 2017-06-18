package com.example.windows10gamer.demo2.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.windows10gamer.demo2.R;
import com.example.windows10gamer.demo2.activity.ProductActivity;
import com.example.windows10gamer.demo2.model.Product;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by Windows 10 Gamer on 08/06/2017.
 */

public class ProductAdapter extends BaseAdapter {
    Context context;
    ArrayList<Product> arrProduct;

    public ProductAdapter(Context context, ArrayList<Product> arrProduct) {
        this.context = context;
        this.arrProduct = arrProduct;
    }

    @Override
    public int getCount() {
        return arrProduct.size();
    }

    @Override
    public Object getItem(int position) {
        return arrProduct.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    public class ProductAdapterViewHolder{
        public TextView txtname, txtcost, txtdescription;
        public ImageView image;
    }
    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ProductAdapterViewHolder productAdapterViewHolder = null;
        if (view == null){
            productAdapterViewHolder = new ProductAdapterViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.line_product,null);
            productAdapterViewHolder.txtname = (TextView) view.findViewById(R.id.textview_name_p);
            productAdapterViewHolder.txtcost = (TextView) view.findViewById(R.id.textview_cost_p);
            productAdapterViewHolder.txtdescription = (TextView) view.findViewById(R.id.textview_description_p);
            productAdapterViewHolder.image = (ImageView) view.findViewById(R.id.imageview_img_p);
            view.setTag(productAdapterViewHolder);
        }else {
            productAdapterViewHolder = (ProductAdapterViewHolder) view.getTag();
        }
        Product product = (Product) getItem(position);
        productAdapterViewHolder.txtname.setMaxLines(2);
        productAdapterViewHolder.txtname.setEllipsize(TextUtils.TruncateAt.END);
        productAdapterViewHolder.txtname.setText(product.getName());
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        productAdapterViewHolder.txtcost.setText("Giá : " + decimalFormat.format(product.getList_price()) + " đ");
        productAdapterViewHolder.txtdescription.setMaxLines(4);
        productAdapterViewHolder.txtdescription.setEllipsize(TextUtils.TruncateAt.END);
        productAdapterViewHolder.txtdescription.setText(product.getDescription_sale());
        byte[] decodedString = Base64.decode(product.getImage(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        productAdapterViewHolder.image.setImageBitmap(decodedByte);

        return view;
    }
}
