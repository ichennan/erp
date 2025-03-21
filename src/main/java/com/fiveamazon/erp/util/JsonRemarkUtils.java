package com.fiveamazon.erp.util;

import cn.hutool.json.JSONObject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsonRemarkUtils {
    public static String setJsonRemark(String jsonRemarkStr, String jsonKey, String jsonValue) {
        JSONObject json;
        try {
            json = new JSONObject(jsonRemarkStr);
        } catch (Exception e) {
            json = new JSONObject();
        }
        json.put(jsonKey, jsonValue);
        return json.toString();
    }

}
