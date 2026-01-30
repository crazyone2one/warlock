import type {BatchApiParams} from "/@/api/types.ts";

export interface CreateUserResult {
    errorEmails: Record<string, any>;
    successList: any[];
}

export interface SimpleUserInfo {
    id?: string;
    name: string;
    nickName: string;
    email: string;
    phone?: string;
}

export interface CreateUserParams {
    userInfoList: SimpleUserInfo[];
    userRoleIdList: string[];
}

export interface UpdateUserInfoParams extends SimpleUserInfo {
    id: string;
    userRoleIdList: string[];
}

export interface UpdateUserStatusParams extends BatchApiParams {
    enable: boolean;
}

export type DeleteUserParams = BatchApiParams;
export type ResetUserPasswordParams = BatchApiParams;

export interface ImportUserParams {
    fileList: File[];
}

export interface UserListItem {
    id: string;
    userName: string;
    nickName: string;
    email: string;
    password: string;
    enable: boolean;
    createTime: number;
    updateTime: number;
    phone: string;
    lastProjectId: string; // 当前项目ID
    createUser: string;
    updateUser: string;
    userRoleList: UserRoleListItem[]; // 用户所属用户组
    userRoles?: UserRoleListItem[]; // 用户所属用户组
    selectUserGroupVisible: boolean;
    selectUserGroupLoading: boolean;
    userRoleIdList: string[];
}

export interface UserRoleListItem {
    id: string;
    name: string;
    description: string;
    internal: boolean; // 是否内置用户组
    type: string; // 所属类型 SYSTEM ORGANIZATION PROJECT
    createTime: number;
    updateTime: number;
    createUser: string;
    scopeId: string; // 应用范围
}

export interface SystemRole {
    id: string;
    name: string;
    disabled: boolean; // 是否可选
    closeable: boolean; // 是否可取消
}

export type UserModalMode = 'create' | 'edit';

export interface UserForm {
    list: SimpleUserInfo[];
    userGroup: Record<string, any>[];
    userGroupIdList?: string[];
}
export interface ImportResult {
    importCount: number;
    successCount: number;
    errorMessages: Record<string, any>;
}