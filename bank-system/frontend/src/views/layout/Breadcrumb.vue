<template>
  <div class="breadcrumb">
    <template v-for="(item, index) in breadcrumbs" :key="item.path">
      <span v-if="index > 0" class="separator">/</span>
      <span
        class="crumb"
        :class="{ active: index === breadcrumbs.length - 1 }"
        @click="navigate(item)"
      >
        {{ item.title }}
      </span>
    </template>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

interface Crumb {
  path: string
  title: string
}

const breadcrumbs = computed<Crumb[]>(() => {
  const matched = route.matched.filter(r => r.name && r.name !== 'Layout')
  if (matched.length === 0) return []
  return matched.map(r => ({
    path: r.path,
    title: (r.meta.title as string) || (r.name as string)
  }))
})

function navigate(item: Crumb) {
  if (item.path !== route.path) {
    router.push(item.path)
  }
}
</script>

<style scoped>
.breadcrumb {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  flex-shrink: 0;
}
.separator {
  color: var(--text-muted);
  font-size: 11px;
}
.crumb {
  color: var(--text-muted);
  cursor: pointer;
  transition: color 0.2s;
  white-space: nowrap;
}
.crumb:hover {
  color: var(--neon-cyan);
}
.crumb.active {
  color: var(--neon-cyan);
  text-shadow: 0 0 6px rgba(0, 212, 255, 0.3);
}
</style>
