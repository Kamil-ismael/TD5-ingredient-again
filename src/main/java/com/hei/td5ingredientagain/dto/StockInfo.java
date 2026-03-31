package com.hei.td5ingredientagain.dto;

import com.hei.td5ingredientagain.entity.Unit;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StockInfo {
    private Unit unit;
    private double value;
}
