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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.JdkDeserializers.UUIDDeserializer;

import com.nesscomputing.uuid.NessUUID;

class CustomUuidDeserializer extends UUIDDeserializer
{
    private static final long serialVersionUID = 1L;

    @Override
    protected UUID _deserialize(String value, DeserializationContext ctxt)
    throws IOException, JsonProcessingException
    {
        return NessUUID.fromString(value);
    }
}
