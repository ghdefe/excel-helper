import com.csvreader.CsvWriter;
import org.apache.commons.io.FileUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
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

    /**
     * 统计所有不重复的表
     *
     * @param path
     */
    public void countAllToOne(String path) throws IOException {
        TreeMap<TableDomain, TableErrorCount> resMap = new TreeMap<>();
        File file = new File(path);
        addResult(file, resMap);


        File resultFile = new File(System.getProperty("user.dir") + "/result", "table-result-" + file.getName() + ".csv ");
        FileUtils.forceMkdir(resultFile.getParentFile());
        CsvWriter csvWriter = null;
        try (FileOutputStream fos = new FileOutputStream(resultFile)) {
            csvWriter = new CsvWriter(fos, ',', Charset.forName("GBK"));
            CsvWriter finalCsvWriter = csvWriter;
            finalCsvWriter.writeRecord(new String[]{
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
            finalCsvWriter.writeRecord(new String[]{
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    ""
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (csvWriter != null) {
                csvWriter.close();
            }
        }

    }

    private void addResult(File file, TreeMap<TableDomain, TableErrorCount> resMap) {
        try (XSSFWorkbook workbook = new XSSFWorkbook(file)) {
            System.out.println("当前文件: " + file.getName());
            workbook.sheetIterator().forEachRemaining(sheet -> {
                System.out.println("当前表: " + sheet.getSheetName());
                if (sheet.getSheetName().equals("统计报告") || sheet.getSheetName().equals("检查方法")) {
                    return;
                }
                sheet.forEach(row -> {
                    if (Objects.isNull(row.getCell(4).getStringCellValue())) {
                        return;
                    }
                    TableDomain tableDomain = new TableDomain();
                    Cell port = row.getCell(1);
                    port.setCellType(CellType.STRING);
                    tableDomain.setTenantImpl(row.getCell(0).getStringCellValue());
                    tableDomain.setPort(port.getStringCellValue());
                    Optional.ofNullable(row.getCell(2)).ifPresent(cell -> tableDomain.setRemark(cell.getStringCellValue()));
                    tableDomain.setDbName(row.getCell(3).getStringCellValue());
                    tableDomain.setTableName(row.getCell(4).getStringCellValue());
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


        } catch (InvalidFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        ExcelHelper excelHelper = new ExcelHelper();
        String fileName = "定期巡检结果 - 数据库规范 - 省本级 - 202103.xlsx";
        String parentPath = "C:\\Users\\chunmiaoz\\Desktop\\工作记录\\caiting\\定期巡检结果-3月\\定期巡检结果 - 数据库规范\\";
        excelHelper.countAllToOne(parentPath + fileName);
    }

}
