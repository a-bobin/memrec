package test.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

public class WindowsUtils {

    public static List<Process> listRunningProcesses(String processName) {
        List<Process> processes = new ArrayList<>();
        String filter = processName == null ? "" : " /fi \"IMAGENAME eq " + processName + "\"";
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
                Runtime.getRuntime().exec("tasklist /fo csv /nh" + filter).getInputStream()))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] lineToArray = line.substring(1,line.length()-1).split("\",\"");
                Process process = new Process(lineToArray);
                processes.add(process);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return processes;
    }

    public static boolean processesContainLike(String string) {
        return listRunningProcesses(null).stream().anyMatch(s -> s.getName().contains(string));
    }

    public static Integer getTotalMemory(String processName) {
        return listRunningProcesses(processName).stream().map(Process::getMemory).reduce(0, Integer::sum);
    }

    public static Long countProcesses(String processName) {
        return (long) listRunningProcesses(processName).size();
    }

    public static Long countDistinctProcesses() {
        return listRunningProcesses(null).stream().map(Process::getName).distinct().count();
    }

    public static List<String> getOutput(Order order, String processName) {
        int len = listRunningProcesses(processName).stream()
                .map(p -> p.getName().length())
                .max(Integer::compare).orElseThrow(NoSuchElementException::new);
        String format = "%-" + len + "s   %-5d   %7.02f MB";
        Comparator<Process> comparator;
        switch (order) {
            case BY_NAME: comparator = Comparator.comparing(Process::getName); break;
            case BY_MEMORY_ASC: comparator = Comparator.comparing(Process::getMemory); break;
            case BY_MEMORY_DESC: comparator = (p1, p2) -> p2.getMemory().compareTo(p1.getMemory()); break;
            case BY_PID:
            default: comparator = (p1, p2) -> 0;
        }

        return listRunningProcesses(processName).stream()
                .sorted(comparator)
                .map(p -> String.format(format, p.getName(), p.getPid(), p.getMemory()/1024.0))
                .collect(Collectors.toList());
    }

    enum Order {
        BY_NAME,
        BY_MEMORY_ASC,
        BY_MEMORY_DESC,
        BY_PID
    }

    public static void main(String[] args) {
        System.out.printf("Total memory consumption is %.2f GB\n", getTotalMemory(null)/1048576.0);
        System.out.printf("Total processes count is %d; of them, %d are distinct\n",
                countProcesses(null), countDistinctProcesses());

        System.out.printf("Processes contain something like 'chrome': %s\n", processesContainLike("chrome"));

        System.out.printf("'Chrome.exe' processes count is %d\n", countProcesses("chrome.exe"));
        System.out.printf("Memory consumption for 'chrome.exe' is %.2f GB\n", getTotalMemory("chrome.exe")/1048576.0);
        System.out.println("---");
        getOutput(Order.BY_MEMORY_DESC, "chrome.exe").forEach(System.out::println);
    }
}
