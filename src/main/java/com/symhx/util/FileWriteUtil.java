package com.symhx.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URI;
import java.util.Date;
import java.util.Objects;
import java.util.Random;


public class FileWriteUtil {
    private static final Logger log = LoggerFactory.getLogger(FileWriteUtil.class);
    public static String TEMP_PATH = "/tmp/quickmedia/";
    private static Random FILENAME_GEN_RANDOM = new Random();

    public FileWriteUtil() {
    }

    public static String getTmpPath() {
        String tmpPathEnvProperties = System.getProperty("quick.media.tmp.path");
        if (StringUtils.isNotBlank(tmpPathEnvProperties)) {
            if (tmpPathEnvProperties.endsWith("/")) {
                TEMP_PATH = tmpPathEnvProperties;
            } else {
                TEMP_PATH = tmpPathEnvProperties + "/";
            }
        }

        return TEMP_PATH + DateFormatUtils.format(new Date(), "yyyyMMdd");
    }

    public static <T> FileWriteUtil.FileInfo saveFile(T src, String inputType) throws Exception {
        if (src instanceof String) {
            return saveFileByPath((String)src);
        } else if (src instanceof URI) {
            return saveFileByURI((URI)src);
        } else if (src instanceof InputStream) {
            return saveFileByStream((InputStream)src, inputType);
        } else {
            throw new IllegalStateException("save file parameter only support String/URI/InputStream type! but input type is: " + (src == null ? null : src.getClass()));
        }
    }

    private static FileWriteUtil.FileInfo saveFileByPath(String path) throws Exception {
        if (path.startsWith("http")) {
            return saveFileByURI(URI.create(path));
        } else {
            String tmpAbsFile;
            if (BasicFileUtil.isAbsFile(path)) {
                tmpAbsFile = path;
            } else if (path.startsWith("~")) {
                tmpAbsFile = BasicFileUtil.parseHomeDir2AbsDir(path);
            } else {
                tmpAbsFile = FileWriteUtil.class.getClassLoader().getResource(path).getFile();
            }

            return parseAbsFileToFileInfo(tmpAbsFile);
        }
    }

    private static FileWriteUtil.FileInfo saveFileByURI(URI uri) throws Exception {
        String path = uri.getPath();
        if (path.endsWith("/")) {
            throw new IllegalArgumentException("a select uri should be choosed! but input path is: " + path);
        } else {
            int index = path.lastIndexOf("/");
            String filename = path.substring(index + 1);
            FileWriteUtil.FileInfo fileInfo = new FileWriteUtil.FileInfo();
            extraFileName(filename, fileInfo);
            fileInfo.setPath(getTmpPath());

            try {
                InputStream inputStream = HttpUtil.downFile(uri);
                return saveFileByStream(inputStream, fileInfo);
            } catch (Exception var6) {
                log.error("down file from url: {} error! e: {}", uri, var6);
                throw var6;
            }
        }
    }

    public static FileWriteUtil.FileInfo saveFileByStream(InputStream inputStream, String fileType) throws Exception {
        return saveFileByStream(inputStream, getTmpPath(), genTempFileName(), fileType);
    }

    public static FileWriteUtil.FileInfo saveFileByStream(InputStream stream, String path, String filename, String fileType) throws FileNotFoundException {
        return saveFileByStream(stream, new FileWriteUtil.FileInfo(path, filename, fileType));
    }

    public static FileWriteUtil.FileInfo saveFileByStream(InputStream stream, FileWriteUtil.FileInfo fileInfo) throws FileNotFoundException {
        if (!StringUtils.isBlank(fileInfo.getPath())) {
            mkDir(new File(fileInfo.getPath()));
        }

        String tempAbsFile = fileInfo.getPath() + "/" + fileInfo.getFilename() + "." + fileInfo.getFileType();
        BufferedOutputStream outputStream = null;
        BufferedInputStream inputStream = null;

        FileWriteUtil.FileInfo var6;
        try {
            inputStream = new BufferedInputStream(stream);
            outputStream = new BufferedOutputStream(new FileOutputStream(tempAbsFile));
            int len = inputStream.available();
            if (len <= 4096) {
                byte[] bytes = new byte[len];
                inputStream.read(bytes);
                outputStream.write(bytes);
            } else {
                byte[] bytes = new byte[4096];

                int byteCount;
                while((byteCount = inputStream.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, byteCount);
                }
            }

            var6 = fileInfo;
            return var6;
        } catch (Exception var16) {
            log.error("save stream into file error! filename: {} e: {}", tempAbsFile, var16);
            var6 = null;
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.flush();
                    outputStream.close();
                }

                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException var15) {
                log.error("close stream error! e: {}", var15);
            }

        }

        return var6;
    }

    private static String genTempFileName() {
        return System.currentTimeMillis() + "_" + FILENAME_GEN_RANDOM.nextInt(1000);
    }

    public static void mkDir(File path) throws FileNotFoundException {
        if (path.getParentFile() == null) {
            path = path.getAbsoluteFile();
        }

        if (path.getParentFile().exists()) {
            modifyFileAuth(path);
            if (!path.exists() && !path.mkdir()) {
                throw new FileNotFoundException();
            }
        } else {
            mkDir(path.getParentFile());
            modifyFileAuth(path);
            if (!path.exists() && !path.mkdir()) {
                throw new FileNotFoundException();
            }
        }

    }

    private static void modifyFileAuth(File file) {
        boolean ans = file.setExecutable(true, false);
        ans = file.setReadable(true, false) && ans;
        ans = file.setWritable(true, false) && ans;
        if (log.isDebugEnabled()) {
            log.debug("create file auth : {}", ans);
        }

    }

    public static FileWriteUtil.FileInfo parseAbsFileToFileInfo(String absFile) {
        FileWriteUtil.FileInfo fileInfo = new FileWriteUtil.FileInfo();
        extraFilePath(absFile, fileInfo);
        extraFileName(fileInfo.getFilename(), fileInfo);
        return fileInfo;
    }

    private static void extraFilePath(String absFilename, FileWriteUtil.FileInfo fileInfo) {
        int index = absFilename.lastIndexOf("/");
        if (index < 0) {
            fileInfo.setPath(getTmpPath());
            fileInfo.setFilename(absFilename);
        } else {
            fileInfo.setPath(absFilename.substring(0, index));
            fileInfo.setFilename(index + 1 == absFilename.length() ? "" : absFilename.substring(index + 1));
        }

    }

    private static void extraFileName(String fileName, FileWriteUtil.FileInfo fileInfo) {
        int index = fileName.lastIndexOf(".");
        if (index < 0) {
            fileInfo.setFilename(fileName);
            fileInfo.setFileType("");
        } else {
            fileInfo.setFilename(fileName.substring(0, index));
            fileInfo.setFileType(index + 1 == fileName.length() ? "" : fileName.substring(index + 1));
        }

    }

    public static class FileInfo {
        private String path;
        private String filename;
        private String fileType;

        public FileInfo() {
        }

        public FileInfo(String path, String filename, String fileType) {
            this.path = path;
            this.filename = filename;
            this.fileType = fileType;
        }

        public String getAbsFile() {
            return this.path + "/" + this.filename + "." + this.fileType;
        }

        public String getPath() {
            return this.path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getFilename() {
            return this.filename;
        }

        public void setFilename(String filename) {
            this.filename = filename;
        }

        public String getFileType() {
            return this.fileType;
        }

        public void setFileType(String fileType) {
            this.fileType = fileType;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            } else if (o != null && this.getClass() == o.getClass()) {
                FileWriteUtil.FileInfo fileInfo = (FileWriteUtil.FileInfo)o;
                return Objects.equals(this.path, fileInfo.path) && Objects.equals(this.filename, fileInfo.filename) && Objects.equals(this.fileType, fileInfo.fileType);
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            return Objects.hash(new Object[]{this.path, this.filename, this.fileType});
        }

        @Override
        public String toString() {
            return "FileInfo{path='" + this.path + '\'' + ", filename='" + this.filename + '\'' + ", fileType='" + this.fileType + '\'' + '}';
        }
    }
}
