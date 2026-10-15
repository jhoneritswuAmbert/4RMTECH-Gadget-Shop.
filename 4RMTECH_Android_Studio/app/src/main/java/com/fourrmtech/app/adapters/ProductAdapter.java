package com.fourrmtech.app.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fourrmtech.app.R;
import com.fourrmtech.app.models.Product;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    private Context context;
    private List<Product> products;
    private OnProductClickListener listener;

    public ProductAdapter(Context context, List<Product> products, OnProductClickListener listener) {
        this.context = context;
        this.products = products;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = products.get(position);

        holder.tvBrand.setText(product.getBrand());
        holder.tvName.setText(product.getName());
        holder.tvPrice.setText(product.getFormattedPrice());
        holder.tvStock.setText(product.getStockStatus());

        // Status badge color
        String status = product.getStockStatus();
        if (status.equals("In Stock")) {
            holder.tvStock.setTextColor(Color.parseColor("#10B981"));
            holder.tvStock.setBackgroundResource(R.drawable.bg_status_approved);
        } else if (status.equals("Low Stock")) {
            holder.tvStock.setTextColor(Color.parseColor("#F59E0B"));
            holder.tvStock.setBackgroundResource(R.drawable.bg_status_pending);
        } else {
            holder.tvStock.setTextColor(Color.parseColor("#EF4444"));
            holder.tvStock.setBackgroundResource(R.drawable.bg_status_cancelled);
        }

        // Load image
        Glide.with(context)
                .load(product.getImageUrl())
                .centerCrop()
                .into(holder.ivProduct);

        holder.card.setOnClickListener(v -> {
            if (listener != null) listener.onProductClick(product);
        });
    }

    @Override
    public int getItemCount() { return products.size(); }

    public void updateList(List<Product> newList) {
        this.products = newList;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView card;
        ImageView ivProduct;
        TextView tvBrand, tvName, tvPrice, tvStock;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.card);
            ivProduct = itemView.findViewById(R.id.ivProduct);
            tvBrand = itemView.findViewById(R.id.tvBrand);
            tvName = itemView.findViewById(R.id.tvName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvStock = itemView.findViewById(R.id.tvStock);
        }
    }
}
