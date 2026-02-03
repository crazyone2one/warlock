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
    querySchedulePage: (data: ITableQueryParams) => globalInstance.Post<IPageResponse<IScheduleInfo>>('/system/task-center/page', data),
    createOrUpdateSchedule: (data: Partial<ICreateScheduleTask>) => globalInstance.Post(data.id ? '/system/task-center/update' : '/system/task-center/save', data),
    // 更新cron表达式
    updateScheduleCron: (data: IUpdateScheduleCron) => globalInstance.Post('/system/task-center/update-cron', data),
    // 删除定时任务
    deleteSchedule: (id: string) => globalInstance.Delete(`/system/task-center/remove/${id}`),
    // 配置定时任务运行参数
    updateScheduleConfig: (data: IScheduleConfig) => globalInstance.Post('/system/task-center/schedule-config', data),
    getScheduleInfo: (id: string) => globalInstance.Get<IScheduleInfo>(`/system/task-center/getInfo/${id}`),
    pauseSchedule: (id: string) => globalInstance.Get(`/system/task-center/pause/task/${id}`),
    scheduleSwitch: (id: string) => globalInstance.Get(`/system/task-center/schedule/switch/${id}`),
}