package com.matyrobbrt.website.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum License {
    MIT("mit"),
    LGPLv3("lgpl-3.0"),
    GPLv3("gpl-3.0"),
    Apache_v2_0("apache-2.0", "Apache 2.0"),
    Unlicense("unlicense"),
    WTFPL("wtfpl"),
    CC0_v1_0("cc0-1.0", "CCO-1.0");

    private final URL download;
    private final String displayName;

    License(String name) {
        this.download = downloadURL(name);
        this.displayName = name();
    }

    License(String name, String displayName) {
        this.download = downloadURL(name);
        this.displayName = displayName;
    }

    private static URL downloadURL(String id) {
        try {
            return URI.create("https://raw.githubusercontent.com/github/choosealicense.com/0ccd6c5d0ce1c0f68e088ec0c936322e43b2fbfb/_licenses/" + id + ".txt").toURL();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public String read(String year, String fullName) throws IOException {
        try (final InputStream in = download.openStream()) {
            String full = new String(in.readAllBytes());
            full = full.substring(full.lastIndexOf("---") + 3).trim();
            return full.replace("[year]", year)
                    .replace("[fullname]", fullName);
        }
    }

    @Override
    public String toString() {
        return displayName;
    }

    public static final Map<String, License> BY_NAME = Arrays.stream(values())
            .collect(Collectors.toMap(License::toString, Function.identity()));
    public static final List<String> ALL_LICENSES = Stream.concat(Stream.of("ARR"), Arrays.stream(values())
            .map(License::toString)).toList();
}
