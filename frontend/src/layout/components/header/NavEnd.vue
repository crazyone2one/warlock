<script setup lang="ts">
import {h} from 'vue'
import {NAvatar, NText} from 'naive-ui'

const renderCustomHeader = () => {
  return h(
      'div',
      {
        style: 'display: flex; align-items: center; padding: 8px 12px;'
      },
      [
        h(NAvatar, {
          round: true,
          style: 'margin-right: 12px;',
          src: 'https://07akioni.oss-cn-beijing.aliyuncs.com/demo1.JPG'
        }),
        h('div', null, [
          h('div', null, [h(NText, {depth: 2}, {default: () => '打工仔'})]),
          h('div', {style: 'font-size: 12px;'}, [
            h(
                NText,
                {depth: 3},
                {default: () => '毫无疑问，你是办公室里最亮的星'}
            )
          ])
        ])
      ]
  )
}
const dropOptions = [
  {
    key: 'header',
    type: 'render',
    render: renderCustomHeader
  },
  {
    key: 'header-divider',
    type: 'divider'
  },
  {
    label: '用户资料',
    key: 'profile',
    icon: () => h('div', {class: 'i-solar:user-id-linear'})
  },
  {
    label: '退出登录',
    key: 'logout',
    icon: () => h('div', {class: 'i-solar:logout-linear'})
  },
]
</script>

<template>
  <n-flex>
    <n-button text>Button1</n-button>
    <n-button text>Button2</n-button>
    <n-button text>
      <template #icon>
        <n-icon>
          <div class="i-solar:bell-line-duotone"/>
        </n-icon>
      </template>
    </n-button>
    <n-dropdown trigger="click" :options="dropOptions">
      <n-button text>
        <template #icon>
          <n-icon>
            <div class="i-solar:user-speak-linear"/>
          </n-icon>
        </template>
      </n-button>
    </n-dropdown>
  </n-flex>
</template>

<style scoped>
</style>