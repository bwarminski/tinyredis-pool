tinyredis-pool
==============

A connection pool for your tinyredis clients. See https://github.com/bwarminski/tinyredis for a description of what tinyredis is
and what it solves.

Installation
------------

Prior to packaging, make sure tinyqs in available in your local maven repo. (If this catches on, we can add it to a public repo)

Only needs to be done once unless there's a version bump for some reason (such as an async netty implementation of tinyreds or automatic sentinel failover, both of which would be awesome)

```
cd tinyredis/
mvn package
mvn install:install-file -Dfile=target/tinyredis-$(VERSION).jar -DgroupId=co.tinyqs -DartifactId=tinyredis-Dversion=$(VERSION) -Dpackaging=jar
```

Afterward, a simple `mvn package` in the root directory should get the job done.

Basic Usage
-----------

```java
String luaScriptFilename = "redisScript.lua";
ScriptSHAPair myScript = null;
try (Reader reader = new InputStreamReader(Preconditions.checkNotNull(Scripts.class.getResourceAsStream(luaScriptFilename)), "UTF-8"))
{
    String script = CharStreams.toString(reader);
    String sha = Hashing.sha1().hashString(script, Charsets.UTF_8).toString();
    myScript = new ScriptSHAPair(script, sha);
}

RedisConfiguration config = new RedisConfiguration();
// Set port / host on configuration, or just use the defaults

RedisConnectionPool pool = new RedisConnectionPool(config, ImmutableList.of(myScript));

RedisConnection conn = pool.borrowObject();
try
{   
    // Talk with redis. Note your script will be pre-hashed, so you can use EVALSHA as usual
    context.returnObject(conn);
    conn = null;
}
finally
{
    if (conn != null)
    {
        context.invalidateObject(conn);
    }
}
```
