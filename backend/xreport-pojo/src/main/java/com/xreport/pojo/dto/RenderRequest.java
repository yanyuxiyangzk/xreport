package com.xreport.pojo.dto;

import java.util.Map;

/**
 * 报表渲染请求
 */
public class RenderRequest {
    private Long tplId;
    private String format;
    private Map<String, Object> parameters;

    public RenderRequest() {}

    public Long getTplId() {
        return tplId;
    }

    public void setTplId(Long tplId) {
        this.tplId = tplId;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }
}