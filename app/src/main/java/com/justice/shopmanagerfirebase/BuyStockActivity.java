package com.justice.shopmanagerfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class BuyStockActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private static BuyFragment buyFragment;
    private StockFragment stockFragment;

    public static List<Product> stockList = new ArrayList<>();
    public static List<Product> buyList = new ArrayList<>();
    private static int totalAmount = 0;

    public static Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_stock);
        initWidgets();
        context = this;
        initFragments();
        fetchDataForBuyList();
        fetchDataForStockList();
        setOnClickListeners();
        getSupportFragmentManager().beginTransaction().replace(R.id.container, buyFragment).commit();

    }


    private void fetchDataForBuyList() {
        totalAmount=0;
        for (Product product : buyList) {
            totalAmount += product.getPrice();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                buyFragment.updateTotalAmount(totalAmount);
            }
        },1000);

        buyFragment.adapter.notifyDataSetChanged();


    }

    private void fetchDataForStockList() {
        stockList.clear();
        FirebaseFirestore.getInstance().collection("stock").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Toast.makeText(BuyStockActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (queryDocumentSnapshots.isEmpty()) {
                    return;
                }

                for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {

                    if (doc.getType() == DocumentChange.Type.ADDED) {
                        Product productAdded = doc.getDocument().toObject(Product.class);
                        productAdded.setId(doc.getDocument().getId());
                        stockList.add(productAdded);

                    }

                }
                stockFragment.adapter.notifyDataSetChanged();


            }
        });

    }

    private void initFragments() {
        buyFragment = new BuyFragment();
        buyFragment.adapter.setList(buyList);


        stockFragment = new StockFragment();
        stockFragment.adapter.setList(stockList);
    }

    private void setOnClickListeners() {
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.buyMenu:
                        buyFragment.totalAmountTxtView.setText("hello dear");

                        getSupportFragmentManager().beginTransaction().replace(R.id.container, buyFragment).commit();


                        break;

                    case R.id.stockMenu:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, stockFragment).commit();


                        break;

                }


                return true;
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.buy_stock_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.clearItem:

                if (bottomNavigationView.getSelectedItemId() == R.id.buyMenu) {
                    buyFragment.totalAmountTxtView.setText("$ " + 0);
                    buyList.clear();
                    buyFragment.adapter.notifyDataSetChanged();


                } else {

                    for (final Product product : stockList) {

                        FirebaseFirestore.getInstance().collection("stock").document(product.getId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(BuyStockActivity.this, "Removed", Toast.LENGTH_SHORT).show();
                                    stockList.remove(product);
                                    stockFragment.adapter.notifyDataSetChanged();

                                } else {
                                    Toast.makeText(BuyStockActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }

                            }
                        });


                    }

                }

                break;
        }

        return true;
    }

    private void initWidgets() {
        bottomNavigationView = findViewById(R.id.bottomNav);
    }

    public static void addBuyList(Product product) {
        totalAmount += product.getPrice();
        buyFragment.totalAmountTxtView.setText("$ " + totalAmount);
        buyList.add(product);
        buyFragment.adapter.notifyDataSetChanged();

    }

    public static void removeBuyList(Product product) {
        totalAmount -= product.getPrice();
        buyFragment.totalAmountTxtView.setText("$ " + totalAmount);
        buyList.remove(product);
        buyFragment.adapter.notifyDataSetChanged();

    }

}
