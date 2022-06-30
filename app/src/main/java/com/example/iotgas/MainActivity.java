package com.example.iotgas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;

import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final int NOTIFICATION_ID = 1;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference("Gas");

    TextView CanhBao, KhiGas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CanhBao = findViewById(R.id.CanhBao);
        KhiGas = findViewById(R.id.KhiGas);

        // Read from the database
        DatabaseReference ref = database.getReference("Gas");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String value = snapshot.getValue().toString();
                int khigas = Integer.parseInt(value);
                if (khigas > 400) {
                    KhiGas.setText("Khí gas: "+value);
                    DatabaseReference data = database.getReference("Canh Bao");
                    data.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String canhbao = snapshot.getValue().toString();
                            CanhBao.setText(canhbao);
                            Toast.makeText(MainActivity.this, "Cảnh Báo", Toast.LENGTH_SHORT).show();
                            sendNotifications();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(MainActivity.this, "Đã xảy ra lỗi "+ error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else {
                    KhiGas.setText("Khí gas hiện tại: "+value);
                    CanhBao.setText("");
                }
            }

            private void sendNotifications() {
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
                Uri sound = Uri.parse("android.resource://" + getPackageName() + "/" +R.raw.notification);
                Notification notification = new NotificationCompat.Builder(MainActivity.this, MyApplication.CHANNEL_ID)
                        .setContentTitle("Cảnh Báo")
                        .setContentText("Khí Gas đã vượt quá mức 400 rất nguy hiểm" + "\n" +
                                        "XIN HÃY KIỂM TRA NGAY!")
                        .setSmallIcon(R.drawable.ic_warning)
                        .setLargeIcon(bitmap)
                        .setSound(sound)
                        .build();
                NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(MainActivity.this);
                notificationManagerCompat.notify(getNotificationId(),notification);
            }

            private int getNotificationId() {
                return (int) new Date().getTime();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Đã xảy ra lỗi "+ error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
