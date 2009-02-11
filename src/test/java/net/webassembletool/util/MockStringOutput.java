package net.webassembletool.util;

import net.webassembletool.output.StringOutput;

public final class MockStringOutput extends StringOutput {
    private final String content;

    public MockStringOutput(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return content;
    }
}
