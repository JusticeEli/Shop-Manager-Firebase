package com.justice.shopmanagerfirebase;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StockAdapter extends RecyclerView.Adapter<StockAdapter.ViewHolder> {
    private List<Product> list;
    public Context context;



    @NonNull
    @Override
    public StockAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_buy, parent, false);
        return new StockAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StockAdapter.ViewHolder holder, final int position) {

        final Product product = list.get(position);
        RequestOptions requestOptions=new RequestOptions();
        requestOptions.placeholder(R.mipmap.place_holder);
        Glide.with(BuyStockActivity.context).applyDefaultRequestOptions(requestOptions).load(product.getUrl()).into(holder.imageView);
        holder.nameTxtView.setText(product.getName());
        holder.priceTxtView.setText("$ " + product.getPrice());


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView nameTxtView;
        private TextView priceTxtView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            nameTxtView = itemView.findViewById(R.id.nameTxtView);
            priceTxtView = itemView.findViewById(R.id.priceTxtView);
        }
    }

    public List<Product> getList() {
        return list;
    }

    public void setList(List<Product> list) {
        this.list = list;
    }
}
