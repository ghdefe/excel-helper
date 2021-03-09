import com.csvreader.CsvWriter;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

public class JVMSourceHelper {

    public void getInResult() {
        File file = new File("C:\\Users\\chunmiaoz\\Desktop\\工作记录\\caiting\\定期巡检结果-3月\\资源配额标准 - 定期巡检结果 - 阳江 - 202103.xlsx");
        try (
                FileInputStream is = new FileInputStream(file);
                XSSFWorkbook workbook = new XSSFWorkbook(is);
                FileOutputStream os = new FileOutputStream(new File("C:\\Users\\chunmiaoz\\Desktop\\工作记录\\caiting\\excel-helper\\result\\result.csv"),true)
        ) {
            CsvWriter writer = new CsvWriter(os, ',', Charset.forName("GBK"));
            XSSFSheet stand = workbook.getSheetAt(0);
            XSSFSheet data = workbook.getSheetAt(2);
            Map<String, JVMConfig> resultHashMap = getDataResultHashMap(data);
            int lastRowNum = stand.getLastRowNum();
            for (int i = 0; i < lastRowNum; i++) {
                XSSFRow row = stand.getRow(i);
                if (Objects.nonNull(row)) {
                    XSSFCell cell = row.getCell(3);
                    String name = cell.getStringCellValue();
                    JVMConfig config = resultHashMap.get(name);
                    if (Objects.nonNull(config)) {
                        double copy = config.getCopy();
                        double cpu = config.getCpu();
                        String groupName = config.getGroupName();
                        double initMemory = config.getInitMemory();
                        double maxMemory = config.getMaxMemory();
                        double memory = config.getMemory();
                        String nameSpace = config.getNameSpace();
                        writer.writeRecord(new String[]{
                                name, String.valueOf(cpu), String.valueOf(memory), String.valueOf(copy), String.valueOf(initMemory), String.valueOf(maxMemory), groupName, nameSpace
                        });
                    } else {
                        writer.writeRecord(new String[]{
                                name
                        });
                    }

                }
            }
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }



    public Map<String, JVMConfig> getDataResultHashMap(XSSFSheet data) {
        HashMap<String, JVMConfig> map = new HashMap<>();
        int lastRowNum = data.getLastRowNum();
        for (int i = 1; i < lastRowNum; i++) {
            XSSFRow row = data.getRow(i);
            String name = row.getCell(2).getStringCellValue();
            if (name.isEmpty()) {
                continue;
            }
            double copy = row.getCell(3).getNumericCellValue();
            double cpu = row.getCell(4).getNumericCellValue();
            double memory = row.getCell(5).getNumericCellValue();
            double initMemory = row.getCell(6).getNumericCellValue();
            double maxMemory = row.getCell(7).getNumericCellValue();
            String groupName = row.getCell(0).getStringCellValue();
            String nameSpace = row.getCell(1).getStringCellValue();
            JVMConfig config = new JVMConfig();
            config.setName(name);
            config.setCopy(copy);
            config.setCpu(cpu);
            config.setMemory(memory);
            config.setInitMemory(initMemory);
            config.setMaxMemory(maxMemory);
            config.setGroupName(groupName);
            config.setNameSpace(nameSpace);

            if (map.containsKey(name)) {
                System.out.println(name + "已存在");
            } else {
                map.put(name, config);
            }


        }
        return map;
    }

    public void getNotInResult() {
        File file = new File("C:\\Users\\chunmiaoz\\Desktop\\工作记录\\caiting\\定期巡检结果-3月\\资源配额标准 - 定期巡检结果 - 阳江 - 202103.xlsx");
        try (
                FileInputStream is = new FileInputStream(file);
                XSSFWorkbook workbook = new XSSFWorkbook(is);
                FileOutputStream os = new FileOutputStream(new File("C:\\Users\\chunmiaoz\\Desktop\\工作记录\\caiting\\excel-helper\\result\\result.csv"),true)
        ) {
            CsvWriter writer = new CsvWriter(os, ',', Charset.forName("GBK"));
            XSSFSheet stand = workbook.getSheetAt(0);
            XSSFSheet data = workbook.getSheetAt(2);
            final HashSet<String> resSet = new HashSet<>();
            int lastRowNum = stand.getLastRowNum();
            for (int i = 0; i < lastRowNum; i++) {
                XSSFRow row = stand.getRow(i);
                if (Objects.nonNull(row)) {
                    XSSFCell cell = row.getCell(3);
                    String name = cell.getStringCellValue();
                    resSet.add(name);
                }
            }
            final int lastRowNum1 = data.getLastRowNum();
            for (int i = 1; i < lastRowNum1; i++) {
                final XSSFRow row = data.getRow(i);
                final String name = row.getCell(2).getStringCellValue();
                if (!resSet.contains(name)) {
                    writer.writeRecord(new String[]{
                            name,
                            String.valueOf(row.getCell(4).getNumericCellValue()),
                            String.valueOf(row.getCell(5).getNumericCellValue()),
                            String.valueOf(row.getCell(3).getNumericCellValue()),
                            String.valueOf(row.getCell(6).getNumericCellValue()),
                            String.valueOf(row.getCell(7).getNumericCellValue()),
                            row.getCell(0).getStringCellValue(),
                            row.getCell(1).getStringCellValue()
                    });

                }
            }
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new JVMSourceHelper().getNotInResult();
    }
}
