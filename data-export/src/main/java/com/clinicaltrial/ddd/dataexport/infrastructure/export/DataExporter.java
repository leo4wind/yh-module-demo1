package com.clinicaltrial.ddd.dataexport.infrastructure.export;

import com.clinicaltrial.ddd.dataexport.domain.model.valueobject.ExportResult;

import java.util.List;
import java.util.Map;

/**
 * DataExporter — 数据导出器策略接口.
 * <p>
 * 定义数据导出的策略接口，支持多种文件格式的导出实现。
 * 每个实现类负责将数据转换为特定格式的文件。
 * </p>
 *
 * <p>
 * 实现类：
 * <ul>
 *   <li>{@link XlsxExporter} — Excel (.xlsx) 格式</li>
 *   <li>{@link CsvExporter} — CSV (.csv) 格式</li>
 * </ul>
 * </p>
 */
public interface DataExporter {

    /**
     * 执行数据导出.
     *
     * @param data        要导出的数据（列表形式，每行为字段名-值的映射）
     * @param targetPath  目标文件路径
     * @param fileName    导出文件名（不含扩展名）
     * @return ExportResult 包含导出结果信息
     */
    ExportResult export(List<Map<String, Object>> data, String targetPath, String fileName);

    /**
     * 获取此导出器支持的文件格式.
     *
     * @return 格式标识（如 xlsx, csv）
     */
    String supportedFormat();
}
