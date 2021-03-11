import com.csvreader.CsvWriter;
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
import java.util.TreeSet;

public class ExcelHelper {

    /**
     * 统计所有不重复的表
     *
     * @param path
     */
    public void countAllToOne(String path) {
        String[] inputs = {
                "定期巡检结果 - 数据库规范 - 省本级 - 202103.xlsx"
//                "定期巡检结果 - 数据库规范 - 江门 - 202103.xlsx",
//                "定期巡检结果 - 数据库规范 - 阳江 - 202103.xlsx",
//                "定期巡检结果 - 数据库规范 - 云浮 - 202103.xlsx"
        };
        TreeSet<TableDomain> resSet = new TreeSet<>();
        for (String input : inputs) {
            File file = new File("D:\\工作记录\\caiting\\3月巡检\\" + input);
            addResult(file, resSet);

        }


        File resultFile = new File(System.getProperty("user.dir") + "/result", "table-result.csv");
        CsvWriter csvWriter = null;
        try (FileOutputStream fos = new FileOutputStream(resultFile)) {
            csvWriter = new CsvWriter(fos, ',', Charset.forName("GBK"));
            CsvWriter finalCsvWriter = csvWriter;
            for (TableDomain tableDomain : resSet) {
                csvWriter.writeRecord(new String[]{
                        tableDomain.getTableName(),
                        tableDomain.getRemark(),
                        tableDomain.getDbName(),
                        tableDomain.getPort(),
                        tableDomain.getTenantDev(),
                        tableDomain.getTenantImpl()
                });
            }
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

    private void addResult(File file, TreeSet<TableDomain> resSet) {
        try (XSSFWorkbook workbook = new XSSFWorkbook(file)) {
            System.out.println("当前文件: " + file.getName());
            workbook.sheetIterator().forEachRemaining(sheet -> {
                System.out.println("当前表: " + sheet.getSheetName());
                if (sheet.getSheetName().equals("统计报告")) {
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
                    tableDomain.setRemark(row.getCell(2).getStringCellValue());
                    tableDomain.setDbName(row.getCell(3).getStringCellValue());
                    tableDomain.setTableName(row.getCell(4).getStringCellValue());
                    resSet.add(tableDomain);

                });

            });


        } catch (InvalidFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ExcelHelper excelHelper = new ExcelHelper();
        excelHelper.countAllToOne("D:\\工作记录\\caiting\\3月巡检\\定期巡检结果 - 数据库规范 - 省本级 - 202103.xlsx");
    }

}
