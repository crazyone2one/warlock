import {globalInstance} from "/@/api";
import type {
    AuthTableItem,
    SaveUSettingData,
    SystemUserGroupParams,
    UserGroupAuthSetting,
    UserGroupItem
} from "/@/api/types/user-group.ts";

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
    updateOrAddUserGroup: (data: SystemUserGroupParams) => globalInstance.Post<UserGroupItem>(data.id?'/user/role/update':'user/role/save', data),
}