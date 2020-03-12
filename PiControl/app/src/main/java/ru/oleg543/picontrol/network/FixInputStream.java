package ru.oleg543.picontrol.network;

import java.io.IOException;
import java.io.InputStream;

public class FixInputStream {
    private InputStream mStream;

    public FixInputStream(InputStream pInputStream) {
        mStream = pInputStream;
    }

    public String readString() throws IOException {
        StringBuilder response = new StringBuilder();
        response.append((char) mStream.read());
        byte[] buffer = new byte[mStream.available()];
        mStream.read(buffer);
        response.append(new String(buffer));

        return response.toString();
    }
}
