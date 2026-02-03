import {createAlova} from 'alova';
import adapterFetch from 'alova/fetch';
import VueHook from 'alova/vue';
import {clearToken, getToken} from "/@/utils/auth.ts";
import {useAppStore} from "/@/store";
import router from "/@/router";

export const globalInstance = createAlova({
    baseURL: `${window.location.origin}/${import.meta.env.VITE_API_BASE_URL}`,
    statesHook: VueHook,
    requestAdapter: adapterFetch(),
    timeout: 300 * 1000,
    cacheFor: {
        GET: 0, // 关闭所有GET缓存
        // POST: 60 * 60 * 1000 // 设置所有POST缓存1小时
        POST: 0 // 关闭所有POST缓存
    },
    beforeRequest(method) {
        const token = getToken();
        // if (token && (!method.meta?.authRole || method.meta?.authRole !== 'refreshToken')) {
        if (token && (!method.meta?.ignoreToken)) {
            method.config.headers.Authorization = `Bearer ${token.accessToken}`;
        }
        const appStore = useAppStore();
        appStore.showLoading();
    },
    responded: {
        onSuccess: async (response, method) => {
            const appStore = useAppStore();
            appStore.hideLoading();
            if (response.status >= 400) {
                const json = await response.json();
                // 检查是否为JWT过期错误
                if (response.status === 401 && json.code === 'TOKEN_EXPIRED') {
                    window.$message?.error(json.message || '登录已过期，请重新登录');
                    // 清除本地存储的token
                    clearToken();
                    // 重定向到登录页面
                    await router.push({
                        name: 'login',
                        query: {
                            ...router.currentRoute.value.query,
                            redirect: router.currentRoute.value.name as string,
                        },
                    });
                    return Promise.reject(new Error(json.message));
                }
                window.$message?.error((response.status === 500 ? json.message || '系统错误' : json.message) || `${response.statusText}`);
                throw new Error(response.statusText);
            }
            if (method.meta?.isBlob) {
                return response.blob();
            }
            const json = await response.json();
            // 检查是否为JWT过期错误
            if (json.code === 'TOKEN_EXPIRED') {
                window.$message?.error(json.message || '登录已过期，请重新登录');
                // 清除本地存储的token
                clearToken();
                // 重定向到登录页面
                await router.push({
                    name: 'login',
                    query: {
                        ...router.currentRoute.value.query,
                        redirect: router.currentRoute.value.name as string,
                    },
                });
                return Promise.reject(new Error(json.message));
            }
            if (json.code !== 100200) {
                // 抛出错误或返回reject状态的Promise实例时，此请求将抛出错误
                throw new Error(json.message);
            }
            // 解析的响应数据将传给method实例的transform钩子函数，这些函数将在后续讲解
            return json.data;
        },
        // 错误响应处理
        onError: async (error, method) => {
            // const appStore = useAppStore();
            // appStore.hideLoading();
            const tip = `[${method.type}] - [${method.url}] - ${error.message}`;
            window.$message?.warning(tip);
            const appStore = useAppStore();
            appStore.hideLoading();
            // 检查错误消息是否包含JWT过期相关信息
            if (error.message.includes('登录已过期') || error.message.includes('TOKEN_EXPIRED')) {
                // 清除本地存储的token
                clearToken();
                // 重定向到登录页面
                await router.push({
                    name: 'login',
                    query: {
                        ...router.currentRoute.value.query,
                        redirect: router.currentRoute.value.name as string,
                    },
                });
            }
        },
        onComplete: () => {
            const appStore = useAppStore();
            appStore.hideLoading();
        }
    }
});