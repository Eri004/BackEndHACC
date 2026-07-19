package com.haccphoenix.api.request;

import java.util.List;

public class ReportRequest {
    public String type;
    public String period;
    public String format;
    public Integer edificioId;
    public List<Integer> edificioIds;
}