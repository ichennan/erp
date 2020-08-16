//package com.fiveamazon.erp.entity;
//
//import cn.hutool.json.JSONObject;
//import com.fiveamazon.erp.common.SimpleCommonEntity;
//import com.fiveamazon.erp.common.SimpleCommonView;
//import lombok.Data;
//
//import javax.persistence.Entity;
//import javax.persistence.Id;
//import javax.persistence.Table;
//import java.math.BigDecimal;
//
//@Data
//@Entity
//@Table(name = "view_sku")
//public class SkuViewPO extends SimpleCommonView {
//    @Id
//    String skuDesc;
//    Integer skuId;
//    Integer productId;
//    String sku;
//    Integer storeId;
//    String fnsku;
//    String asin;
//    Integer combineId;
//    Integer combineCount;
//    //
//    String name;
//    String sn;
//    String color;
//    String size;
//    Integer allPurchaseQuantity;
//    Integer allPacketQuantity;
//    Integer allStocktakingQuantity;
//    Integer allShipmentQuantity;
//    Integer inventoryQuantity;
//    Integer onthewayShipmentQuantity;
//    Integer onthewayPurchaseQuantity;
//
//    public JSONObject toJson(){
//        return new JSONObject(this);
//    }
//
//    @Override
//    public String toString(){
//        return toJson().toString();
//    }
//}
