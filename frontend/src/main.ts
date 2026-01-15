import {createApp} from 'vue'
// import './style.css'
import App from './App.vue'
import 'virtual:uno.css'
import 'vfonts/Lato.css'
import 'vfonts/FiraCode.css'
import {naive} from '/@/utils/naive.ts'
import router from "/@/router";
import pinia from '/@/store'

const app = createApp(App)
app.use(naive)
app.use(router)
app.use(pinia)
app.mount('#app')
