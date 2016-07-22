package writer;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.RegionUtils;
import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.AmazonKinesisClient;
import com.amazonaws.services.kinesisfirehose.AmazonKinesisFirehoseClient;
import model.LeadData;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import utils.ConfigurationUtils;
import utils.CredentialUtils;

import static writer.StreamWriter.*;


/**
 * Created by nkandavel on 7/8/16.
 */
public class leadProducerFirehouse {


    public static void main(String args[])  {
        try {
            String streamName = "nintestdelvstr";
            String regionName = "us-east-1";
            Region region = RegionUtils.getRegion(regionName);
            if (region == null) {
                System.err.println(regionName + " is not a valid AWS region.");
                System.exit(1);
            }

            //Get the credentials
            //AWSCredentials credentials = CredentialUtils.getCredentialsProvider().getCredentials();

            BasicAWSCredentials awsCreds = new BasicAWSCredentials("AKIAJRK24UQVD73GD2IA", "8hb2Msw1fbLL8S1db9s0UB08X7r3/bfRnUzYRT1F");

            System.out.println("came till here");

            //Instantiate firehouse Client
            AmazonKinesisFirehoseClient firehoseClient = new AmazonKinesisFirehoseClient(awsCreds);

            validateStream(firehoseClient, streamName);
            System.out.println("but not here");

            //generate Lead Data
            StreamGenerator leadGenerator = new StreamGenerator();

            //Path pt = new Path("hdfs://LBDEV3NN/dw/dataeng/cdc/reported_lead/2016/07/21/16/15/part-m-00000.gz");
            //leadGenerator.readLines(pt, new Configuration());

        for(int i=0;i<15;i++) {

            LeadData leadinfo = leadGenerator.getRandomTrade();
            sendLeadInfo(leadinfo, firehoseClient, streamName);
        }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}
