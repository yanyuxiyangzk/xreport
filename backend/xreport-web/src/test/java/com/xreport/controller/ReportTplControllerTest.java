package com.xreport.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageInfo;
import com.xreport.controller.report.ReportTplController;
import com.xreport.pojo.dto.ReportTplDto;
import com.xreport.pojo.entity.ReportTpl;
import com.xreport.pojo.entity.ReportTplSheet;
import com.xreport.service.report.IReportRenderService;
import com.xreport.service.report.IReportTplService;
import com.xreport.service.report.IReportTplSheetService;
import com.xreport.integration.TestApplication;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.xreport.integration.TestSecurityHelper.mockJwt;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * ReportTplController Test
 * 测试报表模板管理接口：list, createTemplate, updateTemplate, deleteTemplate, getTemplateById, publishTemplate
 */
@SpringBootTest(classes = TestApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("报表模板控制器测试")
public class ReportTplControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IReportTplService tplService;

    @MockBean
    private IReportTplSheetService sheetService;

    @MockBean
    private IReportRenderService renderService;

    private ReportTpl createTestTemplate(Long id) {
        ReportTpl tpl = new ReportTpl();
        tpl.setId(id);
        tpl.setTplName("Test Template " + id);
        tpl.setTplType(1);
        tpl.setStatus(1);
        tpl.setCreateTime(LocalDateTime.now());
        tpl.setCreateUserId(1L);
        return tpl;
    }

    @Test
    @DisplayName("测试分页查询模板列表")
    void testPageList() throws Exception {
        ReportTpl tpl1 = createTestTemplate(1L);
        ReportTpl tpl2 = createTestTemplate(2L);
        List<ReportTpl> list = Arrays.asList(tpl1, tpl2);
        PageInfo<ReportTpl> pageInfo = new PageInfo<>(list);

        when(tplService.pageQuery(any(ReportTplDto.class))).thenReturn(pageInfo);

        mockMvc.perform(get("/api/report/tpls")
                        .with(mockJwt(1L, Arrays.asList("report:manage")))
                        .param("pageNum", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.list").isArray())
                .andExpect(jsonPath("$.data.list.length()").value(2));

        verify(tplService).pageQuery(any(ReportTplDto.class));
    }

    @Test
    @DisplayName("测试查询所有模板列表")
    void testList() throws Exception {
        ReportTpl tpl1 = createTestTemplate(1L);
        ReportTpl tpl2 = createTestTemplate(2L);
        List<ReportTpl> list = Arrays.asList(tpl1, tpl2);

        when(tplService.list(any(ReportTplDto.class))).thenReturn(list);

        mockMvc.perform(get("/api/report/tpls/all")
                        .with(mockJwt(1L, Arrays.asList("report:manage"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2));

        verify(tplService).list(any(ReportTplDto.class));
    }

    @Test
    @DisplayName("测试根据ID获取模板详情")
    void testGetById() throws Exception {
        ReportTpl tpl = createTestTemplate(1L);

        when(tplService.getById(1L)).thenReturn(tpl);

        mockMvc.perform(get("/api/report/tpls/1")
                        .with(mockJwt(1L, Arrays.asList("report:manage"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.tplName").value("Test Template 1"));

        verify(tplService).getById(1L);
    }

    @Test
    @DisplayName("测试创建模板")
    void testAddTemplate() throws Exception {
        ReportTpl tpl = createTestTemplate(null);
        tpl.setTplName("New Template");

        mockMvc.perform(post("/api/report/tpls")
                        .with(mockJwt(1L, Arrays.asList("report:manage")))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tpl)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(tplService).add(any(ReportTpl.class));
    }

    @Test
    @DisplayName("测试更新模板")
    void testUpdateTemplate() throws Exception {
        ReportTpl tpl = createTestTemplate(1L);
        tpl.setTplName("Updated Template");

        mockMvc.perform(put("/api/report/tpls/1")
                        .with(mockJwt(1L, Arrays.asList("report:manage")))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tpl)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(tplService).update(any(ReportTpl.class));
    }

    @Test
    @DisplayName("测试删除模板")
    void testDeleteTemplate() throws Exception {
        mockMvc.perform(delete("/api/report/tpls/1")
                        .with(mockJwt(1L, Arrays.asList("report:manage")))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(tplService).delete(1L);
    }

    @Test
    @DisplayName("测试更新模板状态")
    void testUpdateStatus() throws Exception {
        mockMvc.perform(put("/api/report/tpls/1/status")
                        .with(mockJwt(1L, Arrays.asList("report:manage")))
                        .with(csrf())
                        .param("status", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(tplService).updateStatus(1L, 2);
    }

    @Test
    @DisplayName("测试发布模板")
    void testPublishTemplate() throws Exception {
        mockMvc.perform(post("/api/report/tpls/1/publish")
                        .with(mockJwt(1L, Arrays.asList("report:manage")))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(tplService).publish(1L);
    }

    @Test
    @DisplayName("测试获取模板下的 sheets 列表")
    void testListSheets() throws Exception {
        ReportTplSheet sheet1 = new ReportTplSheet();
        sheet1.setId(1L);
        sheet1.setTplId(1L);
        sheet1.setSheetName("Sheet 1");

        ReportTplSheet sheet2 = new ReportTplSheet();
        sheet2.setId(2L);
        sheet2.setTplId(1L);
        sheet2.setSheetName("Sheet 2");

        List<ReportTplSheet> sheets = Arrays.asList(sheet1, sheet2);

        when(sheetService.getByTplId(1L)).thenReturn(sheets);

        mockMvc.perform(get("/api/report/tpls/1/sheets")
                        .with(mockJwt(1L, Arrays.asList("report:manage"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2));

        verify(sheetService).getByTplId(1L);
    }

    @Test
    @DisplayName("测试预览模板")
    void testPreview() throws Exception {
        when(renderService.previewReport(1L)).thenReturn(Map.of("preview", "data"));

        mockMvc.perform(get("/api/report/tpls/1/preview")
                        .with(mockJwt(1L, Arrays.asList("report:manage"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.preview").value("data"));

        verify(renderService).previewReport(1L);
    }
}
