package com.github.julman99.gsonfire.gson;

import com.github.julman99.gsonfire.GsonFireBuilder;
import com.github.julman99.gsonfire.annotations.ExposeMethodResult;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @autor: julio
 */
public class ExposeMethodResultTest {

    @Test
    public void test(){
        GsonFireBuilder builder = new GsonFireBuilder()
                .enableExposeMethodResult();

        Gson gson = builder.createGson();

        B a = new B();
        a.a = "a";
        a.b = "b";

        JsonObject obj = gson.toJsonTree(a).getAsJsonObject();

        assertEquals(obj.get("a").getAsString(), "a");
        assertEquals(obj.get("pub").getAsString(), "a-pub");
        assertEquals(obj.get("pro").getAsString(), "a-pro");
        assertEquals(obj.get("pri").getAsString(), "a-pri");
        assertEquals(obj.get("b").getAsString(), "b");
        assertEquals(obj.get("pub2").getAsString(), "b-pub2");
        assertEquals(obj.get("pro2").getAsString(), "b-pro2");
        assertEquals(obj.get("pri2").getAsString(), "b-pri2");
    }

    private class A{
        public String a;

        @ExposeMethodResult("pub")
        public String pub(){
            return a + "-pub";
        }

        @ExposeMethodResult("pro")
        protected String pro(){
            return a + "-pro";
        }

        @ExposeMethodResult("pri")
        private String pri(){
            return a + "-pri";
        }
    }

    private class B extends A{
        public String b;

        @ExposeMethodResult("pub2")
        public String pub2(){
            return b + "-pub2";
        }

        @ExposeMethodResult("pro2")
        protected String pro2(){
            return b + "-pro2";
        }

        @ExposeMethodResult("pri2")
        private String pri2(){
            return b + "-pri2";
        }
    }
}
