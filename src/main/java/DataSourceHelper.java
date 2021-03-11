import com.csvreader.CsvWriter;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;

public class DataSourceHelper {

    public void readExcel() {
        File file = new File("D:\\工作记录\\caiting\\3月巡检\\定期巡检结果 - 数据库规范 - 云浮 - 202103.xlsx");
        try (
                XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(file));
        ) {
            final Iterator<Sheet> iterator = workbook.sheetIterator();
//            iterator.next();
            while (iterator.hasNext()) {
                Sheet sheet = iterator.next();
                String sheetName = sheet.getSheetName();
                HashMap<DBInstance, Integer> resHM = new HashMap<>();
                System.out.println("___________________ 当前表： " + sheetName + "--------------------------");
                int firstRow = 1;
                int lastRow = sheet.getLastRowNum();
                for (int i = firstRow; i < lastRow; i++) {
                    XSSFRow row = (XSSFRow) sheet.getRow(i);
                    XSSFCell cellTenant = row.getCell(0);
                    XSSFCell cellPort = row.getCell(1);
                    XSSFCell cellRemark = row.getCell(2);
                    XSSFCell cellDBName = row.getCell(3);
                    if (Objects.nonNull(cellTenant) && Objects.nonNull(cellPort) && Objects.nonNull(cellRemark) && Objects.nonNull(cellDBName)) {
                        cellTenant.setCellType(CellType.STRING);
                        cellPort.setCellType(CellType.STRING);
                        cellRemark.setCellType(CellType.STRING);
                        cellDBName.setCellType(CellType.STRING);
                        String port = cellPort.getStringCellValue();
                        String dbName = cellDBName.getStringCellValue();
                        cellDBName.getStringCellValue();
                        if (!port.isEmpty() && !dbName.isEmpty()) {
                            DBInstance dbInstance = new DBInstance();
                            dbInstance.setPort(port);
                            dbInstance.setRemark(cellRemark.getStringCellValue());
                            dbInstance.setName(dbName);
                            dbInstance.setTenant(cellTenant.getStringCellValue());
                            if (!resHM.containsKey(dbInstance)) {
                                resHM.put(dbInstance, 1);
                            } else {
                                Integer count = resHM.get(dbInstance);
                                resHM.put(dbInstance, count + 1);

                            }
                        }

                    }
                }

                // 输出统计结果
                generateResult(resHM,sheetName);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void generateResult(HashMap<DBInstance, Integer> resHM,String question) {
        File file = new File(System.getProperty("user.dir") + "/result","result-db.csv");
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        try (FileOutputStream fileOutputStream = new FileOutputStream(file,true)) {
                CsvWriter writer = new CsvWriter(fileOutputStream, ',', Charset.forName("GBK"));
//                writer.writeRecord(new String[]{
//                        "不合规项","厂家","数据库","数据库备注","异常数"
//                });
                resHM.forEach((dbInstance, integer) -> {
                    String[] data = {
                            question,
                            dbInstance.getTenant(),
                            dbInstance.getName(),
                            dbInstance.getRemark(),
                            String.valueOf(integer)
                    };
                    try {
                        writer.writeRecord(data);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }




    public static void main(String[] args) {
        new DataSourceHelper().readExcel();
    }
}
