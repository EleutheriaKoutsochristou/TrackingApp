package App;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.trackingapp.R;
import java.util.Scanner;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class UserResults extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_results);

        try {
            Scanner in = new Scanner(new File(Environment.getExternalStorageDirectory().toString() + "/results.txt"));
            String x = in.nextLine();
            TextView label = findViewById(R.id.label);
            label.setText(x);

            x = in.nextLine();
            double y = (double) Math.round(Double.parseDouble(x) * 1000) / 1000;
            x = "Distance Travelled: " + y + " km";
            label = findViewById(R.id.textView2);
            label.setText(x);

            x = in.nextLine();
            y = (double) Math.round((Double.parseDouble(x) * 60) * 100) / 100;
            x = "Time: " + y + " minutes";
            label = findViewById(R.id.textView3);
            label.setText(x);

            x = in.nextLine();
            y = (double) Math.round(Double.parseDouble(x) * 100) / 100;
            x = "Ascend: " + y + " meters";
            label = findViewById(R.id.textView4);
            label.setText(x);

            x = in.nextLine();
            y = (double) Math.round(Double.parseDouble(x) * 100) / 100;
            x = "Average Speed: " + y + " km/h";
            label = findViewById(R.id.textView5);
            label.setText(x);

            x = in.nextLine();
            y = (double) Math.round(Double.parseDouble(x) * 1000) / 1000;
            x = "Total Distance: " + y + " km";
            label = findViewById(R.id.textView7);
            label.setText(x);

            x = in.nextLine();
            double z = (double) Math.round(Double.parseDouble(x) * 1000) / 1000;
            double a = (z - y) / y * 100;
            a = (double) Math.round(a * 100) / 100;
            if(a < 0){
                x = "Your Total Distance is " + -a + "% higher than the average";
            } else if(a > 0) {
                x = "Your Total Distance is " + a + "% lower than the average";
            }else{
                x = "Your Total Distance is on the average";
            }
            label = findViewById(R.id.textView10);
            label.setText(x);

            x = in.nextLine();
            y = (double) Math.round(Double.parseDouble(x)  * 100) / 100;
            x = "Total Exercise Time: " + y + " hours";
            label = findViewById(R.id.textView8);
            label.setText(x);

            x = in.nextLine();
            z = (double) Math.round(Double.parseDouble(x) * 100) / 100;
            a = (z - y) / y * 100;
            a = (double) Math.round(a * 100) / 100;
            if(a < 0){
                x = "Your Total Distance is " + -a + "% higher than the average";
            } else if(a > 0) {
                x = "Your Total Distance is " + a + "% lower than the average";
            }else{
                x = "Your Total Distance is on the average";
            }
            label = findViewById(R.id.textView11);
            label.setText(x);

            x = in.nextLine();
            y = (double) Math.round(Double.parseDouble(x) * 100) / 100;
            x = "Total Ascend: " + y + " meters";
            label = findViewById(R.id.textView9);
            label.setText(x);

            x = in.nextLine();
            z = (double) Math.round(Double.parseDouble(x) * 100) / 100;
            a = (z - y) / y * 100;
            a = (double) Math.round(a * 100) / 100;
            if(a < 0){
                x = "Your Total Distance is " + -a + "% higher than the average";
            } else if(a > 0) {
                x = "Your Total Distance is " + a + "% lower than the average";
            }else{
                x = "Your Total Distance is on the average";
            }
            label = findViewById(R.id.textView12);
            label.setText(x);


        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }


    }
    public void launchbackmainmenu(View v){

        Intent i=new Intent(this, MainMenu.class);
        startActivity(i);
    }
}