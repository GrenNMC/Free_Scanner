package hcmute.edu.androidck;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import hcmute.edu.androidck.Activity.FileActivity;
import hcmute.edu.androidck.Activity.FolderActivity;

public class MainActivity extends AppCompatActivity {


    private Button btn_allFile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trangchu);
        btn_allFile = findViewById(R.id.btn_allfile);

        btn_allFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FolderActivity.class);
//                Bundle bundle = new Bundle();
//                intent.putExtras(bundle);
                startActivity(intent);
            }
        });


    }
}