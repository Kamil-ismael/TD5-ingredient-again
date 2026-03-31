package com.hei.td5ingredientagain.entity;

public class StockValue {
    private Double quantity;
    private Unit unit;

    public StockValue(double quantity, Unit unit) {
    }

    public StockValue() {

    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    @Override
    public String toString() {
        return "StockValue{" +
                "quantity=" + quantity +
                ", unit=" + unit +
                '}';
    }
}
