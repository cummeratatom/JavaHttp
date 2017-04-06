package com.jiaufwei.log;

import java.util.Date;

/**
 * 日志
 * <br>szlogger.ini 设置 CONSOLE_PRINT 日志是否输出到控制台 true or false
 * <br>szlogger.ini 设置 LOG_LEVEL 日志的等级,忽律大小写 DEBUG INFO WARN ERROR
 * <br>szlogger.ini 设置 LOG_PATH 日志的文件名带目录，log/sz.log
 * <br>szlogger.ini 设置 CONSOLE_FILE 日志是否输出到文件 true or false
 * <br>szlogger.ini 设置 CONSOLE_FILE_BUFFER 日志双缓冲输出到文件 true or false
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class SzLogger {

    public static void main(String[] args) throws InterruptedException {
        CommUtil.BUFFER_TIME = 20;
        CommUtil.BUFFER_TIME_LOGSIZE = 50000;
//        CommUtil.PRINT_CONSOLE = false;
        CommUtil.LOG_PRINT_PATH = "../log/sz.log";
        CommUtil.LOG_PRINT_LEVEL = LogLevel.INFO;
        getLogger().debug("debug");
        getLogger().info("info");
        getLogger().error("error");
        getLogger().writeLogFile("../log/tt.log", LogLevel.ERROR, "ccccccccccccccc");
//        while (true) {
//            Thread.sleep(1000);
//            for (int i = 0; i < 10; i++) {
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        for (int i = 0; i < 2000; i++) {
//                            ME_LOGGER.error("ccccccccccccccccccccccccccccccccccccccccccccccccchw4hjnbshb45tyw4e56ye56u53hbwfbsrty4w56yrhbsfbhwry735464bhsfhbw456y34w5dsfh");
//                        }
//                    }
//                }).start();
//            }
//
//        }

        System.exit(0);
    }
    private static SzLogger ME_LOGGER = null;

    public static SzLogger getLogger() {
        if (ME_LOGGER == null) {
            synchronized (SzLogger.class) {
                if (ME_LOGGER == null) {
                    ME_LOGGER = new SzLogger();
                }
            }
        }
        return ME_LOGGER;
    }

    public static SzLogger getLogger(String name) {
        return getLogger();
    }

    public static SzLogger getLogger(Class clazz) {
        return getLogger();
    }

    private SzLogger() {
        CommUtil.initConfig();
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                CommUtil.ISRUN = false;
            }
        }));
    }

//    LogThread logThread = new LogThread();
    WriterFile wfile = null;
    WriterFile wfileError = null;
    WriterConsole wconsole = null;

    /**
     *
     * @return
     */
    public boolean isDebugEnabled() {
        return CommUtil.LOG_PRINT_LEVEL.getGroup() <= LogLevel.DEBUG.getGroup();
    }

    /**
     *
     * @return
     */
    public boolean isInfoEnabled() {
        return CommUtil.LOG_PRINT_LEVEL.getGroup() <= LogLevel.INFO.getGroup();
    }

    /**
     *
     * @return
     */
    public boolean isWarnEnabled() {
        return CommUtil.LOG_PRINT_LEVEL.getGroup() <= LogLevel.WARN.getGroup();
    }

    /**
     *
     * @return
     */
    public boolean isErrorEnabled() {
        return CommUtil.LOG_PRINT_LEVEL.getGroup() <= LogLevel.ERROR.getGroup();
    }

    /**
     * 写调试日志
     *
     * @param logMsg 日志内容
     */
    public void debug(Object logMsg) {
        debug(logMsg, null);
    }

    /**
     * 写调试日志
     *
     * @param logMsg 日志内容
     * @param rowable
     */
    public void debug(Object logMsg, Throwable rowable) {
        if (isDebugEnabled()) {
            writeLog(LogLevel.DEBUG, logMsg, rowable);
        }
    }

    /**
     * 写普通日志
     *
     * @param logMsg 日志内容
     */
    public void info(Object logMsg) {
        info(logMsg, null);
    }

    /**
     * 写普通日志
     *
     * @param logMsg 日志内容
     * @param rowable
     */
    public void info(Object logMsg, Throwable rowable) {
        if (isInfoEnabled()) {
            writeLog(LogLevel.INFO, logMsg, rowable);
        }
    }

    /**
     * 写警告日志
     *
     * @param logMsg 日志内容
     */
    public void warn(Object logMsg) {
        warn(logMsg, null);
    }

    /**
     * 写警告日志
     *
     * @param logMsg 日志内容
     * @param rowable
     */
    public void warn(Object logMsg, Throwable rowable) {
        if (isWarnEnabled()) {
            writeLog(LogLevel.WARN, logMsg, rowable);
        }
    }

    /**
     * 写错误日志
     *
     * @param logMsg 日志内容
     */
    public void error(Object logMsg) {
        error(logMsg, null);
    }

    /**
     * 写错误日志
     *
     * @param logMsg 日志内容
     * @param rowable
     */
    public void error(Object logMsg, Throwable rowable) {
        if (isErrorEnabled()) {
            writeLog(LogLevel.ERROR, logMsg, rowable);
        }
    }

    /**
     * 写严重错误日志
     *
     * @param logMsg 日志内容
     */
    public void fatal(Object logMsg) {
        fatal(logMsg, null);
    }

    /**
     * 写严重错误日志
     *
     * @param logMsg 日志内容
     * @param rowable
     */
    public void fatal(Object logMsg, Throwable rowable) {
        writeLog(LogLevel.FATAL, logMsg, rowable);
    }

    /**
     * 写系统日志
     *
     * @param level 日志级别
     * @param logMsg 日志内容
     */
    public void writeLog(LogLevel level, Object logMsg) {
        writeLog(level, logMsg, null);
    }

    /**
     * 输出日志
     *
     * @param level
     * @param object
     * @param throwable
     */
    private void writeLog(LogLevel level, Object object, Throwable throwable) {
        StringBuilder builder = getStringBuilder(level, object);
        if (CommUtil.ISRUN) {
            if (CommUtil.LOG_PRINT_CONSOLE) {
                if (wconsole == null) {
                    synchronized (this) {
                        if (wconsole == null) {
                            wconsole = new WriterConsole();
                        }
                    }
                }
                String toString = builder.toString();
                wconsole.add(toString, throwable);
            }

            if (CommUtil.LOG_PRINT_FILE) {
                actionThrowable(builder, throwable);
                String toString = builder.toString();
                if (throwable != null) {
                    /*处理异常输出到异常文件，额外输出，便于查看*/
                    if (wfileError == null) {
                        synchronized (this) {
                            if (wfileError == null) {
                                wfileError = new WriterFile(true);
                            }
                        }
                    }
                    wfileError.add(toString);
                }

                /*处理日志输出到文件*/
                if (wfile == null) {
                    synchronized (this) {
                        if (wfile == null) {
                            wfile = new WriterFile();
                        }
                    }
                }

                wfile.add(toString);
            }
        } else {
            writeConsole(object, throwable);
        }
    }

    /**
     * 写日志,在高并发下会造成阻塞，block
     *
     * @param logFileName 日志文件名,包含路径
     * @param level 日志级别
     * @param logMsg 日志内容
     */
    @Deprecated
    public void writeLogFile(String logFileName, LogLevel level, Object logMsg) {
        writeLogFile(logFileName, level, logMsg, null);
    }

    /**
     * 写日志,在高并发下会造成阻塞，block
     *
     * @param logFileName 日志文件名,包含路径
     * @param level 日志级别
     * @param logMsg 日志内容
     * @param throwable
     */
    @Deprecated
    public void writeLogFile(String logFileName, LogLevel level, Object logMsg, Throwable throwable) {
        if (CommUtil.LOG_PRINT_LEVEL.getGroup() <= level.getGroup()) {
            try (WriterFile writerFile = new WriterFile(logFileName)) {
                writerFile.createFileWriter();
                StringBuilder stringBuilder = getStringBuilder(level, logMsg);
                if (CommUtil.LOG_PRINT_CONSOLE) {
                    writeConsole(stringBuilder, throwable);
                }
                actionThrowable(stringBuilder, throwable);
                writerFile.write(stringBuilder.toString());
            }
        }
    }

    static StringBuilder getStringBuilder(LogLevel level, Object content) {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        builder.append(CommUtil.DateFormat_Log.format(new Date()));
        builder.append(":");
        builder.append(level.getLevelString());
        builder.append(":");
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        /*打印调用函数堆栈信息*/
        for (int i = 2; i < stackTrace.length; i++) {
            StackTraceElement stackTraceElement = stackTrace[i];

            if (stackTraceElement.getClassName().equalsIgnoreCase(SzLogger.class.getName())) {
                continue;
            }
            builder.append(stackTraceElement.getFileName().replace(".java", "."));
            builder.append(stackTraceElement.getMethodName());
            builder.append("():");
            builder.append(stackTraceElement.getLineNumber());
            break;
        }
        builder.append("] ");
        builder.append(content.toString());
        builder.append("\n");
        return builder;
    }

    /**
     * 处理错误日志的堆栈信息
     *
     * @param builder
     * @param throwable
     */
    static void actionThrowable(StringBuilder builder, Throwable throwable) {
        if (throwable != null) {
            StackTraceElement[] stackTraces = throwable.getStackTrace();
            for (int i = 0; i < stackTraces.length; i++) {
                StackTraceElement stackTraceElement = stackTraces[i];
                builder.append("at ");
                builder.append(stackTraceElement.getClassName());
                builder.append(".");
                builder.append(stackTraceElement.getMethodName());
                builder.append("(");
                builder.append(stackTraceElement.getFileName());
                builder.append(":");
                builder.append(stackTraceElement.getLineNumber());
                builder.append(")\n");
            }
            builder.append("===========================================\n");
        }
    }

    /**
     * 会加入换行符，和时间调用堆栈
     * <br>
     * 高并发调用回导致卡死
     *
     * @param object
     */
    @Deprecated
    public static void writeConsoleln(Object object) {
        writeConsoleln(object, null);
    }

    /**
     * 会加入换行符，和时间调用堆栈
     * <br>
     * 高并发调用回导致卡死
     *
     * @param object
     * @param throwable
     */
    @Deprecated
    public static void writeConsoleln(Object object, Throwable throwable) {
        StringBuilder stringBuilder = SzLogger.getStringBuilder(LogLevel.INFO, object);
        writeConsole(stringBuilder.toString(), throwable);
    }

    /**
     * 打印日志到控制台
     * <br>
     * 高并发调用回导致卡死
     *
     * @param object
     */
    @Deprecated
    public static void writeConsole(Object object) {
        writeConsole(object, null);
    }

    /**
     * 打印日志到控制台
     * <br>
     * 高并发调用回导致卡死
     *
     * @param object
     * @param throwable
     */
    @Deprecated
    public static void writeConsole(Object object, Throwable throwable) {
        try {
            System.out.print(object.toString());
            if (throwable != null) {
                throwable.printStackTrace(System.out);
            }
        } catch (Throwable e) {
            e.printStackTrace(System.out);
        }
    }

    public int logSize() {
        return wfile == null ? 0 : wfile.logs == null ? 0 : wfile.logs.size();
    }
}
