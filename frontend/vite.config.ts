import {defineConfig, loadEnv} from 'vite'
import UnoCSS from 'unocss/vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

export default defineConfig(({mode}) => {
    const env = loadEnv(mode, process.cwd(), '')
    const isProduction = mode === 'production'
    return {
        define: {
            // 提供从 env var 派生的显式应用程序级常量。
            __APP_ENV__: JSON.stringify(env.APP_ENV),
        },
        // 例如：使用 env var 有条件地设置开发服务器端口。
        server: !isProduction ? {
            port: env.APP_PORT ? Number(env.APP_PORT) : 5173,
            watch: {
                usePolling: true
            }
        } : {},
        plugins: [UnoCSS(), vue()],
        resolve: {
            alias: [{
                find: /\/@\//,
                replacement: path.resolve(__dirname, '.', 'src') + '/',
            }],
        },
        build: {
            rolldownOptions: {
                output: {

                }
            }
        }
    }
})