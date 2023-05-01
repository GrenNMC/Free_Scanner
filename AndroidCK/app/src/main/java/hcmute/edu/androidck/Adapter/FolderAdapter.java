package hcmute.edu.androidck.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;




import java.util.ArrayList;

import hcmute.edu.androidck.Activity.FileActivity;
import hcmute.edu.androidck.Model.Folder;
import hcmute.edu.androidck.R;

public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.FolderHolder> {



    private ArrayList<Folder> listFolder;
    private Context context;

    private int idLayout;
    private int positionSelect = -1;


    @NonNull
    @Override
    public FolderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_folder, parent,false);
        return new FolderHolder(view);
    }
    public FolderAdapter(Context context,ArrayList<Folder> listFolder){
        this.listFolder = listFolder;
        this.context = context;
    }
    @Override
    public void onBindViewHolder(@NonNull FolderHolder holder, int position) {
        Folder folder = listFolder.get(position);

        if(folder == null)
        {
            return;
        }
        holder.title_folder.setText(folder.getNameFolder());
        holder.layout_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, FileActivity.class);
//                Bundle bundle = new Bundle();
//                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        if(listFolder != null)
        {
            return listFolder.size();
        }
        return 0;
    }

    public class FolderHolder extends RecyclerView.ViewHolder{

        private TextView title_folder;
        private LinearLayout layout_item;

        public FolderHolder(@NonNull View v){
            super(v);
            title_folder = v.findViewById(R.id.title_folder);
            layout_item = v.findViewById(R.id.layout_item);


        }
    }


}