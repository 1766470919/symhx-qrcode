package com.symhx.util;

import com.google.common.base.Joiner;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author lj
 * @date 2020/12/11
 */
public class FileReadUtil {
    public FileReadUtil() {
    }

    public static String readAll(String fileName) throws IOException {
        BufferedReader reader = createLineRead(fileName);
        List<String> lines = (List)reader.lines().collect(Collectors.toList());
        return Joiner.on("\n").join(lines);
    }

    public static InputStream createByteRead(String fileName) throws IOException {
        return getStreamByFileName(fileName);
    }

    public static Reader createCharRead(String fileName) throws IOException {
        return new InputStreamReader(getStreamByFileName(fileName), Charset.forName("UTF-8"));
    }

    public static BufferedReader createLineRead(String fileName) throws IOException {
        return new BufferedReader(new InputStreamReader(getStreamByFileName(fileName), Charset.forName("UTF-8")));
    }

    public static InputStream getStreamByFileName(String fileName) throws IOException {
        if (fileName == null) {
            throw new IllegalArgumentException("fileName should not be null!");
        } else if (fileName.startsWith("http")) {
            return HttpUtil.downFile(fileName);
        } else if (BasicFileUtil.isAbsFile(fileName)) {
            Path path = Paths.get(fileName);
            return Files.newInputStream(path);
        } else if (fileName.startsWith("~")) {
            fileName = BasicFileUtil.parseHomeDir2AbsDir(fileName);
            return Files.newInputStream(Paths.get(fileName));
        } else {
            return FileReadUtil.class.getClassLoader().getResourceAsStream(fileName);
        }
    }

    private static String bytesToHex(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src != null && src.length > 0) {
            byte[] var2 = src;
            int var3 = src.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                byte aSrc = var2[var4];
                int v = aSrc & 255;
                String hv = Integer.toHexString(v);
                if (hv.length() < 2) {
                    stringBuilder.append(0);
                }

                stringBuilder.append(hv);
            }

            return stringBuilder.toString();
        } else {
            return null;
        }
    }

    public static String getMagicNum(String file) {
        try {
            InputStream stream = getStreamByFileName(file);
            Throwable var2 = null;

            String var4;
            try {
                byte[] b = new byte[28];
                stream.read(b, 0, 28);
                var4 = bytesToHex(b);
            } catch (Throwable var14) {
                var2 = var14;
                throw var14;
            } finally {
                if (stream != null) {
                    if (var2 != null) {
                        try {
                            stream.close();
                        } catch (Throwable var13) {
                            var2.addSuppressed(var13);
                        }
                    } else {
                        stream.close();
                    }
                }

            }

            return var4;
        } catch (IOException var16) {
            var16.printStackTrace();
            return null;
        }
    }

    public static String getMagicNum(ByteArrayInputStream inputStream) {
        byte[] bytes = new byte[28];
        inputStream.read(bytes, 0, 28);
        inputStream.reset();
        return bytesToHex(bytes);
    }
}
