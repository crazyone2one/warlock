// 创建定义参数
import type {ExecuteRequestParams, ResponseDefinition} from "/@/api/types/common-types.ts";
import type {RequestDefinitionStatus, RequestMethodsType} from "/@/utils/api-enum.ts";
import type {ITableQueryParams} from "/@/api/types.ts";

// 定义列表查询参数
export interface ApiDefinitionPageParams extends ITableQueryParams {
    id: string;
    name: string;
    protocols: string[];
    projectId: string;
    versionId: string;
    refId: string;
    moduleIds: string[];
    deleted: boolean;
}

// 定义-自定义字段
export interface ApiDefinitionCustomField {
    apiId: string;
    fieldId: string;
    value: string;
}

export interface ApiDefinitionCreateParams extends ExecuteRequestParams {
    tags: string[];
    response: ResponseDefinition[];
    description: string;
    status: RequestDefinitionStatus;
    customFields: ApiDefinitionCustomField[];
    moduleId: string;
    versionId: string;

    [key: string]: any; // 其他前端定义的参数
}

// 更新定义参数
export interface ApiDefinitionUpdateParams extends Partial<ApiDefinitionCreateParams> {
    id: string;
    deleteFileIds?: string[];
    unLinkFileIds?: string[];
}

// 定义-自定义字段详情
export interface ApiDefinitionCustomFieldDetail {
    id: string;
    name: string;
    scene: string;
    type: string;
    remark: string;
    internal: boolean;
    scopeType: string;
    createTime: number;
    updateTime: number;
    createUser: string;
    refId: string;
    enableOptionKey: boolean;
    scopeId: string;
    value: string;
    apiId: string;
    fieldId: string;
}

// 定义详情
export interface ApiDefinitionDetail extends ApiDefinitionCreateParams {
    id: string;
    name: string;
    protocol: string;
    method: RequestMethodsType | string;
    path: string;
    num: number;
    pos: number;
    projectId: string;
    moduleId: string;
    latest: boolean;
    versionId: string;
    refId: string;
    createTime: number;
    createUser: string;
    updateTime: number;
    updateUser: string;
    deleteUser: string;
    deleteTime: number;
    deleted: boolean;
    createUserName: string;
    updateUserName: string;
    deleteUserName: string;
    versionName: string;
    caseTotal: number;
    casePassRate: string;
    caseStatus: string;
    follow: boolean;
    customFields: ApiDefinitionCustomFieldDetail[];
}