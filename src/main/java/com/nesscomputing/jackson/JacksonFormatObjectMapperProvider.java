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

import org.codehaus.jackson.map.ObjectMapper;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.nesscomputing.logging.Log;

/**
 * An ObjectMapper provider that can be configured to return json or smile.
 */
public class JacksonFormatObjectMapperProvider implements Provider<ObjectMapper>
{
    private final Log LOG = Log.findLog();
    private final JacksonFormatConfig jacksonFormatConfig;

    private ObjectMapper objectMapper = null;

    public JacksonFormatObjectMapperProvider(final JacksonFormatConfig jacksonFormatConfig)
    {
        this.jacksonFormatConfig = jacksonFormatConfig;
    }

    @Inject(optional=true)
    void injectObjectMappers(@Json final ObjectMapper jsonObjectMapper,
                             @Smile final ObjectMapper smileObjectMapper)
    {
        switch (jacksonFormatConfig.getDataFormat()) {

        case JSON:
            LOG.trace("Using JSON format");
            this.objectMapper = jsonObjectMapper;
            break;

        case SMILE:
            LOG.trace("Using SMILE format");
            this.objectMapper = smileObjectMapper;
            break;
        }
    }

    @Override
    public ObjectMapper get()
    {
        Preconditions.checkState(objectMapper != null, "Object mapper was not set!");
        return objectMapper;
    }
}


