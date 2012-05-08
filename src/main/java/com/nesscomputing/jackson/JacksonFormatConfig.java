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

import org.skife.config.Config;
import org.skife.config.Default;

public abstract class JacksonFormatConfig
{
    public enum DataFormat
    {
       /** Store JSON in the user cache. */
       JSON,

       /** Store SMILE in the user cache. */
       SMILE
    }

    /**
     * Returns a data format selected for Jackson. This should be used in
     * conjunction with a bean prefix to select the format for various object
     * mappers.
     */
    @Config("data-format")
    @Default("JSON")
    public DataFormat getDataFormat()
    {
        return DataFormat.JSON;
    }
}

