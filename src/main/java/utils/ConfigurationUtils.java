package utils;

import com.amazonaws.ClientConfiguration;

/**
 * Created by nkandavel on 7/7/16.
 */
public class ConfigurationUtils {

    private static final String APPLICATION_NAME = "amazon-kinesis-lead-reporting";
    private static final String VERSION = "1.0.0";

    public static ClientConfiguration getClientConfigWithUserAgent() {
        final ClientConfiguration config = new ClientConfiguration();
        final StringBuilder userAgent = new StringBuilder(ClientConfiguration.DEFAULT_USER_AGENT);

        // Separate fields of the user agent with a space
        userAgent.append(" ");
        // Append the application name followed by version number of the sample
        userAgent.append(APPLICATION_NAME);
        userAgent.append("/");
        userAgent.append(VERSION);

        config.setUserAgent(userAgent.toString());
        config.setProxyHost("squid.common.lb.truecarcorp.com");
        config.setProxyPort(3128);

        return config;
    }
}
