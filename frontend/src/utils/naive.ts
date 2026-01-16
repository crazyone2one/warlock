import {
    create,
    NButton,
    NCard,
    NDataTable,
    NDropdown,
    NFlex,
    NForm,
    NFormItem,
    NIcon,
    NInput,
    NLayout,
    NLayoutContent,
    NLayoutFooter,
    NLayoutHeader,
    NLayoutSider,
    NMenu,
    NModal,
    NResult,
    NSwitch
} from 'naive-ui'

export const naive = create({
    components: [NButton, NLayout, NLayoutSider, NLayoutHeader, NLayoutContent, NLayoutFooter, NResult, NMenu, NFlex,
        NIcon, NDropdown, NCard, NForm, NFormItem, NInput, NDataTable, NModal, NSwitch]
})