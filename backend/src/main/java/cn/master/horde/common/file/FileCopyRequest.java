package cn.master.horde.common.file;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author : 11's papa
 * @since : 2026/2/9, 星期一
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class FileCopyRequest extends FileRequest {
    /**
     * 复制的文件目录
     */
    private String copyFolder;

    /**
     * 复制的文件名称
     */
    private String copyfileName;
}
