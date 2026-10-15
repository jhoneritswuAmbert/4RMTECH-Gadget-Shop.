package com.fourrmtech.app.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.fourrmtech.app.R;
import com.fourrmtech.app.models.Customer;

import java.util.List;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.ViewHolder> {

    public interface OnCustomerClickListener {
        void onCustomerClick(Customer customer);
    }

    private static final int[] AVATAR_COLORS = {
            0xFF2563EB, 0xFF16A34A, 0xFFEA580C, 0xFF9333EA, 0xFFDC2626
    };

    private Context context;
    private List<Customer> customers;
    private OnCustomerClickListener listener;

    public CustomerAdapter(Context context, List<Customer> customers, OnCustomerClickListener listener) {
        this.context = context;
        this.customers = customers;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_customer_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Customer customer = customers.get(position);

        holder.tvInitials.setText(customer.getInitials());
        holder.tvInitials.setBackgroundColor(AVATAR_COLORS[customer.getId() % AVATAR_COLORS.length]);
        holder.tvFullName.setText(customer.getFullName());
        holder.tvEmail.setText(customer.getEmail());

        String contact = customer.getContactNumber();

        if (contact == null || contact.trim().isEmpty()) {
            holder.tvContact.setText("No contact number");
        } else {
            holder.tvContact.setText(contact);
        }

        String joinedDate = customer.getJoinedDate();

        if (joinedDate != null && joinedDate.length() >= 7) {
            holder.tvJoined.setText(joinedDate.substring(0, 7));
        } else {
            holder.tvJoined.setText("—");
        }

        holder.card.setOnClickListener(v -> {
            if (listener != null) listener.onCustomerClick(customer);
        });
    }

    @Override
    public int getItemCount() { return customers.size(); }

    public void updateList(List<Customer> newList) {
        this.customers = newList;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView card;
        TextView tvInitials, tvFullName, tvEmail, tvContact, tvJoined;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.card);
            tvInitials = itemView.findViewById(R.id.tvInitials);
            tvFullName = itemView.findViewById(R.id.tvFullName);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvContact = itemView.findViewById(R.id.tvContact);
            tvJoined = itemView.findViewById(R.id.tvJoined);
        }
    }
}
