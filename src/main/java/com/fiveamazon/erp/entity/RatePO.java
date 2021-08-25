package com.fiveamazon.erp.entity;

import cn.hutool.json.JSONObject;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Entity
@Table(name = "tbl_rate")
public class RatePO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    String currency;
    Date effectiveTime;
    BigDecimal rate;
    String jsonRemark;

    public JSONObject toJson(){
        JSONObject toJson = new JSONObject(this);
        return toJson;
    }

    @Override
    public String toString(){
        return toJson().toString();
    }
}
