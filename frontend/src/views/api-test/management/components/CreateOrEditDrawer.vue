<script setup lang="ts">
import {NButton, NDrawer, NDrawerContent, NInputGroup} from "naive-ui";
import {useI18n} from "vue-i18n";
import {ref} from "vue";
import {RequestComposition} from "/@/utils/api-enum.ts";

const {t} = useI18n()
const active = defineModel<boolean>('active', {type: Boolean, default: false});
const isUrlError = ref(false);
</script>

<template>
  <n-drawer v-model:show="active" :width="800">
    <n-drawer-content>
      <template #header>
        {{ t('apiTestManagement.newApi') }}
      </template>
      <div class="request-composition flex h-full flex-col">
        <div class="mb-[8px] px-[18px] pt-[8px]">
          <div class="flex flex-wrap  justify-between gap-[12px]">
            <div class="flex flex-1 flex-wrap items-center gap-[16px]">
              <!--              <n-select class="w-[100px]"/>-->
              <n-input-group>
                <n-select class="w-[120px]" :placeholder="t('common.pleaseSelect')"/>
                <n-input :maxlength="255" :placeholder="t('apiTestDebug.definitionUrlPlaceholder')" clearable
                         class="flex-1 hover:z-10"/>
              </n-input-group>
              <div v-if="isUrlError" class="url-input-tip">
                <span>{{ t('apiTestDebug.apiUrlRequired') }}</span>
              </div>
            </div>
            <n-radio-group>
              <n-radio-button value="definition">{{ t('apiTestManagement.definition') }}</n-radio-button>
              <n-radio-button value="debug">{{ t('apiTestManagement.debug') }}</n-radio-button>
            </n-radio-group>
          </div>
          <div class="w-full">
            <n-input :placeholder="t('apiTestManagement.apiNamePlaceholder')"/>
          </div>
        </div>
        <div class="flex-1">
          <n-tabs :bar-width="28" type="line" class="custom-tabs">
            <n-tab-pane :name="RequestComposition.BASE_INFO" :tab="t('apiScenario.baseInfo')">
              Wonderwall
            </n-tab-pane>
            <n-tab-pane :name="RequestComposition.HEADER" :tab="t('apiTestDebug.header')">
              Hey Jude
            </n-tab-pane>
            <n-tab-pane :name="RequestComposition.BODY" :tab="t('apiTestDebug.body')">
              七里香
            </n-tab-pane>
            <n-tab-pane :name="RequestComposition.PRECONDITION" :tab="t('apiTestDebug.prefix')">
              七里香
            </n-tab-pane>
            <n-tab-pane :name="RequestComposition.POST_CONDITION" :tab="t('apiTestDebug.post')">
              七里香
            </n-tab-pane>
            <n-tab-pane :name="RequestComposition.SETTING" :tab="t('apiTestDebug.setting')">
              七里香
            </n-tab-pane>
          </n-tabs>
          <div>

          </div>
        </div>
      </div>
      <template #footer>
        <n-button @click="active = false">Footer</n-button>
      </template>
    </n-drawer-content>
  </n-drawer>
</template>

<style scoped>
.url-input-tip {
  @apply w-full;

  margin-top: -14px;
  padding-left: 226px;
  font-size: 12px;

  line-height: 16px;
}
</style>