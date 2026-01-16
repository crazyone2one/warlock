import {defineConfig, presetIcons,presetWind3} from 'unocss'

export default defineConfig({
    presets: [
        presetWind3(),
        presetIcons({
            collections: {
                solar: () => import('@iconify-json/solar/icons.json').then(i => i.default)
            }
        })
    ],
    // rules:[
    //     [/^m-([.\d]+)$/, ([_, num]) => ({ margin: `${num}px` })],
    //     [/^mt-([.\d]+)$/, ([_, num]) => ({ 'margin-top': `${num}px` })]
    // ]
})