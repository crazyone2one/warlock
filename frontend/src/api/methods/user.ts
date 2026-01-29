import {globalInstance} from "/@/api";
import type {
    CreateUserParams,
    CreateUserResult,
    DeleteUserParams,
    ImportUserParams,
    ResetUserPasswordParams,
    SystemRole,
    UpdateUserInfoParams,
    UpdateUserStatusParams,
    UserListItem
} from "/@/api/types/user.ts";
import type {IPageResponse, ITableQueryParams} from "/@/api/types.ts";
import {formatPhoneNumber} from "/@/utils";

export const userApi = {
    // 批量创建用户
    batchCreateUser: (data: CreateUserParams) => globalInstance.Post<CreateUserResult>("system/user/save", data),
    // 更新用户信息
    updateUserInfo: (data: UpdateUserInfoParams) => globalInstance.Post("/system/user/update", data),
    // 更新用户启用/禁用状态
    toggleUserStatus: (data: UpdateUserStatusParams) => globalInstance.Post("/system/user/update/enable", data),
    // 删除用户
    deleteUserInfo: (data: DeleteUserParams) => globalInstance.Post("/system/user/delete", data),
    // 重置用户密码
    resetUserPassword: (data: ResetUserPasswordParams) => globalInstance.Post("/system/user/reset/password", data),
    importUserInfo: (data: ImportUserParams) => {
        const formData = new FormData();
        Array.from(data.fileList).forEach((file: File) => {
            formData.append('file', file); // 多个文件用同一个字段名
        });
        return globalInstance.Post("/system/user/import", formData, {})
    },
    queryUserPage: (params: ITableQueryParams) => {
        return globalInstance.Post<IPageResponse<UserListItem>>('/system/user/page', params, {
            transform(data: any, _headers) {
                return {
                    ...data,
                    records: data.records.map((item: UserListItem) => {
                        return {
                            ...item,
                            phone: formatPhoneNumber(item.phone || ''),
                            selectUserGroupVisible: false,
                            selectUserGroupLoading: false,
                            userRoleIdList: item.userRoleList.map((e) => e.id),
                        }
                    })
                }

            }
        })
    },
    // 获取系统用户组
    getSystemRoles: () => globalInstance.Get<Array<SystemRole>>('/system/user/get/system/role')
}