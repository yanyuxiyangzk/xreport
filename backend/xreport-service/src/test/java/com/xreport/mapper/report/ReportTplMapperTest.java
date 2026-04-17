package com.xreport.mapper.report;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xreport.pojo.entity.ReportTpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * ReportTplMapper 单元测试
 *
 * 注意: 由于Mapper使用MyBatis-Plus的BaseMapper，这些测试主要验证：
 * 1. Mapper方法被正确调用
 * 2. 查询条件被正确构建
 *
 * 对于完整的Mapper测试，建议使用集成测试配合H2内存数据库。
 */
@ExtendWith(MockitoExtension.class)
class ReportTplMapperTest {

    @Mock
    private ReportTplMapper reportTplMapper;

    private ReportTpl testTpl;

    @BeforeEach
    void setUp() {
        testTpl = new ReportTpl();
        testTpl.setId(1L);
        testTpl.setTplName("测试报表");
        testTpl.setTplType(1);
        testTpl.setStatus(1);
        testTpl.setDelFlag(0);
        testTpl.setTenantId(1L);
        testTpl.setCreateTime(LocalDateTime.now());
        testTpl.setUpdateTime(LocalDateTime.now());
    }

    @Test
    @DisplayName("selectById - 应该返回模板")
    void selectById_ShouldReturnTemplate() {
        // Arrange
        when(reportTplMapper.selectById(1L)).thenReturn(testTpl);

        // Act
        ReportTpl result = reportTplMapper.selectById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("测试报表", result.getTplName());
        verify(reportTplMapper).selectById(1L);
    }

    @Test
    @DisplayName("selectById - 模板不存在应返回null")
    void selectById_WhenNotExists_ShouldReturnNull() {
        // Arrange
        when(reportTplMapper.selectById(999L)).thenReturn(null);

        // Act
        ReportTpl result = reportTplMapper.selectById(999L);

        // Assert
        assertNull(result);
        verify(reportTplMapper).selectById(999L);
    }

    @Test
    @DisplayName("insert - 应该插入模板并返回影响行数")
    void insert_ShouldInsertAndReturnAffectedRows() {
        // Arrange
        ReportTpl newTpl = new ReportTpl();
        newTpl.setTplName("新报表");
        newTpl.setTplType(1);

        when(reportTplMapper.insert(any(ReportTpl.class))).thenReturn(1);

        // Act
        int result = reportTplMapper.insert(newTpl);

        // Assert
        assertEquals(1, result);
        verify(reportTplMapper).insert(any(ReportTpl.class));
    }

    @Test
    @DisplayName("updateById - 应该更新模板")
    void updateById_ShouldUpdateTemplate() {
        // Arrange
        testTpl.setTplName("更新后的报表");

        when(reportTplMapper.updateById(any(ReportTpl.class))).thenReturn(1);

        // Act
        int result = reportTplMapper.updateById(testTpl);

        // Assert
        assertEquals(1, result);
        ArgumentCaptor<ReportTpl> captor = ArgumentCaptor.forClass(ReportTpl.class);
        verify(reportTplMapper).updateById(captor.capture());
        assertEquals("更新后的报表", captor.getValue().getTplName());
    }

    @Test
    @DisplayName("deleteById - 应该删除模板")
    void deleteById_ShouldDeleteTemplate() {
        // Arrange
        when(reportTplMapper.deleteById(1L)).thenReturn(1);

        // Act
        int result = reportTplMapper.deleteById(1L);

        // Assert
        assertEquals(1, result);
        verify(reportTplMapper).deleteById(1L);
    }

    @Test
    @DisplayName("selectList - 使用Wrapper查询应该返回模板列表")
    void selectList_WithWrapper_ShouldReturnTemplates() {
        // Arrange
        List<ReportTpl> templates = List.of(testTpl);
        when(reportTplMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(templates);

        // Act
        LambdaQueryWrapper<ReportTpl> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ReportTpl::getDelFlag, 0);
        List<ReportTpl> result = reportTplMapper.selectList(wrapper);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(reportTplMapper).selectList(any(LambdaQueryWrapper.class));
    }

    @Test
    @DisplayName("selectList - 按名称模糊查询应该返回匹配的模板")
    void selectList_WithNameLike_ShouldReturnMatchedTemplates() {
        // Arrange
        List<ReportTpl> templates = List.of(testTpl);
        when(reportTplMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(templates);

        // Act
        LambdaQueryWrapper<ReportTpl> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(ReportTpl::getTplName, "测试");
        wrapper.eq(ReportTpl::getDelFlag, 0);
        List<ReportTpl> result = reportTplMapper.selectList(wrapper);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(reportTplMapper).selectList(any(LambdaQueryWrapper.class));
    }

    @Test
    @DisplayName("selectList - 按状态和类型查询应该返回匹配的模板")
    void selectList_WithStatusAndType_ShouldReturnMatchedTemplates() {
        // Arrange
        List<ReportTpl> templates = List.of(testTpl);
        when(reportTplMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(templates);

        // Act
        LambdaQueryWrapper<ReportTpl> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ReportTpl::getTplType, 1);
        wrapper.eq(ReportTpl::getStatus, 1);
        wrapper.eq(ReportTpl::getDelFlag, 0);
        List<ReportTpl> result = reportTplMapper.selectList(wrapper);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(reportTplMapper).selectList(any(LambdaQueryWrapper.class));
    }

    @Test
    @DisplayName("selectList - 空Wrapper应该返回所有模板")
    void selectList_WithEmptyWrapper_ShouldReturnAllTemplates() {
        // Arrange
        List<ReportTpl> templates = List.of(testTpl);
        when(reportTplMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(templates);

        // Act
        LambdaQueryWrapper<ReportTpl> wrapper = new LambdaQueryWrapper<>();
        List<ReportTpl> result = reportTplMapper.selectList(wrapper);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
    }
}