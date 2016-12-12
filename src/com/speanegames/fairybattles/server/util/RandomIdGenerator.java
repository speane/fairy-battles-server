package com.speanegames.fairybattles.server.util;

import org.apache.commons.lang3.RandomStringUtils;

public class RandomIdGenerator {

    public String generate(int length) {
        return RandomStringUtils.random(length, true, true);
    }
}
