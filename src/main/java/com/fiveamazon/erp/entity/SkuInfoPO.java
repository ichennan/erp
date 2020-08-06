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
@Table(name = "tbl_sku_info")
public class SkuInfoPO extends SimpleCommonEntity {
    Integer productId;
    String sku;
    String store;
    Integer storeId;
    String fnsku;
    String asin;
    Integer combineId;

    public JSONObject toJson(){
        JSONObject toJson = new JSONObject(this);
        return toJson;
    }

    @Override
    public String toString(){
        return toJson().toString();
    }
}
