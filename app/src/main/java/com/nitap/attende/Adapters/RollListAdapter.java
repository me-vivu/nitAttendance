package com.nitap.attende.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nitap.attende.ClickListener;
import com.nitap.attende.model.RollListModel;
import com.ttv.facerecog.R;

import java.util.ArrayList;

public class RollListAdapter extends RecyclerView.Adapter<RollListAdapter.ViewHolder> {

    ArrayList<RollListModel> rollList;
    Context context;
    ClickListener clickListener;

    public RollListAdapter(ArrayList<RollListModel> data, Context context, ClickListener clickListener) {
        this.rollList = data;
        this.context = context;
        this.clickListener = clickListener;


    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.roll_list, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        RollListModel roll = rollList.get(position);

        holder.rollNum.setText(roll.getRoll_num());
        holder.giveAttendanceSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickListener.giveAttendanceTo(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return rollList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView rollNum;
        ImageButton giveAttendanceSwitch;

        public ViewHolder(View itemView) {
            super(itemView);

            rollNum = itemView.findViewById(R.id.rollNum);
            giveAttendanceSwitch = itemView.findViewById(R.id.present_switch);
        }
    }


    public void removeItem(int position) {
        rollList.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(RollListModel item, int position) {
        rollList.add(position, item);
        notifyItemInserted(position);
    }

    public ArrayList<RollListModel> getData() {
        return rollList;
    }
}

