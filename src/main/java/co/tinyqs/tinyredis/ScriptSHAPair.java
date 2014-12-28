package co.tinyqs.tinyredis;

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
