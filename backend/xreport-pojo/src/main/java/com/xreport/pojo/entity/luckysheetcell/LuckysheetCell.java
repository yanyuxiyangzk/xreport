package com.xreport.pojo.entity.luckysheetcell;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Luckysheet单元格实体
 */
@Data
@TableName("luckysheet_cell")
public class LuckysheetCell {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("sheet_id")
    private Long sheetId;

    @TableField("row_index")
    private Integer rowIndex;

    @TableField("column_index")
    private Integer columnIndex;

    @TableField("cell_value")
    private String cellValue;

    @TableField("cell_config")
    private String cellConfig;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;

    @TableLogic
    @TableField("del_flag")
    private Integer delFlag;
}
