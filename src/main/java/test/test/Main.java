package test.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static test.test.FileUtils.*;
import static test.test.WindowsUtils.*;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        do {
            System.out.println("Enter process name:");
            String input = bufferedReader.readLine().trim();
            if (input.equalsIgnoreCase("exit")) {
                break;
            } else if (processesContain(input)) {
                recordContinously(input);
            } else {
                System.out.printf("Processes do not contain '%s', continue or exit\n", input);
            }
        } while (true);
    }

    private static void recordContinously(String processName) throws InterruptedException {
        String fileName = generateFileName(processName);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATETIME_FORMAT);
        while (true) {
            String line = String.format("%s,%.2f\n",
                    formatter.format(LocalDateTime.now()),
                    getTotalMemory(processName)/1024.0);
            System.out.println("Writing line '" + line.trim() + "' to log");
            writeToFile(line, "log/" + fileName);
            Thread.sleep(1000);
        }
    }
}
