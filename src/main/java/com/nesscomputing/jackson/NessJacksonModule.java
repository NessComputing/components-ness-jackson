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

import java.util.UUID;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.smile.SmileFactory;

import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;
import com.nesscomputing.config.ConfigProvider;
import com.nesscomputing.jackson.CustomUuidDeserializer;
import com.nesscomputing.jackson.CustomUuidModule;

public class NessJacksonModule extends AbstractModule
{
    @Override
    public void configure()
    {
        // Annotated version (@Json) is also bound to json.
        bind(ObjectMapper.class).annotatedWith(Json.class).toProvider(NessObjectMapperProvider.class).in(Scopes.SINGLETON);

        // Annotated version (@Smile) is bound to the smile factory.
        bind(ObjectMapper.class).annotatedWith(Smile.class).toProvider(new NessObjectMapperProvider(new SmileFactory())).in(Scopes.SINGLETON);

        // Default (not annotated) instance is bound to json.
        bind(ObjectMapper.class).toProvider(NessObjectMapperProvider.class).in(Scopes.SINGLETON);

        bind(NessJacksonConfig.class).toProvider(ConfigProvider.of(NessJacksonConfig.class)).in(Scopes.SINGLETON);

        NessObjectMapperBinder.bindJacksonModule(binder()).toInstance(new GuavaModule());
        bind(new TypeLiteral<org.codehaus.jackson.map.JsonDeserializer<UUID>>() {}).to(CustomUuidDeserializer.class);
        NessObjectMapperBinder.bindJacksonModule(binder()).to(CustomUuidModule.class).in(Scopes.SINGLETON);
    }
}
