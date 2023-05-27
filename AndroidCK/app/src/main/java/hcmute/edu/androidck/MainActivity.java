package hcmute.edu.androidck;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


import hcmute.edu.androidck.Activity.CustomImageActivity;
import hcmute.edu.androidck.Activity.FileActivity;
import hcmute.edu.androidck.Activity.Ocr_text_recognition_activity;
import hcmute.edu.androidck.Activity.ScanActivity;

public class MainActivity extends AppCompatActivity {


    private Button btn_allFile;
    private Button btn_ImageToText,btn_scan,btn_customImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trangchu);
        btn_allFile = findViewById(R.id.btn_allfile);
        btn_ImageToText= findViewById(R.id.btn_ImageToText);
        btn_scan = findViewById(R.id.btn_scan);
        btn_customImg = findViewById(R.id.btn_split);

        btn_ImageToText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Ocr_text_recognition_activity.class);
                startActivity(intent);
            }
        });

        btn_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ScanActivity.class);
                startActivity(intent);

            }
        });

        btn_customImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CustomImageActivity.class);
                startActivity(intent);
            }
        });


        btn_allFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FileActivity.class);
//                Bundle bundle = new Bundle();
//                intent.putExtras(bundle);
                startActivity(intent);
            }
        });


    }
}