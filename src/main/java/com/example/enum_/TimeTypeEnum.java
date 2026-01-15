package com.example.enum_;

public enum TimeTypeEnum {

    SECOND(1L),
    MINUTE(60L),
    HOUR(60L * 60L),
    DAY(24L * 60L * 60L),
    WEEK(7L * 24L * 60L * 60L),
    MONTH(30L * 24L * 60L * 60L),
    YEAR(365L * 24L * 60L * 60L);

    public final long value;

    private final long nanos = 1_000_000_000L;

    TimeTypeEnum(Long s) {
        this.value = s;
    }

    /**
     * 纳秒转换
     * @param timeTypeEnum 时间单位
     * @param keepAliveTime 时间单位数量
     * @return 时间单位 * 单位数量 * 纳秒转换 返回纳秒
     */
    public static long toNanos(TimeTypeEnum timeTypeEnum, Long keepAliveTime) {
        return (keepAliveTime * timeTypeEnum.value) * timeTypeEnum.nanos;
    }
}
