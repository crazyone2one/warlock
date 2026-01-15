import {createAlova} from 'alova';
import adapterFetch from 'alova/fetch';
import VueHook from 'alova/vue';
import {getToken} from "/@/utils/auth.ts";

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
        if (token && (!method.meta?.authRole || method.meta?.authRole !== 'refreshToken')) {
            method.config.headers.Authorization = `Bearer ${token.accessToken}`;
        }
    },
    responded: {
        onSuccess: async (response, method) => {
            if (response.status >= 400) {
                throw new Error(response.statusText);
            }
            const json = await response.json();
            if (json.code !== 100200) {
                // 抛出错误或返回reject状态的Promise实例时，此请求将抛出错误
                throw new Error(json.message);
            }
            if (method.meta?.isBlob) {
                return response.blob();
            }
            // 解析的响应数据将传给method实例的transform钩子函数，这些函数将在后续讲解
            return json.data;
        },
        // 错误响应处理
        onError: (error, method) => {
            // const appStore = useAppStore();
            // appStore.hideLoading();
            const tip = `[${method.type}] - [${method.url}] - ${error.message}`;
            window.$message?.warning(tip);
        }
    }
});