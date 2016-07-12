package writer;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.RegionUtils;
import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.AmazonKinesisClient;
import com.amazonaws.services.kinesisfirehose.AmazonKinesisFirehoseClient;
import model.LeadData;
import utils.ConfigurationUtils;
import utils.CredentialUtils;

import static writer.StreamWriter.*;


/**
 * Created by nkandavel on 7/8/16.
 */
public class leadProducerFirehouse {


    public static void main(String args[]) throws Exception {
        String streamName = "nintestdelvstr";
        String regionName = "us-east-1";
        Region region = RegionUtils.getRegion(regionName);
        if (region == null) {
            System.err.println(regionName + " is not a valid AWS region.");
            System.exit(1);
        }

        //Get the credentials
        AWSCredentials credentials = CredentialUtils.getCredentialsProvider().getCredentials();

        //Instantiate firehouse Client
        AmazonKinesisFirehoseClient firehoseClient = new AmazonKinesisFirehoseClient(credentials);
                //ConfigurationUtils.getClientConfigWithUserAgent());
        // Validate that the stream exists and is active
        validateStream(firehoseClient, streamName);

        // Repeatedly send stock trades with a 100 milliseconds wait in between
        StreamGenerator stockTradeGenerator = new StreamGenerator();
        for(int i=0;i<15;i++) {

            LeadData leadinfo = stockTradeGenerator.getRandomTrade();
            sendLeadInfo(leadinfo, firehoseClient, streamName);
        }
        /*while(true) {
            LeadData leadinfo = stockTradeGenerator.getRandomTrade();
            sendLeadInfo(leadinfo, firehoseClient, streamName);

            Thread.sleep(100);
        }*/


    }
}
