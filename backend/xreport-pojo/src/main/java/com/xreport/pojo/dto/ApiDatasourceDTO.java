package com.xreport.pojo.dto;

import java.util.Map;

/**
 * API数据源DTO - 用于调用API数据源
 */
public class ApiDatasourceDTO {

    private String apiUrl;

    private String method;

    private Map<String, String> headers;

    private Map<String, Object> params;

    private String body;

    private Long datasourceId;

    public ApiDatasourceDTO() {}

    public String getApiUrl() { return apiUrl; }
    public void setApiUrl(String apiUrl) { this.apiUrl = apiUrl; }

    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }

    public Map<String, String> getHeaders() { return headers; }
    public void setHeaders(Map<String, String> headers) { this.headers = headers; }

    public Map<String, Object> getParams() { return params; }
    public void setParams(Map<String, Object> params) { this.params = params; }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }

    public Long getDatasourceId() { return datasourceId; }
    public void setDatasourceId(Long datasourceId) { this.datasourceId = datasourceId; }
}