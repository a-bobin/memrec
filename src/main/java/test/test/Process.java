package test.test;

public class Process {

    private String name;

    private Integer pid;

    private Integer memory;

    public Process(String[] input) {
        if (input != null && input.length > 4) {
            name = input[0];
            pid = Integer.parseInt(input[1]);
            String memStr = input[4].trim();
            memory = memStr.isEmpty() ? 0 : Integer.parseInt(memStr.replaceAll("\\D", ""));
        }
    }

    public String getName() {
        return name;
    }

    public Integer getPid() {
        return pid;
    }

    public Integer getMemory() {
        return memory;
    }

    @Override
    public String toString() {
        return String.format("Process{%s|%d|%.2fMB}", name, pid, memory/1024.0);
    }
}
