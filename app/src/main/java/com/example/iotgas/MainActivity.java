package com.example.iotgas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

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

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Đã xảy ra lỗi "+ error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
