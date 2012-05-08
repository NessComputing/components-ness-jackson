package ness.jackson;

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

