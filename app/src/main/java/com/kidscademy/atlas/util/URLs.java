package com.kidscademy.atlas.util;

import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class URLs {
    /**
     * Pattern for domain without TLD.
     */
    private static final Pattern BASEDOMAIN_PATTERN = Pattern.compile("^(?:[^.]+\\.)*([^.]+)\\..+$");

    public static String basedomain(URL url) {
        Matcher matcher = BASEDOMAIN_PATTERN.matcher(url.getHost());
        matcher.find();
        return matcher.group(1);
    }
}
