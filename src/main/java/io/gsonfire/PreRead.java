package io.gsonfire;

import com.google.gson.JsonElement;

import java.util.Optional;

public interface PreRead<T> {
    Optional<T> read(JsonElement element);
}
