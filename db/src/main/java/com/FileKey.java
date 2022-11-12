package com;

import java.util.Objects;

public class FileKey {
    private final String fileName;
    private final String filePath;

    public FileKey(String fileName,String filePath) {
        if (fileName==null && filePath==null) {
            throw new IllegalArgumentException();
        }
        this.fileName = fileName;
        this.filePath = filePath;
    }

    public static FileKey ofFileName(String fileName) {
        return new FileKey(fileName,null);
    }

    public static FileKey ofFilePath(String filePath) {
        return new FileKey(null,filePath);
    }

    private boolean isAllExist() {
        return this.fileName!=null && this.filePath!=null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FileKey fileKey = (FileKey) o;

        if (this.isAllExist() && fileKey.isAllExist()) {
            return Objects.equals(fileName,fileKey.fileName) &&
                    Objects.equals(filePath,fileKey.filePath);
        }

        if (fileName==null && fileKey.fileName==null) {
            return Objects.equals(filePath,fileKey.filePath);
        }

        if (filePath==null && fileKey.filePath==null) {
            return Objects.equals(fileName,fileKey.fileName);
        }

        return false;
    }

    @Override
    public int hashCode() {
        int result = fileName != null ? fileName.hashCode() : 0;
        result = 31 * result + (filePath != null ? filePath.hashCode() : 0);
        return result;
    }
}
