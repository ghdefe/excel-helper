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

    public void getInResult(String path) {
        File file = new File(path);
        try (
                FileInputStream is = new FileInputStream(file);
                XSSFWorkbook workbook = new XSSFWorkbook(is);
                FileOutputStream os = new FileOutputStream(new File(System.getProperty("user.dir") + "/result", "result-no-in.csv"), false)
        ) {
            CsvWriter writer = new CsvWriter(os, ',', Charset.forName("GBK"));
            XSSFSheet stand = workbook.getSheetAt(0);
            XSSFSheet data = workbook.getSheetAt(1);
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
                                name, "","",String.valueOf(cpu), String.valueOf(memory), String.valueOf(copy), String.valueOf(initMemory), String.valueOf(maxMemory), groupName, nameSpace
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

    public void getNotInResult(String path) {
        int copyErrorCount = 0;
        int memoryErrorCount = 0;
        int jvmConfigErrorCount = 0;
        File file = new File(path);
        try (
                FileInputStream is = new FileInputStream(file);
                XSSFWorkbook workbook = new XSSFWorkbook(is);
                FileOutputStream os = new FileOutputStream(new File(System.getProperty("user.dir") + "/result", "result-in.csv"), false)
        ) {
            CsvWriter writer = new CsvWriter(os, ',', Charset.forName("GBK"));
            XSSFSheet stand = workbook.getSheetAt(0);
            XSSFSheet data = workbook.getSheetAt(1);
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
                if (name.isEmpty()) {
                    continue;
                }
                String cpu = String.valueOf(row.getCell(4).getNumericCellValue());
                String memory = String.valueOf(row.getCell(5).getNumericCellValue());
                String copy = String.valueOf(row.getCell(3).getNumericCellValue());
                String initMemory = String.valueOf(row.getCell(6).getNumericCellValue());
                String maxMemory = String.valueOf(row.getCell(7).getNumericCellValue());
                String groupName = row.getCell(0).getStringCellValue();
                String nameSpace = row.getCell(1).getStringCellValue();
                if (!checkCopy(copy)) {
                    copyErrorCount++;
                }

                if (!checkMemory(memory, initMemory, maxMemory)) {
                    memoryErrorCount++;
                }

                if (!initMemory.contains("%")) {
                    jvmConfigErrorCount++;
                }
                if (!resSet.contains(name)) {

                    writer.writeRecord(new String[]{
                            name,
                            "",
                            "",
                            cpu,
                            memory,
                            copy,
                            initMemory,
                            maxMemory,
                            groupName,
                            nameSpace
                    });

                }
            }
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("副本数不符: " + copyErrorCount);
        System.out.println("内存配额不当: " + memoryErrorCount);
        System.out.println("Jvm配置不当: " + jvmConfigErrorCount);
    }

    public boolean checkCopy(String copy) {
        return Float.parseFloat(copy) >1;

    }

    public boolean checkMemory(String memoryStr,String initMemoryStr,String maxMemoryStr) {
        if (initMemoryStr.contains("%")) {
            float initRam = Float.parseFloat(initMemoryStr.substring(0, initMemoryStr.length() - 1));
            float maxRam = Float.parseFloat(maxMemoryStr.substring(0, maxMemoryStr.length() - 1));
            return checkMemory(initRam,maxRam);
        } else {

            float memory = Float.parseFloat(memoryStr);
            float initMemory = Float.parseFloat(initMemoryStr);
            float maxMemory = Float.parseFloat(maxMemoryStr);
            memory = memory < 100 ? memory * 1024 : memory;
            initMemory = initMemory < 100 ? initMemory * 1024 : initMemory;
            maxMemory = maxMemory < 100 ? maxMemory * 1024 : maxMemory;
            return checkMemory(initMemory,maxMemory,memory);
        }





    }


    public boolean checkMemory(float initRam, float maxRam, float totalRam) {
        float initPercent = initRam / totalRam;
        float maxPercent = maxRam / totalRam;

        if (initPercent >= 0.3f && initPercent <= 0.5f) {
            if (maxPercent >= 0.8f && maxPercent <= 0.9f) {
            } else {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

    public boolean checkMemory(float initRam, float maxRam) {
        if (initRam >= 30 && initRam <= 50) {
            if (maxRam >= 80 && maxRam <= 90) {
            } else {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

    public static void main(String[] args) {
        JVMSourceHelper jvmSourceHelper = new JVMSourceHelper();
        String path = "D:\\工作记录\\caiting\\3月巡检\\定期巡检结果 - 资源配额标准 - 汕头 - 202103.xlsx";
        jvmSourceHelper.getInResult(path);
        jvmSourceHelper.getNotInResult(path);
    }
}
