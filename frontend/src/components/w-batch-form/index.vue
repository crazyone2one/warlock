<script setup lang="ts">
import type {FormItemModel, FormMode} from "/@/components/w-batch-form/types.ts";
import {ref, unref, watchEffect} from "vue";
import {type FormInst, NScrollbar} from "naive-ui";
import {scrollIntoView} from "/@/utils/dom.ts";
import {VueDraggable} from "vue-draggable-plus";
import {useI18n} from "vue-i18n";

export interface BatchFormProps {
  models: FormItemModel[];
  formMode: FormMode;
  addText?: string;
  maxHeight?: string;
  defaultVals?: any[]; // 当外层是编辑状态时，可传入已填充的数据
  isShowDrag?: boolean; // 是否可以拖拽
  formWidth?: string; // 自定义表单区域宽度
  showEnable?: boolean; // 是否显示启用禁用switch状态
  hideAdd?: boolean; // 是否隐藏添加按钮
  addToolTip?: string;
  enableType?: 'circle' | 'round' | 'line';
}

const props = withDefaults(defineProps<BatchFormProps>(), {
  maxHeight: '30vh',
  isShowDrag: false,
  hideAdd: false,
  enableType: 'line',
});

const emit = defineEmits(['change']);
const defaultForm = {
  list: [] as Record<string, any>[],
};
const {t} = useI18n()
const form = ref<Record<string, any>>({list: [...defaultForm.list]});
const formRef = ref<FormInst | null>(null);
const formItem: Record<string, any> = {};
const getFormResult = () => {
  return unref<Record<string, any>[]>(form.value.list);
}
const formValidate = (cb: (res?: Record<string, any>[]) => void, isSubmit = true) => {
  formRef.value?.validate(errors => {
    if (errors) {
      scrollIntoView(document.querySelector('.n-form-item-feedback'), {block: 'center'});
      return;
    }
    if (typeof cb === 'function') {
      if (isSubmit) {
        cb(getFormResult());
        return;
      }
      cb();
    }
  })
}
const addField = () => {
  const item = [{...formItem}];
  if (item[0]) {
    item[0].type = [];
  }
  formValidate(() => {
    form.value.list.push(...item); // 序号自增，不会因为删除而重复
  }, false);
}
const removeField = (i: number) => {
  form.value.list.splice(i, 1);
}
const resetForm = () => {
  form.value.list = [...defaultForm.list];
};
watchEffect(() => {
  props.models.forEach((e) => {
    // 默认填充表单项
    let value: string | number | boolean | string[] | number[] | undefined;
    if (e.type === 'inputNumber') {
      value = undefined;
    } else if (e.type === 'tagInput') {
      value = [];
    } else {
      value = e.defaultValue;
    }
    formItem[e.field] = value;
    if (props.showEnable) {
      // 如果有开启关闭状态，将默认禁用
      formItem.enable = false;
    }
    // 默认填充表单项的子项
    e.children?.forEach((child) => {
      formItem[child.field] = child.type === 'inputNumber' ? null : child.defaultValue;
    });
  });
  form.value.list = [{...formItem}];
  if (props.defaultVals?.length) {
    // 取出defaultVals的表单 field
    form.value.list = props.defaultVals.map((e) => e);
  }
});
defineExpose({
  formValidate,
  getFormResult,
  resetForm,
  // setFields,
});
</script>

<template>
  <n-form ref="formRef" :model="form" size="small">
    <div class="mb-[16px] overflow-y-auto rounded-[4px] border border-[#434552] p-[12px]"
         :style="{ width: props.formWidth || '100%' }">
      <n-scrollbar class="overflow-y-auto" :style="{ 'max-height': props.maxHeight }">
        <VueDraggable v-model="form.list"
                      ghost-class="ghost"
                      drag-class="dragChosenClass"
                      :disabled="!props.isShowDrag"
                      :force-fallback="true"
                      :animation="150"
                      handle=".dragIcon">
          <div v-for="(element, index) in form.list"
               :key="`${element.field}${index}`"
               class="draggableElement gap-[8px] pr-[8px]"
               :class="[props.isShowDrag ? 'cursor-move' : '']">
            <div v-if="props.isShowDrag" class="dragIcon ml-[8px] mr-[8px] pt-[8px]">
              <div class="block text-[16px] i-solar:maximize-square-2-outline"/>
            </div>
            <n-form-item v-for="model of props.models" :key="`${model.field}${index}`"
                         :path="`list[${index}].${model.field}`"
                         :class="index > 0 ? 'hidden-item' : 'mb-0 flex-1'"
                         :rule="model.rules?.map(e=>{
                            if(e.notRepeat){
                              return {
                                validator(_rule: any, value: string) {
                                  if(!value) return
                                  for (let i = 0; i < form.list.length; i++) {
                                    if (i !== index && form.list[i][model.field].trim() === value) {
                                        return new Error(t(e.message as string || ''));
                                    }
                                  }
                                }
                              };
                            }
                            return e;
                          })"
                         :show-require-mark="index===0 && model.rules && model.rules.filter(e=>e.required)?.length>0">
              <template #label>
                <div class="inline-flex flex-row">
                  <div>{{ index === 0 && model.label ? t(model.label) : '' }}</div>
                </div>
              </template>
              <n-input v-if="model.type === 'input'" v-model:value="element[model.field]"
                       class="flex-1"
                       :placeholder="t(model.placeholder || '')"
                       :maxlength="model.maxLength || 255"
                       :disabled="model.disabled"
                       @change="emit('change')"/>
              <n-select v-else-if="model.type === 'select'" v-model:value="element[model.field]"
                        class="flex-1"
                        :placeholder="t(model.placeholder || '')"
                        :options="model.options"
                        :disabled="model.disabled"
                        @change="emit('change')"/>
            </n-form-item>
            <div v-if="showEnable">
              <n-switch v-model:value="element.enable" class="mt-[8px]"
                        :style="{ 'margin-top':  !props.isShowDrag ? '36px' : '' }"
                        size="small" @change="emit('change')"/>
            </div>
            <div
                v-if="!props.hideAdd"
                v-show="form.list.length > 1"
                :class="[
                'flex',
                'h-[32px]',
                'w-[32px]',
                'cursor-pointer',
                'items-center',
                'justify-center',
                'rounded',
              ]"
                :style="{ 'margin-top':  !props.isShowDrag ? '20px' : '' }"
                @click="removeField(index)"
            >
              <div class="i-solar:minus-circle-linear text-[18px]"/>
            </div>
          </div>
        </VueDraggable>
      </n-scrollbar>
      <div v-if="props.formMode === 'create' && !props.hideAdd" class="w-full">
        <n-button class="px-0" text @click="addField">
          <template #icon>
            <div class="i-solar:add-circle-linear text-[18px]"/>
          </template>
          {{ t(props.addText || 'common.add') }}
        </n-button>
      </div>
    </div>
  </n-form>
</template>

<style scoped>
.hidden-item {
  @apply mb-0 flex-1;

  .n-form-item-blank {
    @apply !hidden;
  }
}

.draggableElement {
  @apply flex w-full items-start justify-between rounded;
}

.ghost {
  border: 1px dashed #de92f8;
  background-color: #cd3bff;
  @apply rounded;
}

.dragChosenClass {
  background: #242633;
  opacity: 1 !important;
  @apply rounded;

  .minus {
    margin: 0 !important;
  }
}
</style>