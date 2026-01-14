import {defineConfig, presetIcons} from 'unocss'

export default defineConfig({
    presets: [
        presetIcons({
            collections: {
                solar: () => import('@iconify-json/solar/icons.json').then(i => i.default)
            }
        })
    ]
})