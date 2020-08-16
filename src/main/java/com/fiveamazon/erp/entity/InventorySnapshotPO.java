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
//
//@Data
//@Entity
//@Table(name = "tbl_inventory_snapshot")
//public class InventorySnapshotPO extends SimpleCommonEntity {
//    @Id
//    Integer id;
//    String snapshotDate;
//    Integer productId;
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
