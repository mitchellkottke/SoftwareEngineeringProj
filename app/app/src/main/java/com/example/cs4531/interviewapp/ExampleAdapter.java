package com.example.cs4531.interviewapp;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class ExampleAdapter extends RecyclerView.Adapter<ExampleAdapter.ExampleViewHolder> {
    private ArrayList<ExampleItem> mExampleList;

    public static class ExampleViewHolder extends  RecyclerView.ViewHolder{
        public TextView tvQuestion;
        public TextView tvType;
        public TextView tvReasonReport;
        public TextView tvUser;

        public ExampleViewHolder(View itemView) {
            super(itemView);
            tvQuestion = itemView.findViewById(R.id.textViewQuestion);
            tvType = itemView.findViewById(R.id.textViewType);
            tvReasonReport = itemView.findViewById(R.id.textViewReportReason);
            tvUser = itemView.findViewById(R.id.textViewUser);
        }
    }

    public ExampleAdapter(ArrayList<ExampleItem> exampleList) {
        mExampleList = exampleList;
    }

    @NonNull
    @Override
    public ExampleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.example_item, parent, false);
        ExampleViewHolder evh = new ExampleViewHolder(v);
        return evh;
    }

    @Override
    public void onBindViewHolder(@NonNull ExampleViewHolder holder, int position) {
        ExampleItem currentItem = mExampleList.get(position);

        holder.tvQuestion.setText(currentItem.getmQuestion());
        holder.tvType.setText(currentItem.getmType());
        holder.tvReasonReport.setText(currentItem.getmReasonReport());
        holder.tvUser.setText(currentItem.getmUser());

    }

    @Override
    public int getItemCount() {
        return mExampleList.size();
    }
}
