package com.zeezaglobal.posresturant.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zeezaglobal.posresturant.Entities.CartItem;
import com.zeezaglobal.posresturant.R;

import java.util.List;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.CartItemViewHolder> {
    private final List<CartItem> cartItemList;

    public CartItemAdapter(List<CartItem> cartItemList) {
        this.cartItemList = cartItemList;
    }

    @NonNull
    @Override
    public CartItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.checkout_item, parent, false);
        return new CartItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartItemViewHolder holder, int position) {
        CartItem item = cartItemList.get(position);
        String itemName = item.getItem().getItemName();
        String capitalizedItemName = itemName.substring(0, 1).toUpperCase() + itemName.substring(1).toLowerCase();
        holder.textViewItemName.setText(capitalizedItemName);
        holder.textViewItemPrice.setText(item.getItem().getItemPrice()+"");
        holder.textViewItemCount.setText(item.getQuantity()+"");

    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    static class CartItemViewHolder extends RecyclerView.ViewHolder {
        TextView textViewItemName, textViewItemDescription, textViewItemPrice,textViewItemCount;

        public CartItemViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewItemName = itemView.findViewById(R.id.textViewItemName);
            textViewItemDescription = itemView.findViewById(R.id.textViewItemDescription);
            textViewItemPrice = itemView.findViewById(R.id.textViewItemPrice);
            textViewItemCount = itemView.findViewById(R.id.item_count);
        }
    }
}
