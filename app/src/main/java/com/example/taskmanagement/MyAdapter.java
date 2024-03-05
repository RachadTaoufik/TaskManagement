package com.example.taskmanagement;

import static android.content.Intent.EXTRA_TEXT;
import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.bumptech.glide.Glide;
import model.Tache;


import java.util.LinkedList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private LinkedList<Tache> taches;
    private  Context context;
    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(LinkedList<Tache> taches, Context context) {
        this.taches = new LinkedList<Tache>() ;
        this.taches.addAll(taches);
        this.context=context;
    }
    // Create new views (invoked by the layout manager)
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
// create a new view
        View itemLayoutView =
                LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item_layout,
                        parent, false);
        MyViewHolder vh = new MyViewHolder(itemLayoutView);
        return vh;
    }
    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
    // - get element from your dataset at this position
    // - replace the contents of the view with that element
        holder.task=taches.get(position);
        holder.title.setText(taches.get(position).getTitle());
    // Reference to an image file in Cloud Storage
        StorageReference storageReference =
                FirebaseStorage.getInstance().getReferenceFromUrl(taches.get(position).getImg());
    // Download directly from StorageReference using Glide
    // (See MyAppGlideModule for Loader registration)
        Glide.with(context /* context */)
                .load(storageReference)
                .into(holder.img);
    }
    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return taches.size();
    }
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class MyViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener, View.OnLongClickListener {

        Tache task;
        public TextView title;
        public ImageView img;
        // Context is a reference to the activity that contain the the recycler view
        public MyViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            title =itemLayoutView.findViewById(R.id.title);
            img= itemLayoutView.findViewById(R.id.img);
            itemLayoutView.setOnClickListener(this);
            itemLayoutView.setOnLongClickListener(this);


        }
        @Override
        public void onClick(View v) {
            Intent MyIntent= new Intent(context, TaskActivity.class);
            MyIntent.putExtra("task", task);
            context.startActivity(MyIntent);
        }

        @Override
        public boolean onLongClick(View view) {
            PopupMenu popup = new PopupMenu(context, view);
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.tache_menu, popup.getMenu());

            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                public boolean onMenuItemClick(MenuItem item) {
                    if (item.getItemId()==R.id.afficher) {
                        Intent MyIntent= new Intent(context, TaskActivity.class);
                        MyIntent.putExtra("task", task);
                        context.startActivity(MyIntent);
                    }

                    if (item.getItemId()==R.id.partager) {
                        Intent MyIntent= new Intent();
                        MyIntent.setAction(Intent.ACTION_SEND);
                        MyIntent.putExtra(EXTRA_TEXT, task.getDescription());
                        MyIntent.setType("text/plain");
                        context.startActivity(MyIntent);

                    }

                    return true;
                }
            });

            popup.show();
            return true;
        }

    }
}
