<script setup lang="ts">
import {  RouterView } from 'vue-router'
</script>
<template>
  <div class="flex flex-col min-h-screen bg-slate-950 text-gray-300 font-sans">
    
    <main class="flex-grow">
      <RouterView />
    </main>

    <footer class="p-4 text-center text-slate-500">
      &copy; 2025 Folder AI.
    </footer>

  </div>
</template>












