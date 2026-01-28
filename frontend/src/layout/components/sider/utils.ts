import type {RouteRecordRaw} from "vue-router";
import type {IMenu} from "/@/layout/components/sider/index.vue";

/**
 * 根据路由信息生成菜单数据
 * @param routers 路由信息
 */
export const generateMenus = (routers: RouteRecordRaw[]) => {
    return routers.map(router => {
        const menu: IMenu = {
            label: (router.meta?.title as string) || (router.name as string) || '',
            name: (router.name as string) || '',
            key: (router.name as string) || '',
            icon: router.meta?.icon as string | undefined,
            meta: router.meta
        }
        if (router.children && router.children.length > 0) {
            menu.children = generateMenus(router.children)
        }
        return menu
    })
}