package consumer;

import com.amazonaws.services.kinesis.clientlibrary.interfaces.IRecordProcessor;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.IRecordProcessorCheckpointer;
import com.amazonaws.services.kinesis.clientlibrary.types.ShutdownReason;
import com.amazonaws.services.kinesis.model.Record;

import java.util.List;

/**
 * Created by nkandavel on 7/11/16.
 */
public class LeadsProcessor implements IRecordProcessor {
    public void initialize(String s) {

    }

    public void processRecords(List<Record> list, IRecordProcessorCheckpointer iRecordProcessorCheckpointer) {

        for (Record record : list) {
            // process record
            System.out.print(record.getData());

        }

    }

    public void shutdown(IRecordProcessorCheckpointer iRecordProcessorCheckpointer, ShutdownReason shutdownReason) {

    }
}
