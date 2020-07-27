package com.fiveamazon.erp.entity;

import cn.hutool.json.JSONObject;
import com.fiveamazon.erp.common.SimpleCommonEntity;
import lombok.Data;
import org.springframework.util.StringUtils;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "tbl_sku")
public class SkuPO extends SimpleCommonEntity {
    Integer productId;
    String name;
    String store;

    public JSONObject toJson(){
        JSONObject toJson = new JSONObject(this);
        return toJson;
    }

    @Override
    public String toString(){
        return toJson().toString();
    }
}
