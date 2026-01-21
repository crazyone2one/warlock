package cn.master.horde.common.constants;

import lombok.Getter;

/**
 * @author : 11's papa
 * @since : 2026/1/21, 星期三
 **/
@Getter
public enum WkkSensorEnum {
    GNSS_REAL_TIME("GNSS实时数据", "GNSS", "sf_aqjk_gnssbaseinfo", "gnssrealtime");
    private final String fileName;
    private final String cacheKey;
    private final String tableName;
    private final String fileCode;

    WkkSensorEnum(String fileName, String cacheKey, String tableName, String fileCode) {
        this.fileName = fileName;
        this.cacheKey = cacheKey;
        this.tableName = tableName;
        this.fileCode = fileCode;
    }
}
