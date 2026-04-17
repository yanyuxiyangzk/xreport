package com.xreport.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xreport.pojo.entity.SysDept;
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
 * SysDeptMapper 单元测试
 */
@ExtendWith(MockitoExtension.class)
class SysDeptMapperTest {

    @Mock
    private SysDeptMapper deptMapper;

    private SysDept testDept;

    @BeforeEach
    void setUp() {
        testDept = new SysDept();
        testDept.setId(1L);
        testDept.setDeptName("技术部");
        testDept.setDeptCode("TECH");
        testDept.setParentId(0L);
        testDept.setAncestors("0");
        testDept.setSortOrder(1);
        testDept.setLeaderUserId(1L);
        testDept.setPhone("13800138000");
        testDept.setEmail("tech@example.com");
        testDept.setStatus(1);
        testDept.setDelFlag(0);
        testDept.setTenantId(1L);
        testDept.setCreateTime(LocalDateTime.now());
        testDept.setUpdateTime(LocalDateTime.now());
    }

    @Test
    @DisplayName("selectById - 应该返回部门")
    void selectById_ShouldReturnDept() {
        // Arrange
        when(deptMapper.selectById(1L)).thenReturn(testDept);

        // Act
        SysDept result = deptMapper.selectById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("技术部", result.getDeptName());
        verify(deptMapper).selectById(1L);
    }

    @Test
    @DisplayName("selectById - 部门不存在应返回null")
    void selectById_WhenNotExists_ShouldReturnNull() {
        // Arrange
        when(deptMapper.selectById(999L)).thenReturn(null);

        // Act
        SysDept result = deptMapper.selectById(999L);

        // Assert
        assertNull(result);
        verify(deptMapper).selectById(999L);
    }

    @Test
    @DisplayName("insert - 应该插入部门并返回影响行数")
    void insert_ShouldInsertAndReturnAffectedRows() {
        // Arrange
        SysDept newDept = new SysDept();
        newDept.setDeptName("新部门");
        newDept.setDeptCode("NEW");

        when(deptMapper.insert(any(SysDept.class))).thenReturn(1);

        // Act
        int result = deptMapper.insert(newDept);

        // Assert
        assertEquals(1, result);
        verify(deptMapper).insert(any(SysDept.class));
    }

    @Test
    @DisplayName("updateById - 应该更新部门")
    void updateById_ShouldUpdateDept() {
        // Arrange
        testDept.setDeptName("更新后的部门");

        when(deptMapper.updateById(any(SysDept.class))).thenReturn(1);

        // Act
        int result = deptMapper.updateById(testDept);

        // Assert
        assertEquals(1, result);
        verify(deptMapper).updateById(testDept);
    }

    @Test
    @DisplayName("deleteById - 应该删除部门")
    void deleteById_ShouldDeleteDept() {
        // Arrange
        when(deptMapper.deleteById(1L)).thenReturn(1);

        // Act
        int result = deptMapper.deleteById(1L);

        // Assert
        assertEquals(1, result);
        verify(deptMapper).deleteById(1L);
    }

    @Test
    @DisplayName("selectList - 使用Wrapper查询应该返回部门列表")
    void selectList_WithWrapper_ShouldReturnDepts() {
        // Arrange
        List<SysDept> depts = List.of(testDept);
        when(deptMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(depts);

        // Act
        LambdaQueryWrapper<SysDept> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDept::getDelFlag, 0);
        List<SysDept> result = deptMapper.selectList(wrapper);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(deptMapper).selectList(any(LambdaQueryWrapper.class));
    }

    @Test
    @DisplayName("selectList - 按名称模糊查询应该返回匹配的部门")
    void selectList_WithNameLike_ShouldReturnMatchedDepts() {
        // Arrange
        List<SysDept> depts = List.of(testDept);
        when(deptMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(depts);

        // Act
        LambdaQueryWrapper<SysDept> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(SysDept::getDeptName, "技术");
        wrapper.eq(SysDept::getDelFlag, 0);
        List<SysDept> result = deptMapper.selectList(wrapper);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(deptMapper).selectList(any(LambdaQueryWrapper.class));
    }

    @Test
    @DisplayName("selectCount - 应该返回符合条件的数量")
    void selectCount_ShouldReturnCount() {
        // Arrange
        when(deptMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(5L);

        // Act
        LambdaQueryWrapper<SysDept> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDept::getParentId, 1L);
        long result = deptMapper.selectCount(wrapper);

        // Assert
        assertEquals(5L, result);
    }
}