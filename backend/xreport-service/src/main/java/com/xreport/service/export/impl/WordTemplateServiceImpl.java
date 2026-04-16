package com.xreport.service.export.impl;

import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.data.TextRenderData;
import com.xreport.common.exception.BusinessException;
import com.xreport.mapper.WordTemplateMapper;
import com.xreport.pojo.entity.WordTemplate;
import com.xreport.service.export.IWordTemplateService;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Word模板服务实现
 */
@Service
public class WordTemplateServiceImpl implements IWordTemplateService {

    private static final Logger log = LoggerFactory.getLogger(WordTemplateServiceImpl.class);

    private final WordTemplateMapper wordTemplateMapper;

    @Value("${upload.word-template-path:upload/word-templates}")
    private String templateStoragePath;

    public WordTemplateServiceImpl(WordTemplateMapper wordTemplateMapper) {
        this.wordTemplateMapper = wordTemplateMapper;
    }

    @Override
    public WordTemplate uploadTemplate(MultipartFile file, String tplName, String description) {
        try {
            // Validate file
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || !originalFilename.toLowerCase().endsWith(".docx")) {
                throw new BusinessException("只能上传.docx格式的Word模板文件");
            }

            // Create storage directory
            Path storageDir = Paths.get(templateStoragePath);
            if (!Files.exists(storageDir)) {
                Files.createDirectories(storageDir);
            }

            // Generate unique filename: tplName_timestamp.docx
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            String extension = FilenameUtils.getExtension(originalFilename);
            String uniqueFilename = tplName + "_" + timestamp + "." + extension;
            Path filePath = storageDir.resolve(uniqueFilename);

            // Save file
            file.transferTo(filePath.toFile());

            // Save to database
            WordTemplate wordTemplate = new WordTemplate();
            wordTemplate.setTplName(tplName);
            wordTemplate.setTplPath(filePath.toString());
            wordTemplate.setDescription(description);
            wordTemplate.setStatus(1);
            wordTemplate.setDelFlag(0);
            wordTemplate.setTenantId(1L);
            wordTemplate.setCreateUserId(1L);
            wordTemplate.setCreateTime(LocalDateTime.now());
            wordTemplate.setUpdateTime(LocalDateTime.now());

            wordTemplateMapper.insert(wordTemplate);

            log.info("Word模板上传成功: {}, 路径: {}", tplName, filePath);
            return wordTemplate;

        } catch (IOException e) {
            log.error("Word模板上传失败", e);
            throw new BusinessException("Word模板上传失败: " + e.getMessage());
        }
    }

    @Override
    public List<WordTemplate> listTemplates() {
        return wordTemplateMapper.selectList(null);
    }

    @Override
    public WordTemplate getTemplateById(Long id) {
        return wordTemplateMapper.selectById(id);
    }

    @Override
    public void deleteTemplate(Long id) {
        WordTemplate template = wordTemplateMapper.selectById(id);
        if (template == null) {
            throw new BusinessException("模板不存在");
        }

        // Delete file
        try {
            Path filePath = Paths.get(template.getTplPath());
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            log.warn("删除模板文件失败: {}", template.getTplPath(), e);
        }

        // Delete from database
        wordTemplateMapper.deleteById(id);
        log.info("Word模板删除成功: id={}", id);
    }

    @Override
    public byte[] renderTemplate(Long tplId, Map<String, Object> data) {
        WordTemplate template = wordTemplateMapper.selectById(tplId);
        if (template == null) {
            throw new BusinessException("模板不存在");
        }

        File templateFile = new File(template.getTplPath());
        if (!templateFile.exists()) {
            throw new BusinessException("模板文件不存在: " + template.getTplPath());
        }

        try (FileInputStream fis = new FileInputStream(templateFile);
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            // Prepare data for poi-tl: convert to nested map format
            Map<String, Object> renderData = prepareRenderData(data);

            // Render template
            XWPFTemplate xwpfTemplate = XWPFTemplate.compile(fis).render(renderData);
            xwpfTemplate.writeAndClose(out);

            log.info("Word模板渲染成功: tplId={}, dataKeys={}", tplId, data.keySet());
            return out.toByteArray();

        } catch (Exception e) {
            log.error("Word模板渲染失败: tplId={}", tplId, e);
            throw new BusinessException("Word模板渲染失败: " + e.getMessage());
        }
    }

    /**
     * 准备渲染数据
     * 将简单的键值对转换为poi-tl支持的嵌套格式
     * 例如: {name: "张三", age: 25} -> {name: TextRenderData("张三"), age: TextRenderData(25)}
     */
    private Map<String, Object> prepareRenderData(Map<String, Object> data) {
        if (data == null) {
            return new HashMap<>();
        }

        Map<String, Object> result = new HashMap<>();
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            Object value = entry.getValue();
            if (value == null) {
                result.put(entry.getKey(), new TextRenderData(""));
            } else if (value instanceof Map) {
                // 嵌套对象，保持原样
                @SuppressWarnings("unchecked")
                Map<String, Object> nestedMap = (Map<String, Object>) value;
                result.put(entry.getKey(), prepareRenderData(nestedMap));
            } else if (value instanceof List) {
                // 列表数据
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> list = (List<Map<String, Object>>) value;
                List<Map<String, Object>> processedList = new ArrayList<>();
                for (Map<String, Object> item : list) {
                    processedList.add(prepareRenderData(item));
                }
                result.put(entry.getKey(), processedList);
            } else {
                // 基本类型，包装为TextRenderData
                result.put(entry.getKey(), new TextRenderData(String.valueOf(value)));
            }
        }
        return result;
    }
}