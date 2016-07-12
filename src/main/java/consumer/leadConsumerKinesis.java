package consumer;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.RegionUtils;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.IRecordProcessorFactory;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.KinesisClientLibConfiguration;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.Worker;
import utils.CredentialUtils;
import utils.ConfigurationUtils;

import java.util.UUID;

/**
 * Created by nkandavel on 7/8/16.
 */
public class leadConsumerKinesis {
    public static void main(String args[]) throws Exception{

        String applicationName = "";

        String streamName = "nintestdelvstr";
        String region = "us-east-1";
        if (region == null) {
            System.err.println(args[2] + " is not a valid AWS region.");
            System.exit(1);
        }


        AWSCredentialsProvider credentialsProvider = CredentialUtils.getCredentialsProvider();

        String workerId = String.valueOf(UUID.randomUUID());
        KinesisClientLibConfiguration kclConfig =
                new KinesisClientLibConfiguration(applicationName, streamName, credentialsProvider, workerId)
                        .withRegionName(region)
                        .withCommonClientConfig(ConfigurationUtils.getClientConfigWithUserAgent());

        IRecordProcessorFactory recordProcessorFactory = new LeadsProcessorFactory();

        // Create the KCL worker with the stock trade record processor factory
        Worker worker = new Worker(recordProcessorFactory, kclConfig);

        int exitCode = 0;
        try {
            worker.run();
        } catch (Throwable t) {
            System.out.print("Caught throwable while processing data.");
            exitCode = 1;
        }
        System.exit(exitCode);

    }
}
