package org.openjfx.model;

public interface ErrorReader {
    String getMessageByCode(String field, int errorCode, int... index);
}

