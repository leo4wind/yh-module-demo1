package com.clinicaltrial.ddd.dataexport.domain.model.valueobject;

/**
 * FileFormat — 导出文件格式枚举.
 * <p>
 * 定义支持的数据导出文件格式：
 * <ul>
 *   <li>XLSX — Microsoft Excel (.xlsx)</li>
 *   <li>CSV — 逗号分隔值 (.csv)</li>
 *   <li>SAS — SAS传输文件 (.sas7bdat)</li>
 *   <li>SAV — SPSS数据文件 (.sav)</li>
 *   <li>XML — XML格式 (.xml)</li>
 * </ul>
 * </p>
 */
public enum FileFormat {

    /**
     * Microsoft Excel (.xlsx).
     */
    XLSX("xlsx", "Excel格式"),

    /**
     * 逗号分隔值 (.csv).
     */
    CSV("csv", "CSV格式"),

    /**
     * SAS传输文件 (.sas7bdat).
     */
    SAS("sas", "SAS格式"),

    /**
     * SPSS数据文件 (.sav).
     */
    SAV("sav", "SPSS格式"),

    /**
     * XML格式 (.xml).
     */
    XML("xml", "XML格式");

    private final String extension;
    private final String description;

    /**
     * 构造FileFormat.
     *
     * @param extension   文件扩展名
     * @param description 格式描述
     */
    FileFormat(String extension, String description) {
        this.extension = extension;
        this.description = description;
    }

    /**
     * 获取文件扩展名.
     *
     * @return 扩展名字符串（不含点号）
     */
    public String getExtension() {
        return extension;
    }

    /**
     * 获取格式描述.
     *
     * @return 中文描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 根据扩展名获取枚举值.
     *
     * @param extension 文件扩展名
     * @return 对应的枚举值
     * @throws IllegalArgumentException 如果扩展名无效
     */
    public static FileFormat fromExtension(String extension) {
        for (FileFormat format : values()) {
            if (format.extension.equalsIgnoreCase(extension)) {
                return format;
            }
        }
        throw new IllegalArgumentException("Unknown FileFormat extension: " + extension);
    }

    @Override
    public String toString() {
        return extension + "(" + description + ")";
    }
}
