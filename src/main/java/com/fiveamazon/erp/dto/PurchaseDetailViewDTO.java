//package com.fiveamazon.erp.dto;
//
//import cn.hutool.json.JSONObject;
//import com.fiveamazon.erp.common.SimpleCommonView;
//import com.fiveamazon.erp.entity.PurchaseDetailPO;
//import com.fiveamazon.erp.entity.PurchasePO;
//import lombok.Data;
//import org.springframework.beans.BeanUtils;
//
//import java.math.BigDecimal;
//
//@Data
//public class PurchaseDetailViewDTO extends SimpleCommonView {
//    Integer purchaseId;
//    Integer productId;
//    Integer bookQuantity;
//    Integer receivedQuantity;
//    BigDecimal unitPrice;
//    //
//    String bookDate;
//    String deliveryDate;
//    String receivedDate;
//    BigDecimal freight;
//    String supplier;
//
//    public PurchaseDetailViewDTO(PurchaseDetailPO purchaseDetailPO, PurchasePO purchasePO){
//        super();
//        BeanUtils.copyProperties(purchasePO, this, "id");
//        BeanUtils.copyProperties(purchaseDetailPO, this);
//    }
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
