package com.dto;

import com.exception.InputNullParameterException;
import com.exception.MustBePositiveNumberException;
import lombok.Builder;
import lombok.ToString;

import java.util.Objects;

@Builder
@ToString
public class FileDto {
    private final String name;
    private final String path;
    private final int size;

    private FileDto(String name, String path, int size) {
        if (name == null || path == null) {
            throw new InputNullParameterException();
        }
        if (size < 0 ) {
            throw new MustBePositiveNumberException();
        }
        this.name = name;
        this.path = path;
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public int getSize() {
        return size;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FileDto fileDto = (FileDto) o;

        if (size != fileDto.size) return false;
        if (!Objects.equals(name, fileDto.name)) return false;
        return Objects.equals(path, fileDto.path);
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (path != null ? path.hashCode() : 0);
        result = 31 * result + size;
        return result;
    }
}
