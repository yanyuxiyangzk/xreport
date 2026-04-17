package com.xreport.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xreport.pojo.dto.RenderRequest;
import com.xreport.pojo.entity.ReportTpl;
import com.xreport.pojo.entity.reporttplsheet.ReportTplSheet;
import com.xreport.service.auth.IAuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Report Integration Test
 * 测试报表渲染流程：模板创建、报表渲染、报表预览
 * 使用 Mock 数据模拟 DatasetQueryEngine
 */
@SpringBootTest(classes = TestApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("报表渲染集成测试")
public class ReportIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private IAuthService authService;

    private TestSecurityHelper securityHelper;

    @BeforeEach
    void setUp() {
        securityHelper = new TestSecurityHelper(mockMvc, objectMapper, authService, null);
    }

    @Test
    @Order(1)
    @DisplayName("测试创建报表模板")
    void testCreateReportTemplate() throws Exception {
        ReportTpl tpl = createTestTemplate("Test Report Template");

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/api/report/tpls")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tpl))
                .with(securityHelper.mockJwt(1L, List.of("ROLE_ADMIN", "report:manage")));

        MvcResult result = mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andReturn();

        System.out.println("========================================");
        System.out.println("测试报告: 创建报表模板");
        System.out.println("----------------------------------------");
        System.out.println("请求: POST /api/report/tpls");
        System.out.println("模板名称: " + tpl.getTplName());
        System.out.println("模板类型: " + tpl.getTplType());
        System.out.println("响应状态: " + result.getResponse().getStatus());
        System.out.println("测试结果: 通过");
        System.out.println("========================================");
    }

    @Test
    @Order(2)
    @DisplayName("测试查询报表模板列表")
    void testQueryReportTemplates() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/report/tpls")
                .param("pageNum", "1")
                .param("pageSize", "10")
                .with(securityHelper.mockJwt(1L, List.of("ROLE_ADMIN", "report:manage")));

        MvcResult result = mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andReturn();

        System.out.println("========================================");
        System.out.println("测试报告: 查询报表模板列表");
        System.out.println("----------------------------------------");
        System.out.println("请求: GET /api/report/tpls");
        System.out.println("分页: pageNum=1, pageSize=10");
        System.out.println("响应状态: " + result.getResponse().getStatus());
        System.out.println("测试结果: 通过");
        System.out.println("========================================");
    }

    @Test
    @Order(3)
    @DisplayName("测试获取单个报表模板")
    void testGetReportTemplateById() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/report/tpls/1")
                .with(securityHelper.mockJwt(1L, List.of("ROLE_ADMIN", "report:manage")));

        MvcResult result = mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andReturn();

        System.out.println("========================================");
        System.out.println("测试报告: 获取单个报表模板");
        System.out.println("----------------------------------------");
        System.out.println("请求: GET /api/report/tpls/1");
        System.out.println("响应状态: " + result.getResponse().getStatus());
        System.out.println("测试结果: 通过");
        System.out.println("========================================");
    }

    @Test
    @Order(4)
    @DisplayName("测试更新报表模板")
    void testUpdateReportTemplate() throws Exception {
        ReportTpl tpl = new ReportTpl();
        tpl.setTplName("Updated Test Report");
        tpl.setTplType(1);
        tpl.setIsExample(0);
        tpl.setSearchFormType(1);
        tpl.setStatus(1);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/api/report/tpls/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tpl))
                .with(securityHelper.mockJwt(1L, List.of("ROLE_ADMIN", "report:manage")));

        MvcResult result = mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andReturn();

        System.out.println("========================================");
        System.out.println("测试报告: 更新报表模板");
        System.out.println("----------------------------------------");
        System.out.println("请求: PUT /api/report/tpls/1");
        System.out.println("更新后名称: " + tpl.getTplName());
        System.out.println("响应状态: " + result.getResponse().getStatus());
        System.out.println("测试结果: 通过");
        System.out.println("========================================");
    }

    @Test
    @Order(5)
    @DisplayName("测试删除报表模板")
    void testDeleteReportTemplate() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/api/report/tpls/1")
                .with(securityHelper.mockJwt(1L, List.of("ROLE_ADMIN", "report:manage")));

        MvcResult result = mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andReturn();

        System.out.println("========================================");
        System.out.println("测试报告: 删除报表模板");
        System.out.println("----------------------------------------");
        System.out.println("请求: DELETE /api/report/tpls/1");
        System.out.println("响应状态: " + result.getResponse().getStatus());
        System.out.println("测试结果: 通过");
        System.out.println("========================================");
    }

    @Test
    @Order(6)
    @DisplayName("测试获取模板Sheet列表")
    void testGetTemplateSheets() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/report/tpls/1/sheets")
                .with(securityHelper.mockJwt(1L, List.of("ROLE_ADMIN", "report:manage")));

        MvcResult result = mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andReturn();

        System.out.println("========================================");
        System.out.println("测试报告: 获取模板Sheet列表");
        System.out.println("----------------------------------------");
        System.out.println("请求: GET /api/report/tpls/1/sheets");
        System.out.println("响应状态: " + result.getResponse().getStatus());
        System.out.println("测试结果: 通过");
        System.out.println("========================================");
    }

    @Test
    @Order(7)
    @DisplayName("测试为模板添加Sheet")
    void testAddSheetToTemplate() throws Exception {
        ReportTplSheet sheet = new ReportTplSheet();
        sheet.setTplId(1L);
        sheet.setSheetName("New Sheet");
        sheet.setSheetIndex(1);
        sheet.setSheetConfig("{}");

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/api/report/tpls/1/sheets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sheet))
                .with(securityHelper.mockJwt(1L, List.of("ROLE_ADMIN", "report:manage")));

        MvcResult result = mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andReturn();

        System.out.println("========================================");
        System.out.println("测试报告: 为模板添加Sheet");
        System.out.println("----------------------------------------");
        System.out.println("请求: POST /api/report/tpls/1/sheets");
        System.out.println("Sheet名称: " + sheet.getSheetName());
        System.out.println("Sheet索引: " + sheet.getSheetIndex());
        System.out.println("响应状态: " + result.getResponse().getStatus());
        System.out.println("测试结果: 通过");
        System.out.println("========================================");
    }

    @Test
    @Order(8)
    @DisplayName("测试更新模板Sheet")
    void testUpdateTemplateSheet() throws Exception {
        ReportTplSheet sheet = new ReportTplSheet();
        sheet.setSheetName("Updated Sheet");
        sheet.setSheetIndex(0);
        sheet.setSheetConfig("{\"updated\": true}");

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/api/report/tpls/1/sheets/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sheet))
                .with(securityHelper.mockJwt(1L, List.of("ROLE_ADMIN", "report:manage")));

        MvcResult result = mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andReturn();

        System.out.println("========================================");
        System.out.println("测试报告: 更新模板Sheet");
        System.out.println("----------------------------------------");
        System.out.println("请求: PUT /api/report/tpls/1/sheets/1");
        System.out.println("更新后Sheet名称: " + sheet.getSheetName());
        System.out.println("响应状态: " + result.getResponse().getStatus());
        System.out.println("测试结果: 通过");
        System.out.println("========================================");
    }

    @Test
    @Order(9)
    @DisplayName("测试删除模板Sheet")
    void testDeleteTemplateSheet() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/api/report/tpls/1/sheets/1")
                .with(securityHelper.mockJwt(1L, List.of("ROLE_ADMIN", "report:manage")));

        MvcResult result = mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andReturn();

        System.out.println("========================================");
        System.out.println("测试报告: 删除模板Sheet");
        System.out.println("----------------------------------------");
        System.out.println("请求: DELETE /api/report/tpls/1/sheets/1");
        System.out.println("响应状态: " + result.getResponse().getStatus());
        System.out.println("测试结果: 通过");
        System.out.println("========================================");
    }

    @Test
    @Order(10)
    @DisplayName("测试发布模板")
    void testPublishTemplate() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/api/report/tpls/1/publish")
                .with(securityHelper.mockJwt(1L, List.of("ROLE_ADMIN", "report:manage")));

        MvcResult result = mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andReturn();

        System.out.println("========================================");
        System.out.println("测试报告: 发布模板");
        System.out.println("----------------------------------------");
        System.out.println("请求: POST /api/report/tpls/1/publish");
        System.out.println("响应状态: " + result.getResponse().getStatus());
        System.out.println("测试结果: 通过");
        System.out.println("========================================");
    }

    @Test
    @Order(11)
    @DisplayName("测试报表预览")
    void testReportPreview() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/report/tpls/1/preview")
                .with(securityHelper.mockJwt(1L, List.of("ROLE_ADMIN", "report:manage")));

        MvcResult result = mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andReturn();

        System.out.println("========================================");
        System.out.println("测试报告: 报表预览");
        System.out.println("----------------------------------------");
        System.out.println("请求: GET /api/report/tpls/1/preview");
        System.out.println("响应状态: " + result.getResponse().getStatus());
        String content = result.getResponse().getContentAsString();
        @SuppressWarnings("unchecked")
        Map<String, Object> response = objectMapper.readValue(content, Map.class);
        System.out.println("响应码: " + response.get("code"));
        System.out.println("测试结果: 通过");
        System.out.println("========================================");
    }

    @Test
    @Order(12)
    @DisplayName("测试报表渲染")
    void testReportRender() throws Exception {
        RenderRequest request = new RenderRequest();
        request.setTplId(1L);
        request.setFormat("excel");

        Map<String, Object> params = new HashMap<>();
        params.put("param1", "value1");
        params.put("param2", "value2");
        request.setParameters(params);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/api/report/render")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(securityHelper.mockJwt(1L, List.of("ROLE_ADMIN", "report:manage")));

        MvcResult result = mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andReturn();

        System.out.println("========================================");
        System.out.println("测试报告: 报表渲染");
        System.out.println("----------------------------------------");
        System.out.println("请求: POST /api/report/render");
        System.out.println("模板ID: " + request.getTplId());
        System.out.println("导出格式: " + request.getFormat());
        System.out.println("参数: " + params);
        System.out.println("响应状态: " + result.getResponse().getStatus());
        System.out.println("测试结果: 通过");
        System.out.println("========================================");
    }

    @Test
    @Order(13)
    @DisplayName("测试报表导出 - Excel格式")
    void testReportExportExcel() throws Exception {
        RenderRequest request = new RenderRequest();
        request.setTplId(1L);
        request.setFormat("excel");
        request.setParameters(new HashMap<>());

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/api/report/export")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(securityHelper.mockJwt(1L, List.of("ROLE_ADMIN", "report:manage")));

        MvcResult result = mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andReturn();

        System.out.println("========================================");
        System.out.println("测试报告: 报表导出 - Excel格式");
        System.out.println("----------------------------------------");
        System.out.println("请求: POST /api/report/export");
        System.out.println("模板ID: " + request.getTplId());
        System.out.println("导出格式: excel");
        System.out.println("响应状态: " + result.getResponse().getStatus());
        System.out.println("测试结果: 通过");
        System.out.println("========================================");
    }

    @Test
    @Order(14)
    @DisplayName("测试报表导出 - PDF格式")
    void testReportExportPdf() throws Exception {
        RenderRequest request = new RenderRequest();
        request.setTplId(1L);
        request.setFormat("pdf");
        request.setParameters(new HashMap<>());

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/api/report/export")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(securityHelper.mockJwt(1L, List.of("ROLE_ADMIN", "report:manage")));

        MvcResult result = mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andReturn();

        System.out.println("========================================");
        System.out.println("测试报告: 报表导出 - PDF格式");
        System.out.println("----------------------------------------");
        System.out.println("请求: POST /api/report/export");
        System.out.println("模板ID: " + request.getTplId());
        System.out.println("导出格式: pdf");
        System.out.println("响应状态: " + result.getResponse().getStatus());
        System.out.println("测试结果: 通过");
        System.out.println("========================================");
    }

    @Test
    @Order(15)
    @DisplayName("测试无权限访问报表接口")
    void testUnauthorizedReportAccess() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/report/tpls")
                .with(securityHelper.mockJwt(2L, List.of("ROLE_USER")));

        MvcResult result = mockMvc.perform(requestBuilder)
                .andExpect(status().isForbidden())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        @SuppressWarnings("unchecked")
        Map<String, Object> response = objectMapper.readValue(content, Map.class);

        System.out.println("========================================");
        System.out.println("测试报告: 无权限访问报表接口");
        System.out.println("----------------------------------------");
        System.out.println("请求: GET /api/report/tpls");
        System.out.println("用户角色: ROLE_USER (无report:manage权限)");
        System.out.println("响应状态: " + result.getResponse().getStatus());
        System.out.println("响应码: " + response.get("code"));
        System.out.println("测试结果: 通过（正确拒绝无权限访问）");
        System.out.println("========================================");
    }

    private ReportTpl createTestTemplate(String name) {
        ReportTpl tpl = new ReportTpl();
        tpl.setTplName(name);
        tpl.setTplType(1);
        tpl.setIsExample(0);
        tpl.setSearchFormType(1);
        tpl.setStatus(1);
        tpl.setTenantId(1L);
        tpl.setCreateUserId(1L);
        return tpl;
    }
}