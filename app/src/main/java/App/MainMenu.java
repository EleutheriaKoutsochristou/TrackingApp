package App;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.trackingapp.R;
import com.google.android.material.snackbar.Snackbar;

public class MainMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
    }

    public void launchgpxpass(View v){

        Intent i=new Intent(this, GpxPass.class);
        startActivity(i);
    }

    public void launchresults(View v){

        Intent i=new Intent(this, UserResults.class);
        startActivity(i);
    }
}