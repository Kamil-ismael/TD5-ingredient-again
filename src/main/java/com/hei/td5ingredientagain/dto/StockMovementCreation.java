package com.hei.td5ingredientagain.dto;

import com.hei.td5ingredientagain.entity.MovementTypeEnum;
import com.hei.td5ingredientagain.entity.Unit;
import lombok.Data;

@Data
public class StockMovementCreation {
    private Unit unit;
    private double quantity;
    private MovementTypeEnum type;
}

