package fr.epita.assistants.ping.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.eclipse.microprofile.config.ConfigProvider;

public class Logger {
  private static final String RESET_TEXT = "\u001B[0m";
  private static final String RED_TEXT = "\u001B[31m";
  private static final String GREEN_TEXT = "\u001B[32m";

  private static String timestamp() {
    return new SimpleDateFormat("dd/MM/yy - HH:mm:ss")
        .format(Calendar.getInstance().getTime());
  }

  private static void writelog(String varEnv, String message, boolean isError) {
    String formatted = "[" + timestamp() + "] " + message;
    String filepath;

    filepath = ConfigProvider.getConfig()
        .getOptionalValue(varEnv, String.class)
        .orElse(System.getenv(varEnv));

    if (filepath != null && !filepath.isEmpty()) {
      try {
        java.io.File file = new java.io.File(filepath);

        var parent = file.getParentFile();

        if (parent != null)
          parent.mkdirs();

        FileWriter writer = new FileWriter(filepath, true);

        writer.write(formatted + "\n");
        writer.close();
      } catch (IOException e) {
        System.err.println("Logger file write failed: " + e.getMessage());
      }
    }

    if (isError) {
      //io.quarkus.logging.Log.error(formatted);
      System.err.println(RED_TEXT + formatted + RESET_TEXT);
    } else {
      //io.quarkus.logging.Log.info(formatted);
      System.out.println(GREEN_TEXT + formatted + RESET_TEXT);
    }
  }

  public static void log(String message) {
    writelog("LOG_FILE", message, false);
  }

  public static void error(String message) {
    writelog("ERROR_LOG_FILE", message, true);
  }
}
