<template>
  <el-tag :type="meta.tag" size="small" :effect="effect" round>{{ meta.label }}</el-tag>
</template>

<script setup>
import { computed } from 'vue'
import { ITEM_TYPES, ITEM_STATUS, CLAIM_STATUS, MATCH_STATUS } from '../constants/options'

const props = defineProps({
  // 字典类型：itemType / itemStatus / claimStatus / matchStatus
  dict: { type: String, required: true },
  value: { type: String, default: '' },
  effect: { type: String, default: 'light' }
})

const DICTS = { itemType: ITEM_TYPES, itemStatus: ITEM_STATUS, claimStatus: CLAIM_STATUS, matchStatus: MATCH_STATUS }

const meta = computed(() => DICTS[props.dict]?.[props.value] || { label: props.value || '-', tag: 'info' })
</script>
