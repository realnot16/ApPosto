package com.example.project.reservation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.project.R;

import java.text.ParseException;
import java.util.List;

class CustomAdapter extends ArrayAdapter<Reservation> {
    public CustomAdapter(@NonNull Context context, int textViewResourceId, @NonNull List<Reservation> objects) {
        super(context, textViewResourceId, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Reservation r = getItem(position);
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.reservation_simple_row_layout, null);
        TextView bookingId = convertView.findViewById(R.id.bookingLabel_text);
        TextView bookingDate = convertView.findViewById(R.id.dataLabel_text);
        bookingId.setText(r.getId_booking());
        try {
            bookingDate.setText(r.getTime_start());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return convertView;
    }
}
