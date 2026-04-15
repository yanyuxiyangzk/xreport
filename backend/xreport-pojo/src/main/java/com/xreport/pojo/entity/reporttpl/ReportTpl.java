package com.xreport.pojo.entity.reporttpl;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 报表模板实体
 */
@Data
@TableName("report_tpl")
public class ReportTpl {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("tpl_name")
    private String tplName;

    @TableField("tpl_code")
    private String tplCode;

    @TableField("tpl_type")
    private String tplType;

    @TableField("sheet_count")
    private Integer sheetCount;

    @TableField("preview_url")
    private String previewUrl;

    @TableField("merchant_id")
    private Long merchantId;

    private Integer status;

    @TableField("create_by")
    private Long createBy;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_by")
    private Long updateBy;

    @TableField("update_time")
    private LocalDateTime updateTime;

    @TableLogic
    @TableField("del_flag")
    private Integer delFlag;
}
