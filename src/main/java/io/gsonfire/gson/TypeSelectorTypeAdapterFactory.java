package io.gsonfire.gson;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.gsonfire.ClassConfig;
import io.gsonfire.PreRead;
import io.gsonfire.TypeSelector;
import io.gsonfire.util.JsonUtils;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

/**
 * Creates a {@link TypeAdapter} that will run the {@link TypeSelector} and find the {@link TypeAdapter} for the selected
 * type.
 */
public class TypeSelectorTypeAdapterFactory<T> implements TypeAdapterFactory {

    private final ClassConfig<T> classConfig;
    private final Set<TypeToken> alreadyResolvedTypeTokensRegistry;

    public TypeSelectorTypeAdapterFactory(ClassConfig<T> classConfig, Set<TypeToken> alreadyResolvedTypeTokensRegistry) {
        this.classConfig = classConfig;
        this.alreadyResolvedTypeTokensRegistry = alreadyResolvedTypeTokensRegistry;
    }

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        if (alreadyResolvedTypeTokensRegistry.contains(type)) {
            return null;
        }
        if (classConfig.getConfiguredClass().isAssignableFrom(type.getRawType())) {
            TypeAdapter<T> fireTypeAdapter =
                    new NullableTypeAdapter<T>(
                            new TypeSelectorTypeAdapter<T>(type.getRawType(), classConfig.getTypeSelector(), gson, (PreRead<T>) classConfig.getPreRead())
                    );
            return fireTypeAdapter;
        } else {
            return null;
        }
    }

    class TypeSelectorTypeAdapter<T> extends TypeAdapter<T> {

        private final Class superClass;
        private final TypeSelector typeSelector;
        private final Gson gson;
        private final PreRead<T> preRead;

        private TypeSelectorTypeAdapter(Class superClass, TypeSelector typeSelector, Gson gson, PreRead<T> preRead) {
            this.superClass = superClass;
            this.typeSelector = typeSelector;
            this.gson = gson;
            this.preRead = preRead;
        }

        @Override
        public void write(JsonWriter out, T value) throws IOException {
            TypeAdapter otherTypeAdapter = gson.getDelegateAdapter(TypeSelectorTypeAdapterFactory.this, TypeToken.get(value.getClass()));
            otherTypeAdapter.write(out, value);
        }

        @Override
        public T read(JsonReader in) throws IOException {
            JsonElement json = new JsonParser().parse(in);

            Optional<T> re = preRead != null ? preRead.read(json) : Optional.empty();
            if (re.isPresent()) {
                return re.get();
            }

            Class deserialize = this.typeSelector.getClassForElement(json);
            if (deserialize == null) {
                deserialize = superClass;
            }

            TypeToken typeToken = TypeToken.get(deserialize);
            alreadyResolvedTypeTokensRegistry.add(typeToken);
            TypeAdapter<T> otherTypeAdapter;
            try {
                if (deserialize != superClass) {
                    otherTypeAdapter = gson.getAdapter(typeToken);
                } else {
                    otherTypeAdapter = gson.getDelegateAdapter(TypeSelectorTypeAdapterFactory.this, typeToken);
                }
            } finally {
                alreadyResolvedTypeTokensRegistry.remove(typeToken);
            }
            return JsonUtils.fromJsonTree(otherTypeAdapter, in, json);
        }
    }
}
