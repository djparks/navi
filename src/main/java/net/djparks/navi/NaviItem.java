package net.djparks.navi;

import java.util.List;
import java.util.Map;

/**
 * Simple data record for a Navi item consisting of a title and a command,
 * optionally providing placeholder options parsed from the cheatsheet ("$$ name: a|b").
 */
public record NaviItem(String title, String command, Map<String, List<String>> placeholderOptions) { }
