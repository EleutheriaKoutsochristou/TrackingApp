package App;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.View;
import android.os.Bundle;

import com.example.trackingapp.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void launchmainmenu(View v) {

        Intent i = new Intent(this, MainMenu.class);
        startActivity(i);
    }
}