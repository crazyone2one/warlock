import {defineConfig, presetIcons} from 'unocss'

export default defineConfig({
    presets: [
        presetIcons({
            collections: {
                solar: () => import('@iconify-json/solar/icons.json').then(i => i.default)
            }
        })
    ],
    rules:[
        [/^m-([.\d]+)$/, ([_, num]) => ({ margin: `${num}px` })],
        [/^mt-([.\d]+)$/, ([_, num]) => ({ 'margin-top': `${num}px` })]
    ]
})