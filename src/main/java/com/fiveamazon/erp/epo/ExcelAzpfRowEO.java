package com.fiveamazon.erp.epo;

import cn.hutool.json.JSONObject;
import com.alibaba.excel.annotation.ExcelProperty;
import com.fiveamazon.erp.common.AzFileConstant;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ExcelAzpfRowEO {
    @ExcelProperty("开始日期")
    String dateStart;
    @ExcelProperty("结束日期")
    String dateEnd;
    @ExcelProperty("父 ASIN")
    String parentAsin;
    @ExcelProperty("ASIN")
    String asin;
    @ExcelProperty("MSKU")
    String sku;

    //
    @ExcelProperty(AzFileConstant.AZPF_SalesPrice)
    BigDecimal azpfSalesPrice;
    @ExcelProperty(AzFileConstant.AZPF_SoldUnits)
    Integer azpfSoldUnits;
    @ExcelProperty(AzFileConstant.AZPF_ReturnedUnits)
    Integer azpfReturnedUnits;
    @ExcelProperty(AzFileConstant.AZPF_NetSoldUnits)
    Integer azpfNetSoldUnits;
    @ExcelProperty(AzFileConstant.AZPF_SalesAmount)
    BigDecimal azpfSalesAmount;
    @ExcelProperty(AzFileConstant.AZPF_NetSalesAmount)
    BigDecimal azpfNetSalesAmount;
    @ExcelProperty(AzFileConstant.AZPF_BaseFullfillmentFeeQuantity)
    BigDecimal azpfBaseFullfillmentFeeQuantity;
    @ExcelProperty(AzFileConstant.AZPF_BaseFullfillmentFeeTotal)
    BigDecimal azpfBaseFullfillmentFeeTotal;
    @ExcelProperty(AzFileConstant.AZPF_BaseFullfillmentFeePer)
    BigDecimal azpfBaseFullfillmentFeePer;
    @ExcelProperty(AzFileConstant.AZPF_BaseStorageFeeQuantity)
    BigDecimal azpfBaseStorageFeeQuantity;
    @ExcelProperty(AzFileConstant.AZPF_BaseStorageFeeTotal)
    BigDecimal azpfBaseStorageFeeTotal;
    @ExcelProperty(AzFileConstant.AZPF_BaseStorageFeePer)
    BigDecimal azpfBaseStorageFeePer;
    @ExcelProperty(AzFileConstant.AZPF_DisposalOrderFeeQuantity)
    BigDecimal azpfDisposalOrderFeeQuantity;
    @ExcelProperty(AzFileConstant.AZPF_DisposalOrderFeeTotal)
    BigDecimal azpfDisposalOrderFeeTotal;
    @ExcelProperty(AzFileConstant.AZPF_DisposalOrderFeePer)
    BigDecimal azpfDisposalOrderFeePer;
    @ExcelProperty(AzFileConstant.AZPF_FullfillmentFeeQuantity)
    BigDecimal azpfFullfillmentFeeQuantity;
    @ExcelProperty(AzFileConstant.AZPF_FullfillmentFeeTotal)
    BigDecimal azpfFullfillmentFeeTotal;
    @ExcelProperty(AzFileConstant.AZPF_FullfillmentFeePer)
    BigDecimal azpfFullfillmentFeePer;
    @ExcelProperty(AzFileConstant.AZPF_InboundPlacementFeeQuantity)
    BigDecimal azpfInboundPlacementFeeQuantity;
    @ExcelProperty(AzFileConstant.AZPF_InboundPlacementFeeTotal)
    BigDecimal azpfInboundPlacementFeeTotal;
    @ExcelProperty(AzFileConstant.AZPF_InboundPlacementFeePer)
    BigDecimal azpfInboundPlacementFeePer;
    @ExcelProperty(AzFileConstant.AZPF_InboundTransportationFeeQuantity)
    BigDecimal azpfInboundTransportationFeeQuantity;
    @ExcelProperty(AzFileConstant.AZPF_InboundTransportationFeeTotal)
    BigDecimal azpfInboundTransportationFeeTotal;
    @ExcelProperty(AzFileConstant.AZPF_InboundTransportationFeePer)
    BigDecimal azpfInboundTransportationFeePer;
    @ExcelProperty(AzFileConstant.AZPF_StorageFeeQuantity)
    BigDecimal azpfStorageFeeQuantity;
    @ExcelProperty(AzFileConstant.AZPF_StorageFeeTotal)
    BigDecimal azpfStorageFeeTotal;
    @ExcelProperty(AzFileConstant.AZPF_StorageFeePer)
    BigDecimal azpfStorageFeePer;
    @ExcelProperty(AzFileConstant.AZPF_ReferralFeeQuantity)
    BigDecimal azpfReferralFeeQuantity;
    @ExcelProperty(AzFileConstant.AZPF_ReferralFeeTotal)
    BigDecimal azpfReferralFeeTotal;
    @ExcelProperty(AzFileConstant.AZPF_ReferralFeePer)
    BigDecimal azpfReferralFeePer;
    @ExcelProperty(AzFileConstant.AZPF_RefundAdminFeeQuantity)
    BigDecimal azpfRefundAdminFeeQuantity;
    @ExcelProperty(AzFileConstant.AZPF_RefundAdminFeeTotal)
    BigDecimal azpfRefundAdminFeeTotal;
    @ExcelProperty(AzFileConstant.AZPF_RefundAdminFeePer)
    BigDecimal azpfRefundAdminFeePer;
    @ExcelProperty(AzFileConstant.AZPF_ReturnsProcessingFeeQuantity)
    BigDecimal azpfReturnsProcessingFeeQuantity;
    @ExcelProperty(AzFileConstant.AZPF_ReturnsProcessingFeeTotal)
    BigDecimal azpfReturnsProcessingFeeTotal;
    @ExcelProperty(AzFileConstant.AZPF_ReturnsProcessingFeePer)
    BigDecimal azpfReturnsProcessingFeePer;
    @ExcelProperty(AzFileConstant.AZPF_StorageUtilizationSurchargeQuantity)
    BigDecimal azpfStorageUtilizationSurchargeQuantity;
    @ExcelProperty(AzFileConstant.AZPF_StorageUtilizationSurchargeTotal)
    BigDecimal azpfStorageUtilizationSurchargeTotal;
    @ExcelProperty(AzFileConstant.AZPF_StorageUtilizationSurchargePer)
    BigDecimal azpfStorageUtilizationSurchargePer;
    @ExcelProperty(AzFileConstant.AZPF_SponsoredChargeQuantity)
    BigDecimal azpfSponsoredChargeQuantity;
    @ExcelProperty(AzFileConstant.AZPF_SponsoredChargeTotal)
    BigDecimal azpfSponsoredChargeTotal;
    @ExcelProperty(AzFileConstant.AZPF_SponsoredChargePer)
    BigDecimal azpfSponsoredChargePer;
    @ExcelProperty(AzFileConstant.AZPF_CostPer)
    BigDecimal azpfCostPer;
    @ExcelProperty(AzFileConstant.AZPF_MiscellaneousCostPer)
    BigDecimal azpfMiscellaneousCostPer;
    @ExcelProperty(AzFileConstant.AZPF_AzProceedsTotal)
    BigDecimal azpfAzProceedsTotal;
    @ExcelProperty(AzFileConstant.AZPF_AzProceedsPer)
    BigDecimal azpfAzProceedsPer;


    public JSONObject toJson() {
        JSONObject toJson = new JSONObject(this);
        return toJson;
    }

    @Override
    public String toString() {
        return toJson().toString();
    }

}
