import type {AuthScopeEnumType} from "/@/utils/common-enum.ts";

export interface CurrentUserGroupItem {
    // 组ID
    id: string;
    // 组名称
    name: string;
    code: string;
    // 所属类型
    type: AuthScopeEnumType;
    // 是否是内置用户组
    internal: boolean;
    // 组ID
    scopeId?: string;
}
export interface UserGroupItem {
    // 组ID
    id: string;
    // 组名称
    name: string;
    code: string;
    // 组描述
    description: string;
    // 是否是内置用户组
    internal: true;
    // 所属类型
    type: AuthScopeEnumType;
    createTime: number;
    updateTime: number;
    // 创建人
    createUser: string;
    // 应用范围
    scopeId: string;
    // 自定义排序
    pos: number;
}
export interface UserGroupPermissionItem {
    id: string;
    name: string;
    enable: boolean;
}
// 用户组对应的权限配置
export interface UserGroupAuthSetting {
    // 菜单项ID
    id: AuthScopeEnumType;
    // 菜单所属类型
    type?: string;
    // 菜单项名称
    name: string;
    // 是否全选
    enable: boolean;
    // 菜单下的权限列表
    permissions?: UserGroupPermissionItem[];
    // 子菜单
    children?: UserGroupAuthSetting[];
}
// 权限表格DataItem
export interface AuthTableItem {
    id: string;
    name?: string;
    enable: boolean;
    ability?: string | undefined;
    permissions?: UserGroupPermissionItem[];
    // 对应表格权限的复选框组的绑定值
    perChecked?: string[];
    operationObject?: string;
    rowSpan?: number;
    indeterminate?: boolean;
}
export interface SavePermissions {
    id: string;
    enable: boolean;
}
export interface SaveUSettingData {
    userRoleId: string;
    permissions: SavePermissions[];
}