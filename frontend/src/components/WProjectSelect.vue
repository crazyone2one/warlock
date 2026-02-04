<script setup lang="ts">
import {ref, watchEffect} from "vue";
import {useAppStore, useUserStore} from "/@/store";
import {projectApi} from "/@/api/methods/project.ts";
import router from "/@/router";
import EditProjectModal from "/@/views/setting/project/EditProjectModal.vue";

const show = ref(false)
const showEditProjectModal = ref(false)
const appStore = useAppStore()
const userStore = useUserStore()
const handleAdd = () => {
  showEditProjectModal.value = true
}
const handleUpdateValue = (value: string) => {
  projectApi.switchProject({projectId: value, userId: userStore.id || ''})
      .then(() => {
        window.$message.success('切换项目成功')
        router.replace({
          name: router.currentRoute.value.name,
          query: {
            pId: value,
          }
        })
      })
}
const handleCancel = async () => {
  showEditProjectModal.value = false
  appStore.initProjectList()
}
watchEffect(() => {
  appStore.initProjectList()
})
</script>

<template>
  <n-select
      v-model:show="show"
      v-model:value="appStore.currentProjectId"
      filterable
      placeholder="选择项目"
      :options="appStore.projectList"
      label-field="name"
      value-field="id"
      class="w-[240px]"
      @update:value="handleUpdateValue"
  >
    <template v-if="show" #arrow>
      <div class="i-solar:magnifer-linear"/>
    </template>
    <template #action>
      <n-button text type="info" @click="handleAdd">
        <template #icon>
          <n-icon>
            <div class="i-solar:add-circle-linear"/>
          </n-icon>
        </template>
        添加项目
      </n-button>
    </template>
  </n-select>
  <edit-project-modal v-model:show-modal="showEditProjectModal" @cancel="handleCancel"/>
</template>

<style scoped>

</style>