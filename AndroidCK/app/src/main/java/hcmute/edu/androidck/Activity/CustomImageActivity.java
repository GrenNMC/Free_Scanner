package hcmute.edu.androidck.Activity;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.OutputStream;
import java.util.Objects;

import hcmute.edu.androidck.R;

public class CustomImageActivity extends AppCompatActivity {

    ImageView imageView;
    FloatingActionButton fa_clear,fa_next;
    private static int REQUEST_CODE =1;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_images);

        imageView = findViewById(R.id.imageview);
        fa_clear = findViewById(R.id.fab_cancel);
        fa_next = findViewById(R.id.fab_next);



        fa_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.Companion.with(CustomImageActivity.this)
                        .galleryOnly()
                        .crop()	    			//Crop image(Optional), Check Customization for more option
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start();
            }
        });
        fa_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(CustomImageActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED)
                {
                    saveImage();
                }
                else {
                    ActivityCompat.requestPermissions(CustomImageActivity.this,new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    },REQUEST_CODE);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                saveImage();
            }
            else {
                Toast.makeText(CustomImageActivity.this,"Please provide required permission",Toast.LENGTH_LONG).show();
            }
        }
    }

    private void saveImage() {

        Uri image;
        ContentResolver contentResolver = getContentResolver();
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.Q){
            image = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
        }
        else
        {
            image = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }

        ContentValues contentValues  = new ContentValues();
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, System.currentTimeMillis()+".jpg");
        contentValues.put(MediaStore.Images.Media.MIME_TYPE,"images/*");
        Uri uri = contentResolver.insert(image,contentValues);
        try {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) imageView.getDrawable();
            Bitmap bitmap = bitmapDrawable.getBitmap();

            OutputStream outputStream = contentResolver.openOutputStream(Objects.requireNonNull(uri));
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
            Objects.requireNonNull(outputStream);
            Toast.makeText(CustomImageActivity.this,"Image saved Succesfully",Toast.LENGTH_LONG).show();
        }
        catch (Exception e)
        {
            Toast.makeText(CustomImageActivity.this,"Image not saved",Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Uri uri = data.getData();
        imageView.setImageURI(uri);
    }
}
