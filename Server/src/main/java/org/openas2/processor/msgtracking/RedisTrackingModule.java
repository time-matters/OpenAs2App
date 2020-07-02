package org.openas2.processor.msgtracking;


import com.lambdaworks.redis.ClientOptions;
import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.RedisURI;
import com.lambdaworks.redis.api.StatefulRedisConnection;
import com.lambdaworks.redis.api.async.RedisAsyncCommands;
import com.lambdaworks.redis.api.sync.RedisCommands;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openas2.OpenAS2Exception;
import org.openas2.Session;
import org.openas2.message.Message;

import java.util.List;
import java.util.Map;

/**
 * class to write log message to a redis server
 *
 */
public class RedisTrackingModule extends BaseMsgTrackingModule {

    private Log logger = LogFactory.getLog(RedisTrackingModule.class.getSimpleName());

    public final static String PARAM_HOST = "host";
    public final static String PARAM_PORT = "port";
    public final static String PARAM_PW = "pw";

    private String host = null;
    private String port = null;
    private String password = null;

    private RedisClient redisClient;
    private StatefulRedisConnection<String, String> connection;

    @Override
    public void init(Session session, Map<String, String> parameters) throws OpenAS2Exception {
        super.init(session, parameters);
        host = getParameter(PARAM_HOST, true);
        port = getParameter(PARAM_PORT, true);
        password = getParameter(PARAM_PW, true);

        try {
            redisClient = RedisClient.create(
                RedisURI.create("redis://" + password + "@" + host + ":" + port));
            redisClient.setOptions(ClientOptions.builder()
                //.requestQueueSize(requestQueueSize)
                .autoReconnect(true)
                .pingBeforeActivateConnection(true)
                .build()
            );

            connection = redisClient.connect();
            logger.info("Connected to Redis");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String getModuleAction() {
        return DO_TRACK_MSG;
    }

    @Override
    protected void persist(Message as2Msg, Map<String, String> map) {
        try {
            // only log messages with content to redis
            if (as2Msg==null || as2Msg.getData()==null || as2Msg.getData().getContent()==null) {
                logger.info("Empty message!!!");
                return;
            }

            RedisAsyncCommands<String, String> redisCmd = connection.async();
            redisCmd.set(as2Msg.getLogMsgID(), as2Msg.getData().getContent().toString());

            logger.info("Send " + as2Msg.getLogMsgID() + " to redis");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isRunning() {
        return connection.isOpen();
    }

    @Override
    public void start() throws OpenAS2Exception {

    }

    @Override
    public void stop() throws OpenAS2Exception {
        connection.close();
        redisClient.shutdown();
    }

    @Override
    public boolean healthcheck(List<String> failures) {
        RedisCommands<String, String> redisCmd = connection.sync();
        return "PONG".equals(redisCmd.ping());
    }
}
