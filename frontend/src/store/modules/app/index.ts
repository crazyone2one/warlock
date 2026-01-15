import {defineStore} from "pinia";
import type {AppState} from "/@/store/modules/app/types.ts";

const useAppStore = defineStore("app", {
    persist: true,
    state: (): AppState => ({
        currentProjectId: '',
        innerHeight: 0,
        menuCollapse: false,
        loading: false,
        loadingTip: '你不知道你有多幸运...',
        projectList: [],
    }),
    getters: {
        getCurrentProjectId(state: AppState): string {
            return state.currentProjectId;
        },
        getProjectList(state: AppState): Array<any> {
            return state.projectList;
        },
        getMenuCollapse(state: AppState): boolean {
            return state.menuCollapse;
        },
    },
    actions: {
        setCurrentProjectId(id: string) {
            this.currentProjectId = id;
        },
        setMenuCollapse(collapse: boolean) {
            this.menuCollapse = collapse;
        },
        showLoading(tip = '') {
            this.loading = true;
            this.loadingTip = tip || '你不知道你有多幸运...'
        },
        hideLoading() {
            this.loading = false
        },
        // 重置用户信息
        resetInfo() {
            this.$reset();
        },

    },
});
export default useAppStore