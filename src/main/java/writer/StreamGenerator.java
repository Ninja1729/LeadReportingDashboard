package writer;

import model.LeadData;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.compress.CompressionCodecFactory;
import org.apache.commons.io.IOUtils;
import java.io.InputStream;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
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

    public static LeadData readLines(Path location, Configuration conf) throws Exception {
        LeadData ld = null;
        FileSystem fileSystem = FileSystem.get(location.toUri(), conf);
        CompressionCodecFactory factory = new CompressionCodecFactory(conf);
        FileStatus[] items = fileSystem.listStatus(location);
        if (items == null) return new LeadData();


        for(FileStatus item: items) {

            // ignoring files like _SUCCESS
            if(item.getPath().getName().startsWith("_")) {
                continue;
            }

            CompressionCodec codec = factory.getCodec(item.getPath());
            InputStream stream = null;

            // check if we have a compression codec we need to use
            if (codec != null) {
                stream = codec.createInputStream(fileSystem.open(item.getPath()));
            }
            else {
                stream = fileSystem.open(item.getPath());
            }

            StringWriter writer = new StringWriter();
            IOUtils.copy(stream, writer, "UTF-8");
            String raw = writer.toString();
            String[] resulting = raw.split("\n");
            for(String str: raw.split("\n")) {
                String[] s1 = str.split("\u0001");
                ld = new LeadData(Long.parseLong(s1[0]),s1[2],s1[2],s1[3]);


            }
        }
        return ld;
    }


}
