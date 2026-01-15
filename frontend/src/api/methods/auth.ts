import type {AuthenticationResponse, UserState} from "/@/store/modules/user/types.ts";
import {globalInstance} from "/@/api";

export const authApi = {
    login: (data: { username: string, password: string }) => {
        const method = globalInstance.Post<AuthenticationResponse>('/auth/login', data)
        method.meta = {authRole: null,};
        return method;
    },
    getUserInfo: () => globalInstance.Get<UserState>('/system-user/get-user-info', {}),
}