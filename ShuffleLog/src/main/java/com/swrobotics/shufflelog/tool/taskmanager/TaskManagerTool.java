package com.swrobotics.shufflelog.tool.taskmanager;

import com.swrobotics.messenger.client.MessageReader;
import com.swrobotics.messenger.client.MessengerClient;
import com.swrobotics.shufflelog.tool.Tool;
import com.swrobotics.shufflelog.util.Cooldown;
import com.swrobotics.shufflelog.util.FileChooser;
import imgui.flag.ImGuiInputTextFlags;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.type.ImString;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static imgui.ImGui.*;

public final class TaskManagerTool implements Tool {
    private static final String MSG_LIST_FILES     = ":ListFiles";
    private static final String MSG_READ_FILE      = ":ReadFile";
    private static final String MSG_WRITE_FILE     = ":WriteFile";
    private static final String MSG_DELETE_FILE    = ":DeleteFile";
    private static final String MSG_MKDIR          = ":Mkdir";
    private static final String MSG_FILES          = ":Files";
    private static final String MSG_FILE_CONTENT   = ":FileContent";
    private static final String MSG_WRITE_CONFIRM  = ":WriteConfirm";
    private static final String MSG_DELETE_CONFIRM = ":DeleteConfirm";
    private static final String MSG_MKDIR_CONFIRM  = ":MkdirConfirm";

    private final MessengerClient msg;
    private final String name;

    private final Cooldown reqContentCooldown;
    private final RemoteDirectory remoteRoot;

    private final ImString mkdirName;

    public TaskManagerTool(MessengerClient msg, String name) {
        this.msg = msg;
        this.name = name;

        reqContentCooldown = new Cooldown(500_000_000L);
        remoteRoot = new RemoteDirectory("");

        msg.addHandler(name + MSG_FILES, this::onFiles);
        msg.addHandler(name + MSG_DELETE_CONFIRM, this::onDeleteConfirm);
        msg.addHandler(name + MSG_MKDIR_CONFIRM, this::onMkdirConfirm);
        msg.addHandler(name + MSG_WRITE_CONFIRM, this::onWriteConfirm);

        mkdirName = new ImString(64);
    }

    private RemoteNode evalPath(String path) {
        if (path.equals(""))
            return remoteRoot;

        String[] parts = path.split("/");
        RemoteNode node = remoteRoot;
        for (String part : parts) {
            node = ((RemoteDirectory) node).getChild(part);
        }
        return node;
    }

    private byte[] readFile(File file) throws IOException {
        FileInputStream in = new FileInputStream(file);
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) > 0) {
            b.write(buffer, 0, read);
        }
        in.close();
        b.close();
        return b.toByteArray();
    }

    private void uploadFile(File file, String targetDirPath) {
        String path = targetDirPath.equals("") ? file.getName() : targetDirPath + "/" + file.getName();
        if (file.isFile()) {
            try {
                byte[] data = readFile(file);
                msg.prepare(name + MSG_WRITE_FILE)
                        .addString(path)
                        .addInt(data.length)
                        .addRaw(data)
                        .send();
            } catch (IOException e) {
                System.out.println("Failed to read file to upload: " + file);
                e.printStackTrace();
            }
        } else if (file.isDirectory()) {
            msg.prepare(name + MSG_MKDIR)
                    .addString(path)
                    .send();

            File[] files = file.listFiles();
            if (files != null) {
                for (File child : files) {
                    uploadFile(child, path);
                }
            }
        }
    }

    private void showDirectory(RemoteDirectory dir, boolean isRoot) {
        boolean open = treeNodeEx(isRoot ? "Tasks Root" : dir.getName(), isRoot ? ImGuiTreeNodeFlags.DefaultOpen : 0);

        pushID(dir.getName());
        boolean openNewDirPopup = false;
        if (beginPopupContextItem("context_menu")) {
            if (!isRoot && selectable("Delete")) {
                msg.prepare(name + MSG_DELETE_FILE)
                        .addString(dir.getFullPath())
                        .send();
                closeCurrentPopup();
            }
            if (selectable("New directory")) {
                closeCurrentPopup();
                openNewDirPopup = true;
            }
            if (selectable("Upload file(s)")) {
                closeCurrentPopup();
                FileChooser.chooseFileOrFolder((file) -> uploadFile(file, dir.getFullPath()));
            }
            endPopup();
        }

        if (openNewDirPopup) {
            mkdirName.set("");
            openPopup("New Directory");
        }

        if (beginPopupModal("New Directory")) {
            text("New directory name:");
            setNextItemWidth(300);
            if (inputText("##name", mkdirName, ImGuiInputTextFlags.EnterReturnsTrue)) {
                String path = isRoot ? mkdirName.get() : (dir.getFullPath() + "/" + mkdirName.get());
                msg.prepare(name + MSG_MKDIR)
                        .addString(path)
                        .send();
                closeCurrentPopup();
            }
            setItemDefaultFocus();
            endPopup();
        }

        popID();

        if (open) {
            if (dir.needsRefreshContent()) {
                indent(getTreeNodeToLabelSpacing());
                textDisabled("Fetching...");
                unindent(getTreeNodeToLabelSpacing());

                if (reqContentCooldown.request()) {
                    msg.prepare(name + MSG_LIST_FILES)
                            .addString(dir.getFullPath())
                            .send();
                }
            } else {
                for (RemoteNode node : dir.getChildren()) {
                    showNode(node);
                }
            }
            treePop();
        }
    }

    private void showFile(RemoteFile file) {
        treeNodeEx(file.getName(), ImGuiTreeNodeFlags.NoTreePushOnOpen | ImGuiTreeNodeFlags.Leaf);
        pushID(file.getName());
        if (beginPopupContextItem()) {
            if (selectable("Delete")) {
                msg.prepare(name + MSG_DELETE_FILE)
                        .addString(file.getFullPath())
                        .send();
                closeCurrentPopup();
            }
            endPopup();
        }
        popID();
    }

    private void showNode(RemoteNode node) {
        if (node instanceof RemoteDirectory) {
            showDirectory((RemoteDirectory) node, false);
        } else {
            showFile((RemoteFile) node);
        }
    }

    private void onFiles(String type, MessageReader reader) {
        String path = reader.readString();
        boolean success = reader.readBoolean();
        if (!success) {
            System.err.println("File query failed on " + path);
            return;
        }

        RemoteDirectory dir = (RemoteDirectory) evalPath(path);
        dir.setNeedsRefreshContent(false);
        dir.clearChildren();
        int count = reader.readInt();
        for (int i = 0; i < count; i++) {
            String name = reader.readString();
            boolean isDir = reader.readBoolean();

            RemoteNode node;
            if (isDir)
                node = new RemoteDirectory(name);
            else
                node = new RemoteFile(name);

            dir.addChild(node);
        }
    }

    private void onDeleteConfirm(String type, MessageReader reader) {
        String path = reader.readString();
        boolean success = reader.readBoolean();
        if (!success) {
            System.err.println("Delete failed on " + path);
            return;
        }

        RemoteNode node = evalPath(path);
        node.remove();
    }

    private void onMkdirConfirm(String type, MessageReader reader) {
        String path = reader.readString();
        boolean success = reader.readBoolean();
        if (!success) {
            System.err.println("Mkdir failed on " + path);
            return;
        }

        String[] entries = path.split("/");
        RemoteDirectory dir = remoteRoot;

        for (String entry : entries) {
            RemoteNode node = dir.getChild(entry);
            if (node == null) {
                RemoteDirectory newDir = new RemoteDirectory(entry);
                dir.addChild(newDir);
                dir = newDir;
            } else {
                dir = (RemoteDirectory) node;
            }
        }
    }

    private void onWriteConfirm(String type, MessageReader reader) {
        String path = reader.readString();
        boolean success = reader.readBoolean();
        if (!success) {
            System.err.println("Write failed on " + path);
            return;
        }

        String[] entries = path.split("/");

        // Ensure all directories are present locally
        RemoteDirectory dir = remoteRoot;
        for (int i = 0; i < entries.length - 1; i++) {
            String entry = entries[i];
            RemoteNode node = dir.getChild(entry);
            if (node == null) {
                RemoteDirectory newDir = new RemoteDirectory(entry);
                dir.addChild(newDir);
                dir = newDir;
            } else {
                dir = (RemoteDirectory) node;
            }
        }

        // Create the file locally
        String fileName = entries[entries.length - 1];
        if (dir.getChild(fileName) == null) {
            RemoteFile file = new RemoteFile(fileName);
            dir.addChild(file);
        }
    }

    @Override
    public void process() {
        if (begin("Task Manager [" + name + "]")) {
            showDirectory(remoteRoot, true);
        }
        end();
    }
}
