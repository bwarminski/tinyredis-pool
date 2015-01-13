package co.tinyqs.tinyredis.pool;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import co.tinyqs.tinyredis.RedisConnection;
import co.tinyqs.tinyredis.ScriptSHAPair;

/**
 * Given a configuration, sets up an Apache Commons pool of RedisConnections
 */
public class RedisConnectionPool implements ObjectPool<RedisConnection>
{
    private final ObjectPool<RedisConnection> connectionPool;
    
    public RedisConnectionPool(RedisConfiguration config)
    {
        this(config, Collections.<ScriptSHAPair>emptyList());
    }
    
    public RedisConnectionPool(RedisConfiguration config, List<ScriptSHAPair> scripts)
    {
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        poolConfig.setMaxTotal(config.getMaxSize());
        poolConfig.setMinIdle(config.getMinSize());
        connectionPool = new GenericObjectPool<>(new RedisConnectionFactory(config, scripts), poolConfig);
    }

    @Override
    public RedisConnection borrowObject() throws Exception, NoSuchElementException, IllegalStateException
    {
        return connectionPool.borrowObject();
    }

    @Override
    public void returnObject(RedisConnection obj) throws Exception
    {
        connectionPool.returnObject(obj);
    }

    @Override
    public void invalidateObject(RedisConnection obj) throws Exception
    {
        connectionPool.invalidateObject(obj);
    }

    @Override
    public void addObject() throws Exception, IllegalStateException, UnsupportedOperationException
    {
        connectionPool.addObject();
    }

    @Override
    public int getNumIdle()
    {
        return connectionPool.getNumIdle();
    }

    @Override
    public int getNumActive()
    {
        return connectionPool.getNumActive();
    }

    @Override
    public void clear() throws Exception, UnsupportedOperationException
    {
        connectionPool.clear();
    }

    @Override
    public void close()
    {
        connectionPool.close();
    }

}
