package consumer;

import com.amazonaws.services.kinesis.clientlibrary.exceptions.InvalidStateException;
import com.amazonaws.services.kinesis.clientlibrary.exceptions.ShutdownException;
import com.amazonaws.services.kinesis.clientlibrary.exceptions.ThrottlingException;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.IRecordProcessor;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.IRecordProcessorCheckpointer;
import com.amazonaws.services.kinesis.clientlibrary.types.ShutdownReason;
import com.amazonaws.services.kinesis.metrics.impl.InterceptingMetricsFactory;
import com.amazonaws.services.kinesis.model.Record;
import model.LeadData;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.InetAddress;
import java.util.List;

import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;

import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;

import java.io.IOException;
import java.net.InetAddress;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

/**
 * Created by nkandavel on 7/11/16.
 */
public class LeadsProcessor implements IRecordProcessor {
    static int number = 0;


    private String kinesisShardId;

    private Settings settings;
    private Client client;


    /**
     * {@inheritDoc}
     */
    public void initialize(String shardId) {
        System.out.print("am here baby!");
        settings = Settings.settingsBuilder()
                .put("cluster.name", "dev-app").build();
        try {
            client = TransportClient.builder().settings(settings).build()
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("10.74.163.110"), 9300));
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        System.out.print("Initializing record processor for shard: " + shardId);
        this.kinesisShardId = shardId;

    }

    /**
     * {@inheritDoc}
     */

    public void processRecords(List<Record> records, IRecordProcessorCheckpointer checkpointer) {
        System.out.println("came here baby!!!!!!!!!!!"+number++);
        for (Record record : records) {
            // process record
            //System.out.println(record.getData());
            processRecord(record);
        }


    }



    private void processRecord(Record record) {
        LeadData leadinfo = LeadData.fromJsonAsBytes(record.getData().array());
        if (leadinfo == null) {
            System.out.println("Skipping record. Unable to parse record into StockTrade. Partition Key: " + record.getPartitionKey());
            return;
        }
        System.out.println(leadinfo);

        try {
            // CreateIndexResponse ir = client.admin().indices().create(new CreateIndexRequest("twitter2")).actionGet();
            // System.out.print(ir.isAcknowledged());


            XContentBuilder builder = jsonBuilder()
                    .startObject()
                    .field("id", leadinfo.getLead_id())
                    .field("leadDate", leadinfo.getLead_date())
                    .field("type", leadinfo.getLead_email())
                    .field("email", leadinfo.getLead_type())
                    .endObject();

            IndexResponse response = client.prepareIndex("reported_lead", "leaddata", Integer.toString(number))
                    .setSource(builder)
                    .get();
        } catch(Exception ex) {
            System.out.print(ex.getMessage());
        }

    }

    /**
     * {@inheritDoc}
     */
    public void shutdown(IRecordProcessorCheckpointer checkpointer, ShutdownReason reason) {
        System.out.println("Shutting down record processor for shard: " + kinesisShardId);
        // Important to checkpoint after reaching end of shard, so we can start processing data from child shards.
        if (reason == ShutdownReason.TERMINATE) {
            checkpoint(checkpointer);
        }
    }

    private void checkpoint(IRecordProcessorCheckpointer checkpointer) {
        System.out.println("Checkpointing shard " + kinesisShardId);
        try {
            checkpointer.checkpoint();
        } catch (ShutdownException se) {
            // Ignore checkpoint if the processor instance has been shutdown (fail over).
            System.out.println("Caught shutdown exception, skipping checkpoint.");
        } catch (ThrottlingException e) {
            // Skip checkpoint when throttled. In practice, consider a backoff and retry policy.
            System.out.println("Caught throttling exception, skipping checkpoint.");
        } catch (InvalidStateException e) {
            // This indicates an issue with the DynamoDB table (check for table, provisioned IOPS).
            System.out.println("Cannot save checkpoint to the DynamoDB table used by the Amazon Kinesis Client Library.");
        }
    }

}
