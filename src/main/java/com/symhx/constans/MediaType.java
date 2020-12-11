package com.symhx.constans;

public enum MediaType {
    ImageJpg("jpg", "image/jpeg", "FFD8FF", "data:image/jpeg;base64,"),
    ImageGif("gif", "image/gif", "47494638", "data:image/gif;base64,"),
    ImagePng("png", "image/png", "89504E47", "data:image/png;base64,"),
    ImageWebp("webp", "image/webp", "52494646", ""),
    AudioMp3("mp3", "audio/mp3", "", "data:audio/x-mpeg;base64,"),
    AudioWav("wav", "audio/wav", "", "data:audio/wav;base64,"),
    AudioAmr("amr", "audio/amr", "", ""),
    VideoMp4("mp4", "video/mp4", "", "data:video/mp4;base64,"),
    VideoFlv("flv", "video/x-flv", "464C56", ""),
    VideoMov("mov", "video/quicktime", "", "data:video/quicktime;base64");

    private final String ext;
    private final String mime;
    private final String magic;
    private final String prefix;

    private MediaType(String ext, String mime, String magic, String prefix) {
        this.ext = ext;
        this.mime = mime;
        this.magic = magic;
        this.prefix = prefix;
    }

    public String getExt() {
        return this.ext;
    }

    public String getMime() {
        return this.mime;
    }

    public String getMagic() {
        return this.magic;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public static String getExtByMime(String mime) {
        if (mime.contains("jpg")) {
            mime = mime.replaceAll("jpg", "jpeg");
        }

        MediaType[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            MediaType type = var1[var3];
            if (type.getMime().equals(mime)) {
                return type.getExt();
            }
        }

        return "";
    }

    public static MediaType typeOfMagicNum(String magicNum) {
        magicNum = magicNum.toUpperCase();
        MediaType[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            MediaType mediaType = var1[var3];
            if (magicNum.startsWith(mediaType.getMagic())) {
                return mediaType;
            }
        }

        return null;
    }
}
