import {createAlova} from 'alova';
import adapterFetch from 'alova/fetch';
import VueHook from 'alova/vue';
import {clearToken, getToken, setToken} from "/@/utils/auth.ts";
import {useAppStore} from "/@/store";
import router from "/@/router";
import {authApi} from "/@/api/methods/auth.ts";
import {createServerTokenAuthentication} from "alova/client";
import useLocale from "/@/i18n/use-locale.ts";
import {useI18n} from "/@/hooks/useI18n.ts";

const {onAuthRequired, onResponseRefreshToken} = createServerTokenAuthentication({
    assignToken: method => {
        const token = getToken();
        if (token && (!method.meta?.authRole || method.meta?.authRole !== 'refreshToken')) {
            method.config.headers.Authorization = `Bearer ${token.accessToken}`;
        }
        if (token && method.url.includes('/auth/refresh-token')) {
            method.config.headers.Authorization = `Bearer ${token.refreshToken}`;
        }
    },
    refreshTokenOnSuccess: {
        // 响应时触发，可获取到response和method，并返回boolean表示token是否过期
        // 当服务端返回401时，表示token过期
        isExpired: async (response, method) => {
            const res = await response.clone().json();
            const isExpired = method.meta && method.meta.isExpired;
            return !method.url.includes('/auth/refresh-token') && (response.status === 401 || res.code === 'TOKEN_EXPIRED') && !isExpired;
        },

        // 当token过期时触发，在此函数中触发刷新token
        handler: async (response, method) => {
            method.meta = method.meta || {};
            method.meta.isExpired = true;
            if (method.url.includes('/auth/refresh-token')) {
                if (response.status === 401) {
                    // 刷新token失败，说明refresh_token也过期了
                    window.$message?.error('登录已过期，请重新登录');
                    clearToken();
                    await router.push({
                        name: 'login',
                        query: {
                            ...router.currentRoute.value.query,
                            redirect: router.currentRoute.value.name as string,
                        },
                    });
                    return Promise.reject(new Error('Token expired'));
                }

                const json = await response.json();
                if (response.status >= 400 || json.code !== 100200) {
                    window.$message?.error(json.message || '刷新token失败');
                    return Promise.reject(new Error(json.message));
                }
                return json.data;
            }
            try {
                const token = getToken();
                const refreshResult = await authApi.refreshToken();
                if (refreshResult.accessToken) {
                    // 更新本地token
                    setToken(refreshResult.accessToken, token.refreshToken);
                }
            } catch (error) {
                clearToken();
                await router.push({
                    name: 'login',
                    query: {
                        ...router.currentRoute.value.query,
                        redirect: router.currentRoute.value.name as string,
                    },
                });
                // 并抛出错误
                throw error;
            }
        }
    }
});
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
    beforeRequest: onAuthRequired(
        (method) => {
            const appStore = useAppStore();
            const {currentLocale} = useLocale();
            method.config.headers = {
                ...method.config.headers,
                'PROJECT': appStore.currentProjectId,
                'Accept-Language': currentLocale.value,
            };
            appStore.showLoading();
        }
    ),
    responded: onResponseRefreshToken({
        onSuccess: async (response, method) => {
            const appStore = useAppStore();
            appStore.hideLoading();
            const {t} = useI18n();
            if (response.status >= 400) {
                const json = await response.json();

                // 根据HTTP状态码和业务code分别处理
                if (response.status === 403) {
                    // 权限不足
                    window.$message?.error(json.message || t('api.errMsg403'));
                    throw new Error(json.message || t('api.errMsg403'));
                } else if (response.status === 404) {
                    // 资源未找到
                    window.$message?.error(json.message || t('api.errMsg404'));
                    throw new Error(json.message || t('api.errMsg404'));
                } else if (response.status >= 500) {
                    // 服务器内部错误
                    window.$message?.error(json.message || t('api.errMsg500'));
                    throw new Error(json.message || t('api.errMsg500'));
                } else {
                    // 其他客户端错误
                    window.$message?.error((response.status === 500 ? json.message || t('api.errMsg500') : json.message) || `${response.statusText}`);
                    throw new Error(response.statusText);
                }
            }

            if (method.meta?.isBlob) {
                return response.blob();
            }

            const json = await response.json();

            // 根据业务code处理不同情况
            if (json.code !== 100200) {
                // 参数校验失败
                if (json.code === 100400) {
                    window.$message?.warning(json.message || '参数校验失败');
                } else if (json.code === 100401) {
                    // 认证失败
                    window.$message?.error(json.message || '用户认证失败');
                    clearToken();
                    await router.push({
                        name: 'login',
                        query: {
                            ...router.currentRoute.value.query,
                            redirect: router.currentRoute.value.name as string,
                        },
                    });
                } else if (json.code === 100403) {
                    // 权限不足
                    window.$message?.error(json.message || '权限不足');
                } else if (json.code === 100404) {
                    // 资源未找到
                    window.$message?.error(json.message || '资源未找到');
                } else if (json.code === 100500) {
                    // 系统未知异常
                    window.$message?.error(json.message || '系统未知异常');
                } else {
                    // 其他业务错误
                    window.$message?.error(json.message || '请求失败');
                }

                // 抛出错误或返回reject状态的Promise实例时，此请求将抛出错误
                throw new Error(json.message || `业务错误: ${json.code}`);
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
    })
});