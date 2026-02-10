import type {RouteRecordRaw} from "vue-router";

const appRoutes: RouteRecordRaw[] = [
    {
        path: '/dashboard',
        name: 'Dashboard',
        component: () => import('/@/views/dashboard/index.vue'),
        meta: {title: 'menu.workbench', icon: 'i-solar:share-circle-linear'}
    },
    {
        path: '/schedule',
        name: 'Schedule',
        component: () => import('/@/views/schedule/index.vue'),
        meta: {
            title: 'menu.taskCenter',
            roles: ['SYSTEM_SCHEDULE_TASK_CENTER:READ'],
            icon: 'i-solar:document-medicine-linear'
        }
    },
    {
        path: '/api-test',
        name: 'apiTest',
        component: null,
        meta: {title: 'menu.apiTest', roles: ['SYSTEM_PROJECT:READ'], icon: 'i-mdi:api'},
        children: [
            {
                path: '/api-test/management',
                name: 'apiTestManagement',
                component: () => import('/@/views/api-test/management/index.vue'),
                meta: {
                    title: 'menu.apiTest.management',
                    roles: ['PROJECT_API_DEFINITION:READ'],
                },
            },
        ]
    },
    {
        path: '/project-manage',
        name: 'Project',
        component: () => import('/@/views/setting/project/index.vue'),
        meta: {title: 'menu.projectManagement', roles: ['SYSTEM_PROJECT:READ'], icon: 'i-solar:file-text-outline'}
    },
    {
        path: '/setting',
        name: 'setting',
        component: null,
        meta: {title: 'menu.settings', icon: 'i-solar:settings-linear'},
        children: [
            {
                path: '/setting/user',
                name: 'User',
                component: () => import('/@/views/setting/user/index.vue'),
                meta: {
                    title: 'menu.settings.system.user',
                    roles: ['SYSTEM_USER_ROLE:READ'],
                    icon: 'i-solar:user-outline'
                }
            },
            {
                path: '/setting/user-group',
                name: 'UserGroup',
                component: () => import('/@/views/setting/user-group/index.vue'),
                meta: {
                    title: 'menu.settings.system.usergroup',
                    roles: ['SYSTEM_USER_GROUP_ROLE:READ'],
                    icon: 'i-solar:shield-user-linear'
                }
            },
            {
                path: '/setting/field',
                name: 'field',
                component: () => import('/@/views/setting/custom-field/index.vue'),
                meta: {
                    title: 'menu.settings.organization.templateFieldSetting',
                    roles: ['PROJECT_TEMPLATE:READ'],
                    icon: 'i-solar:document-add-linear'
                }
            }
        ]
    }

]
export default appRoutes;