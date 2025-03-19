package dat;

import dat.config.ApplicationConfig;

public class Main {
    public static void main(String[] args) {
        System.out.println("hello");
        ApplicationConfig.startServer(7070);
    }
}