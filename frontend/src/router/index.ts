import {createRouter, createWebHashHistory} from 'vue-router'

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
            },
            {
                path: '/schedule',
                name: 'Schedule',
                component: () => import('/@/views/schedule/index.vue'),
            },
            {
                path: '/setting/project',
                name: 'Project',
                component: () => import('/@/views/setting/project/index.vue')
            },
            {
                path: '/setting/user',
                name: 'User',
                component: () => import('/@/views/setting/user/index.vue')
            }
        ],
    },
    {
        path: '/login',
        component: () => import('/@/views/login/index.vue'),
    },
    {path: '/:pathMatch(.*)*', name: 'NotFound', component: () => import('/@/views/error/NotFound.vue')},
]
export const router = createRouter({
    history: createWebHashHistory(),
    routes,
})