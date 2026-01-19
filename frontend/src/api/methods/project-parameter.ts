import {globalInstance} from "/@/api";

export interface ISshConfig {
    host: string,
    port: string,
    localPath: string,
    remotePath: string,
    username: string,
    password: string
}

export interface IDataSourceConfig {
    name: string,
    url: string,
    username: string,
    password: string
}

export interface IProjectParameter {
    id: string
    projectId: string
    parameterType: string
    parameters: ISshConfig | IDataSourceConfig
}

export const projectParameterApi = {
    addParameter: (data: IProjectParameter) => globalInstance.Post(`/project-parameter/save`, data),
    updateParameter: (data: IProjectParameter) => globalInstance.Put(`/project-parameter/update`, data),
    // 查询项目参数信息
    getParameter: (projectId: string, type: string) => globalInstance.Get<IProjectParameter>(`/project-parameter/getInfo/${projectId}/${type}`)
}