package com.haiyunshan.express.typeface;

/**
 * Created by sanshibro on 2017/6/3.
 */


import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * TTF Font file parser
 * <p>
 * sample:
 * <code><pre>
 *             File fs = new File("C:\\Windows\\Fonts");
 *             File[] files = fs.listFiles(new FilenameFilter() { *
 *               public boolean accept(File dir, String name) {
 *                 if (name.endsWith("ttf")) return true;
 *                   return false;
 *                 }
 *               });
 *             for (File file : files) {
 *               TTFParser parser = new TTFParser();
 *               parser.parse(file.getAbsolutePath());
 *               System.out.println("font name: " + parser.getFontName());
 *             }
 * </pre></code>
 * <p/>
 * Copyright: Copyright (c) 12-8-6 下午3:51
 * <p/>
 * Version: 1.0
 * <p/>
 */
public class TTFParser {

    public static int COPYRIGHT = 0;
    public static int FAMILY_NAME = 1;
    public static int FONT_SUBFAMILY_NAME = 2;
    public static int UNIQUE_FONT_IDENTIFIER = 3;
    public static int FULL_FONT_NAME = 4;
    public static int VERSION = 5;
    public static int POSTSCRIPT_NAME = 6;
    public static int TRADEMARK = 7;
    public static int MANUFACTURER = 8;
    public static int DESIGNER = 9;
    public static int DESCRIPTION = 10;
    public static int URL_VENDOR = 11;
    public static int URL_DESIGNER = 12;
    public static int LICENSE_DESCRIPTION = 13;
    public static int LICENSE_INFO_URL = 14;

    private Map<Integer, String> fontProperties = new HashMap<Integer, String>();

    /**
     * 获取ttf font name
     *
     * @return
     */
    public String getFontName() {
        if (fontProperties.containsKey(FULL_FONT_NAME)) {
            return fontProperties.get(FULL_FONT_NAME);
        } else if (fontProperties.containsKey(FAMILY_NAME)) {
            return fontProperties.get(FAMILY_NAME);
        } else {
            return null;
        }
    }

    /**
     * 获取ttf属性
     *
     * @param nameID 属性标记，见静态变量
     * @return 属性值
     */
    public String getFontPropertie(int nameID) {
        if (fontProperties.containsKey(nameID)) {
            return fontProperties.get(nameID);
        } else {
            return null;
        }
    }

    /**
     * 获取ttf属性集合
     *
     * @return 属性集合(MAP)
     */
    public Map<Integer, String> getFontProperties() {
        return fontProperties;
    }

    /**
     * 执行解析
     *
     * @param fileName ttf文件名
     * @throws IOException
     */
    public void parse(String fileName) throws IOException {
        fontProperties.clear();
        RandomAccessFile f = null;
        try {
            f = new RandomAccessFile(fileName, "r");
            parseInner(f);
        } catch (Exception e) {
            fontProperties.clear();
        } finally {
            try {
                f.close();
            } catch (Exception e) {
                // ignore;
            }
        }
    }


    private void parseInner(RandomAccessFile randomAccessFile) throws IOException {
        int majorVersion = randomAccessFile.readShort();
        int minorVersion = randomAccessFile.readShort();
        int numOfTables = randomAccessFile.readShort();

        if (majorVersion != 1 || minorVersion != 0) {
            return;
        }

        // jump to TableDirectory struct
        randomAccessFile.seek(12);

        boolean found = false;
        byte[] buff = new byte[4];
        TableDirectory tableDirectory = new TableDirectory();
        for (int i = 0; i < numOfTables; i++) {
            randomAccessFile.read(buff);
            tableDirectory.name = new String(buff);
            tableDirectory.checkSum = randomAccessFile.readInt();
            tableDirectory.offset = randomAccessFile.readInt();
            tableDirectory.length = randomAccessFile.readInt();

            if ("name".equalsIgnoreCase(tableDirectory.name)) {
                found = true;
                break;
            } else if (tableDirectory.name == null || tableDirectory.name.length() == 0) {
                break;
            }
        }

        // not found table of name
        if (!found) {
            return;
        }

        randomAccessFile.seek(tableDirectory.offset);
        NameTableHeader nameTableHeader = new NameTableHeader();
        nameTableHeader.fSelector = randomAccessFile.readShort();
        nameTableHeader.nRCount = randomAccessFile.readShort();
        nameTableHeader.storageOffset = randomAccessFile.readShort();

        NameRecord nameRecord = new NameRecord();
        for (int i = 0; i < nameTableHeader.nRCount; i++) {
            nameRecord.platformID = randomAccessFile.readShort();
            nameRecord.encodingID = randomAccessFile.readShort();
            nameRecord.languageID = randomAccessFile.readShort();
            nameRecord.nameID = randomAccessFile.readShort();
            nameRecord.stringLength = randomAccessFile.readShort();
            nameRecord.stringOffset = randomAccessFile.readShort();

            long pos = randomAccessFile.getFilePointer();
            byte[] bf = new byte[nameRecord.stringLength];
            long vpos = tableDirectory.offset + nameRecord.stringOffset + nameTableHeader.storageOffset;
            randomAccessFile.seek(vpos);
            randomAccessFile.read(bf);
            String temp = new String(bf, Charset.forName("utf-16"));
            if (temp.length() > 0) {
                if (!isValidName(temp)) {
                    temp = new String(bf, Charset.forName("gbk"));

                    if (!isValidName(temp)) {
                        temp = new String(bf, Charset.forName("utf-8"));
                    }
                }
            }

            fontProperties.put(nameRecord.nameID, temp);
            randomAccessFile.seek(pos);
        }
    }

    boolean isValidName(String name) {
        boolean result = true;

        int count = 0;
        int length = name.length();
        for (int i = 0; i < length; i++) {
            char c = name.charAt(i);
            if (c <= 0x9FBF) {
                count++;
            }
        }

        result = (count * 100 / length) > 68;
        return result;
    }

    @Override
    public String toString() {
        return fontProperties.toString();
    }

    private static class TableDirectory {
        String name; //table name
        int checkSum; //Check sum
        int offset; //Offset from beginning of file
        int length; //length of the table in bytes
    }

    private static class NameTableHeader {
        int fSelector; //format selector. Always 0
        int nRCount; //Name Records count
        int storageOffset; //Offset for strings storage,
    }

    private static class NameRecord {
        int platformID;
        int encodingID;
        int languageID;
        int nameID;
        int stringLength;
        int stringOffset; //from start of storage area
    }
}

