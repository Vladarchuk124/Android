package com.example.lab1;

import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    EditText editName;
    CheckBox checkPepperoni, checkMargarita, checkHawaiian;
    RadioGroup radioGroupSize;
    Button buttonOk;
    TextView textResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editName = findViewById(R.id.editName);
        checkPepperoni = findViewById(R.id.checkPepperoni);
        checkMargarita = findViewById(R.id.checkMargarita);
        checkHawaiian = findViewById(R.id.checkHawaiian);
        radioGroupSize = findViewById(R.id.radioGroupSize);
        buttonOk = findViewById(R.id.buttonOk);
        textResult = findViewById(R.id.textResult);

        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processOrder();
            }
        });
    }

    private void processOrder() {

        String name = editName.getText().toString().trim();

        if (name.isEmpty() || radioGroupSize.getCheckedRadioButtonId() == -1 ||
                (!checkPepperoni.isChecked() && !checkMargarita.isChecked() && !checkHawaiian.isChecked())) {

            Toast.makeText(this, "Будь ласка, заповніть всі поля!", Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder order = new StringBuilder();
        order.append("Замовник: ").append(name).append("\n");

        order.append("Тип піци: ");
        if (checkPepperoni.isChecked()) order.append("Пепероні ");
        if (checkMargarita.isChecked()) order.append("Маргарита ");
        if (checkHawaiian.isChecked()) order.append("Гавайська ");

        int selectedId = radioGroupSize.getCheckedRadioButtonId();
        RadioButton selectedSize = findViewById(selectedId);

        order.append("\nРозмір: ").append(selectedSize.getText());

        textResult.setText(order.toString());
    }
}