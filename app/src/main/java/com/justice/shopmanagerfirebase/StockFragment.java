package com.justice.shopmanagerfirebase;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;


public class StockFragment extends Fragment {

    private RecyclerView recyclerView;
    private View view;

    public StockAdapter adapter=new StockAdapter() ;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_stock, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initWidgets();
        setUpRecyclerAdapter();
        setOnClickListeners();


    }

    private void setUpRecyclerAdapter() {

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }



    private void setOnClickListeners() {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                final Product product = adapter.getList().get(viewHolder.getAdapterPosition());
                FirebaseFirestore.getInstance().collection("stock").document(product.getId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Snackbar.make(recyclerView, "Deleted: " + product.getName(), Snackbar.LENGTH_LONG).show();
                            BuyStockActivity.stockList.remove(product);
                            adapter.notifyDataSetChanged();

                        } else {
                            Snackbar.make(recyclerView, "Error: " + task.getException().getMessage(), Snackbar.LENGTH_LONG).show();

                        }
                    }
                });

            }
        }).attachToRecyclerView(recyclerView);
    }

    private void initWidgets() {
        recyclerView = view.findViewById(R.id.recyclerView);

    }
}
