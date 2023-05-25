package hcmute.edu.androidck.Activity;

import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

import hcmute.edu.androidck.R;

public class Ocr_text_recognition_activity extends AppCompatActivity {

    public static final String TESS_DATA = "/tessdata";
    private TessBaseAPI tessBaseAPI;
    private ImageView imageview;
    private ImageView btnBack;
    private ImageView btn_clear;
    private ImageView btn_predict;
    private ImageView btn_copy;
    private TextView textResult;
    private Spinner spinner;
    private int model_kit = 0;
    private Uri uri;
    private TextRecognizer textRecognizer;
    private String[] models = {"Play service ML kit (English, Vietnamese,...)", "OCR Tess-two (Tesseract) (English)", "Tesseract OCR (Vietnamese)"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr_text_recognition);
        getPermission();
        setSpinner();
        textResult = findViewById(R.id.textResult);


        btn_predict = findViewById(R.id.btn_predict);
        btn_predict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prepareTessData();
                if(uri == null){
                    Toast.makeText(Ocr_text_recognition_activity.this, "No image!!", Toast.LENGTH_SHORT).show();
                }
                else {
                    switch (model_kit){
                        case 0:{
                            try {
                                Toast.makeText(Ocr_text_recognition_activity.this, "Using model Play service ML kit!", Toast.LENGTH_SHORT).show();
                                InputImage image = InputImage.fromFilePath(view.getContext(), uri);
                                textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

                                Task<Text> taskResult = textRecognizer.process(image)
                                        .addOnSuccessListener(new OnSuccessListener<Text>() {
                                            @Override
                                            public void onSuccess(Text text) {
                                                String resultText = text.getText();
                                                textResult.setText(resultText);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(Ocr_text_recognition_activity.this, "Recognition Fail!!", Toast.LENGTH_SHORT).show();
                                                textResult.setText("Recognition Fail!!");
                                            }
                                        });
                            } catch (IOException e) {
                                Toast.makeText(Ocr_text_recognition_activity.this, "Recognition Fail!!", Toast.LENGTH_SHORT).show();
                                throw new RuntimeException(e);
                            }
                            break;
                        }
                        case 1:{
                            Toast.makeText(Ocr_text_recognition_activity.this, "OCR Tess-two model (English)", Toast.LENGTH_SHORT).show();
                            try{
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(view.getContext().getContentResolver(), uri);
                                String result = getOCRText(bitmap,true);
                                textResult.setText(result);
                            }catch (Exception e){
                                Log.e(TAG, e.getMessage());
                            }
                            break;
                        }
                        case 2:{
                            Toast.makeText(Ocr_text_recognition_activity.this, "OCR Tess-two model (Vietnamese)", Toast.LENGTH_SHORT).show();
                            try{
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(view.getContext().getContentResolver(), uri);
                                String result = getOCRText(bitmap,false);
                                textResult.setText(result);
                            }catch (Exception e){
                                Log.e(TAG, e.getMessage());
                            }
                            break;
                        }
                    }
                }
            }
        });


        btn_copy = findViewById(R.id.btn_copy);
        btn_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(textResult.getText().equals("")){
                    Toast.makeText(Ocr_text_recognition_activity.this, "No text for copy!", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(Ocr_text_recognition_activity.this, "Success copied to clipboard!", Toast.LENGTH_SHORT).show();
                    ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clipData = ClipData.newPlainText("Data", textResult.getText().toString());
                    clipboardManager.setPrimaryClip(clipData);
                }
            }
        });

        btn_clear = findViewById(R.id.btn_clear);
        btn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textResult.setText("");
                imageview.setImageURI(null);
                uri = null;
            }
        });

        imageview = findViewById(R.id.imageview);
        imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                activityResult.launch(intent);
            }
        });

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
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

    public String getOCRText(Bitmap bitmap,boolean isEnglish){
        try{
            tessBaseAPI = new TessBaseAPI();
        }catch (Exception e){
            Log.e(TAG, e.getMessage());
        }
        String dataPath = getExternalFilesDir("/").getPath() + "/";
        //String dataPath = MainApplication.instance.getTessDataParentDirectory();
        if(isEnglish){
            tessBaseAPI.init(dataPath, "eng");
        }
        else {
            tessBaseAPI.init(dataPath, "vie");
        }
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

    public void setSpinner(){
        spinner = findViewById(R.id.spinner);

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

    private ActivityResultLauncher<Intent> activityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        if(result.getData() !=null){
                            uri = result.getData().getData();
                            imageview.setImageURI(uri);
                        }
                    }
                    else {
                        Toast.makeText(Ocr_text_recognition_activity.this, "No image ... ", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    public void getPermission(){
        if(checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(Ocr_text_recognition_activity.this, new String[]{Manifest.permission.CAMERA}, 11);
        }

        if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(Ocr_text_recognition_activity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 11);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 11){
            if(grantResults.length>0){
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    this.getPermission();
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}