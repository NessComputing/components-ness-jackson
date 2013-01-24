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
import com.fasterxml.jackson.dataformat.smile.SmileFactory;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.fasterxml.jackson.module.mrbean.MrBeanModule;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.nesscomputing.config.ConfigProvider;
import com.nesscomputing.jackson.datatype.NessCustomSerializerModule;

public final class NessJacksonModule extends AbstractModule
{
    @Override
    public void configure()
    {
        // Annotated version (@Json) is also bound to json.
        bind(ObjectMapper.class).annotatedWith(JsonMapper.class).toProvider(NessObjectMapperProvider.class).in(Scopes.SINGLETON);

        // Annotated version (@Smile) is bound to the smile factory.
        bind(ObjectMapper.class).annotatedWith(SmileMapper.class).toProvider(new NessObjectMapperProvider(new SmileFactory())).in(Scopes.SINGLETON);

        // Default (not annotated) instance is bound to json.
        bind(ObjectMapper.class).toProvider(NessObjectMapperProvider.class).in(Scopes.SINGLETON);

        bind(NessJacksonConfig.class).toProvider(ConfigProvider.of(NessJacksonConfig.class)).in(Scopes.SINGLETON);

        NessObjectMapperBinder.bindJacksonModule(binder()).toInstance(new GuavaModule());
        NessObjectMapperBinder.bindJacksonModule(binder()).toInstance(new JodaModule());

        install (new NessCustomSerializerModule());

        // MrBean is pretty safe to globally install, since it only deserializes types that would otherwise fail.
        NessObjectMapperBinder.bindJacksonModule(binder()).to(MrBeanModule.class);
    }

    @Override
    public int hashCode()
    {
        return getClass().hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        return obj != null && obj.getClass() == NessJacksonModule.class;
    }
}
