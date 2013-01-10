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

import java.util.UUID;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.inject.Inject;

class CustomUuidModule extends SimpleModule
{
    private static final long serialVersionUID = 1L;

    @Inject
    public CustomUuidModule(JsonDeserializer<UUID> d) {
        super("CustomUuidModule", new Version(2, 0, 0, null, "com.nesscomputing.components", "ness-jackson/CustomUuidModule"));
        addDeserializer(UUID.class, d);
    }
}
