package co.tinyqs.tinyredis.pool;

import java.util.Collections;
import java.util.List;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import co.tinyqs.tinyredis.RedisSerializer;

/**
 * Configuration object for a redis connection pool
 */
public class RedisConfiguration
{
    @Min(0)
    private int minSize = 8;
    
    @Min(1)
    private int maxSize = 256;
    
    @NotEmpty
    private String hostname = "localhost";
    
    @NotNull
    @Min(1)
    private int port = 6379;
    
    @Min(0)
    private int connectTimeout = 0;
    
    @NotNull
    private List<RedisSerializer> serializers = Collections.emptyList();

    public int getConnectTimeout()
    {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout)
    {
        this.connectTimeout = connectTimeout;
    }

    public int getMinSize()
    {
        return minSize;
    }

    public void setMinSize(int minSize)
    {
        this.minSize = minSize;
    }

    public int getMaxSize()
    {
        return maxSize;
    }

    public void setMaxSize(int maxSize)
    {
        this.maxSize = maxSize;
    }

    public String getHostname()
    {
        return hostname;
    }

    public void setHostname(String hostname)
    {
        this.hostname = hostname;
    }

    public int getPort()
    {
        return port;
    }

    public void setPort(int port)
    {
        this.port = port;
    }
    
    /**
     * List of serializers that will be registered with each pooled redis connection
     */
    public List<RedisSerializer> getSerializers()
    {
        return serializers;
    }

    public void setSerializers(List<RedisSerializer> serializers)
    {
        this.serializers = serializers;
    }
}
