package com.hhz.bms.bms.util;

import java.util.UUID;

public class VidGenerator {
    public static String generateVid() {
        return UUID.randomUUID().toString().replace("-", "").toUpperCase().substring(0, 16);
    }
}
