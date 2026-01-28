import {computed} from "vue";
import appRoutes from "/@/router/routes";
import type {RouteRecordRaw} from "vue-router";

export default function useMenuTree() {
    const menuTree = computed(() => {
        const copyRouter = appRoutes;

        function travel(_routes: RouteRecordRaw[], layer: number) {
            if (!_routes) return null;
            const collector = _routes.map((route) => {
                if (!route.children) {
                    route.children = [];
                    return route;
                }
                // 过滤隐藏的菜单
                route.children = route.children.filter((x) => x.meta?.hideInMenu !== true);
                const subItem = travel(route.children, layer + 1);
                if (subItem && subItem.length) {
                    route.children = subItem as RouteRecordRaw[];
                    return route;
                }

                if (layer > 1) {
                    route.children = subItem as RouteRecordRaw[];
                    return route;
                }
                return null;
            })
            return collector.filter(Boolean);
        }

        return travel(copyRouter, 0);
    });
    return {menuTree};
}