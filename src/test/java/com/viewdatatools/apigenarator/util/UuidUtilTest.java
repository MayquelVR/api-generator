package com.viewdatatools.apigenarator.util;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UuidUtilTest {

    @Test
    void generateUuidV7_shouldGenerateValidUuidV7() {
        UUID uuid = UuidUtil.generateUuidV7();

        assertNotNull(uuid);
        assertEquals(7, uuid.version());
    }

    @Test
    void isUuidV7_withV7Uuid_shouldReturnTrue() {
        UUID uuid = UuidUtil.generateUuidV7();

        assertTrue(UuidUtil.isUuidV7(uuid));
    }

    @Test
    void isUuidV7_withNullUuid_shouldReturnFalse() {
        assertFalse(UuidUtil.isUuidV7(null));
    }

    @Test
    void isUuidV7_withV4Uuid_shouldReturnFalse() {
        UUID uuid = UUID.randomUUID();

        assertFalse(UuidUtil.isUuidV7(uuid));
    }

    @Test
    void validateUuidV7_withValidUuid_shouldReturnSameUuid() {
        UUID uuid = UuidUtil.generateUuidV7();

        UUID result = UuidUtil.validateUuidV7(uuid);

        assertEquals(uuid, result);
    }

    @Test
    void validateUuidV7_withNullUuid_shouldThrowException() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> UuidUtil.validateUuidV7(null)
        );

        assertEquals("UUID cannot be null", exception.getMessage());
    }

    @Test
    void validateUuidV7_withInvalidVersion_shouldThrowException() {
        UUID uuid = UUID.randomUUID(); // V4

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> UuidUtil.validateUuidV7(uuid)
        );

        assertTrue(exception.getMessage().contains("UUID must be version 7"));
    }

    @Test
    void validateOrGenerateUuidV7_withNullUuid_shouldGenerateNewUuid() {
        UUID result = UuidUtil.validateOrGenerateUuidV7(null);

        assertNotNull(result);
        assertEquals(7, result.version());
    }

    @Test
    void validateOrGenerateUuidV7_withValidUuid_shouldReturnSameUuid() {
        UUID uuid = UuidUtil.generateUuidV7();

        UUID result = UuidUtil.validateOrGenerateUuidV7(uuid);

        assertEquals(uuid, result);
    }

    @Test
    void generateUuidV7_shouldGenerateUniqueIds() {
        UUID uuid1 = UuidUtil.generateUuidV7();
        UUID uuid2 = UuidUtil.generateUuidV7();

        assertNotEquals(uuid1, uuid2);
    }
}

