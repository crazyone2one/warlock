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
    jobKey: string;
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

export interface ActionsItem {
    label?: string;
    eventTag?: string; // 事件标识
    isDivider?: boolean; // 是否分割线，true 的话只展示分割线，没有其他内容
    danger?: boolean; // 是否危险操作，true 的话会显示红色按钮
    disabled?: boolean; // 是否禁用
    icon?: string; // 按钮图标
    permission?: string[]; // 权限标识
}

export interface BatchApiParams {
    selectIds: string[]; // 已选 ID 集合，当 selectAll 为 false 时接口会使用该字段
    excludeIds?: string[]; // 需要忽略的用户 id 集合，当selectAll为 true 时接口会使用该字段
    selectAll: boolean; // 是否跨页全选，即选择当前筛选条件下的全部表格数据
    condition: Record<string, any>; // 当前表格查询的筛选条件
    currentSelectCount?: number; // 当前已选择的数量
    projectId?: string; // 项目 ID
    moduleIds?: (string | number)[]; // 模块 ID 集合
    versionId?: string; // 版本 ID
    refId?: string; // 版本来源
    protocols?: string[]; // 协议集合
    combineSearch?: FilterResult;
}

export interface FilterResult {
    // 匹配模式 所有/任一
    searchMode?: AccordBelowType;
    // 高级搜索
    conditions?: ConditionsItem[];
}

export type AccordBelowType = 'AND' | 'OR';
export type CombineItem = Pick<FilterFormItem, 'value' | 'customField'>;

export interface ConditionsItem extends CombineItem {
    name?: string;
}

export interface FilterFormItem {
    dataIndex?: string; // 第一列下拉的value
    title?: string; // 第一列下拉显示的label
    // operator?: OperatorEnum; // 第二列的值
    // type: FilterType; // 类型：判断第二列下拉数据和第三列显示形式
    value?: any; // 第三列的值
    customField?: boolean; // 是否是自定义字段
    customFieldType?: string; // 自定义字段的类型
    // cascaderOptions?: CascaderOption[]; // 级联选择的选项
    // dataProps?: Partial<DataProps>;
    // numberProps?: Partial<NumberProps>;
    // selectProps?: Partial<MsSearchSelectProps>; // select的props, 参考 MsSelect
    // cascaderProps?: Partial<MsCascaderProps>; // cascader的props, 参考 MsCascader
    // treeSelectData?: TreeNodeData[];
    // treeSelectProps?: Partial<TreeSelectProps>;
}
export interface BatchActionQueryParams {
    excludeIds?: string[]; // 排除的id
    selectedIds?: string[];
    selectAll: boolean; // 是否跨页全选
    params?: ITableQueryParams; // 查询参数
    currentSelectCount?: number; // 当前选中的数量
    condition?: any; // 查询条件
    [key: string]: any;
}