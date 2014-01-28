/**
 * Copyright (C) 2012 Ness Computing, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nesscomputing.jackson;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import com.google.common.base.Function;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;

import com.nesscomputing.config.Config;
import com.nesscomputing.config.ConfigModule;

import org.junit.Test;

public class TestSerializerBinderBuilder {

    @Inject
    @JsonSerializerFunction
    Function<Integer, String> serializer;

    @Inject
    @JsonDeserializerFunction
    Function<String, Integer> deserializer;

    @Inject
    @SmileSerializerFunction
    Function<Integer, byte[]> serializerBytes;

    @Inject
    @SmileDeserializerFunction
    Function<byte[], Integer> deserializerBytes;

    @Test
    public void testBasicSerialization() {
        Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                install (new ConfigModule(Config.getEmptyConfig()));
                install (new NessJacksonModule());

                JacksonSerializerBinder.builderOf(binder(), Integer.class)
                    .bind();
            }
        }).injectMembers(this);

        String serialized = serializer.apply(3);

        assertEquals("3", serialized);

        assertEquals(3, (int) deserializer.apply(serialized));
    }

    @Test
    public void testSmileSerialization() {
        Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                install (new ConfigModule(Config.getEmptyConfig()));
                install (new NessJacksonModule());

                JacksonSerializerBinder.builderOf(binder(), Integer.class)
                    .bind();
            }
        }).injectMembers(this);

        byte[] serialized = serializerBytes.apply(3);

        assertArrayEquals(new byte[] {58, 41, 10, 1, -58}, serialized);

        assertEquals(3, (int) deserializerBytes.apply(serialized));
    }

    @Test
    public void testDuplicateBindings()
    {
        Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure()
            {
                install (new ConfigModule(Config.getEmptyConfig()));
                install (new NessJacksonModule());

                JacksonSerializerBinder.builderOf(binder(), Integer.class).bind();
                JacksonSerializerBinder.builderOf(binder(), Integer.class).bind();
            }
        });
    }
}
