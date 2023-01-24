package com.matyrobbrt.website.util;

import java.util.List;

public record Configuration(String cfApiKey, String cfUser, List<String> modsToExclude) {
}
