package com.nesscomputing.jackson.datatype;

import java.util.UUID;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;

import com.nesscomputing.jackson.NessObjectMapperBinder;

public class NessCustomSerializerModule extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind(new TypeLiteral<JsonDeserializer<UUID>>() {}).to(CustomUuidDeserializer.class);

        NessObjectMapperBinder.bindJacksonModule(binder()).to(CustomUuidModule.class).in(Scopes.SINGLETON);
        NessObjectMapperBinder.bindJacksonModule(binder()).to(MapEntryModule.class).in(Scopes.SINGLETON);
        NessObjectMapperBinder.bindJacksonModule(binder()).to(CommonsLang3Module.class).in(Scopes.SINGLETON);
    }
}
