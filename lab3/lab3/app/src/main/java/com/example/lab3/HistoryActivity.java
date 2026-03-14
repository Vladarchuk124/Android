package com.example.lab3;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class HistoryActivity extends AppCompatActivity {

    private TextView tvStorageContent;
    private Button btnDeleteAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        tvStorageContent = findViewById(R.id.tvStorageContent);
        btnDeleteAll = findViewById(R.id.btnDeleteAll);

        loadData();

        btnDeleteAll.setOnClickListener(v -> {
            boolean success = StorageHelper.clearOrders(this);
            if (success) {
                Toast.makeText(this, "Дані видалено", Toast.LENGTH_SHORT).show();
                loadData();
            } else {
                Toast.makeText(this, "Помилка видалення", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadData() {
        String data = StorageHelper.readOrders(this);

        if (data.isEmpty()) {
            tvStorageContent.setText("Сховище порожнє");
        } else {
            tvStorageContent.setText(data);
        }
    }
}