package com.nesscomputing.jackson.datatype;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Map.Entry;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

public class CommonsLang3SerializerTest
{
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new CommonsLang3Module()).registerModule(new MapEntryModule());

    @Test
    public void testPair() throws IOException
    {
        Pair<String, Boolean> pair = Pair.of("foo", true);
        Pair<String, Boolean> newPair = mapper.readValue(mapper.writeValueAsBytes(pair), new TypeReference<Pair<String, Boolean>>() { });
        assertEquals(pair, newPair);
    }

    @Test
    public void testEntryCrossover() throws IOException
    {
        Entry<String, Boolean> pair = Maps.immutableEntry("foo", true);
        Pair<String, Boolean> newPair = mapper.readValue(mapper.writeValueAsBytes(pair), new TypeReference<Pair<String, Boolean>>() { });
        assertEquals(pair.getKey(), newPair.getKey());
        assertEquals(pair.getValue(), newPair.getValue());
        pair = mapper.readValue(mapper.writeValueAsBytes(newPair), new TypeReference<Entry<String, Boolean>>() { });
        assertEquals(newPair.getKey(), pair.getKey());
        assertEquals(newPair.getValue(), pair.getValue());
    }

    @Test
    public void testTrumpetPairCompatibility() throws IOException
    {
        Pair<String, Boolean> pair = mapper.readValue("{\"car\":\"foo\",\"cdr\":true}", new TypeReference<Pair<String, Boolean>>() { });
        assertEquals("foo", pair.getKey());
        assertEquals(true, pair.getValue());
    }
}
