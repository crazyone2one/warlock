import {defineStore} from "pinia";
import type {UserState} from "/@/store/modules/user/types.ts";
import {useAppStore} from "/@/store";
import {composePermissions} from "/@/utils/permission.ts";

const useUserStore = defineStore('user', {
    persist: true,
    state: (): UserState => ({
        id: '',
        name: '',
        lastProjectId: '',
        avatar: undefined,
        email: '',
        phone: '',
        userRolePermissions: [],
        userRoles: [],
        userRoleRelations: [],
    }),
    getters: {
        userInfo(state: UserState): UserState {
            return {...state};
        },
        currentRole(state: UserState): {
            projectPermissions: string[];
            systemPermissions: string[];
        } {
            const appStore = useAppStore();
            state.userRoleRelations?.forEach((ug) => {
                state.userRolePermissions?.forEach((gp) => {
                    if (gp.userRole.code === ug.roleCode) {
                        ug.userRolePermissions = gp.userRolePermissions;
                        ug.userRole = gp.userRole;
                    }
                });
            });
            return {
                projectPermissions: composePermissions(state.userRoleRelations || [], 'PROJECT', appStore.currentProjectId),
                systemPermissions: composePermissions(state.userRoleRelations || [], 'SYSTEM', 'global'),
            };
        },
        isAdmin(state: UserState): boolean {
            if (!state.userRolePermissions) return false;
            return state.userRolePermissions.findIndex((ur) => ur.userRole.code === 'admin') > -1;
        },
    },
    actions: {
        // 设置用户信息
        setInfo(partial: Partial<UserState>) {
            this.$patch(partial);
        },
        // 重置用户信息
        resetInfo() {
            this.$reset();
        },
    },
})
export default useUserStore