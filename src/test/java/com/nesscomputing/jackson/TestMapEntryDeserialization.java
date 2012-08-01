package com.nesscomputing.jackson;

import static org.junit.Assert.assertEquals;

import java.util.Map.Entry;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Maps;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.nesscomputing.config.ConfigModule;

public class TestMapEntryDeserialization
{
    @Inject
    ObjectMapper mapper;

    @Before
    public void setUp()
    {
        Guice.createInjector(ConfigModule.forTesting(), new NessJacksonModule()).injectMembers(this);
    }

    @Test
    public void testMapEntrySerialization() throws Exception
    {
        Entry<String, Integer> entry = Maps.immutableEntry("hi", 3);
        assertEquals(entry, mapper.readValue(mapper.writeValueAsString(entry), new TypeReference<Entry<String, Integer>>() {}));
    }

    @Test
    public void testMapEntryNullValueSerialization() throws Exception
    {
        Entry<String, Integer> entry = Maps.immutableEntry("hi", null);
        assertEquals(entry, mapper.readValue(mapper.writeValueAsString(entry), new TypeReference<Entry<String, Integer>>() {}));
    }

    @Test
    public void testMapEntryNullKeySerialization() throws Exception
    {
        Entry<String, Integer> entry = Maps.immutableEntry(null, 3);
        assertEquals(entry, mapper.readValue(mapper.writeValueAsString(entry), new TypeReference<Entry<String, Integer>>() {}));
    }
}
