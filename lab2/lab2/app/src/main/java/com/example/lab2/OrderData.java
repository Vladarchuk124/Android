package com.example.lab2;

import java.util.ArrayList;
import java.util.List;

public class OrderData {
    public String customerName;
    public String pizzaType;
    public String size;
    public List<String> toppings = new ArrayList<>();

    public String toPrettyString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Замовник: ").append(customerName).append("\n");
        sb.append("Тип піци: ").append(pizzaType).append("\n");
        sb.append("Розмір: ").append(size).append("\n");
        sb.append("Топінги: ");
        if (toppings == null || toppings.isEmpty()) {
            sb.append("немає");
        } else {
            for (int i = 0; i < toppings.size(); i++) {
                sb.append(toppings.get(i));
                if (i < toppings.size() - 1) sb.append(", ");
            }
        }
        return sb.toString();
    }
}