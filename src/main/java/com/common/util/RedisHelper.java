package com.common.util;

import com.common.dict.Const;
import com.string.widget.util.ValueWidget;
import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by whuang on 17/3/14.
 */
public class RedisHelper {

    public static Logger logger = Logger.getLogger(RedisHelper.class);

    private RedisHelper() {//每次从池里取新连接
    }

    public static RedisHelper getInstance() {
        RedisHelper instance = new RedisHelper();

        return instance;
    }

    public synchronized void saveCache(String k, String v) {
        if (ValueWidget.isNullOrEmpty(v)) {
            return;
        }
        if (null == Const.pool) {
            Const.connectRedisServer();
            return;
        }
        Jedis jedis = null;
        try {
            jedis = Const.pool.getResource();
        } catch (redis.clients.jedis.exceptions.JedisConnectionException e) {
            e.printStackTrace();
            return;
        }

        try {
            jedis.set(k, v);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("saveCache", e);
            Const.pool.returnBrokenResource(jedis);
            jedis = null;
        } finally {
            if (jedis != null) {
                Const.pool.returnResource(jedis);
            }
        }

    }

    /***
     * Only set the key if it does not already exist
     *
     * @param k
     * @param v
     * @param second : second
     */
    public String saveExpxKeyCache(String k, String v, long second) {
        return saveExpxKeyCache(k, v, "NX", second);
    }

    /***
     * @param k
     * @param v
     * @param nxxx :  NX|XX, NX -- Only set the key if it does not already exist. XX -- Only set the key
     *             if it already exist.
     * @param time : second
     */
    public synchronized String saveExpxKeyCache(String k, String v, String nxxx, long time) {
        if (null == Const.pool) {
            Const.connectRedisServer();
            return null;
        }
        if (null == v) {
            return null;
        }
        Jedis jedis = null;
        try {
            jedis = Const.pool.getResource();
        } catch (redis.clients.jedis.exceptions.JedisConnectionException e) {
            e.printStackTrace();
            return null;
        }
        try {
            return jedis.set(k, v, nxxx, "EX"/*seconds*/, time);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("saveKeyCache", e);
            Const.pool.returnBrokenResource(jedis);
            jedis = null;
        } finally {
            if (jedis != null) {
                Const.pool.returnResource(jedis);
            }
        }
        return null;
    }

    public synchronized void saveKeyCache(String id, String k, String v) {
        if (null == v) {//modified by huangweii at 2018-03-19 ,if (ValueWidget.isNullOrEmpty(v)) {
            return;
        }
        if (null == Const.pool) {
            Const.connectRedisServer();
            return;
        }
        Jedis jedis = null;
        try {
            jedis = Const.pool.getResource();
        } catch (redis.clients.jedis.exceptions.JedisConnectionException e) {
            e.printStackTrace();
            return;
        }

        try {
            jedis.hset(id, k, v);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("saveKeyCache", e);
            Const.pool.returnBrokenResource(jedis);
            jedis = null;
        } finally {
            if (jedis != null) {
                Const.pool.returnResource(jedis);
            }
        }
    }

    public void saveKeyCacheExpire15days(String id, String k, String v) {
        saveKeyCacheAndExpire(id, k, v, 24 * 15);
    }

    public void saveKeyCacheExpire1hour(String id, String k, String v) {
        saveKeyCacheAndExpire(id, k, v, 1);
    }

    public void saveKeyCacheExpire1day(String id, String k, String v) {
        saveKeyCacheAndExpire(id, k, v, 24);
    }

    public void saveKeyCacheAndExpire(String id, String k, String v, int hours) {
        if (ValueWidget.isNullOrEmpty(v)) {
            return;
        }
        if (null == Const.pool) {
            Const.connectRedisServer();
            return;
        }
        Jedis jedis = null;
        try {
            jedis = Const.pool.getResource();
        } catch (redis.clients.jedis.exceptions.JedisConnectionException e) {
            e.printStackTrace();
            return;
        }
        try {
            jedis.hset(id, k, v);
//            jedis.expire(id, 60 * 60 * 24 * 15);
            jedis.expire(id, 60 * 60 * hours);

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("saveKeyCache", e);
            Const.pool.returnBrokenResource(jedis);
            jedis = null;
        } finally {
            if (jedis != null) {
                Const.pool.returnResource(jedis);
            }
        }

    }

    public void setExpire(String id, int hours) {
        if (null == Const.pool) {
            Const.connectRedisServer();
            return;
        }
        Jedis jedis = null;
        try {
            jedis = Const.pool.getResource();
        } catch (redis.clients.jedis.exceptions.JedisConnectionException e) {
            e.printStackTrace();
            return;
        }
        jedis.expire(id, 60 * 60 * hours);
    }

    public void saveAllKeyCache(String id, Map kv) {
        if (ValueWidget.isNullOrEmpty(id) || ValueWidget.isNullOrEmpty(kv)) {
            return;
        }
        if (null == Const.pool) {
            Const.connectRedisServer();
            return;
        }
        Jedis jedis = null;
        try {
            jedis = Const.pool.getResource();
        } catch (redis.clients.jedis.exceptions.JedisConnectionException e) {
            e.printStackTrace();
            return;
        }

        try {
            jedis.hmset(id, kv);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("saveAllKeyCache", e);
            Const.pool.returnBrokenResource(jedis);
            jedis = null;
        } finally {
            if (jedis != null) {
                Const.pool.returnResource(jedis);
            }
        }

    }

    public Long clearKeyCache(String id, String k) {
        if (null == Const.pool) {
            Const.connectRedisServer();
            return null;
        }
        Jedis jedis = null;
        try {
            jedis = Const.pool.getResource();
        } catch (redis.clients.jedis.exceptions.JedisConnectionException e) {
            e.printStackTrace();
            return null;
        }

        try {
            return jedis.hdel(id, k);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("clearKeyCache", e);
            Const.pool.returnBrokenResource(jedis);
            jedis = null;
        } finally {
            if (jedis != null) {
                Const.pool.returnResource(jedis);
            }
        }
        return null;
    }

    public String getCache(String k) {
        if (null == Const.pool) {
            Const.connectRedisServer();
            return null;
        }
        Jedis jedis = null;
        try {
            jedis = Const.pool.getResource();
        } catch (redis.clients.jedis.exceptions.JedisConnectionException e) {
            e.printStackTrace();
            return null;
        }
        String v = "";

        try {
            v = jedis.get(k);

        } catch (redis.clients.jedis.exceptions.JedisConnectionException ex) {
            Const.connectRedisServer();
            v = jedis.get(k);
        } catch (java.lang.ClassCastException e) {
            logger.error("getCache,k:" + k, e);
            try {
                logger.error("value:" + new String(jedis.get(k.getBytes(SystemHWUtil.CHARSET_UTF)), SystemHWUtil.CHARSET_UTF));
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("getCache", e);
            //Exception in thread "Thread-50" redis.clients.jedis.exceptions.JedisConnectionException: java.net.SocketTimeoutException: Read timed out
            //Caused by: java.lang.IllegalStateException: Returned object not currently part of this pool
            String errorMessage = e.getMessage();
            //java.lang.ClassCastException: java.lang.Long cannot be cast to [B
            //	at redis.clients.jedis.Connection.getBinaryBulkReply(Connection.java:259)
            if (errorMessage.contains("SocketTimeoutException")
                    || errorMessage.contains("Read timed out")
                    || errorMessage.contains("java.lang.Long cannot be cast to")
                    || errorMessage.contains("redis.clients.jedis.exceptions.JedisConnectionException")
                    || errorMessage.contains("Returned object not currently part of this pool")) {
                System.out.println("断开连接2 :");
                jedis.close();
            } else {
                Const.pool.returnBrokenResource(jedis);
            }
            jedis = null;
        } finally {
            if (jedis != null) {
                try {
                    Const.pool.returnResource(jedis);
                } catch (redis.clients.jedis.exceptions.JedisException e) {
                    e.printStackTrace();
                    Const.connectRedisServer();
                    Const.pool.returnResource(jedis);
                }
            }
        }

        return v;
    }

    public String getKeyCache(String id, String k) {
        if (null == Const.pool) {
            Const.connectRedisServer();
            return null;
        }
        Jedis jedis = null;
        try {
            jedis = Const.pool.getResource();
        } catch (redis.clients.jedis.exceptions.JedisConnectionException e) {
            e.printStackTrace();
            Const.connectRedisServer();
            return null;
        }
        String v = "";

        try {
            v = jedis.hget(id, k);
        } catch (redis.clients.jedis.exceptions.JedisConnectionException ex) {
            v = jedis.hget(id, k);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("getKeyCache", e);
            String errorMessage = e.getMessage();
            if (errorMessage.contains("Unknown reply")
                    || errorMessage.contains("java.lang.Long cannot be cast to")) {//redis.clients.jedis.exceptions.JedisConnectionException: Unknown reply: o
                System.out.println("关闭连接 :");
                jedis.close();
            } else {
                Const.pool.returnBrokenResource(jedis);
            }

            jedis = null;
        } finally {
            if (jedis != null) {
                Const.pool.returnResource(jedis);
            }
        }

        return v;
    }

    public Map getAllKeyCache(String id) {
        if (null == Const.pool) {
            Const.connectRedisServer();
            return new HashMap();
        }
        Jedis jedis = null;
        try {
            jedis = Const.pool.getResource();
        } catch (redis.clients.jedis.exceptions.JedisConnectionException e) {
            e.printStackTrace();
            return null;
        }
        Map v = null;

        try {
            v = jedis.hgetAll(id);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("getAllKeyCache", e);
            Const.pool.returnBrokenResource(jedis);
            jedis = null;
        } finally {
            if (jedis != null) {
                Const.pool.returnResource(jedis);
            }
        }
        return v;

    }

    public Long clearCache(String id) {
        if (null == Const.pool) {
            Const.connectRedisServer();
            return null;
        }
        Jedis jedis = null;
        try {
            jedis = Const.pool.getResource();
        } catch (redis.clients.jedis.exceptions.JedisConnectionException e) {
            e.printStackTrace();
            return null;
        }

        try {
            return jedis.del(id);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("clearCache", e);
            Const.pool.returnBrokenResource(jedis);
            jedis = null;
            Const.connectRedisServer();
        } finally {
            if (jedis != null) {
                Const.pool.returnResource(jedis);
            }
        }
        return null;
    }

    public static boolean isOk(String result) {
        return "OK".equals(result);
    }
}
