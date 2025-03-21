package com.fiveamazon.erp.common;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommonTable<T> {
    Long total;
    List rows;

    public JSONObject toJson() {
        JSONObject toJson = new JSONObject();
        JSONArray array = new JSONArray();
        for (T t : (List<T>) rows) {
            JSONObject json = ((SimpleCommonEntity) t).toJson();
            array.put(json);
        }
        toJson.put("total", total);
        toJson.put("rows", array);
        return toJson;
    }
}
