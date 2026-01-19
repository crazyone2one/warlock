import type {IRunConfig} from "/@/api/types.ts";
import {globalInstance} from "/@/api";

export interface IProjectParameter {
    id: string
    projectId: string
    parameterType: string
    parameters: IRunConfig
}

export const projectParameterApi = {
    addOrUpdate: (data: IProjectParameter) => globalInstance.Post(`/project-parameter/save`, data)
}