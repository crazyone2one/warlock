import {createRouter, createWebHashHistory} from 'vue-router'
import createRouteGuard from "/@/router/guard";

const routes = [
    {
        path: '/',
        component: () => import('/@/layout/index.vue'),
        redirect: '/dashboard',
        children: [
            {
                path: '/dashboard',
                name: 'Dashboard',
                component: () => import('/@/views/dashboard/index.vue'),
                meta: {title: '工作台'}
            },
            {
                path: '/schedule',
                name: 'Schedule',
                component: () => import('/@/views/schedule/index.vue'),
                meta: {title: '任务中心'}
            },
            {
                path: '/setting/project',
                name: 'Project',
                component: () => import('/@/views/setting/project/index.vue'),
                meta: {title: '项目管理'}
            },
            {
                path: '/setting/user',
                name: 'User',
                component: () => import('/@/views/setting/user/index.vue'),
                meta: {title: '用户管理'}
            }
        ],
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
})
router.afterEach(to => {
    const items = [import.meta.env.VITE_APP_TITLE]
    to.meta.title != null && items.unshift(to.meta.title as string)
    document.title = items.join(' · ')
})
createRouteGuard(router);
export default router