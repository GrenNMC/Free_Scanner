package hcmute.edu.androidck.Activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import hcmute.edu.androidck.Model.File;
import hcmute.edu.androidck.R;

public class AddNewImage extends AppCompatActivity {


    private static final int PICK_IMAGE_REQUEST = 1;
    Button btn_choose, btn_upload,btn_back;
    ImageView imageView;
    EditText editName;
    private Uri uri;
    ProgressBar mProgress;
    private StorageReference mStorageRef;
    private DatabaseReference mDataRef;

    private StorageTask mUploadTask;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new_image);
        btn_back = findViewById(R.id.btn_back);
        editName = findViewById(R.id.file_name);
        btn_choose = findViewById(R.id.btn_chooseFile);
        btn_upload = findViewById(R.id.btn_upload);
        mProgress = findViewById(R.id.progress_bar);
        imageView = findViewById(R.id.view_iamge);
        mStorageRef = FirebaseStorage.getInstance().getReference("files");
        mDataRef = FirebaseDatabase.getInstance().getReference("files");

        btn_choose.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
                   openFileChoose();
           }
        });
        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mUploadTask != null && mUploadTask.isInProgress()){
                    Toast.makeText(AddNewImage.this, "Upload in progress", Toast.LENGTH_SHORT).show();
                }
                else {
                    uploadFile();
                }
            }
        });
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddNewImage.this,FileActivity.class);
                startActivity(intent);
            }
        });
    }
    private String getFileExtension(Uri uri){
        ContentResolver resolver = getContentResolver();
        MimeTypeMap map = MimeTypeMap.getSingleton();
        return map.getExtensionFromMimeType(resolver.getType(uri));
    }
    private void uploadFile() {
        if(uri != null){
            StorageReference storageReference = mStorageRef.child(System.currentTimeMillis() +"."+getFileExtension(uri));
            mUploadTask = storageReference.putFile(uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mProgress.setProgress(0);
                                }
                            },500);
                            Toast.makeText(AddNewImage.this, "Upload Success", Toast.LENGTH_SHORT).show();
                            Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!urlTask.isSuccessful());
                            Uri downloadUrl = urlTask.getResult();
                            final String sdownload_url = String.valueOf(downloadUrl);
                            File file = new File(editName.getText().toString().trim(),sdownload_url);
                            String fileId = mDataRef.push().getKey();
                            mDataRef.child(fileId).setValue(file);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddNewImage.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            double progress = (100.0 * snapshot.getBytesTransferred()/snapshot.getTotalByteCount());
                            mProgress.setProgress((int) progress);
                        }
                    });

        }else {
            Toast.makeText(this,"No file selected",Toast.LENGTH_SHORT).show();
        }

    }

    private void openFileChoose() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
        && data!= null && data.getData() != null){

            uri = data.getData();
            Picasso.with(this).load(uri).into(imageView);
        }
    }
}
