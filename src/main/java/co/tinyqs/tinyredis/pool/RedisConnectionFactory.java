package co.tinyqs.tinyredis.pool;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;

import co.tinyqs.tinyredis.Preconditions;
import co.tinyqs.tinyredis.RedisConnection;
import co.tinyqs.tinyredis.RedisReply;
import co.tinyqs.tinyredis.RedisSerializer;
import co.tinyqs.tinyredis.ScriptSHAPair;

public class RedisConnectionFactory implements PooledObjectFactory<RedisConnection>
{
    private final RedisConfiguration config;
    private final ScriptSHAPair[] scripts;
    private final String scriptExistsCmd;
    
    public RedisConnectionFactory(RedisConfiguration config)
    {
        this(config, Collections.<ScriptSHAPair>emptyList());
    }
    
    public RedisConnectionFactory(RedisConfiguration config, List<ScriptSHAPair> scripts)
    {
        this.config = config;
        this.scripts = scripts.toArray(new ScriptSHAPair[]{});
        
        StringBuilder sb = new StringBuilder();
        sb.append("SCRIPT EXISTS");
        for (ScriptSHAPair script : scripts)
        {
            sb.append(' ');
            sb.append(script.getSHA());
        }
        scriptExistsCmd = sb.toString();
    }
    
    public PooledObject<RedisConnection> makeObject() throws Exception
    {
        RedisConnection conn = RedisConnection.connect(new InetSocketAddress(config.getHostname(), 
                                                                             config.getPort()), 
                                                                             config.getConnectTimeout());
        
        for (RedisSerializer serializer : config.getSerializers())
        {
            conn.registerSerializer(serializer);
        }
        
        if (scripts.length > 0)
        {
            try
            {
                RedisReply reply = conn.sendCommand(scriptExistsCmd);
                Preconditions.checkState(reply.isArray(), "Expected array reply to script exists command");
                RedisReply[] replyElements = reply.getElements();
                Preconditions.checkState(replyElements.length == scripts.length, "Expected the same length of reply elements and the scripts sent");
                
                List<ScriptSHAPair> scriptsNeeded = new ArrayList<>();
                for (int i = 0; i < scripts.length; i++)
                {
                    RedisReply exists = replyElements[i];
                    Preconditions.checkState(exists.isInteger(), "Expected integer reply from SCRIPT EXISTS");
                    if (exists.getInteger() != 1)
                    {
                        scriptsNeeded.add(scripts[i]);
                        conn.appendCommand("SCRIPT LOAD %s", scripts[i].getScript());
                    }
                }
                
                for (ScriptSHAPair script : scriptsNeeded)
                {
                    RedisReply scriptLoad = conn.getReply();
                    Preconditions.checkState(scriptLoad.isString(), "Expected script reply from script load");
                    Preconditions.checkState(script.getSHA().equalsIgnoreCase(scriptLoad.getString()), "Expected SHA to match script");
                }
            }
            catch (Exception e)
            {
                conn.close();
                throw e;
            }
        }
        return new DefaultPooledObject<>(conn);
    }

    public void destroyObject(PooledObject<RedisConnection> p) throws Exception
    {
        RedisConnection conn = p.getObject();
        if (conn == null)
        {
            return;
        }
        
        conn.close();       
    }

    public boolean validateObject(PooledObject<RedisConnection> p)
    {
        RedisConnection conn = p.getObject();
        if (conn == null)
        {
            return false;
        }
        
        // TODO: Issue short ping and see if we get a response. Client needs a timeout option for responses.
        return true;
    }

    public void activateObject(PooledObject<RedisConnection> p) throws Exception
    {
        // TODO: Consider setting DB to 0, check PING, check sentinel, etc.
    }

    public void passivateObject(PooledObject<RedisConnection> p) throws Exception
    {
        // TODO: Consider setting DB to 0, check ping, check sentinel, etc.
    }
    
}
