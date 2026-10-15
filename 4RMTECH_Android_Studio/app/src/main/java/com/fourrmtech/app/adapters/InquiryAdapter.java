package com.fourrmtech.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.fourrmtech.app.R;
import com.fourrmtech.app.models.Inquiry;

import java.util.List;

public class InquiryAdapter
        extends RecyclerView.Adapter<InquiryAdapter.ViewHolder> {

    public interface OnInquiryClickListener {
        void onInquiryClick(Inquiry inquiry);
    }

    private final Context context;
    private List<Inquiry> inquiries;
    private final OnInquiryClickListener listener;

    public InquiryAdapter(
            Context context,
            List<Inquiry> inquiries,
            OnInquiryClickListener listener) {

        this.context = context;
        this.inquiries = inquiries;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater
                .from(context)
                .inflate(
                        R.layout.item_inquiry_card,
                        parent,
                        false
                );

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position) {

        Inquiry inquiry = inquiries.get(position);

        holder.tvCustomerName.setText(
                inquiry.getCustomerName()
        );

        holder.tvEmail.setText(
                safeValue(inquiry.getEmail())
        );

        holder.tvMessage.setText(
                safeValue(inquiry.getMessage())
        );

        holder.tvDate.setText(
                formatDate(inquiry.getDateSubmitted())
        );

        holder.card.setOnClickListener(v -> {

            if (listener != null) {
                listener.onInquiryClick(inquiry);
            }

        });
    }

    @Override
    public int getItemCount() {
        return inquiries.size();
    }

    public void updateList(List<Inquiry> newList) {
        this.inquiries = newList;
        notifyDataSetChanged();
    }

    private String safeValue(String value) {

        if (value == null || value.trim().isEmpty()) {
            return "Not provided";
        }

        return value;
    }

    private String formatDate(String date) {

        if (date == null || date.trim().isEmpty()) {
            return "Date not available";
        }

        if (date.length() >= 16) {
            return date.substring(0, 16)
                    .replace("T", " ");
        }

        return date;
    }

    static class ViewHolder
            extends RecyclerView.ViewHolder {

        CardView card;
        TextView tvCustomerName;
        TextView tvEmail;
        TextView tvMessage;
        TextView tvDate;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            card = itemView.findViewById(
                    R.id.card
            );

            tvCustomerName = itemView.findViewById(
                    R.id.tvCustomerName
            );

            tvEmail = itemView.findViewById(
                    R.id.tvEmail
            );

            tvMessage = itemView.findViewById(
                    R.id.tvMessage
            );

            tvDate = itemView.findViewById(
                    R.id.tvDate
            );
        }
    }
}