package com.fiveamazon.erp.common;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**常量类
 */
public class SimpleConstant {

	public static final String BOX_PLAN = "Plan";

	public static final BigDecimal BIG_DECIMAL_0 = new BigDecimal(0);
	public static final BigDecimal BIG_DECIMAL_MAX = new BigDecimal(999999);

	public static final String DATE_17 = "yyyy-MM-dd HH:mm:ss";
	public static final String DATE_14 = "yyyyMMddHHmmss";
	public static final String DATE_8 = "yyyyMMdd";
	public static final String DATE_10 = "yyyy-MM-dd";
	public static final String ACTION_DELETE = "delete";

	public static final String FILE_CATEGORY_fba = "fba";
	public static final String FILE_CATEGORY_fbatsv = "fbatsv";
	public static final String FILE_CATEGORY_MonthlyUnifiedTransaction = "MonthlyUnifiedTransaction";
	public static final String FILE_CATEGORY_supplierDelivery = "supplierDelivery";
	public static final String FILE_CATEGORY_CarrierBillCainiao = "CarrierBillCainiao";
	public static final String FILE_CATEGORY_AZWS = "azws";
	public static final String FILE_CATEGORY_AZPF = "azpf";

	public static final String CARRIER_Cainiao = "菜鸟运通";

	public static final SimpleDateFormat TRANSACTION_TIME_DATE_FORMAT = new SimpleDateFormat("MMM d, yyyy h:m:s aa z", Locale.ENGLISH);

	public static final String AMAZON_TYPE_ORDER = "Order";
	public static final String AMAZON_TYPE_DEAL_FEE = "Deal Fee";
	public static final String AMAZON_TYPE_FBA_Inventory_Fee = "FBA Inventory Fee";
	public static final String AMAZON_TYPE_Refund_Retrocharge = "Refund_Retrocharge";
	public static final String AMAZON_TYPE_Adjustment = "Adjustment";
	public static final String AMAZON_TYPE_Refund = "Refund";
	public static final String AMAZON_TYPE_FBA_Customer_Return_Fee = "FBA Customer Return Fee";
	public static final String AMAZON_TYPE_Order_Retrocharge = "Order_Retrocharge";
	public static final String AMAZON_TYPE_Fee_Adjustment = "Fee Adjustment";
	public static final String AMAZON_TYPE_Service_Fee = "Service Fee";
	public static final String AMAZON_TYPE_Others = "Others";
	public static final String AMAZON_TYPE_Transfer = "Transfer";
	public static final String AMAZON_TYPE_Coupon_Fee_TERRY = "";

	public static final String JSON_REMARK_CHARGE_UPDATE_BY_CARRIER_BILL_EXCEL = "chargeUpdatedByCarrierBillExcel";
	public static final String JSON_REMARK_CREATED_BY_OVERSEA_DETAIL = "createdByOverseaDetail";

	public static final String FBA = "FBA";

	public static final Integer parentStoreId = 999;



}
