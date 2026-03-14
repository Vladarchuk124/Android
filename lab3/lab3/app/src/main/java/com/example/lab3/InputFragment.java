package com.example.lab3;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;

public class InputFragment extends Fragment {

    private OrderViewModel viewModel;

    private EditText etName;
    private RadioGroup rgType, rgSize;
    private CheckBox cbExtraCheese, cbMushrooms, cbOlives;

    public InputFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_input, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(OrderViewModel.class);

        etName = view.findViewById(R.id.etName);
        rgType = view.findViewById(R.id.rgType);
        rgSize = view.findViewById(R.id.rgSize);

        cbExtraCheese = view.findViewById(R.id.cbExtraCheese);
        cbMushrooms = view.findViewById(R.id.cbMushrooms);
        cbOlives = view.findViewById(R.id.cbOlives);

        ((RadioButton) view.findViewById(R.id.rbPepperoni)).setChecked(true);
        ((RadioButton) view.findViewById(R.id.rbMedium)).setChecked(true);

        Button btnOk = view.findViewById(R.id.btnOk);
        Button btnOpen = view.findViewById(R.id.btnOpen);

        btnOk.setOnClickListener(v -> onOkClicked());

        btnOpen.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), HistoryActivity.class);
            startActivity(intent);
        });

        viewModel.getClearSignal().observe(getViewLifecycleOwner(), signal -> {
            if (signal != null) clearForm();
        });
    }

    private void onOkClicked() {
        String name = etName.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(requireContext(), "Введіть ім'я", Toast.LENGTH_SHORT).show();
            return;
        }

        String type = getSelectedRadioText(rgType);
        String size = getSelectedRadioText(rgSize);

        List<String> toppings = new ArrayList<>();
        if (cbExtraCheese.isChecked()) toppings.add("Додатковий сир");
        if (cbMushrooms.isChecked()) toppings.add("Гриби");
        if (cbOlives.isChecked()) toppings.add("Оливки");

        OrderData data = new OrderData();
        data.customerName = name;
        data.pizzaType = type;
        data.size = size;
        data.toppings = toppings;

        viewModel.submitOrder(data);

        boolean success = StorageHelper.saveOrder(requireContext(), data);
        if (success) {
            Toast.makeText(requireContext(), "Дані успішно збережено", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireContext(), "Помилка запису у сховище", Toast.LENGTH_SHORT).show();
        }
    }

    private String getSelectedRadioText(RadioGroup group) {
        int id = group.getCheckedRadioButtonId();
        RadioButton rb = group.findViewById(id);
        return (rb != null) ? rb.getText().toString() : "";
    }

    private void clearForm() {
        etName.setText("");

        rgType.check(R.id.rbPepperoni);
        rgSize.check(R.id.rbMedium);

        cbExtraCheese.setChecked(false);
        cbMushrooms.setChecked(false);
        cbOlives.setChecked(false);
    }
}