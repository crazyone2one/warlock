<script setup lang="ts">
import {NTag} from "naive-ui";

import {computed, useAttrs} from "vue";
import {characterLimit} from "/@/utils";

const {
  showNum = 2, nameKey = 'name', size = 'small', tagPosition = 'top', tagList = [],
  showTable = false, isStringTag = false, allowEdit = false
} = defineProps<{
  tagList: Array<any>;
  showNum?: number;
  nameKey?: string;
  isStringTag?: boolean; // 是否是字符串数组的标签
  size?: 'tiny' | 'small' | 'medium' | 'large';
  allowEdit?: boolean;
  showTable?: boolean;
  tagPosition?:
      | 'top'
      | 'tl'
      | 'tr'
      | 'bottom'
      | 'bl'
      | 'br'
      | 'left'
      | 'lt'
      | 'lb'
      | 'right'
      | 'rt'
      | 'rb'
      | undefined; // 提示位置防止窗口抖动
}>()
const emit = defineEmits<{
  (e: 'click'): void;
}>();

const attrs = useAttrs();
const filterTagList = computed(() => {
  return (tagList || []).filter((item: any) => item) || [];
});
const showTagList = computed(() => {
  // 在表格展示则全部展示，按照自适应去展示标签个数
  if (showTable) {
    return filterTagList.value;
  }
  return filterTagList.value.slice(0, showNum);
});
const tagsTooltip = computed(() => {
  return filterTagList.value.map((e: any) => (isStringTag ? e : e[nameKey])).join('，');
});

function getTagContent(tag: { [x: string]: any }) {
  const tagContent = (isStringTag ? tag : tag[nameKey]) || '';
  if (showTable) {
    return tagContent.length > 16 ? characterLimit(tagContent, 9) : tagContent;
  }
  return tagContent;
}
</script>

<template>
  <div v-if="showTagList.length"
       @click="emit('click')">
    <n-tag v-for="tag in showTagList" :key="tag.id"
           v-bind="attrs" :size="size" :bordered="false"
           class="cursor-pointer">
      {{ getTagContent(tag) }}
    </n-tag>
    <n-tooltip trigger="hover" :placement="tagPosition">
      <template #trigger>
        <n-tag v-show="tagList.length > showNum" :size="size" v-bind="attrs">
          + {{ tagList.length - showNum }}
        </n-tag>
      </template>
      {{ tagsTooltip }}
    </n-tooltip>
  </div>
  <div v-else :class="`tag-group-class ${allowEdit ? 'min-h-[24px] cursor-pointer' : ''}`" @click="emit('click')">
    -
  </div>
</template>

<style scoped>
</style>