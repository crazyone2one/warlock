import {globalInstance} from "/@/api";
import type {IPageResponse, IProjectItem, ITableQueryParams} from "../types";

export const projectApi = {
    queryProjectPage: (data: ITableQueryParams) => globalInstance.Post<IPageResponse<IProjectItem>>('/system-project/page', data),
    createOrUpdateProject: (data: Partial<IProjectItem>) => globalInstance.Post(data.id ? '/system-project/update' : '/system-project/save', data),
    deleteProject: (id: string) => globalInstance.Delete(`/system-project/remove/${id}`),
    enableOrDisableProject: (id: string, isEnable = true) => globalInstance.Get(`${isEnable ? 'system-project/enable/' : 'system-project/disable/'}${id}`),
    queryProjectList: () => globalInstance.Get<IProjectItem[]>('/system-project/list'),
    switchProject: (data: { projectId: string, userId: string }) => globalInstance.Post(`/system-project/switch`, data)
}