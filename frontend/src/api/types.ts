export interface ITableQueryParams {
    // 当前页
    page?: number;
    // 每页条数
    pageSize?: number;
    // 排序仅针对单个字段
    sort?: object;
    // 排序仅针对单个字段
    sortString?: string;
    // 表头筛选
    filter?: object;
    // 查询条件
    keyword?: string;

    [key: string]: string | number | object | undefined;
}

export interface IPageResponse<T> {
    [x: string]: any;

    pageSize: number;
    totalPage: number;
    pageNumber: number;
    totalRow: number;
    records: T[];
}

export interface IProjectItem {
    id: string;
    name: string;
    description: string;
    enable: boolean;
    num: string;
    updateTime: number;
    createTime: number;
}

export interface IScheduleInfo {
    id: string;
    name: string;
    resourceType?: string;
    projectName: string;
    num: number;
    enable: boolean;
    value: string;
    triggerStatus?: string;
    lastTime?: number;
    nextTime?: number;
    createUser?: string
    runConfig?: IScheduleConfig;
}

export interface IScheduleConfig {
    jobKey: string
    cron: string
    enable: boolean
    runConfig: IRunConfig
}

export interface IRunConfig {
    [key: string]: any;
}

export interface ICreateScheduleTask {
    id: string;
    name: string;
    enable: boolean;
    value: string;
    jobKey: string;
    projectId: string;
    job: string;
    type: string
    resourceType: string
    runConfig?: IScheduleConfig;
}
export interface IUpdateScheduleCron {
    id: string
    cron: string
}