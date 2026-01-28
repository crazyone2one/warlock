import permission from "/@/directive/permission";
import outerClick from './outerClick';
import type {App} from "vue";

export default {
    install(Vue: App) {
        Vue.directive('permission', permission);
        Vue.directive('outer', outerClick);
    },
};