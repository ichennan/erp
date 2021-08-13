package com.fiveamazon.erp.entity.excel;

import cn.hutool.json.JSONObject;
import com.fiveamazon.erp.common.SimpleCommonEntity;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Data
@Entity
@Table(name = "excel_transaction")
public class ExcelTransactionPO extends SimpleCommonEntity {
    String fileName;
    Date dateFrom;
    Date dateTo;
    Integer storeId;

    @Override
    public JSONObject toJson(){
        JSONObject toJson = new JSONObject(this);
        return toJson;
    }

    @Override
    public String toString(){
        return toJson().toString();
    }
}
