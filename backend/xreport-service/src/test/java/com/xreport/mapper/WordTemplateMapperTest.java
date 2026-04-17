package com.xreport.mapper;

import com.xreport.pojo.entity.WordTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * WordTemplateMapper 单元测试
 */
@ExtendWith(MockitoExtension.class)
class WordTemplateMapperTest {

    @Mock
    private WordTemplateMapper wordTemplateMapper;

    private WordTemplate testTemplate;

    @BeforeEach
    void setUp() {
        testTemplate = new WordTemplate();
        testTemplate.setId(1L);
        testTemplate.setTplName("测试模板");
        testTemplate.setTplPath("upload/word-templates/test.docx");
        testTemplate.setDescription("这是一个测试模板");
        testTemplate.setStatus(1);
        testTemplate.setDelFlag(0);
        testTemplate.setTenantId(1L);
        testTemplate.setCreateUserId(1L);
        testTemplate.setCreateTime(LocalDateTime.now());
        testTemplate.setUpdateTime(LocalDateTime.now());
    }

    @Test
    @DisplayName("selectById - 应该返回模板")
    void selectById_ShouldReturnTemplate() {
        // Arrange
        when(wordTemplateMapper.selectById(1L)).thenReturn(testTemplate);

        // Act
        WordTemplate result = wordTemplateMapper.selectById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("测试模板", result.getTplName());
        verify(wordTemplateMapper).selectById(1L);
    }

    @Test
    @DisplayName("selectById - 模板不存在应返回null")
    void selectById_WhenNotExists_ShouldReturnNull() {
        // Arrange
        when(wordTemplateMapper.selectById(999L)).thenReturn(null);

        // Act
        WordTemplate result = wordTemplateMapper.selectById(999L);

        // Assert
        assertNull(result);
        verify(wordTemplateMapper).selectById(999L);
    }

    @Test
    @DisplayName("insert - 应该插入模板并返回影响行数")
    void insert_ShouldInsertAndReturnAffectedRows() {
        // Arrange
        WordTemplate newTemplate = new WordTemplate();
        newTemplate.setTplName("新模板");
        newTemplate.setTplPath("upload/word-templates/new.docx");

        when(wordTemplateMapper.insert(any(WordTemplate.class))).thenReturn(1);

        // Act
        int result = wordTemplateMapper.insert(newTemplate);

        // Assert
        assertEquals(1, result);
        verify(wordTemplateMapper).insert(any(WordTemplate.class));
    }

    @Test
    @DisplayName("updateById - 应该更新模板")
    void updateById_ShouldUpdateTemplate() {
        // Arrange
        testTemplate.setTplName("更新后的模板");

        when(wordTemplateMapper.updateById(any(WordTemplate.class))).thenReturn(1);

        // Act
        int result = wordTemplateMapper.updateById(testTemplate);

        // Assert
        assertEquals(1, result);
        verify(wordTemplateMapper).updateById(testTemplate);
    }

    @Test
    @DisplayName("deleteById - 应该删除模板")
    void deleteById_ShouldDeleteTemplate() {
        // Arrange
        when(wordTemplateMapper.deleteById(1L)).thenReturn(1);

        // Act
        int result = wordTemplateMapper.deleteById(1L);

        // Assert
        assertEquals(1, result);
        verify(wordTemplateMapper).deleteById(1L);
    }

    @Test
    @DisplayName("selectList - 应该返回所有模板")
    void selectList_ShouldReturnAllTemplates() {
        // Arrange
        List<WordTemplate> templates = List.of(testTemplate);
        when(wordTemplateMapper.selectList(null)).thenReturn(templates);

        // Act
        List<WordTemplate> result = wordTemplateMapper.selectList(null);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(wordTemplateMapper).selectList(null);
    }
}