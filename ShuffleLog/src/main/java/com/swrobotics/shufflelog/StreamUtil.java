package com.swrobotics.shufflelog;

import java.io.*;

public final class StreamUtil {
    public static byte[] readResourceToByteArray(String res) throws IOException {
        InputStream in = StreamUtil.class.getClassLoader().getResourceAsStream(res);
        if (in == null) {
            throw new IOException("Resource not found: " + res);
        }

        ByteArrayOutputStream b = new ByteArrayOutputStream();

        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = in.read(buffer)) > 0) {
            b.write(buffer, 0, bytesRead);
        }

        return b.toByteArray();
    }

    public static String getStackTrace(Throwable t) {
        StringWriter writer = new StringWriter();
        PrintWriter out = new PrintWriter(writer);
        t.printStackTrace(out);
        return writer.toString();
    }

    private StreamUtil() {
        throw new AssertionError();
    }
}
