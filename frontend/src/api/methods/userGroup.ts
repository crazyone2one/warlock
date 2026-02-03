import {globalInstance} from "/@/api";
import type {
    AuthTableItem,
    SaveUSettingData,
    SystemUserGroupParams,
    UserGroupAuthSetting,
    UserGroupItem,
    UserOptionItem,
    UserTableItem
} from "/@/api/types/user-group.ts";
import type {IPageResponse, ITableQueryParams} from "/@/api/types.ts";

export const userGroupApi = {
    // 系统-获取用户组列表
    getUserGroupList: () => globalInstance.Get<Array<UserGroupItem>>("/user/role/list"),
    // 获取用户组对应的权限配置
    getGlobalUSetting: (id: string, transformData?: (data: Array<UserGroupAuthSetting>) => void) =>
        globalInstance.Get<Array<AuthTableItem>>(`/user/role/permission/setting/${id}`, {
            transform(data: any, _headers) {
                if (transformData) {
                    return transformData(data)
                }
                return data;
            },
        }),
    saveGlobalUSetting: (data: SaveUSettingData) => globalInstance.Post('/user/role/permission/update', data),
    updateOrAddUserGroup: (data: SystemUserGroupParams) => globalInstance.Post<UserGroupItem>(data.id ? '/user/role/update' : 'user/role/save', data),
    removeUserGroup: (id: string) => globalInstance.Get(`/user/role/remove/${id}`),
    // 系统-添加用户到用户组
    addUserToUserGroup: (data: {
        roleId: string;
        userIds: string[]
    }) => globalInstance.Post('/user/role/relation/save', data),
    // 获取用户组对应的用户列表
    queryUserPageByUserGroup: (params: ITableQueryParams) => {
        return globalInstance.Post<IPageResponse<UserTableItem>>('/user/role/relation/page', params, {})
    },
    // 删除用户组对应的用户
    removeUserFromUserGroup: (id: string) => globalInstance.Get(`/user/role/relation/remove/${id}`),
    // 获取需要关联的用户选项
    getSystemUserGroupOption: (id: string, keyword: string) => globalInstance.Get<Array<UserOptionItem>>(`/user/role/relation/user/option/${id}`, {params: {keyword}}),
}