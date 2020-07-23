package com.justice.shopmanagerfirebase;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
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

import java.util.ArrayList;
import java.util.List;


public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> implements Filterable {
    private static final int EDIT_MENU = 1;
    private static final int BUY_MENU = 2;
    private static final int DELETE_MENU = 3;
    private static final int STOCK_MENU = 4;

    private List<Product> list;
    private Context context;
    private ProgressDialog progressdialog;


    public MainAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_main, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        progressdialog = new ProgressDialog(context);
/////////////////////////////////////////////////////////////////////////////////////////////////////////////
        final Product product = list.get(position);
        RequestOptions requestOptions=new RequestOptions();
        requestOptions.placeholder(R.mipmap.place_holder);

        Glide.with(context).applyDefaultRequestOptions(requestOptions).load(product.getUrl()).into(holder.imageView);
        holder.nameTxtView.setText(product.getName());
        holder.priceTxtView.setText("" + product.getPrice());


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BuyStockActivity.buyList.add(product);
                Toast.makeText(context, "bought", Toast.LENGTH_SHORT).show();

            }
        });


        holder.itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.setHeaderTitle("Do you want to...");
                MenuItem editMenu = menu.add(Menu.NONE, EDIT_MENU, 1, "edit");
                MenuItem buyMenu = menu.add(Menu.NONE, BUY_MENU, 1, "buy");
                MenuItem deleteMenu = menu.add(Menu.NONE, DELETE_MENU, 1, "delete");
                MenuItem stockMenu = menu.add(Menu.NONE, STOCK_MENU, 1, "out of stock");

                editMenu.setOnMenuItemClickListener(onMenuItemClickListener);
                buyMenu.setOnMenuItemClickListener(onMenuItemClickListener);
                deleteMenu.setOnMenuItemClickListener(onMenuItemClickListener);
                stockMenu.setOnMenuItemClickListener(onMenuItemClickListener);


            }

            MenuItem.OnMenuItemClickListener onMenuItemClickListener = new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case EDIT_MENU:
                            ApplicationClass.product = product;
                            context.startActivity(new Intent(context, AddProductActivity.class));
                            break;
                        case BUY_MENU:
                            BuyStockActivity.addBuyList(product);
                            Toast.makeText(context, "bought", Toast.LENGTH_SHORT).show();

                            break;
                        case DELETE_MENU:
                            progressdialog.setTitle("Delete");
                            progressdialog.setMessage("deleting " + product.getName() + "...");
                            progressdialog.show();
                            FirebaseFirestore.getInstance().collection("product").document(product.getId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(context, "deleted", Toast.LENGTH_SHORT).show();

                                    } else {
                                        Toast.makeText(context, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                    progressdialog.dismiss();

                                }
                            });
                            notifyDataSetChanged();

                            break;
                        case STOCK_MENU:
                            progressdialog.setTitle("Stock");
                            progressdialog.setMessage("adding to stock " + product.getName() + "...");

                            progressdialog.show();
                            FirebaseFirestore.getInstance().collection("stock").document(product.getId()).set(product).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(context, "success", Toast.LENGTH_SHORT).show();
                                        progressdialog.dismiss();

                                    } else {
                                        Toast.makeText(context, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                    progressdialog.dismiss();
                                }
                            });

                            break;


                    }

                    return true;
                }
            };
        });


        /**
         *  holder.itemView.setOnClickListener(new View.OnClickListener() {
         *             @Override
         *             public void onClick(View v) {
         *
         *
         *                 progressdialog.show();
         *                 FirebaseFirestore.getInstance().collection("stock").document(product.getId()).set(product).addOnCompleteListener(new OnCompleteListener<Void>() {
         *                     @Override
         *                     public void onComplete(@NonNull Task<Void> task) {
         *                         if (task.isSuccessful()) {
         *                             Toast.makeText(context, "success", Toast.LENGTH_SHORT).show();
         *                             progressdialog.dismiss();
         *
         *                         } else {
         *                             Toast.makeText(context, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
         *                         }
         *                         progressdialog.dismiss();
         *                     }
         *                 });
         *
         *
         *             }
         *         });
         */


        /**
         *  holder.itemView.setOnClickListener(new View.OnClickListener() {
         *                       @Override
         *                       public void onClick(View v) {
         *                           progressdialog.show();
         *                           FirebaseFirestore.getInstance().collection("buy").document(product.getId()).set(product).addOnCompleteListener(new OnCompleteListener<Void>() {
         *                               @Override
         *                               public void onComplete(@NonNull Task<Void> task) {
         *                                   if (task.isSuccessful()) {
         *                                       Toast.makeText(context, "success", Toast.LENGTH_SHORT).show();
         *                                       progressdialog.dismiss();
         *
         *                                   } else {
         *                                       Toast.makeText(context, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
         *                                   }
         *                                  progressdialog.dismiss();
         *                               }
         *                           });
         *
         *                       }
         *                   });
         */


        /**
         *  holder.itemView.setOnClickListener(new View.OnClickListener() {
         *             @Override
         *             public void onClick(View v) {
         *                 ApplicationClass.product = list.get(position);
         *                 context.startActivity(new Intent(context, AddProductActivity.class));
         *
         *
         *             }
         *         });
         */


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private Filter filter = new Filter() {
        List<Product> filteredList = new ArrayList<>();
        private FilterResults result = new FilterResults();


        @Override
        protected FilterResults performFiltering(CharSequence constraint) {


            filteredList.clear();
            if (constraint == null || constraint.toString().isEmpty()) {

                result.values = MainActivity.mainList;
                return result;
            }

            for (Product product : MainActivity.mainList) {
                if (product.getName().toLowerCase().contains(constraint.toString().toLowerCase())) {
                    filteredList.add(product);
                }
            }

            result.values = filteredList;
            return result;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            setList((List) results.values);
            notifyDataSetChanged();

        }
    };


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

    public void setList(List<Product> list) {
        this.list = list;
        notifyDataSetChanged();
    }
}
