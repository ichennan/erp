package com.fiveamazon.erp.util;

import com.fiveamazon.erp.common.SimpleCommonException;

public class CommonUtils {
    public static Integer str2Int(String str, Boolean isException){
        Integer rs = 0;
        try{
            rs = Integer.valueOf(str);
        }catch (Exception e){
            if(isException){
                throw new SimpleCommonException("不能转化为数字: " + str);
            }else{
                return rs;
            }
        }
        return rs;
    }
}
