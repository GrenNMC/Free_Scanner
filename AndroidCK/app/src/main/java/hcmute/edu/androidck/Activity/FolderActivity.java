package hcmute.edu.androidck.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;



import java.util.ArrayList;
import java.util.List;

import hcmute.edu.androidck.Adapter.FolderAdapter;
import hcmute.edu.androidck.Model.Folder;
import hcmute.edu.androidck.R;

public class FolderActivity extends AppCompatActivity {


    private RecyclerView recyclerView;
    private ImageView btn_back;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_folder);
        btn_back = findViewById(R.id.btn_back);
        recyclerView = findViewById(R.id.rcv_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);

        FolderAdapter FolderAdapter = new FolderAdapter((Context) this, (ArrayList<Folder>) getListFolder());
        recyclerView.setAdapter(FolderAdapter);


        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }


    private List<Folder> getListFolder() {
        List<Folder> list = new ArrayList<>();
        list.add(new Folder(0,"Folder 1"));
        list.add(new Folder(1,"Folder 2"));
        list.add(new Folder(2,"Folder 3"));


        return list;
    }
}