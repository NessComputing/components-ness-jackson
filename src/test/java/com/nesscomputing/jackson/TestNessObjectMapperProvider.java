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

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.type.TypeReference;
import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Scopes;
import com.google.inject.Stage;
import com.google.inject.TypeLiteral;
import com.google.inject.util.Modules;
import com.nesscomputing.config.ConfigModule;
import com.nesscomputing.jackson.NessJacksonModule;
import com.nesscomputing.jackson.NessObjectMapperBinder;
import com.nesscomputing.jackson.CustomUuidDeserializer;

public class TestNessObjectMapperProvider
{
    private ObjectMapper getObjectMapper(final Module module)
    {
        return getObjectMapper(module, new AbstractModule() { @Override protected void configure() {} });
    }

    private ObjectMapper getObjectMapper(final Module module, final Module overrides)
    {
        final Injector injector = Guice.createInjector(Stage.PRODUCTION,
                Modules.override(
                                                       ConfigModule.forTesting(),
                                                       new NessJacksonModule(),
                                                       new AbstractModule() {
            @Override
            public void configure() {
                if (module != null) {
                    install(module);
                }
            }
        }).with(overrides));

        return injector.getInstance(ObjectMapper.class);
    }

    @Test
    public void testSimple()
    {
        final ObjectMapper mapper = getObjectMapper(null);
        Assert.assertNotNull(mapper);
    }

	// This test ensures that the GuavaModule is correctly installed
	@Test
	public void testMultisetDeserialization() throws Exception {
	    ObjectMapper mapper = getObjectMapper(new AbstractModule() {
            @Override
            protected void configure() {
                NessObjectMapperBinder.bindJacksonModule(binder()).to(GuavaModule.class).in(Scopes.SINGLETON);
            }
        });
        Multiset<String> set = mapper.readValue("[\"a\",\"a\"]", new TypeReference<HashMultiset<String>>() {});
	    Assert.assertEquals(ImmutableMultiset.of("a", "a"), set);

	    Multimap<String, String> map = mapper.readValue("{\"a\":[\"b\",\"c\"]}", new TypeReference<ImmutableMultimap<String, String>>() {});
	    Assert.assertEquals(ImmutableMultimap.of("a", "b", "a", "c"), map);
	}

    // This test ensures that the CustomUuidModule is correctly installed
    @Test
    public void testCustomUUID() throws Exception {
        final UUID orig = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        final AtomicBoolean called = new AtomicBoolean(false);
        ObjectMapper mapper = getObjectMapper(null, new AbstractModule() {
            @Override
            protected void configure() {
                bind(new TypeLiteral<JsonDeserializer<UUID>>() {}).toInstance(new CustomUuidDeserializer() {
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

    public static class DummyBean
    {
        private final String dummyValue;

        @JsonCreator
        public DummyBean(@JsonProperty final String dummyValue) {
            this.dummyValue = dummyValue;
        }

        public String getDummyValue()
        {
            return dummyValue;
        }
    }

    public static final class ExtendedDummyBean extends DummyBean
    {
        @JsonCreator
        public ExtendedDummyBean(@JsonProperty final String dummyValue) {
            super(dummyValue);
        }

    }

    public static final class DummySerializer extends JsonSerializer<DummyBean>
    {
        @Override
        public void serialize(DummyBean value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException
        {
            jgen.writeStartObject();
            jgen.writeStringField("dummyValue", value.getDummyValue() + "world");
            jgen.writeEndObject();
        }

    }

    public static final class DummyDeserializer extends JsonDeserializer<DummyBean>
    {

        @Override
        public DummyBean deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException
        {
            return new DummyBean("hello, world");
        }
    }
}
