package consumer;

import com.amazonaws.services.kinesis.clientlibrary.interfaces.IRecordProcessor;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.IRecordProcessorFactory;

/**
 * Created by nkandavel on 7/11/16.
 */
public class LeadsProcessorFactory implements IRecordProcessorFactory {
    public LeadsProcessorFactory() {
        super();
    }

    public IRecordProcessor createProcessor() {
        return new LeadsProcessor();
    }
}
