// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.beacon.api.util;

import java.security.SecureRandom;
import java.util.Locale;
import java.util.Random;

public class RandomString {

    public static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String LOWERCASE = UPPERCASE.toLowerCase(Locale.ROOT);
    public static final String DIGITS = "0123456789";
    public static final String ALPHANUMERIC = UPPERCASE + LOWERCASE + DIGITS;
    public static final String HEX = "0123456789abcdef";

    private final Random random;

    private final char[] symbols;

    private final char[] buf;

    public RandomString(int length, String symbols) {
        if (length < 1) {
            throw new IllegalArgumentException();
        }

        if (symbols.length() < 2) {
            throw new IllegalArgumentException();
        }

        this.random = new SecureRandom();
        this.symbols = symbols.toCharArray();
        this.buf = new char[length];
    }

    public String nextString() {
        for (int idx = 0; idx < buf.length; ++idx)
            buf[idx] = symbols[random.nextInt(symbols.length)];
        return new String(buf);
    }
}