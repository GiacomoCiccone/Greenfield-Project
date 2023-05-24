package utils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.management.ManagementFactory;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;

public class Logger {

    private static final String LOG_FOLDER = getMainClassPathName() + File.separator + "logs";
    private static final java.util.logging.Logger logger;
    private static final String processId = getProcessId();

    private static String getProcessId() {
        String processName = ManagementFactory.getRuntimeMXBean().getName();
        return processName.split("@")[0]; // PID@hostname
    }

    static {
        try {
            File logFolder = new File(LOG_FOLDER);
            if (!logFolder.exists()) {
                logFolder.mkdirs();
            }
            String loggerName = getMainClassPackageName();
            String fileName = LOG_FOLDER + File.separator + getDateFormatted().substring(0, 10) + "." + processId + ".log";
            FileHandler fileHandler = new FileHandler(fileName, true);
            Formatter formatter = new Formatter() {
                @Override
                public String format(LogRecord record) {
                    String logType;
                    if (record.getLevel().intValue() == Level.FINE.intValue()) {
                        logType = "DEBUG";
                    } else {
                        logType = record.getLevel().getName();
                    }
                    String message = record.getMessage();
                    String functionName = getCallingMethodName();
                    int lineNumber = getCallingLineNumber();
                    String date = getDateFormatted();
                    return String.format("[%s] | %s \t | %s:%d - %s\n", date, logType, functionName, lineNumber, message);
                }
            };
            fileHandler.setFormatter(formatter);
            logger = java.util.logging.Logger.getLogger(loggerName);
            logger.setUseParentHandlers(false);
            for (Handler handler : logger.getHandlers()) {
                logger.removeHandler(handler);
            }
            logger.addHandler(fileHandler);
            logger.setLevel(Level.ALL);
            logger.setLevel(new CustomLevel("DEBUG", Level.FINE.intValue() - 1));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to create logger");
        }
    }


    public static void debug(String message) {
        logger.log(Level.FINE, message);
    }

    public static void info(String message) {
        logger.log(Level.INFO, message);
    }

    public static void error(String message) {
        logger.log(Level.SEVERE, message);
    }

    public static void logException(Exception e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        String stackTrace = sw.toString();
        logger.log(Level.SEVERE, stackTrace);
    }

    private static String getDateFormatted() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(new Date());
    }

    static class CustomLevel extends Level {
        public CustomLevel(String name, int value) {
            super(name, value);
        }
    }

    private static String getMainClassPackageName() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        String mainClassName = stackTrace[stackTrace.length - 1].getClassName();
        try {
            Class<?> mainClass = Class.forName(mainClassName);
            return mainClass.getPackage().getName();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to get main class package name");
        }
    }

    private static String getMainClassPathName() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        String mainClassName = stackTrace[stackTrace.length - 1].getClassName();
        try {
            Class<?> mainClass = Class.forName(mainClassName);
            String name = mainClass.getName().replace(".", File.separator);
            return System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator + "java" + File.separator + name.substring(0, name.lastIndexOf(File.separator));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to get main class path name");
        }
    }

    private static String getCallingMethodName() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        String className = stackTrace[10].getClassName();
        return className.substring(className.lastIndexOf(".") + 1) + "::" + stackTrace[10].getMethodName();
    }

    private static int getCallingLineNumber() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        return stackTrace[10].getLineNumber();
    }

}
