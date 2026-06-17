package com.clinicaltrial.ddd.dataexport.domain.model.valueobject;

import com.clinicaltrial.ddd.common.model.ValueObject;

import java.util.Objects;

/**
 * ExportResult — 导出结果值对象.
 * <p>
 * 封装一次导出操作的输出结果信息，包括生成的文件URL、
 * 文件名、记录数和文件大小。
 * 作为值对象，一旦创建即不可变。
 * </p>
 */
public class ExportResult implements ValueObject {

    private final String fileUrl;
    private final String fileName;
    private final Integer recordCount;
    private final Long fileSize;

    /**
     * 构造ExportResult.
     *
     * @param fileUrl     生成文件的访问URL
     * @param fileName    导出文件名
     * @param recordCount 导出的记录数
     * @param fileSize    文件大小（字节）
     */
    public ExportResult(String fileUrl, String fileName, Integer recordCount, Long fileSize) {
        this.fileUrl = fileUrl;
        this.fileName = fileName;
        this.recordCount = recordCount;
        this.fileSize = fileSize;
    }

    /**
     * 获取文件访问URL.
     *
     * @return 文件URL
     */
    public String getFileUrl() {
        return fileUrl;
    }

    /**
     * 获取导出文件名.
     *
     * @return 文件名
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * 获取导出的记录数.
     *
     * @return 记录数
     */
    public Integer getRecordCount() {
        return recordCount;
    }

    /**
     * 获取文件大小（字节）.
     *
     * @return 文件大小
     */
    public Long getFileSize() {
        return fileSize;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ExportResult that = (ExportResult) o;
        return Objects.equals(fileUrl, that.fileUrl)
                && Objects.equals(fileName, that.fileName)
                && Objects.equals(recordCount, that.recordCount)
                && Objects.equals(fileSize, that.fileSize);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileUrl, fileName, recordCount, fileSize);
    }

    @Override
    public String toString() {
        return "ExportResult{"
                + "fileUrl='" + fileUrl + '\''
                + ", fileName='" + fileName + '\''
                + ", recordCount=" + recordCount
                + ", fileSize=" + fileSize
                + '}';
    }
}
