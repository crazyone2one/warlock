import {
    create,
    NButton,
    NCard,
    NDataTable,
    NDropdown,
    NFlex,
    NForm,
    NFormItem,
    NFormItemGi,
    NGrid,
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
    NSelect,
    NSwitch,
    NTabPane,
    NTabs,NDrawer,NDrawerContent
} from 'naive-ui'

export const naive = create({
    components: [NButton, NLayout, NLayoutSider, NLayoutHeader, NLayoutContent, NLayoutFooter, NResult, NMenu, NFlex,
        NIcon, NDropdown, NCard, NForm, NFormItem, NInput, NDataTable, NModal, NSwitch, NSelect, NTabs, NTabPane, NGrid, NFormItemGi, NDrawer, NDrawerContent]
})