package hcmute.edu.androidck.Activity;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextDetector;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;
import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Objects;

import hcmute.edu.androidck.R;

public class ScanActivity extends AppCompatActivity {

    public static final String TESS_DATA = "/tessdata";
    private ImageView btn_scan, btn_copy, btn_save, btn_back, image_scan;
    private TextView tv_result;
    private TessBaseAPI tessBaseAPI;
    private Bitmap bitmap;
    private static final int REQUEST_CAMERA_CODE = 100;
    private Spinner spinner;
    private int model_kit = 0;
    private TextRecognizer textRecognizer;

    private String[] models = {"Play service ML kit (English, Vietnamese,...)","Firebase ML kit (English, Vietnamese,...)", "OCR Tess-two (Tesseract) (English)"};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan_images);

        btn_scan = findViewById(R.id.btn_scan);
        btn_back = findViewById(R.id.btnBack);
        image_scan = findViewById(R.id.image_scan);
        btn_copy = findViewById(R.id.button_copy);
        btn_save = findViewById(R.id.btn_save);
        tv_result = findViewById(R.id.tv_result);

        setSpinner();

        if (ContextCompat.checkSelfPermission(ScanActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(ScanActivity.this, new String[] {
                    Manifest.permission.CAMERA
            }, REQUEST_CAMERA_CODE);
        }

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        btn_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tv_result.getText().equals("")){
                    Toast.makeText(ScanActivity.this, "No text for copy!", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(ScanActivity.this, "Success copied to clipboard!", Toast.LENGTH_SHORT).show();
                    ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clipData = ClipData.newPlainText("Data", tv_result.getText().toString());
                    clipboardManager.setPrimaryClip(clipData);
                }
            }
        });

        btn_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.Companion.with(ScanActivity.this)
                        .cameraOnly()
                        .crop()
                        .compress(1024)
                        .maxResultSize(1080,1080)
                        .start();

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

            Toast.makeText(this, "Saved Successfully", Toast.LENGTH_SHORT).show();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    private void askPermission(){
        ActivityCompat.requestPermissions(ScanActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 100){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                saveImageToGallery();
            } else {
                Toast.makeText(this, "Please provide the required permissions", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK){
            Uri resultUri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),resultUri);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            image_scan.setImageBitmap(bitmap);
            getTextFromImage(bitmap);
        }
    }
    private void getTextFromImage(Bitmap bitmap){
        prepareTessData();
        switch (model_kit){
            case 0:{
                Toast.makeText(ScanActivity.this, "Using model Play service ML kit!", Toast.LENGTH_SHORT).show();
                InputImage image = InputImage.fromBitmap(bitmap, 0);
                textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

                Task<Text> taskResult = textRecognizer.process(image)
                        .addOnSuccessListener(new OnSuccessListener<Text>() {
                            @Override
                            public void onSuccess(Text text) {
                                String resultText = text.getText();
                                tv_result.setText(resultText);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ScanActivity.this, "Recognition Fail!!", Toast.LENGTH_SHORT).show();
                                tv_result.setText("Recognition Fail!!");
                            }
                        });
                break;
            }
            case 1:{
                FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
                FirebaseVisionTextDetector detector = FirebaseVision.getInstance().getVisionTextDetector();
                detector.detectInImage(image).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                    @Override
                    public void onSuccess(FirebaseVisionText firebaseVisionText) {
                        recognitionText(firebaseVisionText);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ScanActivity.this, "Recognition Fail!!", Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            }
            case 2:{
                Toast.makeText(ScanActivity.this, "OCR Tess-two model (English)", Toast.LENGTH_SHORT).show();
                try{
                    String result = getOCRText(bitmap);
                    tv_result.setText(result);
                }catch (Exception e){
                    Log.e(TAG, e.getMessage());
                }
                break;
            }
        }
    }

    private void setSpinner(){
        spinner = findViewById(R.id.spinner1);

        ArrayAdapter adapter = new ArrayAdapter(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, models);
        adapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                model_kit = i;
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                model_kit = 0;
            }
        });
    }
    private void recognitionText(FirebaseVisionText text){
        List<FirebaseVisionText.Block> blocks = text.getBlocks();
        if (blocks.size() == 0) {
            Toast.makeText(this, "No text for recognition!", Toast.LENGTH_SHORT).show();
            return;
        }
        for (FirebaseVisionText.Block block : text.getBlocks()) {
            String txt = block.getText();
            tv_result.setText(txt);
        }
    }
    public String getOCRText(Bitmap bitmap){
        try{
            tessBaseAPI = new TessBaseAPI();
        }catch (Exception e){
            Log.e(TAG, e.getMessage());
        }
        String dataPath = getExternalFilesDir("/").getPath() + "/";
        //String dataPath = MainApplication.instance.getTessDataParentDirectory();
        tessBaseAPI.init(dataPath, "eng");
        tessBaseAPI.setImage(bitmap);
        String retStr = "No result";
        try{
            retStr = tessBaseAPI.getUTF8Text();
        }catch (Exception e){
            Log.e(TAG, e.getMessage());
        }
        tessBaseAPI.end();
        return retStr;
    }
    private void prepareTessData(){
        try{
            File dir = getExternalFilesDir(TESS_DATA);
            if(!dir.exists()){
                if (!dir.mkdir()) {
                    Toast.makeText(getApplicationContext(), "The folder " + dir.getPath() + "was not created", Toast.LENGTH_SHORT).show();
                }
            }
            String fileList[] = getAssets().list("");
            for(String fileName : fileList){
                String pathToDataFile = dir + "/" + fileName;
                if(!(new File(pathToDataFile)).exists()){
                    InputStream in = getAssets().open(fileName);
                    OutputStream out = new FileOutputStream(pathToDataFile);
                    byte [] buff = new byte[1024];
                    int len ;
                    while(( len = in.read(buff)) > 0){
                        out.write(buff,0,len);
                    }
                    in.close();
                    out.close();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }
}
