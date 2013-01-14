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
package com.nesscomputing.jackson.datatype;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Stage;
import com.google.inject.TypeLiteral;
import com.google.inject.util.Modules;

import org.junit.Assert;
import org.junit.Test;

import com.nesscomputing.config.ConfigModule;
import com.nesscomputing.jackson.NessJacksonModule;

public class TestCustomUuidModule
{
    // This test ensures that the CustomUuidModule is correctly installed
    @Test
    public void testCustomUUID() throws Exception {
        final UUID orig = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        final AtomicBoolean called = new AtomicBoolean(false);
        ObjectMapper mapper = getObjectMapper(new AbstractModule() {
            @Override
            protected void configure() {
                bind(new TypeLiteral<JsonDeserializer<UUID>>() {}).toInstance(new CustomUuidDeserializer() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    protected UUID _deserialize(String value,
                            DeserializationContext ctxt) throws IOException, JsonProcessingException {
                        UUID foo = super._deserialize(value, ctxt);
                        called.set(true);
                        return foo;
                    }
                });
            }
        });
        UUID uuid = mapper.readValue('"' + orig.toString() + '"', new TypeReference<UUID>(){});
        Assert.assertEquals(orig, uuid);
        Assert.assertTrue(called.get());
    }

    private ObjectMapper getObjectMapper(final Module overrides)
    {
        final Injector injector = Guice.createInjector(Stage.PRODUCTION,
                Modules.override(ConfigModule.forTesting(), new NessJacksonModule()).with(overrides));

        return injector.getInstance(ObjectMapper.class);
    }
}
