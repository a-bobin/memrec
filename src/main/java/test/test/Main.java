package test.test;

public class Main {

    public static void main(String[] args) {
        boolean verbose = false;
        if (args != null && args.length > 0 && args[0].equals("-v")) {
            verbose = true;
        }

        new Recorder(verbose).run();
    }
}
