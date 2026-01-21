import type {
    ICreateScheduleTask,
    IPageResponse,
    IScheduleConfig,
    IScheduleInfo,
    ITableQueryParams,
    IUpdateScheduleCron
} from "/@/api/types.ts";
import {globalInstance} from "/@/api";

export const scheduleApi = {
    querySchedulePage: (data: ITableQueryParams) => globalInstance.Post<IPageResponse<IScheduleInfo>>('/system-schedule/page', data),
    createOrUpdateSchedule: (data: Partial<ICreateScheduleTask>) => globalInstance.Post(data.id ? '/system-schedule/update' : '/system-schedule/save', data),
    // 更新cron表达式
    updateScheduleCron: (data: IUpdateScheduleCron) => globalInstance.Post('/system-schedule/update-cron', data),
    // 删除定时任务
    deleteSchedule: (id: string) => globalInstance.Delete(`/system-schedule/remove/${id}`),
    // 配置定时任务运行参数
    updateScheduleConfig: (data: IScheduleConfig) => globalInstance.Post('/system-schedule/schedule-config', data),
    getScheduleInfo: (id: string) => globalInstance.Get<IScheduleInfo>(`/system-schedule/getInfo/${id}`)
}