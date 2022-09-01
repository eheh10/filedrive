package com.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

public class HeaderParser {

    public Map<String,List<String>> parse(BufferedReader br, int limitLength) throws IOException {
        if (br == null) {
            throw new RuntimeException("HeaderParser.parse().BufferedReader is null");
        }
        if (limitLength < 1) {
            throw new RuntimeException("HeaderParser.parse().limitLength must be greater than 0");
        }

        Map<String,List<String>> fields = new HashMap<>();

        String line = "";
        int headerLength = 0;

        while(!(line=br.readLine()).isEmpty()) {
            headerLength += line.length();

            if (headerLength > limitLength) {
                throw new RuntimeException("431 Request header too large");
            }

            StringTokenizer filedTokenizer = new StringTokenizer(line,":");
            String filedName = filedTokenizer.nextToken().strip().toUpperCase();
            String filedValues = filedTokenizer.nextToken().strip();

            StringTokenizer valueTokenizer = new StringTokenizer(filedValues,",");
            List<String> values = new ArrayList<>(Math.max(10,valueTokenizer.countTokens()));

            while(valueTokenizer.hasMoreTokens()) {
                values.add(valueTokenizer.nextToken().strip());
            }

            fields.put(filedName,values);
        }

        return Collections.unmodifiableMap(fields);
    }

    public Map<String,List<String>> parse(BufferedReader br) throws IOException {
        return parse(br, 8192);
    }
}
