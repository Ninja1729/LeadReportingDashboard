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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
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
    public static final String ELASTICSEARCH_CLUSTER = "dev-app";


    /**
     * {@inheritDoc}
     */
    public void initialize(String shardId) {

        //Set the elastic search cluster
        settings = Settings.settingsBuilder()
                .put("cluster.name", ELASTICSEARCH_CLUSTER).build();
        try {
            client = TransportClient.builder().settings(settings).build()
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("10.74.163.110"), 9300));
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        //initialize shards
        System.out.print("Initializing record processor for shard: " + shardId);
        this.kinesisShardId = shardId;

        // CreateIndexResponse ir = client.admin().indices().create(new CreateIndexRequest("twitter3")).actionGet();
        // System.out.print(ir.isAcknowledged());

    }

    /**
     * {@inheritDoc}
     */

    public void processRecords(List<Record> records, IRecordProcessorCheckpointer checkpointer) {

        for (Record record : records) {
            // process record
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
            //Put the lead info as json
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            Date date = df.parse(leadinfo.getLead_date());
            System.out.print(date.toString());

            XContentBuilder builder = jsonBuilder()
                    .startObject()
                    .field("leadId", leadinfo.getLead_id())
                    .field("leadDate", date)
                    .field("leadType", leadinfo.getLead_email())
                    .field("leadEmail", leadinfo.getLead_type())
                    .endObject();

            //index lead data
            IndexResponse response = client.prepareIndex("reported_lead1", "rtype")
                    .setSource(builder)
                    .get();
            System.out.print(response.isCreated());
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
