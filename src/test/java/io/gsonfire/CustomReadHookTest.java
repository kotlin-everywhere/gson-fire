package io.gsonfire;

import com.google.gson.Gson;
import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertEquals;

public class CustomReadHookTest {
    @Test
    public void test() {
        final Typed staticTyped = new Typed();
        final Gson gson = new GsonFireBuilder()
                .registerTypeSelector(Typed.class, readElement -> null, element -> Optional.of(staticTyped))
                .createGson();

        final String json = gson.toJson(new Typed());
        assertEquals("{\"name\":\"io.gsonfire.CustomReadHookTest$Typed\"}", json);

        final Typed instance = gson.fromJson(json, Typed.class);
        Assert.assertEquals(staticTyped, instance);
    }

    private static class Typed {
        String name = this.getClass().getName();
    }
}

