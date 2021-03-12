import com.csvreader.CsvWriter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.util.StringUtil;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;
import java.util.TreeSet;

public class ExcelHelper {

    public static void main(String[] args) throws IOException, InvalidFormatException {
        ExcelHelper excelHelper = new ExcelHelper();
        File inputDir = new File("C:\\Users\\chunmiaoz\\Desktop\\工作记录\\caiting\\定期巡检结果-3月\\定期巡检结果 - 数据库规范\\提交的巡检报告");
        for (File input : inputDir.listFiles()) {
            excelHelper.countAllToOne(input, "C:\\Users\\chunmiaoz\\Desktop\\工作记录\\caiting\\定期巡检结果-3月\\定期巡检结果 - 数据库规范\\数字财政项目 - 数据库规范 - 库表信息收集 - 同步 (1).xlsx");

        }
    }

    /**
     * 统计所有不重复的表
     */
    public void countAllToOne(File originFile, String templatePath) throws IOException, InvalidFormatException {
        TreeMap<TableDomain, TableErrorCount> resMap = new TreeMap<>();
        File templateFile = new File(templatePath);
        addResult(originFile, resMap);
//        writeResultToCsvFile(file, resMap);
        writeCSVResultFileWithTemplate(originFile, resMap, templateFile);
        System.out.println(resMap);


    }


    /**
     * 将结果resMap输出到csv文件保存
     *
     * @param originFile
     * @param resMap
     * @throws IOException
     */
    private void writeResultToCsvFile(File originFile, TreeMap<TableDomain, TableErrorCount> resMap) throws IOException {
        File resultFile = new File(System.getProperty("user.dir") + "/result", "table-result-" + originFile.getName() + ".csv ");
        FileUtils.forceMkdir(resultFile.getParentFile());
        try (FileOutputStream fos = new FileOutputStream(resultFile)) {
            CsvWriter csvWriter = new CsvWriter(fos, ',', Charset.forName("GBK"));
            CsvWriter finalCsvWriter = csvWriter;
            finalCsvWriter.writeRecord(new String[]{
                    "类别",
                    "数据表名",
                    "数据表中文注释",
                    "所属数据库名",
                    "所属数据库实例端口",
                    "开发厂商",
                    "实施厂商",
                    "无主键",
                    "重复索引",
                    "字符集不一致",
                    "无效表/临时表",
                    "字段精度有误",
                    "无增量时间戳",
                    "字段无注释",
                    "存二进制数据"
            });
            resMap.forEach((tableDomain, tableErrorCount) -> {
                try {
                    finalCsvWriter.writeRecord(new String[]{
                            tableDomain.getTableName(),
                            tableDomain.getRemark(),
                            tableDomain.getDbName(),
                            tableDomain.getPort(),
                            tableDomain.getTenantDev(),
                            tableDomain.getTenantImpl(),
                            String.valueOf(tableErrorCount.getNoPrimaryKey()),
                            String.valueOf(tableErrorCount.getRepeatIndex()),
                            String.valueOf(tableErrorCount.getCharacterIncorrect()),
                            String.valueOf(tableErrorCount.getIrregularName()),
                            String.valueOf(tableErrorCount.getPrecisionError()),
                            String.valueOf(tableErrorCount.getNoIncrementalTimestamp()),
                            String.valueOf(tableErrorCount.getNoColumnComment()),
                            String.valueOf(tableErrorCount.getSaveBitData())
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            csvWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeCSVResultFileWithTemplate(File originFile, TreeMap<TableDomain, TableErrorCount> resMap, File template) throws IOException, InvalidFormatException {
        File resultFile = new File(System.getProperty("user.dir") + "/result", "table-result-" + originFile.getName() + ".csv ");
        FileUtils.forceMkdir(resultFile.getParentFile());
        try (
                XSSFWorkbook workbook = new XSSFWorkbook(template);
                FileOutputStream fos = new FileOutputStream(resultFile)
        ) {
            CsvWriter csvWriter = new CsvWriter(fos, ',', Charset.forName("GBK"));
            writeHeader(csvWriter);
            XSSFSheet templateSheet = workbook.getSheetAt(0);
            templateSheet.forEach(cells -> {
                Optional.ofNullable(cells.getCell(0)).ifPresent(cell -> {
                    Cell tableCell = cells.getCell(1);
                    tableCell.setCellType(CellType.STRING);
                    String tableName = tableCell.getStringCellValue();
                    String dbName = cells.getCell(3).getStringCellValue();
                    String port = Optional.ofNullable(cells.getCell(4)).map(cell1 -> {
                        cell1.setCellType(CellType.STRING);
                        return cell1.getStringCellValue();
                    }).orElse("");
                    if (tableName.equals("表名") || tableName.equals("数据表名")){
                        return;
                    }
                    TableDomain tempDomain = new TableDomain();
                    tempDomain.setTableName(tableName);
                    tempDomain.setDbName(dbName);
                    tempDomain.setPort(port);
                    // 从模板中获取标准值
                    String comment = Optional.ofNullable(cells.getCell(2)).map(Cell::getStringCellValue).orElse("");
                    String tenantDev = Optional.ofNullable(cells.getCell(5)).map(Cell::getStringCellValue).orElse("");
                    String tenantImpl = Optional.ofNullable(cells.getCell(6)).map(Cell::getStringCellValue).orElse("");
                    String remark = Optional.ofNullable(cells.getCell(7)).map(Cell::getStringCellValue).orElse("");
                    // 该行在模板文件中有对应行则输出该行结果
                    Optional<TableErrorCount> optionalTableErrorCount = Optional.ofNullable(resMap.get(tempDomain));
                    optionalTableErrorCount.ifPresentOrElse(tableErrorCount -> {
                                try {

                                    csvWriter.writeRecord(new String[]{
                                            cell.getStringCellValue(),
                                            tableName,
                                            comment,
                                            dbName,
                                            port,
                                            tenantDev,
                                            tenantImpl,
                                            remark,
                                            String.valueOf(tableErrorCount.getNoPrimaryKey()),
                                            String.valueOf(tableErrorCount.getRepeatIndex()),
                                            String.valueOf(tableErrorCount.getCharacterIncorrect()),
                                            String.valueOf(tableErrorCount.getIrregularName()),
                                            String.valueOf(tableErrorCount.getPrecisionError()),
                                            String.valueOf(tableErrorCount.getNoIncrementalTimestamp()),
                                            String.valueOf(tableErrorCount.getNoColumnComment()),
                                            String.valueOf(tableErrorCount.getSaveBitData())
                                    });
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                resMap.remove(tempDomain);
                            },
                            () -> {
                                try {
                                    csvWriter.writeRecord(new String[]{
                                            cell.getStringCellValue(),
                                            tableName,
                                            comment,
                                            dbName,
                                            port,
                                            tenantDev,
                                            tenantImpl,
                                            remark
                                    });
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            });

                });
            });
            resMap.forEach((tableDomain, tableErrorCount) -> {
                try {
                    csvWriter.writeRecord(new String[]{
                            "未匹配",
                            tableDomain.getTableName(),
                            "",
                            tableDomain.getDbName(),
                            tableDomain.getPort(),
                            tableDomain.getTenantDev(),
                            tableDomain.getTenantImpl(),
                            tableDomain.getRemark(),
                            String.valueOf(tableErrorCount.getNoPrimaryKey()),
                            String.valueOf(tableErrorCount.getRepeatIndex()),
                            String.valueOf(tableErrorCount.getCharacterIncorrect()),
                            String.valueOf(tableErrorCount.getIrregularName()),
                            String.valueOf(tableErrorCount.getPrecisionError()),
                            String.valueOf(tableErrorCount.getNoIncrementalTimestamp()),
                            String.valueOf(tableErrorCount.getNoColumnComment()),
                            String.valueOf(tableErrorCount.getSaveBitData())
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            csvWriter.close();
        }
    }

    private void writeHeader(CsvWriter csvWriter) throws IOException {
        csvWriter.writeRecord(new String[]{
                "类别",
                "数据表名",
                "数据表中文注释",
                "所属数据库名",
                "所属数据库实例端口",
                "开发厂商",
                "实施厂商",
                "备注",
                "无主键",
                "重复索引",
                "字符集不一致",
                "无效表/临时表",
                "字段精度有误",
                "无增量时间戳",
                "字段无注释",
                "存二进制数据"
        });
    }


    /**
     * 增加扫描结果到TreeMap
     *
     * @param file
     * @param resMap
     */
    private void addResult(File file, TreeMap<TableDomain, TableErrorCount> resMap) {
        try (XSSFWorkbook workbook = new XSSFWorkbook(file)) {
            System.out.println("当前文件: " + file.getName());
            workbook.sheetIterator().forEachRemaining(sheet -> {
                System.out.println("当前表: " + sheet.getSheetName());
                if (sheet.getSheetName().equals("统计报告") || sheet.getSheetName().equals("检查方法")) {
                    return;
                }
                sheet.forEach(row -> {


                    if (Optional.ofNullable(row.getCell(4)).isEmpty() || StringUtils.isEmpty(row.getCell(4).getStringCellValue())) {
                        return;
                    }
                    TableDomain tableDomain = new TableDomain();
                    Optional.ofNullable(row.getCell(1)).ifPresent(cell -> {
                        cell.setCellType(CellType.STRING);
                        tableDomain.setPort(cell.getStringCellValue());
                    });
                    Optional.ofNullable(row.getCell(0)).ifPresent(cell -> tableDomain.setTenantImpl(cell.getStringCellValue()));
                    Optional.ofNullable(row.getCell(2)).ifPresent(cell -> tableDomain.setRemark(cell.getStringCellValue()));
                    Optional.ofNullable(row.getCell(3)).ifPresent(cell -> tableDomain.setDbName(cell.getStringCellValue()));
                    Optional.ofNullable(row.getCell(4)).ifPresent(cell -> tableDomain.setTableName(cell.getStringCellValue()));
                    if (tableDomain.getTableName().equals("表名")){
                        return;
                    }
                    TableErrorCount count = resMap.getOrDefault(tableDomain, new TableErrorCount());
                    String sheetName = sheet.getSheetName();
                    switch (sheetName) {
                        case "无主键表":
                            count.addNoPrimaryKey();
                            break;

                        case "重复索引":
                            count.addRepeatIndex();
                            break;

                        case "字符集不一致（非utf8-utf8_bin）":
                        case "字符集不一致(非utf8-utf8_bin)":
                            count.addCharacterIncorrect();
                            break;
                        case "备份或临时表不规范":
                            count.addIrregularName();
                            break;
                        case "精度有误":
                            count.addPrecisionError();
                            break;
                        case "无增量时间戳":
                            count.addNoIncrementalTimestamp();
                            break;
                        case "无注释":
                            count.addNoColumnComment();
                            break;
                        case "存二进制数据":
                            count.addSaveBitData();
                            break;
                        default:
                            System.out.println("没有相应的表格: " + sheetName);
                    }

                    resMap.put(tableDomain, count);


                });

            });


        } catch (InvalidFormatException | IOException e) {
            e.printStackTrace();
        }
    }


}
