package com.fiveamazon.erp.entity.excel;

import cn.hutool.json.JSONObject;
import com.fiveamazon.erp.common.SimpleCommonEntity;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "excel_azpf")
public class ExcelAzpfPO extends SimpleCommonEntity {
    String dateStart;
    String dateEnd;
    String parentAsin;
    String asin;
    String sku;
    Integer status;
    Integer storeId;
    //
    BigDecimal azpfSalesPrice;
    Integer azpfSoldUnits;
    Integer azpfReturnedUnits;
    Integer azpfNetSoldUnits;
    BigDecimal azpfSalesAmount;
    BigDecimal azpfNetSalesAmount;
    BigDecimal azpfBaseFullfillmentFeeQuantity;
    BigDecimal azpfBaseFullfillmentFeeTotal;
    BigDecimal azpfBaseFullfillmentFeePer;
    BigDecimal azpfBaseStorageFeeQuantity;
    BigDecimal azpfBaseStorageFeeTotal;
    BigDecimal azpfBaseStorageFeePer;
    BigDecimal azpfDisposalOrderFeeQuantity;
    BigDecimal azpfDisposalOrderFeeTotal;
    BigDecimal azpfDisposalOrderFeePer;
    BigDecimal azpfFullfillmentFeeQuantity;
    BigDecimal azpfFullfillmentFeeTotal;
    BigDecimal azpfFullfillmentFeePer;
    BigDecimal azpfInboundPlacementFeeQuantity;
    BigDecimal azpfInboundPlacementFeeTotal;
    BigDecimal azpfInboundPlacementFeePer;
    BigDecimal azpfInboundTransportationFeeQuantity;
    BigDecimal azpfInboundTransportationFeeTotal;
    BigDecimal azpfInboundTransportationFeePer;
    BigDecimal azpfStorageFeeQuantity;
    BigDecimal azpfStorageFeeTotal;
    BigDecimal azpfStorageFeePer;
    BigDecimal azpfReferralFeeQuantity;
    BigDecimal azpfReferralFeeTotal;
    BigDecimal azpfReferralFeePer;
    BigDecimal azpfRefundAdminFeeQuantity;
    BigDecimal azpfRefundAdminFeeTotal;
    BigDecimal azpfRefundAdminFeePer;
    BigDecimal azpfReturnsProcessingFeeQuantity;
    BigDecimal azpfReturnsProcessingFeeTotal;
    BigDecimal azpfReturnsProcessingFeePer;
    BigDecimal azpfStorageUtilizationSurchargeQuantity;
    BigDecimal azpfStorageUtilizationSurchargeTotal;
    BigDecimal azpfStorageUtilizationSurchargePer;
    BigDecimal azpfSponsoredChargeQuantity;
    BigDecimal azpfSponsoredChargeTotal;
    BigDecimal azpfSponsoredChargePer;
    BigDecimal azpfCostPer;
    BigDecimal azpfMiscellaneousCostPer;
    BigDecimal azpfAzProceedsTotal;
    BigDecimal azpfAzProceedsPer;

    @Override
    public JSONObject toJson() {
        JSONObject toJson = new JSONObject(this);
        return toJson;
    }

    @Override
    public String toString() {
        return toJson().toString();
    }
}
