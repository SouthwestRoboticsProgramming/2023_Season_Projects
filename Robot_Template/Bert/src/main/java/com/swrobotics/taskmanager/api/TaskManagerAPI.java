package com.swrobotics.taskmanager.api;

import com.swrobotics.messenger.client.MessengerClient;

import java.io.*;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static com.swrobotics.messenger.client.MessengerUtils.encodeStringUTF;

public final class TaskManagerAPI {
    private static final String IN_PONG = ":Pong";
    private static final String IN_TASK_IS_RUNNING = ":TaskRunning";
    private static final String IN_TASK_EXISTS = ":TaskExists";
    private static final String IN_TASKS = ":Tasks";
    private static final String IN_UPLOAD_STATUS = ":UploadStatus";
    private static final String IN_TASK_STDOUT = ":StdOut";
    private static final String IN_TASK_STDERR = ":StdErr";

    private static final String OUT_PING = ":Ping";
    private static final String OUT_TASK_START = ":StartTask";
    private static final String OUT_TASK_STOP = ":StopTask";
    private static final String OUT_TASK_IS_RUNNING = ":IsTaskRunning";
    private static final String OUT_TASK_DELETE = ":DeleteTask";
    private static final String OUT_TASK_UPLOAD = ":UploadTask";
    private static final String OUT_TASK_EXISTS = ":GetTaskExists";
    private static final String OUT_GET_TASKS = ":GetTasks";

    private final MessengerClient msg;
    private final Map<String, Set<CompletableFuture<Boolean>>> taskRunningFutures;
    private final Map<String, Set<CompletableFuture<Boolean>>> taskExistsFutures;
    private final Map<String, Set<CompletableFuture<UploadResult>>> taskUploadFutures;
    private final Set<CompletableFuture<Set<String>>> tasksFutures;

    private final String inPong;
    private final String inTaskIsRunning;
    private final String inTaskExists;
    private final String inTasks;
    private final String inUploadStatus;
    private final String inTaskStdOut;
    private final String inTaskStdErr;

    private final String outPing;
    private final String outTaskStart;
    private final String outTaskStop;
    private final String outTaskIsRunning;
    private final String outTaskDelete;
    private final String outTaskUpload;
    private final String outTaskExists;
    private final String outGetTasks;

    private boolean alive;
    private TaskOutputHandler stdOutHandler;
    private TaskOutputHandler stdErrHandler;

    public TaskManagerAPI(MessengerClient msg, String prefix) {
        this.msg = msg;

        inPong = prefix + IN_PONG;
        inTaskIsRunning = prefix + IN_TASK_IS_RUNNING;
        inTaskExists = prefix + IN_TASK_EXISTS;
        inTasks = prefix + IN_TASKS;
        inUploadStatus = prefix + IN_UPLOAD_STATUS;
        inTaskStdOut = prefix + IN_TASK_STDOUT;
        inTaskStdErr = prefix + IN_TASK_STDERR;

        outPing = prefix + OUT_PING;
        outTaskStart = prefix + OUT_TASK_START;
        outTaskStop = prefix + OUT_TASK_STOP;
        outTaskIsRunning = prefix + OUT_TASK_IS_RUNNING;
        outTaskDelete = prefix + OUT_TASK_DELETE;
        outTaskUpload = prefix + OUT_TASK_UPLOAD;
        outTaskExists = prefix + OUT_TASK_EXISTS;
        outGetTasks = prefix + OUT_GET_TASKS;

        taskRunningFutures = new HashMap<>();
        taskExistsFutures = new HashMap<>();
        taskUploadFutures = new HashMap<>();
        tasksFutures = new HashSet<>();

        stdOutHandler = (task, line) -> System.out.println("[" + task + "/OUT] " + line);
        stdErrHandler = (task, line) -> System.err.println("[" + task + "/ERR] " + line);

        msg.makeHandler()
                .listen(inPong)
                .listen(inTaskIsRunning)
                .listen(inTaskExists)
                .listen(inTasks)
                .listen(inUploadStatus)
                .listen(inTaskStdOut)
                .listen(inTaskStdErr)
                .setHandler(this::messageHandler);

        alive = false;
    }

    public void ping() {
        msg.sendMessage(outPing, new byte[0]);
    }

    public boolean isAlive() {
        return alive;
    }

    public void startTask(String task) {
        msg.sendMessage(outTaskStart, encodeStringUTF(task));
    }

    public void stopTask(String task) {
        msg.sendMessage(outTaskStop, encodeStringUTF(task));
    }

    public CompletableFuture<Boolean> isTaskRunning(String task) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        taskRunningFutures.computeIfAbsent(task, (t) -> new HashSet<>()).add(future);

        msg.sendMessage(outTaskIsRunning, encodeStringUTF(task));

        return future;
    }

    public void deleteTask(String task) {
        msg.sendMessage(outTaskDelete, encodeStringUTF(task));
    }

    public CompletableFuture<UploadResult> uploadTask(String task, File source) {
        CompletableFuture<UploadResult> future = new CompletableFuture<>();
        taskUploadFutures.computeIfAbsent(task, (t) -> new HashSet<>()).add(future);

        byte[] payload;

        try {
            Path path = source.toPath();
            if (Files.isDirectory(path)) {
                Path pathAbsolute = path.toAbsolutePath();

                ByteArrayOutputStream b = new ByteArrayOutputStream();
                ZipOutputStream zip = new ZipOutputStream(b);

                Files.walkFileTree(path, new SimpleFileVisitor<>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) throws IOException {
                        if (Files.isDirectory(file)) {
                            return FileVisitResult.CONTINUE;
                        }

                        Path absolute = file.toAbsolutePath();
                        Path relative = pathAbsolute.relativize(absolute);
                        System.out.println("Packing " + relative.toString());

                        ZipEntry entry = new ZipEntry(relative.toString());
                        zip.putNextEntry(entry);

                        byte[] fileData = Files.readAllBytes(file);
                        zip.write(fileData);
                        zip.closeEntry();

                        return FileVisitResult.CONTINUE;
                    }
                });

                zip.close();

                payload = b.toByteArray();
            } else {
                payload = Files.readAllBytes(path);
            }

            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);

            out.writeUTF(task);
            out.writeInt(payload.length);
            out.write(payload);

            System.out.println("Sending");
            msg.sendMessage(outTaskUpload, b.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
            future.complete(UploadResult.ENCODE_FAILED);
        }

        return future;
    }

    public CompletableFuture<Boolean> taskExists(String task) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        taskExistsFutures.computeIfAbsent(task, (t) -> new HashSet<>()).add(future);

        msg.sendMessage(outTaskExists, encodeStringUTF(task));

        return future;
    }

    public CompletableFuture<Set<String>> getTasks() {
        CompletableFuture<Set<String>> future = new CompletableFuture<>();
        tasksFutures.add(future);

        msg.sendMessage(outGetTasks, new byte[0]);

        return future;
    }

    public void setTaskStdOutHandler(TaskOutputHandler handler) {
        stdOutHandler = handler;
    }

    public void setTaskStdErrHandler(TaskOutputHandler handler) {
        stdErrHandler = handler;
    }

    private void handlePong() {
        alive = true;
    }

    private void handleTaskRunning(DataInputStream in) throws IOException {
        String task = in.readUTF();
        Set<CompletableFuture<Boolean>> futures = taskRunningFutures.get(task);
        if (futures != null) {
            for (CompletableFuture<Boolean> future : futures) {
                future.complete(in.readBoolean());
            }
        }
        taskRunningFutures.remove(task);
    }

    private void handleTaskExists(DataInputStream in) throws IOException {
        String task = in.readUTF();

        Set<CompletableFuture<Boolean>> futures = taskExistsFutures.get(task);
        if (futures != null) {
            for (CompletableFuture<Boolean> future : futures) {
                future.complete(in.readBoolean());
            }
        }

        taskExistsFutures.remove(task);
    }

    private void handleTasks(DataInputStream in) throws IOException {
        int count = in.readInt();
        Set<String> tasks = new HashSet<>();
        for (int i = 0; i < count; i++) {
            tasks.add(in.readUTF());
        }

        for (CompletableFuture<Set<String>> future : tasksFutures) {
            future.complete(Collections.unmodifiableSet(tasks));
        }
    }

    private void handleUploadStatus(DataInputStream in) throws IOException {
        String task = in.readUTF();
        boolean success = in.readBoolean();

        Set<CompletableFuture<UploadResult>> futures = taskUploadFutures.get(task);
        if (futures != null) {
            for (CompletableFuture<UploadResult> future : futures) {
                future.complete(success ? UploadResult.SUCCESS : UploadResult.DECODE_FAILED);
            }
        }

        taskUploadFutures.remove(task);
    }

    private void handleTaskStdOut(DataInputStream in) throws IOException {
        String task = in.readUTF();
        String line = in.readUTF();

        stdOutHandler.handle(task, line);
    }

    private void handleTaskStdErr(DataInputStream in) throws IOException {
        String task = in.readUTF();
        String line = in.readUTF();

        stdErrHandler.handle(task, line);
    }

    private void messageHandler(String type, DataInputStream in) throws IOException {
        if (type.equals(inPong)) {
            handlePong();
        } else if (type.equals(inTaskIsRunning)) {
            handleTaskRunning(in);
        } else if (type.equals(inTaskExists)) {
            handleTaskExists(in);
        } else if (type.equals(inTasks)) {
            handleTasks(in);
        } else if (type.equals(inUploadStatus)) {
            handleUploadStatus(in);
        } else if (type.equals(inTaskStdOut)) {
            handleTaskStdOut(in);
        } else if (type.equals(inTaskStdErr)) {
            handleTaskStdErr(in);
        } else {
            System.err.println("Unknown message: " + type);
        }
    }
}
