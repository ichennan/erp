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
@Table(name = "tbl_product")
public class ProductPO extends SimpleCommonEntity {
    String name;
    String sn;
    String color;
    String size;
    Integer enablePacketSeq;
    BigDecimal purchasePrice;
    String store;

    public JSONObject toJson(){
        JSONObject toJson = new JSONObject(this);
        toJson.put("snname", (StringUtils.isEmpty(getSn()) ? "" : getSn() + ":") + getName());
        return toJson;
    }

    @Override
    public String toString(){
        return toJson().toString();
    }
}
