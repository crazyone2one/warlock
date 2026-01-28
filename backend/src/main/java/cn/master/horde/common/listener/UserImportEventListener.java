package cn.master.horde.common.listener;

import cn.idev.excel.context.AnalysisContext;
import cn.idev.excel.event.AnalysisEventListener;
import cn.master.horde.dto.UserExcel;
import cn.master.horde.dto.UserExcelRowDTO;
import cn.master.horde.util.Translator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

/**
 * @author : 11's papa
 * @since : 2026/1/28, 星期三
 **/
@Slf4j
public class UserImportEventListener extends AnalysisEventListener<UserExcel> {
    private final ExcelParseDTO<UserExcelRowDTO> excelParseDTO;

    public UserImportEventListener() {
        excelParseDTO = new ExcelParseDTO<>();
    }

    @Override
    public void invoke(UserExcel data, AnalysisContext context) {
        String errMsg;
        Integer rowIndex = context.readRowHolder().getRowIndex();
        try {
            // 使用javax.validation校验excel数据
            errMsg = UserExcelValidateHelper.validateEntity(data);
            if (StringUtils.isEmpty(errMsg)) {
                errMsg = businessValidate(data);
            }
        } catch (NoSuchFieldException e) {
            errMsg = Translator.get("excel.parse.error");
            log.error(e.getMessage(), e);
        }
        UserExcelRowDTO userExcelRowDTO = new UserExcelRowDTO();
        BeanUtils.copyProperties(data, userExcelRowDTO);
        userExcelRowDTO.setDataIndex(rowIndex);
        if (StringUtils.isEmpty(errMsg)) {
            excelParseDTO.addRowData(userExcelRowDTO);
        } else {
            userExcelRowDTO.setErrorMessage(errMsg);
            excelParseDTO.addErrorRowData(rowIndex, userExcelRowDTO);
        }
    }

    private String businessValidate(UserExcel rowData) {
        if (CollectionUtils.isNotEmpty(excelParseDTO.getDataList())) {
            for (UserExcelRowDTO userExcelRowDTO : excelParseDTO.getDataList()) {
                if (userExcelRowDTO.getEmail().equalsIgnoreCase(rowData.getEmail())) {
                    return Translator.get("user.email.repeat") + ":" + rowData.getEmail();
                }
            }
        }
        return null;
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {

    }

    public ExcelParseDTO<UserExcelRowDTO> getExcelParseDTO() {
        return excelParseDTO;
    }
}
