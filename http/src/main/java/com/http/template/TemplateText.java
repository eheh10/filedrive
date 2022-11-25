package com.http.template;

public enum TemplateText {
    ERROR_TEMPLATE("{{","}}"),
    FILE_LIST_TEMPLATE("{{","}}");

    private final String start;
    private final String end;

    TemplateText(String start, String end) {
        this.start = start;
        this.end = end;
    }

    public String getStart() {
        return start;
    }

    public String getEnd() {
        return end;
    }
}
