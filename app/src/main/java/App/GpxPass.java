package App;

import static java.lang.Thread.sleep;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;


import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trackingapp.R;
import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Objects;

public class GpxPass extends AppCompatActivity {

    int requestcode = 1;

    Handler myHandler;

    Route route;
    User user;
    MasterData data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gpx_pass);

        myHandler = new Handler(Looper.getMainLooper(),
                new Handler.Callback() {
                    @Override
                    public boolean handleMessage(@NonNull Message message) {

                        route = (Route) message.getData().getSerializable("route");
                        user = (User) message.getData().getSerializable("user");
                        data = (MasterData) message.getData().getSerializable("data");

                        try {
                            FileOutputStream fout = new FileOutputStream(new File(Environment.getExternalStorageDirectory().toString() + "/results.txt"));
                            String x = route.getRouteUser() + "\n";
                            fout.write(x.getBytes());
                            Double y = route.getTotalDistance();
                            x = y + "\n";
                            fout.write(x.getBytes());
                            y = route.getTotalTime();
                            x = y + "\n";
                            fout.write(x.getBytes());
                            y =route.getTotalAscend();
                            x = y + "\n";
                            fout.write(x.getBytes());
                            y =route.getAverageSpeed();
                            x = y + "\n";
                            fout.write(x.getBytes());
                            y = user.getTotalDistance();
                            x = y + "\n";
                            fout.write(x.getBytes());
                            y = data.getAverageUserDistance();
                            x = y + "\n";
                            fout.write(x.getBytes());
                            y = user.getTotalExersciseTime();
                            x = y + "\n";
                            fout.write(x.getBytes());
                            y = data.getAvetageUserTime();
                            x = y + "\n";
                            fout.write(x.getBytes());
                            y = user.getTotalElevation();
                            x = y + "\n";
                            fout.write(x.getBytes());
                            y = data.getAverageUserAscend();
                            x = y + "\n";
                            fout.write(x.getBytes());

                            fout.close();
                        }
                        catch (IOException e) {
                            Log.e("Exception", "File write failed: " + e.toString());
                        }

                        if(ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED){
                            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
                        }

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            CharSequence name = getString(R.string.channel_name);
                            int importance = NotificationManager.IMPORTANCE_DEFAULT;
                            NotificationChannel channel = new NotificationChannel("1", name, importance);
                            NotificationManager notificationManager = getSystemService(NotificationManager.class);
                            notificationManager.createNotificationChannel(channel);
                        }

                        Intent intent = new Intent(getBaseContext(), UserResults.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(getBaseContext(), "1")
                                .setSmallIcon(R.drawable.eo_circle_green_checkmark_removebg_preview)
                                .setContentTitle("RESULTS!")
                                .setContentText("Your Results Are Ready! Tap to View Them.")
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                .setContentIntent(pendingIntent)
                                .setAutoCancel(true);
                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getBaseContext());
                        notificationManager.notify(1, builder.build());
                        return true;
                    }
                });
    }

    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);
        if (requestCode == requestcode && resultCode == GpxPass.RESULT_OK) {
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                try {
                    String contents = readTextFromUri(uri);
                    byte[] content_bytes = contents.getBytes();
                    SendDataToServer send_data = new SendDataToServer(content_bytes, myHandler);
                    send_data.start();
                    Snackbar.make(findViewById(R.id.GpxPassLayout), R.string.File_sent, Snackbar.LENGTH_LONG)
                                    .show();
                }catch (IOException ioException) {
                    ioException.printStackTrace();
                }

            }
        }
    }

    private String readTextFromUri(Uri uri) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        try (InputStream inputStream =
                     getContentResolver().openInputStream(uri);
             BufferedReader reader = new BufferedReader(
                     new InputStreamReader(Objects.requireNonNull(inputStream)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
        }
        return stringBuilder.toString();
    }



    public void choosegpx(View v){

        if(ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.MANAGE_EXTERNAL_STORAGE}, 1);
        }

         Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
         i.setType("*/*");
         startActivityForResult(i,requestcode);
    }

    public void launchbackmainmenu(View v){
        finish();
    }

}
