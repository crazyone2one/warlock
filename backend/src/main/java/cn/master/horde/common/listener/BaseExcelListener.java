package cn.master.horde.common.listener;

import cn.idev.excel.context.AnalysisContext;
import cn.idev.excel.event.AnalysisEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : 11's papa
 * @since : 2026/1/28, 星期三
 **/
public class BaseExcelListener<T> extends AnalysisEventListener<T> {
    // 用于存储读取到的Excel数据对象列表
    private List<T> dataList = new ArrayList<>();
    @Override
    public void invoke(T data, AnalysisContext context) {
        dataList.add(data);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {

    }
    public List<T> getDataList() {
        return dataList;
    }
}
