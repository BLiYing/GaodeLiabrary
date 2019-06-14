package com.example.gaodelibrary;

import android.os.Build;
import android.text.TextUtils;

/**
 * Created by      android studio
 *
 * @author :       ly
 * Date            :       2019-01-23
 * Time            :       上午10:59
 * Version         :       1.0
 * location        :       武汉研发中心
 * 功能描述         :       获取操作系统，华为，小米，其他
 **/
public class OSUtils {
    private static final String PREFIX_HUAWEI = "HUAWEI";
    private static final String PREFIX_XIAOMI = "XIAOMI";
    private static final String PREFIX_OTHERS = "OTHERS";

    //MIUI标识
    public static final String BRAND_MIUI = "xiaomi";

    //EMUI标识
    public static final String BRAND_EMUI1 = "huawei";
    public static final String BRAND_EMUI2 = "honor";


    public enum ROM_TYPE {
        MIUI(PREFIX_XIAOMI),
        EMUI(PREFIX_HUAWEI),
        OTHER(PREFIX_OTHERS);



        private String prefix;

        ROM_TYPE(String prefix) {
            this.prefix = prefix;
        }

        public String getPrefix() {
            return prefix;
        }
    }

    /**
     * @param
     * @return ROM_TYPE ROM类型的枚举
     * @description获取ROM类型: MIUI_ROM, EMUI_ROM, OTHER_ROM
     */

    public static ROM_TYPE getRomType() {
        ROM_TYPE rom_type = ROM_TYPE.OTHER;

        String brand = Build.MANUFACTURER;
        if (!TextUtils.isEmpty(brand)) {
            if (brand.toLowerCase().contains(BRAND_EMUI1) || brand.toLowerCase().contains(BRAND_EMUI2)) {
                return ROM_TYPE.EMUI;
            }
            if (brand.toLowerCase().contains(BRAND_MIUI)) {
                return ROM_TYPE.MIUI;
            }
        }
        return rom_type;

    }

}
