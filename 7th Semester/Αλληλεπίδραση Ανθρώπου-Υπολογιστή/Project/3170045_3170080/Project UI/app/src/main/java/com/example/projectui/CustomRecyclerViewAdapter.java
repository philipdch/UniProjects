package com.example.projectui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CustomRecyclerViewAdapter extends RecyclerView.Adapter<CustomRecyclerViewAdapter.ViewHolder> {

    private List<Menu> menuData = new ArrayList<>();
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Context context;

    // data is passed into the constructor
    CustomRecyclerViewAdapter(Context context, List<Menu> menuList) {
        this.mInflater = LayoutInflater.from(context);
        this.menuData = menuList;
        this.context = context;
    }

    // inflates the cell layout from xml when needed
    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.menu_button_row, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each cell
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        //TODO: set menu image, name
        holder.text.setText(menuData.get(position).getName());
        holder.menuButton.setImageResource(menuData.get(position).getImageId());
    }

    // total number of cells
    @Override
    public int getItemCount() {
        return menuData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView text;
        ImageButton menuButton;
        ImageButton deleteButton;

        ViewHolder(View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.menu_name);
            menuButton = itemView.findViewById(R.id.menu_button);
            deleteButton = itemView.findViewById(R.id.menu_delete_button);
            menuButton.setSelected(false);
            menuButton.setOnClickListener(this);
            deleteButton.setOnClickListener(this);
            text.setOnClickListener(this);
            text.setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {
                    // TODO Auto-generated method stub
                    return false;
                }
            });
        }

        @Override
        public void onClick(View view) {
            if(view.equals(deleteButton)){
                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                builder.setTitle("Confirm");
                builder.setMessage("Are you sure?");

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        int pos = getAdapterPosition();
                        menuData.remove(pos);
                        notifyItemRemoved(pos);
                        notifyItemRangeChanged(pos, menuData.size());
                        dialog.dismiss();
                    }
                });

                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // Do nothing
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
            } else if(mClickListener != null) {
                mClickListener.onItemClick(view, getAdapterPosition());
            }
        }
    }

    // convenience method for getting data at click position
    Menu getItem(int id) {
        return menuData.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }


}
