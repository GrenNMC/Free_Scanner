package hcmute.edu.androidck.Activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import android.widget.SearchView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hcmute.edu.androidck.Adapter.FileAdapter;
import hcmute.edu.androidck.Model.File;
import hcmute.edu.androidck.R;

public class FileActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener,FileAdapter.OnItemClickListener
{


    private RecyclerView recyclerView;

    private ImageView imageView;
    private ImageView btn_back;
    private Uri uri;
    private List<File> listFile;
    private FirebaseStorage mStorage;

    private DatabaseReference mDataRef;
    private ValueEventListener mDBListener;
    private FileAdapter mAdapter;
    androidx.appcompat.widget.SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_file);
        btn_back = findViewById(R.id.btn_back);
        recyclerView = findViewById(R.id.rcv_view_list_file);

        searchView =(androidx.appcompat.widget.SearchView) findViewById(R.id.search_bar_edit);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);

        listFile = new ArrayList<>();
        mAdapter =  new FileAdapter(FileActivity.this,  listFile);
        recyclerView.setAdapter(mAdapter);


        mAdapter.setOnItemClickListener(FileActivity.this);
        mStorage =FirebaseStorage.getInstance();
        mDataRef = FirebaseDatabase.getInstance().getReference("files");

        mDBListener =  mDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                    listFile.clear();
                    for(DataSnapshot postSnapshot: snapshot.getChildren()){
                        File file = postSnapshot.getValue(File.class);
                        file.setKey(postSnapshot.getKey());
                        listFile.add(file);
                    }
                    mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(FileActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.getFilter().filter(newText);

                return false;
            }
        });
    }
    public void showPopup(View v){
        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.action_view);
        popupMenu.show();
    }



    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){
            case R.id.item_add:
                Intent intent = new Intent(FileActivity.this,AddNewImage.class);
                startActivity(intent);
                return true;
            case R.id.sub_item2:
                mDataRef = FirebaseDatabase.getInstance().getReference("files");
                Query query = mDataRef.orderByChild("nameFile");
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        listFile.clear();
                        for(DataSnapshot postSnapshot: snapshot.getChildren()){
                            File file = postSnapshot.getValue(File.class);
                            file.setKey(postSnapshot.getKey());
                            listFile.add(file);
                        }
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(FileActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
                return true;
            case R.id.sub_item1:
                mDataRef = FirebaseDatabase.getInstance().getReference("files");
                Query queryDesc = mDataRef.orderByValue().limitToLast(listFile.size());
                queryDesc.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        listFile.clear();
                        for(DataSnapshot postSnapshot: snapshot.getChildren()){
                            File file = postSnapshot.getValue(File.class);
                            file.setKey(postSnapshot.getKey());
                            listFile.add(file);
                        }
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(FileActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

                return true;
            default:
                return false;

        }
    }

    @Override
    public void onItemClick(int position) {
            Toast.makeText(this,"Normal click "+position,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeleteClick(int position) {
        File selectItem = listFile.get(position);
        final String selectKey = selectItem.getKey();


        StorageReference imageRef = mStorage.getReferenceFromUrl(selectItem.getImageUrl());
        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                mDataRef.child(selectKey).removeValue();
                Toast.makeText(FileActivity.this, "Item deleted", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onEditClick(int position) {
        File selectItem = listFile.get(position);
        final String selectKey = selectItem.getKey();


        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.show_dialog);

        final EditText editName = dialog.findViewById(R.id.file_name);
        final Button btn_update = dialog.findViewById(R.id.btn_update);

        editName.setText(selectItem.getNameFile());
        dialog.show();
        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String,Object> map = new HashMap<>();
                map.put("nameFile",editName.getText().toString());

                FirebaseDatabase.getInstance().getReference().child("files")
                        .child(selectKey).updateChildren(map)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                dialog.dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                dialog.dismiss();
                            }
                        });
            }
        });




    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDataRef.removeEventListener(mDBListener);
    }
}