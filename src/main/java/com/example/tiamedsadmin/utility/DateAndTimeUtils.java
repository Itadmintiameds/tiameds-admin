package com.example.tiamedsadmin.utility;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class DateAndTimeUtils {

    public static LocalDateTime getCurrentISTDateTime() {
        return ZonedDateTime.now(ZoneId.of("Asia/Kolkata")).toLocalDateTime();
    }
}
