package com.xreport.service.export;

import com.xreport.pojo.entity.WordTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Word模板服务接口
 */
public interface IWordTemplateService {

    /**
     * 上传Word模板
     * @param file 模板文件
     * @param tplName 模板名称
     * @param description 模板描述
     * @return 上传后的模板信息
     */
    WordTemplate uploadTemplate(MultipartFile file, String tplName, String description);

    /**
     * 获取模板列表
     * @return 模板列表
     */
    List<WordTemplate> listTemplates();

    /**
     * 根据ID获取模板
     * @param id 模板ID
     * @return 模板信息
     */
    WordTemplate getTemplateById(Long id);

    /**
     * 删除模板
     * @param id 模板ID
     */
    void deleteTemplate(Long id);

    /**
     * 使用poi-tl渲染Word模板
     * @param tplId 模板ID
     * @param data 渲染数据
     * @return 渲染后的Word文件字节数组
     */
    byte[] renderTemplate(Long tplId, java.util.Map<String, Object> data);
}