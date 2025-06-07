package fabian.hartman.MinecraftAFKBot.io.logging;

import java.io.PrintStream;
import java.util.logging.Logger;

public class CustomPrintStream extends PrintStream {

    private static final PrintStream originalSystemOut = System.out;
    private static final PrintStream originalSystemErr = System.err;
    private static CustomPrintStream systemOutToLogger;

    private String packageOrClassToLog;
    private Logger logger;

    public static CustomPrintStream enableForPackage(String packageToLog, Logger logger) {
        systemOutToLogger = new CustomPrintStream(originalSystemOut, packageToLog, logger);
        System.setOut(systemOutToLogger);
        System.setErr(systemOutToLogger);
        return systemOutToLogger;
    }

    public static void disable() {
        System.setOut(originalSystemOut);
        System.setErr(originalSystemErr);
        systemOutToLogger = null;
    }

    private CustomPrintStream(PrintStream original, String packageOrClassToLog, Logger logger) {
        super(original);
        this.packageOrClassToLog = packageOrClassToLog;
        this.logger = logger;
    }

    @Override
    public void println(String line) {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        StackTraceElement caller = findCallerToLog(stack);
        if (caller == null) {
            super.println(line);
            return;
        }

        logger.info(line);
    }

    @Override
    public void println(Object x) {
        println(String.valueOf(x));
    }

    public StackTraceElement findCallerToLog(StackTraceElement[] stack) {
        for (StackTraceElement element : stack) {
            if (element.getClassName().startsWith(packageOrClassToLog))
                return element;
        }

        return null;
    }
}