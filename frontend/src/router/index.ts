import {createRouter, createWebHashHistory} from 'vue-router'

const routes = [
    {
        path: '/',
        component: () => import('/@/layout/index.vue'),
        redirect: '/dashboard',
        children: [
            {
                path: '/dashboard',
                component: () => import('/@/views/dashboard/index.vue'),
            },
        ],
    },
]
export const router = createRouter({
    history: createWebHashHistory(),
    routes,
})