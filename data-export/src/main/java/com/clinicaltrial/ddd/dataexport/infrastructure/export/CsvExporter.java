package com.clinicaltrial.ddd.dataexport.infrastructure.export;

import com.clinicaltrial.ddd.dataexport.domain.model.valueobject.ExportResult;

import java.util.List;
import java.util.Map;

/**
 * CsvExporter — CSV (.csv) 格式导出器桩实现.
 * <p>
 * 将数据导出为逗号分隔值 (.csv) 格式。
 * 当前为桩（Stub）实现，等待集成后完成。
 * </p>
 */
public class CsvExporter implements DataExporter {

    @Override
    public ExportResult export(List<Map<String, Object>> data, String targetPath, String fileName) {
        // TODO: 使用Apache Commons CSV或OpenCSV库实现CSV文件生成
        // 1. 创建CSV文件并写入BOM头（处理中文编码）
        // 2. 写入表头行
        // 3. 遍历数据写入行（处理特殊字符转义）
        // 4. 上传到文件存储服务
        // 5. 返回ExportResult
        throw new UnsupportedOperationException("CsvExporter not yet implemented");
    }

    @Override
    public String supportedFormat() {
        return "csv";
    }
}
