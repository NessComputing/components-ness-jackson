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
import com.google.inject.Injector;
import com.google.inject.Stage;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.nesscomputing.config.ConfigModule;

public class TestNessJacksonModule
{
    @Test
    public void testSimple()
    {
        final Injector injector = Guice.createInjector(Stage.PRODUCTION,
                                                       ConfigModule.forTesting(),
                                                       new NessJacksonModule());

        final ObjectMapper mapper = injector.getInstance(ObjectMapper.class);

        Assert.assertNotNull(mapper);
    }

    @Ignore // This isn't supported...
    @Test
    public void testSafeToMultiplyInject()
    {
        final Injector injector = Guice.createInjector(Stage.PRODUCTION,
                                                       ConfigModule.forTesting(),
                                                       new NessJacksonModule(),
            new AbstractModule() {
                @Override
                protected void configure() {
                    install(new NessJacksonModule());
                }
            });

        final ObjectMapper mapper = injector.getInstance(ObjectMapper.class);

        Assert.assertNotNull(mapper);
    }
}
