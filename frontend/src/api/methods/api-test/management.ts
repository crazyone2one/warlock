import {globalInstance} from "/@/api";
import type {ApiDefinitionGetModuleParams, ModuleTreeNode} from "/@/api/types/api-test.ts";

export const apiTestManagementApi = {
    // 获取模块树
    getModuleTree: (data: ApiDefinitionGetModuleParams) => globalInstance.Post<Array<ModuleTreeNode>>('/api/definition/module/tree', data),
    // 更新模块
    updateModule: (data: { id: string, name: string }) => globalInstance.Post('/api/definition/module/update', data),
    // 添加模块
    addModule: (data: {
        projectId: string,
        name: string,
        parentId: string
    }) => globalInstance.Post('/api/definition/module/save', data),
    // 删除模块
    deleteModule: (id: string) => globalInstance.Get(`/api/definition/module/remove/${id}`),
};