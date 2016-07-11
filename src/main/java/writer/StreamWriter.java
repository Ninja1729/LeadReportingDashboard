package writer;

import java.nio.ByteBuffer;

import com.amazonaws.services.kinesis.model.DescribeStreamRequest;
import com.amazonaws.services.kinesis.model.DescribeStreamResult;
import com.amazonaws.services.kinesis.model.PutRecordRequest;
import com.amazonaws.services.kinesis.model.ResourceNotFoundException;
import com.amazonaws.services.kinesisfirehose.AmazonKinesisFirehoseClient;
import com.amazonaws.services.kinesisfirehose.model.DescribeDeliveryStreamRequest;
import com.amazonaws.services.kinesisfirehose.model.DescribeDeliveryStreamResult;
import com.amazonaws.services.kinesisfirehose.model.PutRecordResult;
import com.amazonaws.services.kinesisfirehose.model.Record;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.RegionUtils;
import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.AmazonKinesisClient;
import model.LeadData;
import utils.ConfigurationUtils;
import utils.CredentialUtils;


/**
 * Created by nkandavel on 7/7/16.
 */
public class StreamWriter {
    /**
     * Uses the Kinesis client to send the lead info to the given stream.
     *
     * @param leadinfo Instance representing the lead info
     * @param kinesisClient Amazon Kinesis client
     * @param streamName Name of stream
     */
    public static void sendLeadInfo(LeadData leadinfo, AmazonKinesis kinesisClient,
                                       String streamName) {
        byte[] bytes = leadinfo.toJsonAsBytes();
        // The bytes could be null if there is an issue with the JSON serialization by the Jackson JSON library.
        if (bytes == null) {
            System.out.println("Could not get JSON bytes for stock trade");
            return;
        }

        System.out.println("Putting trade: " + leadinfo.toString());


        PutRecordRequest putRecord = new PutRecordRequest();
        putRecord.setStreamName(streamName);
        // We use the lead type as the partition key
        putRecord.setPartitionKey(leadinfo.getLead_type());
        putRecord.setData(ByteBuffer.wrap(bytes));
        try {
            com.amazonaws.services.kinesis.model.PutRecordResult result = kinesisClient.putRecord(putRecord);
            System.out.println("SequenceId"+result.getShardId());
        } catch (AmazonClientException ex) {
            System.out.println("Error sending record to Amazon Kinesis.");
        }
    }

    /**
     * Uses the Kinesis Firehouse client to send the lead info to the given stream.
     *
     * @param leadinfo Instance representing the lead info
     * @param firehoseClient Amazon Kinesis Firehouse client
     * @param streamName Name of stream
     */
    public static void sendLeadInfo(LeadData leadinfo, AmazonKinesisFirehoseClient firehoseClient,
                                       String streamName) {
        byte[] bytes = leadinfo.toJsonAsBytes();
        // The bytes could be null if there is an issue with the JSON serialization by the Jackson JSON library.
        if (bytes == null) {
            System.out.println("Could not get JSON bytes for stock trade");
            return;
        }

        System.out.println("Putting trade: " + leadinfo.toString());


        Record record = new Record();
        record.setData(ByteBuffer.wrap(bytes));
        com.amazonaws.services.kinesisfirehose.model.PutRecordRequest putRecordRequest = new com.amazonaws.services.kinesisfirehose.model.PutRecordRequest()
                .withDeliveryStreamName(streamName)
                .withRecord(record);
        putRecordRequest.setRecord(record);
        try {
            PutRecordResult result = firehoseClient.putRecord(putRecordRequest);
            System.out.println("RecordId"+result.getRecordId());
        } catch (AmazonClientException ex) {
            System.out.println("Error sending record to Amazon Kinesis.");
        }
    }

    //Validate Firehouse Client
    public static void validateStream(AmazonKinesisFirehoseClient firehoseClient, String streamName) {

        try {

            DescribeDeliveryStreamRequest dr = new DescribeDeliveryStreamRequest();
            dr.setDeliveryStreamName(streamName);
            DescribeDeliveryStreamResult result = firehoseClient.describeDeliveryStream(dr);
            if(!"ACTIVE".equals(result.getDeliveryStreamDescription().getDeliveryStreamStatus())) {
                System.err.println("Stream " + streamName + " is not active. Please wait a few moments and try again.");
                System.exit(1);
            }else{
                System.out.println("Stream is active");
            }
        } catch (ResourceNotFoundException e) {
            System.err.println("Stream " + streamName + " does not exist. Please create it in the console.");
            System.err.println(e);
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Error found while describing the stream " + streamName);
            System.err.println(e);
            System.exit(1);
        }
    }

    //Validate Kinesis Client
    public static void validateStream(AmazonKinesis kinesisClient, String streamName) {

        try {

            DescribeStreamRequest dr = new DescribeStreamRequest();
            dr.setStreamName(streamName);
            DescribeStreamResult result = kinesisClient.describeStream(dr);
            if (!"ACTIVE".equals(result.getStreamDescription().getStreamStatus())) {
                System.err.println("Stream " + streamName + " is not active. Please wait a few moments and try again.");
                System.exit(1);
            } else {
                System.out.println("Stream is active");
            }
        } catch (ResourceNotFoundException e) {
            System.err.println("Stream " + streamName + " does not exist. Please create it in the console.");
            System.err.println(e);
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Error found while describing the stream " + streamName);
            System.err.println(e);
            System.exit(1);
        }

    }
}



