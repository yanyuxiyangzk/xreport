package com.xreport.controller.export;

import com.xreport.pojo.entity.WordTemplate;
import com.xreport.service.export.IWordTemplateService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Word模板控制器
 */
@RestController
@RequestMapping("/api/word-templates")
public class WordTemplateController {

    private final IWordTemplateService wordTemplateService;

    public WordTemplateController(IWordTemplateService wordTemplateService) {
        this.wordTemplateService = wordTemplateService;
    }

    /**
     * 上传Word模板
     */
    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadTemplate(
            @RequestParam("file") MultipartFile file,
            @RequestParam("tplName") String tplName,
            @RequestParam(value = "description", required = false) String description) {

        WordTemplate template = wordTemplateService.uploadTemplate(file, tplName, description);

        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "上传成功");
        result.put("data", template);
        return ResponseEntity.ok(result);
    }

    /**
     * 获取模板列表
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> listTemplates() {
        List<WordTemplate> templates = wordTemplateService.listTemplates();

        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "查询成功");
        result.put("data", templates);
        return ResponseEntity.ok(result);
    }

    /**
     * 获取模板详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getTemplate(@PathVariable Long id) {
        WordTemplate template = wordTemplateService.getTemplateById(id);

        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "查询成功");
        result.put("data", template);
        return ResponseEntity.ok(result);
    }

    /**
     * 删除模板
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteTemplate(@PathVariable Long id) {
        wordTemplateService.deleteTemplate(id);

        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "删除成功");
        return ResponseEntity.ok(result);
    }

    /**
     * 下载模板文件
     */
    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadTemplate(@PathVariable Long id) {
        WordTemplate template = wordTemplateService.getTemplateById(id);
        if (template == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            File file = new File(template.getTplPath());
            if (!file.exists()) {
                return ResponseEntity.notFound().build();
            }

            byte[] fileContent = Files.readAllBytes(file.toPath());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.wordprocessingml.document"));
            headers.setContentDispositionFormData("attachment", template.getTplName() + ".docx");
            headers.setContentLength(fileContent.length);

            return ResponseEntity.ok().headers(headers).body(fileContent);

        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 使用poi-tl渲染Word模板
     */
    @PostMapping("/{id}/render")
    public ResponseEntity<byte[]> renderTemplate(
            @PathVariable Long id,
            @RequestBody Map<String, Object> data) {

        byte[] result = wordTemplateService.renderTemplate(id, data);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.wordprocessingml.document"));
        headers.setContentDispositionFormData("attachment", "rendered_report.docx");
        headers.setContentLength(result.length);

        return ResponseEntity.ok().headers(headers).body(result);
    }
}