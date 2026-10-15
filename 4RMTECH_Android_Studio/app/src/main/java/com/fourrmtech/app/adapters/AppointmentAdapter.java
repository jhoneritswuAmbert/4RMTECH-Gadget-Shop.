package com.fourrmtech.app.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.fourrmtech.app.R;
import com.fourrmtech.app.models.Appointment;

import java.util.List;

public class AppointmentAdapter
        extends RecyclerView.Adapter<AppointmentAdapter.ViewHolder> {

    public interface OnAppointmentClickListener {
        void onAppointmentClick(Appointment appointment);
    }

    public interface OnCancelAppointmentListener {
        void onCancelAppointment(Appointment appointment);
    }

    private Context context;
    private List<Appointment> appointments;
    private boolean isAdmin;

    private OnAppointmentClickListener listener;
    private OnCancelAppointmentListener cancelListener;

    public AppointmentAdapter(
            Context context,
            List<Appointment> appointments,
            boolean isAdmin) {

        this.context = context;
        this.appointments = appointments;
        this.isAdmin = isAdmin;
    }

    public void setOnClickListener(
            OnAppointmentClickListener listener) {

        this.listener = listener;
    }

    public void setOnCancelAppointmentListener(
            OnCancelAppointmentListener listener) {

        this.cancelListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater
                .from(context)
                .inflate(
                        R.layout.item_appointment_card,
                        parent,
                        false
                );

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position) {

        Appointment appt = appointments.get(position);

        holder.tvDevice.setText(
                appt.getDeviceSummary()
        );

        holder.tvIssue.setText(
                appt.getIssue()
        );

        holder.tvDate.setText(
                appt.getDate()
        );

        holder.tvTime.setText(
                appt.getTime()
        );

        holder.tvStatus.setText(
                appt.getStatusLabel()
        );

        if (isAdmin) {

            holder.tvCustomer.setVisibility(
                    View.VISIBLE
            );

            holder.tvCustomer.setText(
                    appt.getCustomerName()
            );

        } else {

            holder.tvCustomer.setVisibility(
                    View.GONE
            );
        }

        // Status badge styling
        switch (appt.getStatus()) {

            case PENDING:

                holder.tvStatus.setTextColor(
                        Color.parseColor("#F59E0B")
                );

                holder.tvStatus.setBackgroundResource(
                        R.drawable.bg_status_pending
                );

                break;

            case APPROVED:

                holder.tvStatus.setTextColor(
                        Color.parseColor("#10B981")
                );

                holder.tvStatus.setBackgroundResource(
                        R.drawable.bg_status_approved
                );

                break;

            case READY_TO_CLAIM:
                holder.tvStatus.setTextColor(Color.parseColor("#7E22CE"));
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_ready_to_claim);
                break;

            case COMPLETED:

                holder.tvStatus.setTextColor(
                        Color.parseColor("#2563EB")
                );

                holder.tvStatus.setBackgroundResource(
                        R.drawable.bg_status_completed
                );

                break;

            case CANCELLED:

                holder.tvStatus.setTextColor(
                        Color.parseColor("#EF4444")
                );

                holder.tvStatus.setBackgroundResource(
                        R.drawable.bg_status_cancelled
                );

                break;
        }

        holder.tvRepairSummary.setText(appt.getRepairSummary().isEmpty() ? "" : "Repair summary available · Tap for details");
        holder.tvRepairSummary.setVisibility(appt.getRepairSummary().isEmpty() ? View.GONE : View.VISIBLE);

        // Cancel button
        if (
                !isAdmin
                        && appt.getStatus()
                        == Appointment.Status.PENDING
        ) {

            holder.btnCancel.setVisibility(
                    View.VISIBLE
            );

            holder.btnCancel.setOnClickListener(v -> {

                if (cancelListener != null) {

                    cancelListener.onCancelAppointment(
                            appt
                    );
                }
            });

        } else {

            holder.btnCancel.setVisibility(
                    View.GONE
            );

            holder.btnCancel.setOnClickListener(
                    null
            );
        }

        // Appointment card click
        holder.card.setOnClickListener(v -> {

            if (listener != null) {

                listener.onAppointmentClick(appt);
            }
        });
    }

    @Override
    public int getItemCount() {

        return appointments.size();
    }

    public void updateList(
            List<Appointment> newList) {

        this.appointments = newList;

        notifyDataSetChanged();
    }

    static class ViewHolder
            extends RecyclerView.ViewHolder {

        CardView card;

        TextView tvCustomer;
        TextView tvDevice;
        TextView tvIssue;
        TextView tvDate;
        TextView tvTime;
        TextView tvStatus;
        TextView tvRepairSummary;

        Button btnCancel;

        ViewHolder(@NonNull View itemView) {

            super(itemView);

            card = itemView.findViewById(
                    R.id.card
            );

            tvCustomer = itemView.findViewById(
                    R.id.tvCustomer
            );

            tvDevice = itemView.findViewById(
                    R.id.tvDevice
            );

            tvIssue = itemView.findViewById(
                    R.id.tvIssue
            );

            tvDate = itemView.findViewById(
                    R.id.tvDate
            );

            tvTime = itemView.findViewById(
                    R.id.tvTime
            );

            tvStatus = itemView.findViewById(
                    R.id.tvStatus
            );

            tvRepairSummary = itemView.findViewById(R.id.tvRepairSummary);

            btnCancel = itemView.findViewById(
                    R.id.btnCancel
            );
        }
    }
}