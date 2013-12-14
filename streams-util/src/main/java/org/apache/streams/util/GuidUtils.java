package org.apache.streams.util;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.hash.Hashing;

import java.nio.charset.Charset;

/**
 * Created by sblackmon on 12/13/13.
 */
public class GuidUtils {

    private static final Charset UTF8_CHARSET = Charset.forName("UTF-8");

    public static String generateGuid(String... parts) {

        StringBuilder seed = new StringBuilder();
        for( String part : parts ) {
            Preconditions.checkNotNull(part);
            Preconditions.checkArgument(!Strings.isNullOrEmpty(part));
            seed.append(part);
        }

        String hash = Hashing.goodFastHash(24).hashString(seed, UTF8_CHARSET).asBytes().toString();

        return hash;

    }
}
