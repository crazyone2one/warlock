package cn.master.horde.controller;

import com.mybatisflex.core.paginate.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import cn.master.horde.model.entity.OperationLog;
import cn.master.horde.service.OperationLogService;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;

/**
 * 操作日志表 控制层。
 *
 * @author 11's papa
 * @since 2026-01-22
 */
@RestController
@Tag(name = "操作日志表接口")
@RequestMapping("/operationLog")
public class OperationLogController {

    @Autowired
    private OperationLogService operationLogService;

    /**
     * 保存操作日志表。
     *
     * @param operationLog 操作日志表
     * @return {@code true} 保存成功，{@code false} 保存失败
     */
    @PostMapping("save")
    @Operation(description="保存操作日志表")
    public boolean save(@RequestBody @Parameter(description="操作日志表")OperationLog operationLog) {
        return operationLogService.save(operationLog);
    }

    /**
     * 根据主键删除操作日志表。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    @Operation(description="根据主键删除操作日志表")
    public boolean remove(@PathVariable @Parameter(description="操作日志表主键") String id) {
        return operationLogService.removeById(id);
    }

    /**
     * 根据主键更新操作日志表。
     *
     * @param operationLog 操作日志表
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    @Operation(description="根据主键更新操作日志表")
    public boolean update(@RequestBody @Parameter(description="操作日志表主键") OperationLog operationLog) {
        return operationLogService.updateById(operationLog);
    }

    /**
     * 查询所有操作日志表。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    @Operation(description="查询所有操作日志表")
    public List<OperationLog> list() {
        return operationLogService.list();
    }

    /**
     * 根据主键获取操作日志表。
     *
     * @param id 操作日志表主键
     * @return 操作日志表详情
     */
    @GetMapping("getInfo/{id}")
    @Operation(description="根据主键获取操作日志表")
    public OperationLog getInfo(@PathVariable @Parameter(description="操作日志表主键") String id) {
        return operationLogService.getById(id);
    }

    /**
     * 分页查询操作日志表。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    @Operation(description="分页查询操作日志表")
    public Page<OperationLog> page(@Parameter(description="分页信息") Page<OperationLog> page) {
        return operationLogService.page(page);
    }

}
