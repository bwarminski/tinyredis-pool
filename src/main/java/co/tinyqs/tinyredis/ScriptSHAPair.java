package co.tinyqs.tinyredis;

/**
 * A pairing between a redis LUA script and its SHA hash.
 */
public class ScriptSHAPair
{   
    private final String script;
    private final String SHA;
    public ScriptSHAPair(String script, String sHA)
    {
        super();
        this.script = script;
        SHA = sHA;
    }
    public String getScript()
    {
        return script;
    }
    public String getSHA()
    {
        return SHA;
    }
    
    
}
