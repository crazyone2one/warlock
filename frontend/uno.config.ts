import {defineConfig, presetIcons, presetWind3} from 'unocss'

export default defineConfig({
    presets: [
        presetWind3(),
        presetIcons({
            collections: {
                solar: () => import('@iconify-json/solar/icons.json').then(i => i.default)
            }
        })
    ],
    safelist: ['i-solar:share-circle-linear', 'i-solar:document-medicine-linear',
        'i-solar:file-text-outline', 'i-solar:settings-linear',
        'i-solar:user-outline', 'i-solar:shield-user-linear'],
    // rules:[
    //     [/^m-([.\d]+)$/, ([_, num]) => ({ margin: `${num}px` })],
    //     [/^mt-([.\d]+)$/, ([_, num]) => ({ 'margin-top': `${num}px` })]
    // ]
})