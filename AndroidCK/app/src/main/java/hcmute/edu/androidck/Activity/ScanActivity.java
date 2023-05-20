package hcmute.edu.androidck.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

import hcmute.edu.androidck.MainActivity;
import hcmute.edu.androidck.R;

public class ScanActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getName();
    private String currentPhotoPath;
    private ImageView btn_scan, btn_gallery, btn_save, btn_back, image_scan;

    private ActivityResultLauncher<Intent> mActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Log.e(TAG,"onActivityResult");
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        if(data == null){
                            return;
                        }
                        Uri uri = data.getData();

                        try{
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                            image_scan.setImageBitmap(bitmap);
                        } catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                }
            }
    );

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan_images);

        btn_scan = findViewById(R.id.btn_scan);
        btn_back = findViewById(R.id.btnBack);
        image_scan = findViewById(R.id.image_scan);
        btn_gallery = findViewById(R.id.btn_gallery);
        btn_save = findViewById(R.id.btn_save);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btn_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String filename = "photo";
                File storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                try {
                    File imageFile = File.createTempFile(filename, ".jpg", storageDirectory);
                    currentPhotoPath = imageFile.getAbsolutePath();
                    Uri imageUri = FileProvider.getUriForFile(ScanActivity.this,
                            "hcmute.edu.androidck.fileprovider", imageFile);
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(intent, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

        btn_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickRequestPermission();
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ContextCompat.checkSelfPermission(ScanActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                    saveImageToGallery();
                } else {
                    askPermission();
                }
            }
        });
    }

    private void saveImageToGallery(){
        Uri image;
        ContentResolver contentResolver = getContentResolver();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            image = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
        }  else{
            image = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, System.currentTimeMillis() + ".jpg");
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "images/*");
        Uri uri = contentResolver.insert(image, contentValues);

        try{
            BitmapDrawable bitmapDrawable = (BitmapDrawable) image_scan.getDrawable();
            Bitmap bitmap = bitmapDrawable.getBitmap();

            OutputStream outputStream = contentResolver.openOutputStream(Objects.requireNonNull(uri));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            Objects.requireNonNull(outputStream);

            image_scan.setImageResource(R.drawable.document);

            Toast.makeText(this, "Saved Successfully", Toast.LENGTH_SHORT).show();
        } catch (Exception e){
            Toast.makeText(this, "Image can not save", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
    private void askPermission(){
        ActivityCompat.requestPermissions(ScanActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
    }
    private void onClickRequestPermission(){
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.R){
            openGallery();
            return;
        }
        if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            openGallery();
        } else {
            String[] permission = {Manifest.permission.READ_EXTERNAL_STORAGE};
            requestPermissions(permission, 10);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 10){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                openGallery();
            }
        }
        if(requestCode == 100){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                saveImageToGallery();
            } else {
                Toast.makeText(this, "Please provide the required permissions", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openGallery(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(intent.ACTION_GET_CONTENT);
        mActivityResultLauncher.launch(intent.createChooser(intent, "Select Image"));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 99 && resultCode == Activity.RESULT_OK){
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            image_scan.setImageBitmap(photo);
        }
        if(requestCode == 1 && resultCode == RESULT_OK){
            Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);
            image_scan.setImageBitmap(bitmap);
        }
    }
}
