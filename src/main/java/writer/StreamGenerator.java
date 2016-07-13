package writer;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.RegionUtils;
import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.AmazonKinesisClient;
import com.amazonaws.services.kinesisfirehose.AmazonKinesisFirehoseClient;
import com.amazonaws.util.CredentialUtils;
import model.LeadData;
import utils.ConfigurationUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import java.sql.Timestamp;
import java.util.Date;
import java.io.*;
import java.util.*;
import java.net.*;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.util.*;
/**
 * Created by nkandavel on 7/7/16.
 */
public class StreamGenerator
{
    private static final List<LeadEmail> LEAD_EMAILS = new ArrayList<LeadEmail>();
    static {
        LEAD_EMAILS.add(new LeadEmail("NEW", "abc@gmail.com"));
        LEAD_EMAILS.add(new LeadEmail("OLD", "def@gmail.com"));
        LEAD_EMAILS.add(new LeadEmail("NEW", "gfhi@gmail.com"));
        LEAD_EMAILS.add(new LeadEmail("OLD", "klm@gmail.com"));
        LEAD_EMAILS.add(new LeadEmail("OLD", "mno@gmail.com"));
        LEAD_EMAILS.add(new LeadEmail("OLD", "pqr@gmail.com"));
        LEAD_EMAILS.add(new LeadEmail("OLD", "stu@gmail.com"));
        LEAD_EMAILS.add(new LeadEmail("NEW", "vwx@gmail.com"));
        LEAD_EMAILS.add(new LeadEmail("NEW", "ijk@gmail.com"));
        LEAD_EMAILS.add(new LeadEmail("NEW", "lmo@gmail.com"));
        LEAD_EMAILS.add(new LeadEmail("NEW", "nop@gmail.com"));
        LEAD_EMAILS.add(new LeadEmail("OLD", "qrs@gmail.com"));
        LEAD_EMAILS.add(new LeadEmail("NEW", "kda@gmail.com"));
        LEAD_EMAILS.add(new LeadEmail("OLD", "abc@gmail.com"));
        LEAD_EMAILS.add(new LeadEmail("NEW", "sdf@gmail.com"));
        LEAD_EMAILS.add(new LeadEmail("NEW", "tue@gmail.com"));
        LEAD_EMAILS.add(new LeadEmail("OLD", "sas@gmail.com"));
        LEAD_EMAILS.add(new LeadEmail("NEW", "qwe@gmail.com"));
        LEAD_EMAILS.add(new LeadEmail("NEW", "xff@gmail.com"));
        LEAD_EMAILS.add(new LeadEmail("OLD", "aaa@gmail.com"));
    }


    private final Random random = new Random();
    private AtomicLong id = new AtomicLong(1);

    /**
     * Return a random email with a unique id every time.
     *
     */
    public LeadData getRandomTrade()  {
        // pick a random email
        LeadEmail leadEmail = LEAD_EMAILS.get(random.nextInt(LEAD_EMAILS.size()));

        Date date1= new java.util.Date();
        SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");


        return new LeadData(id.getAndIncrement(),dt.format(date1),leadEmail.newold,leadEmail.email);
    }

    private static class LeadEmail {
        String newold;
        String email;


        public LeadEmail(String newold, String email) {
            this.newold = newold;
            this.email = email;
        }
    }


}
