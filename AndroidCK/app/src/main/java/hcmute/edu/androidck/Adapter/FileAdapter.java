package hcmute.edu.androidck.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;



import java.util.ArrayList;

import hcmute.edu.androidck.Model.File;
import hcmute.edu.androidck.R;

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.FileHolder> {



    private ArrayList<File> listFile;
    private Context context;

    private int idLayout;
    private int positionSelect = -1;


    @NonNull
    @Override
    public FileHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_file, parent,false);
        return new FileHolder(view);
    }
    public FileAdapter(Context context,ArrayList<File> listFile){
        this.listFile = listFile;
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


    }

    @Override
    public int getItemCount() {
        if(listFile != null)
        {
            return listFile.size();
        }
        return 0;
    }

    public class FileHolder extends RecyclerView.ViewHolder{

        private TextView title_file;
        private LinearLayout layout_item;

        public FileHolder(@NonNull View v){
            super(v);
            title_file = v.findViewById(R.id.title_file);
            layout_item = v.findViewById(R.id.layout_item);


        }
    }


}