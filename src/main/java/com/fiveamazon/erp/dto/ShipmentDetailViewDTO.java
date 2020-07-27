package com.fiveamazon.erp.dto;

import cn.hutool.json.JSONObject;
import com.fiveamazon.erp.common.SimpleCommonView;
import com.fiveamazon.erp.entity.ShipmentDetailPO;
import com.fiveamazon.erp.entity.ShipmentPO;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import javax.persistence.Id;
import java.math.BigDecimal;

@Data
public class ShipmentDetailViewDTO extends SimpleCommonView {
    @Id
    Integer id;
    Integer shipmentId;
    String box;
    Integer productId;
    Integer quantity;
    BigDecimal weight;
    //
    String deliveryDate;
    String carrier;
    String route;
    String fbaNo;
    BigDecimal unitPrice;
    BigDecimal shipmentWeight;
    BigDecimal chargeWeight;
    BigDecimal amount;
    String paymentDate;
    String signedDate;
    String store;
    String statusDelivery;
    Integer boxCount;
    String trackingNumber;

    public ShipmentDetailViewDTO(ShipmentDetailPO shipmentDetailPO, ShipmentPO shipmentPO){
        super();
        BeanUtils.copyProperties(shipmentPO, this, "id");
        BeanUtils.copyProperties(shipmentDetailPO, this);
        this.setShipmentWeight(shipmentPO.getWeight());
    }

    public JSONObject toJson(){
        return new JSONObject(this);
    }

    @Override
    public String toString(){
        return toJson().toString();
    }
}
