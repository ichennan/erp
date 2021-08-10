package com.fiveamazon.erp.common;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**常量类
 */
public class SimpleConstant {


	public static final String ACTION_DELETE = "delete";

	public static final String FILE_CATEGORY_fba = "fba";
	public static final String FILE_CATEGORY_fbatsv = "fbatsv";
	public static final String FILE_CATEGORY_MonthlyUnifiedTransaction = "MonthlyUnifiedTransaction";
	public static final String FILE_CATEGORY_supplierDelivery = "supplierDelivery";

	public static final SimpleDateFormat TRANSACTION_TIME_DATE_FORMAT = new SimpleDateFormat("MMM d, yyyy h:m:s aa 'PDT'", Locale.ENGLISH);





}
