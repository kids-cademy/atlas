package com.kidscademy.atlas.util;

import android.content.Context;
import android.os.Environment;

import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import js.log.AbstractLog;
import js.log.LogLevel;
import js.util.Files;

public final class LogManager {
    private static Printer printer = new NullPrinter();

    private static LogLevel level = LogLevel.OFF;

    private LogManager() {
    }

    public static void activateInAppLogging(Context context, LogLevel level, boolean debug) {
        File appStorage = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            // here we have external storage mounted and good for write operations
            appStorage = context.getExternalFilesDir(null);

            // getExternalFilesDir may return null 'if shared storage is not currently available', condition just checked
            // if appStorage is null is quite possible that application manifest does not have permissions
            // <uses-permission android:name="android.permissions.WRITE_EXTERNAL_STORAGE" />

            if (appStorage != null && !appStorage.exists()) {
                appStorage.mkdirs();
            }
        }

        if (debug) {
            PrintWorker worker = new PrintWorker();
            worker.consolePrinter = new ConsolePrinter();
            worker.filePrinter = appStorage != null ? new FilePrinter(appStorage) : new NullPrinter();
            printer = worker;
        } else {
            printer = appStorage != null ? new FilePrinter(appStorage) : new NullPrinter();
        }

        LogManager.level = level;
    }

    public static LogLevel getLevel() {
        return level;
    }

    public static Printer getPrinter() {
        return printer;
    }

    // --------------------------------------------------------------------------------------------

    private static final class LogImpl extends AbstractLog {
        static final int MAX_MESSAGE_LENGTH = 512;

        private String TAG;
        private Printer printer;
        private int masterLevel;

        public LogImpl(String logName) {
            printer = LogManager.getPrinter();
            TagBuilder builder = new TagBuilder(logName);
            TAG = builder.toString();
            masterLevel = LogManager.getLevel().ordinal();
        }

        @Override
        protected boolean isLoggable(LogLevel level) {
            return level.ordinal() >= masterLevel;
        }

        @Override
        protected void log(LogLevel level, String message) {
            printer.println(level, TAG, ellipsis(message, MAX_MESSAGE_LENGTH));
        }

        @Override
        synchronized public void dump(Object message, Throwable throwable) {
            printer.println(LogLevel.FATAL, TAG, ellipsis(message(message), MAX_MESSAGE_LENGTH));
            printer.println(LogLevel.FATAL, TAG, android.util.Log.getStackTraceString(throwable));
        }

        @Override
        public void print(LogLevel logLevel, String s) {

        }
    }

    private static final class TagBuilder {
        /**
         * Android logger TAG length is limited to 23 characters.
         */
        private static final int MAX_TAG_LENGTH = 23;

        private Appender appender = new Appender();

        public TagBuilder(String loggerName) {
            State state = State.FIRST_CHAR;
            BUILD_LOOP:
            for (int i = 0; i < loggerName.length(); ++i) {
                char c = loggerName.charAt(i);
                switch (state) {
                    case FIRST_CHAR:
                        if (!appender.append(c)) {
                            break BUILD_LOOP;
                        }
                        state = Character.isLowerCase(c) ? State.WAIT_SEPARATOR : State.CLASS;
                        break;

                    case WAIT_SEPARATOR:
                        if (c == '.') {
                            if (!appender.append('-')) {
                                break BUILD_LOOP;
                            }
                            state = State.FIRST_CHAR;
                        }
                        break;

                    case CLASS:
                        if (!appender.append(c)) {
                            break BUILD_LOOP;
                        }
                }
            }
        }

        @Override
        public String toString() {
            return appender.toString();
        }

        /**
         * Internal TAG builder with string length limit.
         *
         * @author Iulian Rotaru
         * @since 1.6
         */
        private static class Appender {
            private StringBuilder sb = new StringBuilder();

            boolean append(char c) {
                if (this.sb.length() == MAX_TAG_LENGTH) {
                    return false;
                }
                this.sb.append(c);
                return true;
            }

            @Override
            public String toString() {
                return this.sb.toString();
            }
        }

        /**
         * Android logger TAG builder state machine.
         *
         * @author Iulian Rotaru
         * @since 1.6
         */
        private static enum State {
            FIRST_CHAR, WAIT_SEPARATOR, CLASS
        }
    }

    // --------------------------------------------------------------------------------------------

    private interface Printer {
        void println(LogLevel level, String TAG, String message);
    }

    private static final class PrintWorker implements Printer {
        Printer consolePrinter;
        Printer filePrinter;

        @Override
        public void println(LogLevel level, String TAG, String message) {
            consolePrinter.println(level, TAG, message);
            filePrinter.println(level, TAG, message);
        }
    }

    private static final class ConsolePrinter implements Printer {
        private static final int[] LEVEL = new int[]
                {
                        android.util.Log.INFO, // Level.TRACE
                        android.util.Log.INFO, // Level.DEBUG
                        android.util.Log.INFO, // Level.INFO
                        android.util.Log.WARN, // Level.WARN
                        android.util.Log.ERROR, // Level.ERROR
                        android.util.Log.ERROR, // Level.FATAL
                        android.util.Log.ERROR, // Level.BUG
                        0
                };

        @Override
        public void println(LogLevel level, String TAG, String message) {
            android.util.Log.println(LEVEL[level.ordinal()], TAG, ellipsis(message, LogImpl.MAX_MESSAGE_LENGTH));
        }

        private static final String ELLIPSIS = "...";

        private static String ellipsis(String message, int maxLength) {
            if (message == null) {
                return "null";
            }
            return message.length() < maxLength ? message : message.substring(0, maxLength - ELLIPSIS.length()) + ELLIPSIS;
        }
    }

    private static final class FilePrinter implements Printer, Runnable, Closeable {
        private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM HH:mm:ss.SSS", Locale.getDefault());

        private static final int QUEUE_CAPACITY = 100;

        private final File logFile;
        private final BlockingQueue<Item> queue;
        private volatile boolean running;

        FilePrinter(File appStorage) {
            logFile = new File(appStorage, "app.log");
            queue = new LinkedBlockingQueue<Item>(QUEUE_CAPACITY);

            final Thread thread = new Thread(this);
            thread.start();
        }

        @Override
        public void println(LogLevel level, String TAG, String message) {
            if (!running) {
                return;
            }

            Record record = new Record();
            record.level = level;
            record.TAG = TAG;
            record.message = message;

            queue.offer(record);
        }

        @Override
        public void close() {
            queue.offer(new Command());
        }

        @Override
        public void run() {
            running = true;
            for (; ; ) {
                final Item item;
                try {
                    item = queue.take();
                } catch (InterruptedException e) {
                    continue;
                }

                if (item instanceof Command) {
                    running = false;
                    break;
                }

                assert item instanceof Record;
                final Record record = (Record) item;

                FileWriter writer = null;
                try {
                    writer = new FileWriter(logFile, true);

                    writer.write(record.level.name().charAt(0));
                    writer.write(" ");
                    writer.write(DATE_FORMAT.format(new Date()));
                    writer.write(" ");
                    writer.write(record.TAG);
                    writer.write(" ");
                    writer.write(record.message);
                    writer.write("\r\n");
                    writer.flush();
                } catch (IOException unused) {
                    // ignore exception since there is no place to store it
                } finally {
                    Files.close(writer);
                }
            }
        }

        // ------------------------------------------------------
        // internall classes

        private static interface Item {
        }

        private final static class Record implements Item {
            LogLevel level;
            String TAG;
            String message;
        }

        private final static class Command implements Item {
        }
    }

    private static class NullPrinter implements Printer {
        @Override
        public void println(LogLevel level, String TAG, String message) {
        }
    }
}
