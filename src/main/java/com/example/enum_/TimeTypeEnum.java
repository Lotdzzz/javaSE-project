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

    TimeTypeEnum(Long s) {
        this.value = s;
    }
}
