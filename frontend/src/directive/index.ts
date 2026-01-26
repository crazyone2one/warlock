import permission from "/@/directive/permission";
import type {App} from "vue";

export default {
    install(Vue: App) {
        Vue.directive('permission', permission);
    },
};