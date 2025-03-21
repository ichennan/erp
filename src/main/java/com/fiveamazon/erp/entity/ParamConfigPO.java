package com.fiveamazon.erp.entity;

import cn.hutool.json.JSONObject;
import com.fiveamazon.erp.common.SimpleCommonEntity;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "tbl_param_config")
public class ParamConfigPO extends SimpleCommonEntity {
    String paramCategory;
    String paramType;
    Integer paramSeq;
    String paramValue;
    String remark1;
    String remark2;
    String remark3;
    String remark4;
    String remark5;

    @Override
    public JSONObject toJson() {
        return new JSONObject(this);
    }

    @Override
    public String toString() {
        return toJson().toString();
    }
}
