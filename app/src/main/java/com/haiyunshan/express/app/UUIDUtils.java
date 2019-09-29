package com.haiyunshan.express.app;

import java.util.UUID;

/**
 * Created by sanshibro on 2017/11/9.
 */

public class UUIDUtils {

    public static final String next() {
        String id = UUID.randomUUID().toString();
        id = id.replace("-", "");

        return id;
    }
}
