package com.example.lab2;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class OrderViewModel extends ViewModel {

    private final MutableLiveData<OrderData> orderData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> showResult = new MutableLiveData<>(false);

    // Лічильник "очистити форму" — щоб InputFragment міг реагувати на Cancel
    private final MutableLiveData<Integer> clearSignal = new MutableLiveData<>(0);

    public LiveData<OrderData> getOrderData() {
        return orderData;
    }

    public LiveData<Boolean> getShowResult() {
        return showResult;
    }

    public LiveData<Integer> getClearSignal() {
        return clearSignal;
    }

    public void submitOrder(OrderData data) {
        orderData.setValue(data);
        showResult.setValue(true);
    }

    public void cancelAndClear() {
        showResult.setValue(false);

        Integer cur = clearSignal.getValue();
        if (cur == null) cur = 0;
        clearSignal.setValue(cur + 1);

        orderData.setValue(null);
    }
}