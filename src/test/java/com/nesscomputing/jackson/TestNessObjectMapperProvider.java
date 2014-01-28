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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
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
import com.google.inject.util.Modules;

import com.nesscomputing.config.ConfigModule;

import org.junit.Assert;
import org.junit.Test;

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
