package com.request;

import com.exception.InvalidValueException;
import com.exception.NullException;
import com.exception.StatusCode431Exception;
import com.field.Field;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

public class Header {

    private final Map<String, Field> values;

    private Header(Map<String, Field> values) {
        this.values = values;
    }

    public static Header parse(BufferedReader br, int limitLength) throws IOException {
        if (br == null) {
            throw new NullException("HeaderParser.parse().BufferedReader is null");
        }
        if (limitLength < 1) {
            throw new InvalidValueException("HeaderParser.parse().limitLength must be greater than 0");
        }

        Map<String,Field> fields = new HashMap<>();

        String line = "";
        int headerLength = 0;

        while(!(line=br.readLine()).isEmpty()) {
            headerLength += line.length();

            if (headerLength > limitLength) {
                throw new StatusCode431Exception();
            }

            StringTokenizer filedTokenizer = new StringTokenizer(line,":");
            String filedName = filedTokenizer.nextToken().strip().toUpperCase();
            String filedValues = filedTokenizer.nextToken().strip();

            StringTokenizer valueTokenizer = new StringTokenizer(filedValues,",");
            List<String> values = new ArrayList<>(Math.max(10,valueTokenizer.countTokens()));

            while(valueTokenizer.hasMoreTokens()) {
                values.add(valueTokenizer.nextToken().strip());
            }

            Field field = new Field(filedName,values);

            fields.put(filedName,field);
        }

        return new Header(Collections.unmodifiableMap(fields));
//        return new Header(fields);
    }

    public static Header parse(BufferedReader br) throws IOException {
        return parse(br, 8192);
    }

    public void display() {
        for(Map.Entry<String, Field> entry:values.entrySet()) {
            System.out.print(entry.getKey()+": ");
            System.out.println(entry.getValue().toString());
        }
    }
}
