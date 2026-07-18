package com.databinder.core.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;

public class ErrorLogger {

    private static final String FILE = "import-errors.txt";

    public static synchronized void log(Exception e, Long cardId) {

        try (FileWriter fw = new FileWriter(FILE, true);
             PrintWriter pw = new PrintWriter(fw)) {

            pw.println("======================================");
            pw.println("TIME: " + LocalDateTime.now());
            pw.println("CARD ID: " + cardId);
            pw.println();

            e.printStackTrace(pw);

            pw.println();

        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
}