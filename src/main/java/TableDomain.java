import java.util.Objects;

public class TableDomain implements Comparable {
    private String tenantDev;

    private String tenantImpl;

    private String port;

    private String remark;

    private String dbName;

    private String tableName;

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    @Override
    public int compareTo(Object o) {
        String a = dbName + tableName;
        TableDomain o1 = (TableDomain) o;
        String b = o1.dbName + o1.tableName;
        return a.compareTo(b);
    }


    @Override
    public String toString() {
        return "TableDomain{" +
                "tenantDev='" + tenantDev + '\'' +
                ", tenantImpl='" + tenantImpl + '\'' +
                ", port=" + port +
                ", remark='" + remark + '\'' +
                ", dbName='" + dbName + '\'' +
                ", tableName='" + tableName + '\'' +
                '}';
    }

    public String getTenantDev() {
        return tenantDev;
    }

    public void setTenantDev(String tenantDev) {
        this.tenantDev = tenantDev;
    }

    public String getTenantImpl() {
        return tenantImpl;
    }

    public void setTenantImpl(String tenantImpl) {
        this.tenantImpl = tenantImpl;
    }


    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }


}
