package com.example.windows10gamer.demo2.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.windows10gamer.demo2.R;
import com.example.windows10gamer.demo2.model.Product;
import com.example.windows10gamer.demo2.model.ProductCategory;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Windows 10 Gamer on 06/06/2017.
 */

public class CategAdapter extends BaseAdapter{
    ArrayList<ProductCategory> arrayListcateg;
    Context context;

    public CategAdapter(ArrayList<ProductCategory> arrayListcateg, Context context) {
        this.arrayListcateg = arrayListcateg;
        this.context = context;
    }

    @Override
    public int getCount() {
        return arrayListcateg.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayListcateg.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    public class ViewHolder{
        TextView txtnamecateg;
        ImageView imgcateg;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder =null;
        if (view == null){
            viewHolder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.line_listview_categproduct_home,null);
            viewHolder.txtnamecateg = (TextView) view.findViewById(R.id.textview_namecateg);
            viewHolder.imgcateg = (ImageView) view.findViewById(R.id.imagecateg);
            view.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) view.getTag();

        }
        ProductCategory categ= (ProductCategory) getItem(i);
        viewHolder.txtnamecateg.setText(categ.getName());
        byte[] decodedString = Base64.decode(categ.getImage(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        viewHolder.imgcateg.setImageBitmap(decodedByte);
        return view;
    }
}

