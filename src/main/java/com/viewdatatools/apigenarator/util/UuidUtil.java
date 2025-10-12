package com.viewdatatools.apigenarator.util;

import com.github.f4b6a3.uuid.UuidCreator;

import java.util.UUID;

public class UuidUtil {

    private UuidUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static UUID generateUuidV7() {
        return UuidCreator.getTimeOrderedEpoch();
    }

    public static boolean isUuidV7(UUID uuid) {
        if (uuid == null) {
            return false;
        }
        return uuid.version() == 7;
    }

    public static UUID validateUuidV7(UUID uuid) {
        if (uuid == null) {
            throw new IllegalArgumentException("UUID cannot be null");
        }
        if (!isUuidV7(uuid)) {
            throw new IllegalArgumentException("UUID must be version 7, but was version " + uuid.version());
        }
        return uuid;
    }

    public static UUID validateOrGenerateUuidV7(UUID uuid) {
        if (uuid == null) {
            return generateUuidV7();
        }
        return validateUuidV7(uuid);
    }
}

