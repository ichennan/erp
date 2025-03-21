package com.fiveamazon.erp.entity;

import cn.hutool.json.JSONObject;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "tbl_store")
public class StorePO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    String name;

    public JSONObject toJson() {
        JSONObject toJson = new JSONObject(this);
        return toJson;
    }

    @Override
    public String toString() {
        return toJson().toString();
    }
}
