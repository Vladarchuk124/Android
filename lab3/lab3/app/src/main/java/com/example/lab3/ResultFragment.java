package com.example.lab3;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

public class ResultFragment extends Fragment {

    private OrderViewModel viewModel;

    public ResultFragment() { }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_result, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(OrderViewModel.class);

        TextView tvResult = view.findViewById(R.id.tvResult);
        Button btnCancel = view.findViewById(R.id.btnCancel);

        viewModel.getOrderData().observe(getViewLifecycleOwner(), data -> {
            if (data == null) {
                tvResult.setText("(порожньо)");
            } else {
                tvResult.setText(data.toPrettyString());
            }
        });

        btnCancel.setOnClickListener(v -> viewModel.cancelAndClear());
    }
}