package com.example.meek.myapplication;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private JSONArray jsonArray;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;

        public ViewHolder(ViewGroup v) {
            super(v);
            mTextView = v.findViewById(R.id.textView);
        }
    }

    public MyAdapter(JSONArray jsonArray) {
        this.jsonArray = jsonArray;
    }

    public void setJsonArray(JSONArray jsonArray) {
        this.jsonArray = jsonArray;
    }

    @Override
    public int getItemCount() {
        return jsonArray.length();
    }

    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewGroup v = (ViewGroup) LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        try {
            JSONObject json = jsonArray.getJSONObject(position);
            String name = json.getString("name");
            holder.mTextView.setText(name);
        } catch (Exception e) {
            Log.e("MyAdaptor", "onBindViewHolder", e);
        }
    }
}