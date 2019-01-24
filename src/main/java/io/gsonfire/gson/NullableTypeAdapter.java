package io.gsonfire.gson;

import com.google.gson.JsonElement;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Function;

/**
 * @autor: julio
 */
public final class NullableTypeAdapter<T> extends TypeAdapter<T> {

    private final TypeAdapter<T> nullable;

    public NullableTypeAdapter(TypeAdapter<T> nullable) {
        this.nullable = nullable;
    }

    @Override
    public void write(JsonWriter out, T value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            nullable.write(out, value);
        }
    }

    @Override
    public T read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        } else {
            return nullable.read(in);
        }
    }

    public void setPreRead(Function<JsonElement, Optional<T>> preRead) {
        ((TypeSelectorTypeAdapterFactory.TypeSelectorTypeAdapter) nullable).setPreRead(preRead);
    }
}
