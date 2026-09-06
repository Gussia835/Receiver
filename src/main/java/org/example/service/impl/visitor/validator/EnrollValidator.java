package org.example.service.impl.visitor.validator;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.example.exception.ReceivingFileException;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

@Component
@Slf4j
public class EnrollValidator {


    private static final Charset FILE_CHARSET = Charset.forName("windows-1251");

    private static final int HEADER_LENGTH = 42;
    private static final int BODY_LENGTH = 152;
    private static final int TRAILER_LENGTH = 20;

    private static final int HEADER_LINE_SIZE = HEADER_LENGTH + 2;
    private static final int BODY_LINE_SIZE = BODY_LENGTH + 2;
    private static final int TRAILER_LINE_SIZE = TRAILER_LENGTH + 2;

    private static final Pattern HEADER_PATTERN = Pattern.compile("^H .{8} .{6} .{9} .{8} .{6}$");
    private static final Pattern TRAILER_PATTERN = Pattern.compile("^T\\s{9}\\s*\\d+$");
    private static final Pattern BODY_PATTERN = Pattern.compile("^.{152,}$");


    private static final Pattern PROC_TYPE_PATTERN = Pattern.compile("^(IMMEDIATE|IN-TIME)$");
    private static final Pattern NUMERIC_PATTERN = Pattern.compile("^\\d+$");

    private static final Pattern FILENAME_PATTERN = Pattern.compile("^Z\\d{3}\\d{3}\\.[A-Z]+_ENROLL\\d{3}\\d{3}\\d\\.\\d{3}$");


    public boolean validateHeader(Path filepath) {
        try {

            String firstLine = readFirstLine(filepath);

            return isValidLen(firstLine, HEADER_LENGTH)
                    && isValidPattern(firstLine, HEADER_PATTERN)
                    && isValidProcTypeHeader(firstLine);
        } catch (IOException e) {
            log.error("cant read header");

            return false;
        }
    }



    private String readFirstLine(Path filepath) throws IOException{

        try (BufferedReader reader = Files.newBufferedReader(filepath, FILE_CHARSET)) {

           return reader.readLine();

        }
    }


    private boolean isValidProcTypeHeader(String line) {

        String procType = line.substring(18, 27).trim();

        if (!PROC_TYPE_PATTERN.matcher(procType).matches()) {

            log.warn("Invalid PROC_TYPE: {}", procType);
            return false;

        }

        return true;

    }

    public boolean isValidFilename(Path filepath) {
        if (filepath == null) {
            return false;
        }

        String filename = filepath.getFileName().toString();

        return FILENAME_PATTERN.matcher(filename).matches();
    }

    public boolean validateTrailer(Path filepath) {
        try {
            String lastLine = readLastLine(filepath);

            return  (isValidLen(lastLine, TRAILER_LENGTH)
                    && isValidPattern(lastLine, TRAILER_PATTERN)
                    && isValidCount(lastLine, filepath));


        } catch (IOException e) {
            log.error("Failed to read trailer", e);
            return false;
        }
    }


    private String readLastLine(Path filepath) throws IOException {

        File file = filepath.toFile();
        long fileLength = file.length();

        try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
            long startPos = fileLength - TRAILER_LINE_SIZE;

            raf.seek(startPos);
            byte[] bytes = new byte[TRAILER_LENGTH];

            raf.readFully(bytes);

            return new String(bytes, FILE_CHARSET);

        }
    }

    private boolean isValidCount(String lastLine, Path filepath) {
        String countStr = lastLine.substring(10, 20).trim();
        int fileCount = Integer.parseInt(countStr);
        int actualCount = countLines(filepath);

        if (fileCount != actualCount) {
            log.warn("Trailer count mismatch. Declared: {}, Actual: {}", fileCount, actualCount);
            return false;
        }


        return true;
    }


    private int countLines(Path filepath) {
        try {


            long fileLength = Files.size(filepath);
            long dataBytes = fileLength - (HEADER_LINE_SIZE + TRAILER_LINE_SIZE);

            return (int) dataBytes / BODY_LINE_SIZE;
//Stream count in Files


        } catch (IOException e) {
            log.error("error: cant find size of file {}", filepath.getFileName().toString(), e);
            throw new ReceivingFileException("file exception cant find size fo file");
        }

    }



    private boolean isValidLen(String line, int len) {
        if (line == null || line.length() < len) {

            log.warn("Header missing or too short");
            return false;

        }

        return true;
    }



    private boolean isValidPattern(String line, Pattern pattern) {

        if (!pattern.matcher(line).matches()) {


            log.warn("Invalid header format");
            return false;

        }

        return true;
    }



    public boolean validateBody(String line) {
        return isValidLen(line, BODY_LENGTH)
                && isValidPattern(line, BODY_PATTERN)
                && isValidOp(line)
                && isValidAccount(line);

    }

    private boolean isValidAccount(String line) {
        String account = line.substring(100, 130).trim();

        return StringUtils.isNotBlank(account) && NUMERIC_PATTERN.matcher(account).matches();
    }

    private boolean isValidOp(String line) {
        String opType = line.substring(130, 132).trim();
        return "DR".equals(opType) || "CR".equals(opType) || "ZR".equals(opType);
    }



    public boolean isTrailerLine(String line) {
        return line != null
                && line.length() == TRAILER_LENGTH
                && TRAILER_PATTERN.matcher(line).matches();
    }

    public boolean isHeaderLine(String line) {
        return line != null && line.length() >= HEADER_LENGTH && line.startsWith("H ");
    }


}
