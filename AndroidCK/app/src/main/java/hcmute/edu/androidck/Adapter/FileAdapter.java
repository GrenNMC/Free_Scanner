package hcmute.edu.androidck.Adapter;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import hcmute.edu.androidck.Model.File;
import hcmute.edu.androidck.R;

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.FileHolder> implements Filterable {



    private List<File> listFile;
    private List<File> listFileOld;
    private Context context;

    private int idLayout;
    private int positionSelect = -1;
    private OnItemClickListener mListener;


    @NonNull
    @Override
    public FileHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_file, parent,false);
        return new FileHolder(view);
    }
    public FileAdapter(Context context, List<File> listFile){
        this.listFile = listFile;
        this.listFileOld = listFile;
        this.context = context;
    }
    @Override
    public void onBindViewHolder(@NonNull FileHolder holder, int position) {
        File file = listFile.get(position);

        if( file== null)
        {
            return;
        }
        holder.title_file.setText(file.getNameFile());
        Picasso.with(context).load(file.getImageUrl()).placeholder(R.mipmap.ic_launcher).fit()
                .centerCrop()
                .into(holder.image_file);

    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String strSearch = constraint.toString();
                if(strSearch.isEmpty()){
                    listFile = listFileOld;
                }
                else {
                    List<File> list = new ArrayList<>();
                    for(File file : listFileOld){
                        if(file.getNameFile().toLowerCase().contains(strSearch.toLowerCase())){
                            list.add(file);
                        }
                    }
                    listFile = list;

                }
                FilterResults filterResults = new FilterResults();
                filterResults.values  = listFile;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                    listFile = (List<File>) results.values;
                    notifyDataSetChanged();
            }
        };
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
        void onEditClick(int position);
        void onDeleteClick(int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    @Override
    public int getItemCount() {
        if(listFile != null)
        {
            return listFile.size();
        }
        return 0;
    }

    public class FileHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnCreateContextMenuListener,
            MenuItem.OnMenuItemClickListener {

        private TextView title_file;
        private LinearLayout layout_item;
        private ImageView image_file;

        public FileHolder(@NonNull View v){
            super(v);
            title_file = v.findViewById(R.id.title_file);
            layout_item = v.findViewById(R.id.layout_item);
            image_file = v.findViewById(R.id.image_file);

            v.setOnClickListener(this);
            v.setOnCreateContextMenuListener(this);

        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Select Action");
            MenuItem editAction = menu.add(Menu.NONE,1,1,"Edit");
            MenuItem deleteAction = menu.add(Menu.NONE,2,2,"Delete");

            editAction.setOnMenuItemClickListener(this);
            deleteAction.setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onMenuItemClick(@NonNull MenuItem item) {
            if(mListener != null){
                int position = getAdapterPosition();
                if(position != RecyclerView.NO_POSITION)
                {
                    switch (item.getItemId()){
                        case 1:
                            mListener.onEditClick(position);
                            return true;
                        case 2:
                            mListener.onDeleteClick(position);
                            return true;
                    }
                }
            }

            return false;
        }

        @Override
        public void onClick(View v) {
            if(mListener != null){
                int position = getAdapterPosition();
                if(position != RecyclerView.NO_POSITION){
                    mListener.onItemClick(position);
                }
            }
        }
        }



}