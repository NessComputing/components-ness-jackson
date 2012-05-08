package ness.jackson;

import org.skife.config.Config;
import org.skife.config.Default;

public abstract class NessJacksonConfig
{
    public enum NessJacksonTimeFormat
    {
        MILLIS, ISO8601;
    }

    @Config("ness.jackson.time-format")
    @Default("MILLIS")
    public NessJacksonTimeFormat getTimeFormat()
    {
        return NessJacksonTimeFormat.MILLIS;
    }
}
