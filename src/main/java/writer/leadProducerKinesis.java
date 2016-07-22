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
public class leadProducerKinesis {
    public static void main(String args[]) throws Exception {
        String streamName = "nintestdelvstr";
        String regionName = "us-east-1";
        Region region = RegionUtils.getRegion(regionName);


        //Get the credentials
        AWSCredentials credentials = CredentialUtils.getCredentialsProvider().getCredentials();

        //Instantiate kinesis Client
        AmazonKinesis kinesisClient = new AmazonKinesisClient(credentials,
                ConfigurationUtils.getClientConfigWithUserAgent());
        kinesisClient.setRegion(region);

        // Validate that the stream exists and is active
        validateStream(kinesisClient, streamName);


        // Repeatedly send stock trades with a 100 milliseconds wait in between
        StreamGenerator stockTradeGenerator = new StreamGenerator();


        while(true) {
            LeadData leadinfo = stockTradeGenerator.getRandomTrade();
            sendLeadInfo(leadinfo, kinesisClient, streamName);

            Thread.sleep(100);
        }


    }
}
