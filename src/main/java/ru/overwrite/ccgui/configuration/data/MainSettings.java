package ru.overwrite.ccgui.configuration.data;

import java.util.Set;

public record MainSettings(
        boolean exactEquality,
        Set<String> commands) {
}
