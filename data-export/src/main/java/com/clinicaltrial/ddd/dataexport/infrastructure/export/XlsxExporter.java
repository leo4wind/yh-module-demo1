package com.clinicaltrial.ddd.dataexport.infrastructure.export;

import com.clinicaltrial.ddd.dataexport.domain.model.valueobject.ExportResult;

import java.util.List;
import java.util.Map;

/**
 * XlsxExporter — Excel (.xlsx) 格式导出器桩实现.
 * <p>
 * 将数据导出为Microsoft Excel (.xlsx) 格式。
 * 当前为桩（Stub）实现，等待集成Apache POI或其他Excel库后完成。
 * </p>
 */
public class XlsxExporter implements DataExporter {

    @Override
    public ExportResult export(List<Map<String, Object>> data, String targetPath, String fileName) {
        // TODO: 使用Apache POI库实现Excel文件生成
        // 1. 创建工作簿 XSSFWorkbook
        // 2. 创建工作表并写入表头
        // 3. 遍历数据写入行
        // 4. 保存到临时文件
        // 5. 上传到文件存储服务
        // 6. 返回ExportResult
        throw new UnsupportedOperationException("XlsxExporter not yet implemented");
    }

    @Override
    public String supportedFormat() {
        return "xlsx";
    }
}
