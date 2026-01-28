import {createApp} from 'vue'
// import './style.css'
import App from './App.vue'
import 'virtual:uno.css'
import 'vfonts/Lato.css'
import 'vfonts/FiraCode.css'
import {naive} from '/@/utils/naive.ts'
import router from "/@/router";
import pinia from '/@/store'
import directive from './directive';
import {setupI18n} from './i18n';
import useLocale from '/@/i18n/use-locale.ts'

const bootstrap = async () => {
    const app = createApp(App)
    app.use(naive)
    app.use(router)
    app.use(pinia)
    app.use(directive)
    await setupI18n(app)
    // 获取默认语言
    const localLocale = localStorage.getItem('locale');
    if (!localLocale) {
        // const defaultLocale = await getDefaultLocale();
        const defaultLocale = "zh-CN";
        const {changeLocale} = useLocale();
        await changeLocale(defaultLocale);
    }
    app.mount('#app')
}
bootstrap()
