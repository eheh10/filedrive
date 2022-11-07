package com.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
public class FileDto {
    private final String fileName;
    private final String filePath;

    public String getFileName() {
        return fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FileDto fileDto = (FileDto) o;

        if (fileName != null ? !fileName.equals(fileDto.fileName) : fileDto.fileName != null) return false;
        return filePath != null ? filePath.equals(fileDto.filePath) : fileDto.filePath == null;
    }

    @Override
    public int hashCode() {
        int result = fileName != null ? fileName.hashCode() : 0;
        result = 31 * result + (filePath != null ? filePath.hashCode() : 0);
        return result;
    }

}
