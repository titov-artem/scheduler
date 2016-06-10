package com.github.sc.scheduler.core.utils;

import java.util.UUID;

/**
 * UUID generator
 *
 * @author Artem Titov titov.artem.u@yandex.com
 */
public class IdGenerator {

    public static String nextId() {
        return UUID.randomUUID().toString();
    }

}
