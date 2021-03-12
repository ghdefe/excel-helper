import java.util.Objects;

public class TableDomain implements Comparable {
    private String tenantDev;

    private String tenantImpl;

    private String port;

    private String remark;

    private String dbName;

    private String tableName;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TableDomain that = (TableDomain) o;

        if (!Objects.equals(dbName.trim(), that.dbName.trim())) return false;
        return Objects.equals(tableName.trim(), that.tableName.trim());
    }

    @Override
    public int hashCode() {
        int result = dbName != null ? dbName.hashCode() : 0;
        result = 31 * result + (tableName != null ? tableName.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(Object o) {
        String a = dbName.trim() + tableName.trim();
        TableDomain o1 = (TableDomain) o;
        String b = o1.dbName.trim() + o1.tableName.trim();
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

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
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
