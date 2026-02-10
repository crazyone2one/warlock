import type {RequestMethods} from "/@/utils/common-enum.ts";
import type {TreeOption} from "naive-ui";

export interface ModuleTreeNode extends TreeOption {
    id: string;
    name: string;
    type: 'MODULE' | 'API';
    children: ModuleTreeNode[];
    attachInfo: {
        method?: keyof typeof RequestMethods;
        protocol: string;
    }; // 附加信息
    count: 0;
    parentId: string;
    path: string;
}
// 定义-获取模块树参数
export interface ApiDefinitionGetModuleParams {
    keyword: string;
    searchMode?: 'AND' | 'OR';
    filter?: Record<string, any>;
    combine?: Record<string, any>;
    moduleIds: string[];
    protocols?: string[];
    projectId: string;
    versionId?: string;
    refId?: string;
    shareId?: string;
    orgId?: string; // 组织id
}