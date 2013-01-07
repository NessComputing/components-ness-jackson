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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Scopes;
import com.google.inject.Stage;
import com.google.inject.name.Names;

import org.junit.Assert;
import org.junit.Test;

import com.nesscomputing.config.ConfigModule;

public class TestJacksonFormatObjectMapperProvider
{
    @Inject @Json
    private ObjectMapper jsonMapper;

    @Inject @Smile
    private ObjectMapper smileMapper;

    @Inject
    private ObjectMapper defaultMapper;

    @Test
    public void testBasics()
    {
        final Injector injector = Guice.createInjector(Stage.PRODUCTION,
                                                       ConfigModule.forTesting(),
                                                       new NessJacksonModule());
        injector.injectMembers(this);
        Assert.assertNotNull(defaultMapper);
        Assert.assertNotNull(smileMapper);
        Assert.assertNotNull(jsonMapper);
        Assert.assertNotSame(defaultMapper, jsonMapper);
        Assert.assertNotSame(defaultMapper, smileMapper);
        Assert.assertNotSame(smileMapper, jsonMapper);
    }

    @Test
    public void testDefault()
    {
        final JacksonFormatObjectMapperProvider defaultProvider = new JacksonFormatObjectMapperProvider(new JacksonFormatConfig() {});

        final Injector injector = Guice.createInjector(Stage.PRODUCTION,
                                                       ConfigModule.forTesting(),
                                                       new NessJacksonModule(),
                                                       new AbstractModule() {
                                                           @Override
                                                           public void configure()
                                                           {
                                                               bind(ObjectMapper.class).annotatedWith(Names.named("default")).toProvider(defaultProvider).in(Scopes.SINGLETON);
                                                           }
                                                       });

        injector.injectMembers(this);
        final ObjectMapper defaultMapper = injector.getInstance(Key.get(ObjectMapper.class, Names.named("default")));

        Assert.assertNotNull(defaultMapper);
        Assert.assertSame(jsonMapper, defaultMapper);
        Assert.assertNotSame(smileMapper, defaultMapper);
    }

    @Test
    public void testJson()
    {
        final JacksonFormatObjectMapperProvider defaultProvider = new JacksonFormatObjectMapperProvider(new JacksonFormatConfig() {
            @Override
            public DataFormat getDataFormat()
            {
                return DataFormat.JSON;
            }
        });

        final Injector injector = Guice.createInjector(Stage.PRODUCTION,
                                                       ConfigModule.forTesting(),
                                                       new NessJacksonModule(),
                                                       new AbstractModule() {
                                                           @Override
                                                           public void configure()
                                                           {
                                                               bind(ObjectMapper.class).annotatedWith(Names.named("default")).toProvider(defaultProvider).in(Scopes.SINGLETON);
                                                           }
                                                       });

        injector.injectMembers(this);
        final ObjectMapper defaultMapper = injector.getInstance(Key.get(ObjectMapper.class, Names.named("default")));

        Assert.assertNotNull(defaultMapper);
        Assert.assertSame(jsonMapper, defaultMapper);
        Assert.assertNotSame(smileMapper, defaultMapper);
    }

    @Test
    public void testSmile()
    {
        final JacksonFormatObjectMapperProvider defaultProvider = new JacksonFormatObjectMapperProvider(new JacksonFormatConfig() {
            @Override
            public DataFormat getDataFormat()
            {
                return DataFormat.SMILE;
            }
        });

        final Injector injector = Guice.createInjector(Stage.PRODUCTION,
                                                       ConfigModule.forTesting(),
                                                       new NessJacksonModule(),
                                                       new AbstractModule() {
                                                           @Override
                                                           public void configure()
                                                           {
                                                               bind(ObjectMapper.class).annotatedWith(Names.named("default")).toProvider(defaultProvider).in(Scopes.SINGLETON);
                                                           }
                                                       });

        injector.injectMembers(this);
        final ObjectMapper defaultMapper = injector.getInstance(Key.get(ObjectMapper.class, Names.named("default")));

        Assert.assertNotNull(defaultMapper);
        Assert.assertNotSame(jsonMapper, defaultMapper);
        Assert.assertSame(smileMapper, defaultMapper);
    }
}

