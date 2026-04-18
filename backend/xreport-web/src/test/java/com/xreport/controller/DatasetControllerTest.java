package com.xreport.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageInfo;
import com.xreport.controller.dataset.DatasetController;
import com.xreport.pojo.dto.DatasetDTO;
import com.xreport.pojo.entity.reportdataset.ReportDataset;
import com.xreport.service.dataset.IDatasetService;
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
import java.util.HashMap;
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
 * DatasetController Test
 * 测试数据集管理接口：createDataset, updateDataset, deleteDataset, listDatasets, executeQuery
 */
@SpringBootTest(classes = TestApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("数据集控制器测试")
public class DatasetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IDatasetService datasetService;

    private ReportDataset createTestDataset(Long id) {
        ReportDataset dataset = new ReportDataset();
        dataset.setId(id);
        dataset.setDatasetName("Test Dataset " + id);
        dataset.setDatasetCode("DS_" + id);
        dataset.setDatasetType("SQL");
        dataset.setStatus(1);
        dataset.setCreateTime(LocalDateTime.now());
        dataset.setCreateUserId(1L);
        return dataset;
    }

    @Test
    @DisplayName("测试分页查询数据集列表")
    void testPageList() throws Exception {
        ReportDataset ds1 = createTestDataset(1L);
        ReportDataset ds2 = createTestDataset(2L);
        List<ReportDataset> list = Arrays.asList(ds1, ds2);
        PageInfo<ReportDataset> pageInfo = new PageInfo<>(list);

        when(datasetService.pageQuery(any(DatasetDTO.class))).thenReturn(pageInfo);

        mockMvc.perform(get("/api/datasets")
                        .with(mockJwt(1L, Arrays.asList("report:manage")))
                        .param("pageNum", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.list").isArray())
                .andExpect(jsonPath("$.data.list.length()").value(2));

        verify(datasetService).pageQuery(any(DatasetDTO.class));
    }

    @Test
    @DisplayName("测试获取数据集详情")
    void testGetById() throws Exception {
        ReportDataset dataset = createTestDataset(1L);

        when(datasetService.getById(1L)).thenReturn(dataset);

        mockMvc.perform(get("/api/datasets/1")
                        .with(mockJwt(1L, Arrays.asList("report:manage"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.datasetName").value("Test Dataset 1"));

        verify(datasetService).getById(1L);
    }

    @Test
    @DisplayName("测试创建数据集")
    void testCreateDataset() throws Exception {
        ReportDataset dataset = createTestDataset(null);
        dataset.setDatasetName("New Dataset");

        mockMvc.perform(post("/api/datasets")
                        .with(mockJwt(1L, Arrays.asList("report:manage")))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dataset)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(datasetService).add(any(ReportDataset.class));
    }

    @Test
    @DisplayName("测试更新数据集")
    void testUpdateDataset() throws Exception {
        ReportDataset dataset = createTestDataset(1L);
        dataset.setDatasetName("Updated Dataset");

        mockMvc.perform(put("/api/datasets/1")
                        .with(mockJwt(1L, Arrays.asList("report:manage")))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dataset)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(datasetService).update(any(ReportDataset.class));
    }

    @Test
    @DisplayName("测试删除数据集")
    void testDeleteDataset() throws Exception {
        mockMvc.perform(delete("/api/datasets/1")
                        .with(mockJwt(1L, Arrays.asList("report:manage")))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(datasetService).delete(1L);
    }

    @Test
    @DisplayName("测试获取启用的数据集列表")
    void testListEnabled() throws Exception {
        ReportDataset ds1 = createTestDataset(1L);
        ReportDataset ds2 = createTestDataset(2L);
        List<ReportDataset> list = Arrays.asList(ds1, ds2);

        when(datasetService.listEnabled()).thenReturn(list);

        mockMvc.perform(get("/api/datasets/enabled")
                        .with(mockJwt(1L, Arrays.asList("report:manage"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2));

        verify(datasetService).listEnabled();
    }

    @Test
    @DisplayName("测试执行数据集查询")
    void testExecuteQuery() throws Exception {
        Map<String, Object> queryResult = new HashMap<>();
        queryResult.put("id", 1);
        queryResult.put("name", "Test");
        List<Map<String, Object>> result = Arrays.asList(queryResult);

        when(datasetService.executeQuery(eq(1L), anyMap())).thenReturn(result);

        Map<String, Object> params = new HashMap<>();
        params.put("datasetId", 1L);
        params.put("params", new HashMap<>());

        mockMvc.perform(post("/api/datasets/query")
                        .with(mockJwt(1L, Arrays.asList("report:manage")))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray());

        verify(datasetService).executeQuery(eq(1L), anyMap());
    }

    @Test
    @DisplayName("测试预览数据集查询结果")
    void testPreviewQuery() throws Exception {
        Map<String, Object> queryResult = new HashMap<>();
        queryResult.put("id", 1);
        queryResult.put("name", "Preview Test");
        List<Map<String, Object>> result = Arrays.asList(queryResult);

        when(datasetService.previewQuery(eq(1L), anyMap())).thenReturn(result);

        Map<String, Object> params = new HashMap<>();
        params.put("datasetId", 1L);
        params.put("params", new HashMap<>());

        mockMvc.perform(post("/api/datasets/preview")
                        .with(mockJwt(1L, Arrays.asList("report:manage")))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray());

        verify(datasetService).previewQuery(eq(1L), anyMap());
    }
}
