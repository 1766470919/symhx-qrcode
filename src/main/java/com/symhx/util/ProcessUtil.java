package com.symhx.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessUtil {
    private static Logger log = LoggerFactory.getLogger(ProcessUtil.class);
    public static final int BUFFER_SIZE = 65536;
    public static final int EXEC_TIME_OUT = 2;
    private ExecutorService exec;

    private ProcessUtil() {
        this.exec = new ThreadPoolExecutor(6, 12, 1L, TimeUnit.MINUTES, new LinkedBlockingQueue(10), new ProcessUtil.CustomThreadFactory("cmd-process"), new CallerRunsPolicy());
    }

    public static ProcessUtil instance() {
        return ProcessUtil.InputStreamConsumer.instance;
    }

    public boolean process(String cmd) throws Exception {
        Process process = Runtime.getRuntime().exec(cmd);
        this.waitForProcess(process);
        return true;
    }

    private int waitForProcess(Process pProcess) throws IOException, InterruptedException, TimeoutException, ExecutionException {
        FutureTask<Object> outTask = new FutureTask(() -> {
            this.processOutput(pProcess.getInputStream(), ProcessUtil.InputStreamConsumer.DEFAULT_CONSUMER);
            return null;
        });
        this.exec.submit(outTask);
        FutureTask<Object> errTask = new FutureTask(() -> {
            this.processError(pProcess.getErrorStream(), ProcessUtil.InputStreamConsumer.DEFAULT_CONSUMER);
            return null;
        });
        this.exec.submit(errTask);

        try {
            outTask.get();
            errTask.get();
        } catch (ExecutionException var8) {
            Throwable t = var8.getCause();
            if (t instanceof IOException) {
                throw (IOException)t;
            }

            if (t instanceof RuntimeException) {
                throw (RuntimeException)t;
            }

            throw new IllegalStateException(var8);
        }

        FutureTask<Integer> processTask = new FutureTask(() -> {
            pProcess.waitFor();
            return pProcess.exitValue();
        });
        this.exec.submit(processTask);
        int rc = (Integer)processTask.get(2L, TimeUnit.SECONDS);

        try {
            pProcess.getInputStream().close();
            pProcess.getOutputStream().close();
            pProcess.getErrorStream().close();
        } catch (Exception var7) {
            log.error("close stream error! e: {}", var7);
        }

        return rc;
    }

    private void processOutput(InputStream pInputStream, ProcessUtil.InputStreamConsumer pConsumer) throws IOException {
        pConsumer.consume(pInputStream);
    }

    private void processError(InputStream pInputStream, ProcessUtil.InputStreamConsumer pConsumer) throws IOException {
        pConsumer.consume(pInputStream);
    }

    private static class CustomThreadFactory implements ThreadFactory {
        private String name;
        private AtomicInteger count = new AtomicInteger(0);

        public CustomThreadFactory(String name) {
            this.name = name;
        }

        public Thread newThread(Runnable r) {
            return new Thread(r, this.name + "-" + this.count.addAndGet(1));
        }
    }

    private static class InputStreamConsumer {
        static ProcessUtil instance = new ProcessUtil();
        static ProcessUtil.InputStreamConsumer DEFAULT_CONSUMER = new ProcessUtil.InputStreamConsumer();

        private InputStreamConsumer() {
        }

        void consume(InputStream stream) throws IOException {
            StringBuilder builder = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream), 65536);

            String temp;
            while((temp = reader.readLine()) != null) {
                builder.append(temp);
            }

            if (ProcessUtil.log.isDebugEnabled()) {
                ProcessUtil.log.debug("cmd process input stream: {}", builder.toString());
            }

            reader.close();
        }
    }
}
