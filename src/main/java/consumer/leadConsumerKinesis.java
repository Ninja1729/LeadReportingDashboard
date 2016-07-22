package consumer;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.RegionUtils;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.IRecordProcessorFactory;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.KinesisClientLibConfiguration;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.Worker;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import utils.CredentialUtils;
import utils.ConfigurationUtils;

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by nkandavel on 7/8/16.
 */
public class leadConsumerKinesis {

    private static final Log LOG = LogFactory.getLog(leadConsumerKinesis.class);

    private static final Logger ROOT_LOGGER = Logger.getLogger("");
    private static final Logger PROCESSOR_LOGGER =
            Logger.getLogger("com.amazonaws.services.kinesis.consumer");


    public static void main(String args[]) throws Exception{

        String applicationName = "nintest";

        String streamName = "nintestdelvstr";
        Region region = RegionUtils.getRegion("us-east-1");
        if (region == null) {
            System.err.println(args[2] + " is not a valid AWS region.");
            System.exit(1);
        }

        ROOT_LOGGER.setLevel(Level.WARNING);
        PROCESSOR_LOGGER.setLevel(Level.INFO);

        AWSCredentialsProvider credentialsProvider = CredentialUtils.getCredentialsProvider();

        String workerId = String.valueOf(UUID.randomUUID());
        KinesisClientLibConfiguration kclConfig =
                new KinesisClientLibConfiguration(applicationName, streamName, credentialsProvider, workerId)
                        .withRegionName(region.getName())
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
