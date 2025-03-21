package com.fiveamazon.erp.util;

import com.fiveamazon.erp.common.SimpleCommonException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

@Slf4j
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

    public static String dateFormatChange(Integer originalLength, Integer targetLength, String str){
        if(StringUtils.hasText(str) && str.length() == originalLength){
            if(originalLength - 10 == 0 && targetLength - 8 == 0){
                return str.substring(6, 10) + "" + str.substring(0,2) + "" + str.substring(3, 5);
            }
        }
        log.error("dateFormatChange error [{}] [{}] [{}]", originalLength, targetLength, str);
        return str;
    }
}
