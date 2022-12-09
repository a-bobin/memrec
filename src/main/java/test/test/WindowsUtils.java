package test.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class WindowsUtils {

    public static List<Process> listRunningProcesses() {
        List<Process> processes = new ArrayList<>();
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
                Runtime.getRuntime().exec("tasklist.exe /fo csv /nh").getInputStream()))) {
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

    public static Integer getTotalMemory(String processName) {
        return listRunningProcesses().stream()
                .filter(process -> process.getName().equals(processName))
                .map(Process::getMemory)
                .reduce(0, Integer::sum);
    }

    public static Long countProcesses(String processName) {
        return listRunningProcesses().stream()
                .filter(process -> process.getName().equals(processName))
                .count();
    }

    public static boolean processesContain(String processName) {
        return listRunningProcesses().stream().anyMatch(s -> s.getName().equals(processName));
    }
}
