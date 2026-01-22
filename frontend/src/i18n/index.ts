import {createI18n, type DefineLocaleMessage} from "vue-i18n";
import zhCN from './locales/zh-CN.ts'
import en from './locales/en.ts'

export type LocaleType = 'zh-CN' | 'en';
const i18n = createI18n<[DefineLocaleMessage], 'en' | 'zh-CN', false>({
    legacy: false, // 必须为 false 以使用 Composition API
    locale: localStorage.getItem('lang') || 'zh-CN',
    fallbackLocale: 'en',
    messages: {
        en,
        'zh-CN': zhCN
    },
})

export default i18n