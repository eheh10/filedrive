package com.table;

import com.dto.FileDto;
import com.exception.InputNullParameterException;
import com.exception.InsertNullException;
import com.exception.NoSearchResultException;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class FileTable {
    private static final Set<FileDto> FILES = new HashSet<>();

    public void insert(FileDto fileDto) {
        if (fileDto == null) {
            throw new InsertNullException();
        }
        FILES.add(fileDto);
    }

    public FileDto searchWithFileName(String targetValue) {
        if (targetValue == null) {
            throw new InputNullParameterException();
        }

        for(FileDto fileDto: FILES) {
            if(Objects.equals(fileDto.getName(),targetValue)) {
                return fileDto;
            }
        }

        throw new NoSearchResultException();
    }

    public FileDto searchWithFilePath(String targetValue) {
        if (targetValue == null) {
            throw new InputNullParameterException();
        }

        for(FileDto fileDto: FILES) {
            if(Objects.equals(fileDto.getPath(),targetValue)) {
                return fileDto;
            }
        }

        throw new NoSearchResultException();
    }
}
