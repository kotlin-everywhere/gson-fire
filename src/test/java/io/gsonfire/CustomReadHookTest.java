package io.gsonfire;

import com.google.gson.Gson;
import io.gsonfire.gson.HooksTypeAdapter;
import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertEquals;

public class CustomReadHookTest {
    @Test
    public void test() {
        final Typed staticTyped = new Typed();
        final Gson gson = new GsonFireBuilder()
                .registerTypeSelector(Typed.class, readElement -> null)
                .createGson();

        final String json = gson.toJson(new Typed());
        assertEquals("{\"name\":\"io.gsonfire.CustomReadHookTest$Typed\"}", json);

        final Typed instance = gson.fromJson(json, Typed.class);
        Assert.assertNotSame(staticTyped, instance);

        final HooksTypeAdapter<Typed> adapter = (HooksTypeAdapter<Typed>) gson.getAdapter(Typed.class);
        adapter.setPreRead(element -> Optional.of(staticTyped));
        Assert.assertEquals(staticTyped, staticTyped);
    }

    private static class Typed {
        String name = this.getClass().getName();
    }
}

