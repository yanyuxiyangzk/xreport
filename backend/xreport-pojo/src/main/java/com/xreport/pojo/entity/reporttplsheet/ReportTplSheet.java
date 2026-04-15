package com.xreport.pojo.entity.reporttplsheet;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 报表模板Sheet实体
 */
@Data
@TableName("report_tpl_sheet")
public class ReportTplSheet {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("tpl_id")
    private Long tplId;

    @TableField("sheet_name")
    private String sheetName;

    @TableField("sheet_index")
    private Integer sheetIndex;

    @TableField("sheet_config")
    private String sheetConfig;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;

    @TableLogic
    @TableField("del_flag")
    private Integer delFlag;
}
