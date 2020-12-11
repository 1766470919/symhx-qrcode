package com.symhx.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class IoUtil {
    public IoUtil() {
    }

    public static ByteArrayInputStream toByteArrayInputStream(InputStream inputStream) throws IOException {
        if (inputStream instanceof ByteArrayInputStream) {
            return (ByteArrayInputStream)inputStream;
        } else {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            Throwable var2 = null;

            try {
                BufferedInputStream br = new BufferedInputStream(inputStream);
                byte[] b = new byte[1024];

                int c;
                while((c = br.read(b)) != -1) {
                    bos.write(b, 0, c);
                }

                br.close();
                inputStream.close();
                ByteArrayInputStream var17 = new ByteArrayInputStream(bos.toByteArray());
                return var17;
            } catch (Throwable var14) {
                var2 = var14;
                throw var14;
            } finally {
                if (bos != null) {
                    if (var2 != null) {
                        try {
                            bos.close();
                        } catch (Throwable var13) {
                            var2.addSuppressed(var13);
                        }
                    } else {
                        bos.close();
                    }
                }

            }
        }
    }
}
