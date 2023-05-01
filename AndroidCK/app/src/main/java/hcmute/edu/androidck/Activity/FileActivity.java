package hcmute.edu.androidck.Activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;
import java.util.List;

import hcmute.edu.androidck.Adapter.FileAdapter;
import hcmute.edu.androidck.Model.File;
import hcmute.edu.androidck.R;

public class FileActivity extends AppCompatActivity {


    private RecyclerView recyclerView;

    private ImageView btn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_file);
        btn_back = findViewById(R.id.btn_back);
        recyclerView = findViewById(R.id.rcv_view_list_file);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);

        FileAdapter fileAdapter = new FileAdapter((Context) this, (ArrayList<File>) getListFile());
        recyclerView.setAdapter(fileAdapter);


        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }


    private List<File> getListFile() {
        List<File> list = new ArrayList<>();
        list.add(new File(0,"filename.jpx",1));
        list.add(new File(0,"filename.jpx",1));
        list.add(new File(0,"filename.jpx",1));

        return list;
    }
}