import {createRouter, createWebHashHistory} from 'vue-router'
import createRouteGuard from "/@/router/guard";
import {i18n} from "/@/i18n";
import appRoutes from "/@/router/routes";

const routes = [
    {
        path: '/',
        component: () => import('/@/layout/index.vue'),
        redirect: '/dashboard',
        children:[
            ...appRoutes,
        ]
    },

    {
        path: '/login', name: 'login',
        component: () => import('/@/views/login/index.vue'),
    },
    {path: '/:pathMatch(.*)*', name: 'NotFound', component: () => import('/@/views/error/NotFound.vue')},
]
const router = createRouter({
    history: createWebHashHistory(),
    routes,
    scrollBehavior() {
        return {top: 0};
    },
})
router.afterEach(to => {
    // const {t} = i18n.global
    const items = [import.meta.env.VITE_APP_TITLE]
    if (to.meta.title != null) {
        const title = typeof to.meta.title === 'string' ? to.meta.title : String(to.meta.title);
        // 类型断言解决 vue-i18n t 方法的类型问题
        const translatedTitle = (i18n.global as any).t(title);
        items.unshift(translatedTitle);
    }
    document.title = items.join(' | ')
})
createRouteGuard(router);
export default router