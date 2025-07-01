package com.example.tllttbdd.ui.notifications;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tllttbdd.R;
import com.example.tllttbdd.data.model.ContactMessage;

import java.util.ArrayList;
import java.util.List;

public class ContactMessageAdapter extends RecyclerView.Adapter<ContactMessageAdapter.ViewHolder> {

    private List<ContactMessage> messages = new ArrayList<>();

    public void setMessages(List<ContactMessage> messages) {
        this.messages = messages;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact_message, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ContactMessage msg = messages.get(position);
        holder.txtContent.setText(msg.text_contact);
        holder.txtDate.setText(msg.date_contact);
        holder.txtPhoneContact.setText(msg.phone_contact);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtContent, txtDate, txtPhoneContact;
        ViewHolder(View itemView) {
            super(itemView);
            txtContent = itemView.findViewById(R.id.txtContent);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtPhoneContact = itemView.findViewById(R.id.txtPhoneContact);
        }
    }
}