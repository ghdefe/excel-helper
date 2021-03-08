import java.util.Objects;

public class DBInstance {
    private String port;

    private String name;

    private String remark;

    private String tenant;

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DBInstance that = (DBInstance) o;
        return ((DBInstance) o).port.equals(that.port) && ((DBInstance) o).name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(port, name);
    }

}
