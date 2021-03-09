import java.util.Objects;

public class JVMConfig {
    private double copy;

    private double cpu;

    private double memory;

    private double initMemory;

    private double maxMemory;

    private String groupName;

    private String nameSpace;

    private String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JVMConfig jvmConfig = (JVMConfig) o;
        return name.equals(jvmConfig.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    public double getCopy() {
        return copy;
    }

    public void setCopy(double copy) {
        this.copy = copy;
    }

    public double getCpu() {
        return cpu;
    }

    public void setCpu(double cpu) {
        this.cpu = cpu;
    }

    public double getMemory() {
        return memory;
    }

    public void setMemory(double memory) {
        this.memory = memory;
    }

    public double getInitMemory() {
        return initMemory;
    }

    public void setInitMemory(double initMemory) {
        this.initMemory = initMemory;
    }

    public double getMaxMemory() {
        return maxMemory;
    }

    public void setMaxMemory(double maxMemory) {
        this.maxMemory = maxMemory;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getNameSpace() {
        return nameSpace;
    }

    public void setNameSpace(String nameSpace) {
        this.nameSpace = nameSpace;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "JVMConfig{" +
                "copy=" + copy +
                ", cpu=" + cpu +
                ", memory=" + memory +
                ", initMemory=" + initMemory +
                ", maxMemory=" + maxMemory +
                ", groupName='" + groupName + '\'' +
                ", nameSpace='" + nameSpace + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
