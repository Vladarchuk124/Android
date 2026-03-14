package com.example.lab3;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

public class MainActivity extends AppCompatActivity {

    private OrderViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewModel = new ViewModelProvider(this).get(OrderViewModel.class);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container_input, new InputFragment())
                    .commit();
        }

        viewModel.getShowResult().observe(this, show -> {
            if (show != null && show) {
                showResultFragment();
            } else {
                hideResultFragment();
            }
        });
    }

    private void showResultFragment() {
        Fragment existing = getSupportFragmentManager().findFragmentById(R.id.container_result);
        if (existing == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container_result, new ResultFragment())
                    .commit();
        }
    }

    private void hideResultFragment() {
        Fragment existing = getSupportFragmentManager().findFragmentById(R.id.container_result);
        if (existing != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .remove(existing)
                    .commit();
        }
    }
}