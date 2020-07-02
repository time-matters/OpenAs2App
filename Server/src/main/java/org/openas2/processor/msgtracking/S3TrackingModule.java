package org.openas2.processor.msgtracking;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openas2.OpenAS2Exception;
import org.openas2.Session;
import org.openas2.message.Message;

import java.util.List;
import java.util.Map;

/**
 * class to write log message to a aws S3
 *
 */
public class S3TrackingModule extends BaseMsgTrackingModule {

    private Log logger = LogFactory.getLog(S3TrackingModule.class.getSimpleName());

    private final static String PARAM_REGION = "region";
    private final static String PARAM_BUCKET = "bucket";

    private String bucket = null;

    private AmazonS3 s3Client;

    @Override
    public void init(Session session, Map<String, String> parameters) throws OpenAS2Exception {
        super.init(session, parameters);
        bucket = getParameter(PARAM_BUCKET, true);
        String region = getParameter(PARAM_REGION, true);

        try {

            //This code expects that you have AWS credentials set up per:
            // https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/setup-credentials.html
            s3Client = AmazonS3ClientBuilder.standard().withRegion(Regions.fromName(region)).build();

        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    protected String getModuleAction() {
        return DO_TRACK_MSG;
    }

    @Override
    protected void persist(Message as2Msg, Map<String, String> map) {
        try {
            // only log messages with content to aws s3
            if (as2Msg==null || as2Msg.getData()==null || as2Msg.getData().getContent()==null) {
                logger.info("Empty message!!!");
                return;
            }

            s3Client.putObject(bucket, as2Msg.getLogMsgID(),
                as2Msg.getData().getContent().toString());

            logger.info("Put " + as2Msg.getLogMsgID() + " in bucket " + bucket);

        } catch (AmazonServiceException e) {
            logger.error("AmazonServiceException: " + e.getMessage());
        } catch (SdkClientException e) {
            logger.error("SdkClientException: " + e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public boolean isRunning() {
        return true;
    }

    @Override
    public void start() throws OpenAS2Exception {

    }

    @Override
    public void stop() throws OpenAS2Exception {
        s3Client.shutdown();
    }

    @Override
    public boolean healthcheck(List<String> failures) {
        return s3Client!=null && s3Client.doesBucketExistV2(bucket);
    }
}
