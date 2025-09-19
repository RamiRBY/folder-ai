<script setup lang="ts">
import { ref, watch, nextTick } from 'vue';
import { useFolderStore } from '@/stores/folderStore';

// The Message interface is updated to handle all response types
interface Message {
  id: number;
  sender: 'user' | 'ai' | 'error';
  content: string;
  // Optional properties for the structured AI response
  projectName?: string;
  tree?: string;
}

const chatWindow = ref<HTMLDivElement | null>(null);
const store = useFolderStore();
const newMessage = ref('');
const messages = ref<Message[]>([]);
const copiedState = ref<Record<number, boolean>>({});

// --- Functions ---

const scrollToBottom = async () => {
  await nextTick();
  if (chatWindow.value) {
    chatWindow.value.scrollTop = chatWindow.value.scrollHeight;
  }
};

const handleSubmit = () => {
  const trimmedMessage = newMessage.value.trim();
  if (!trimmedMessage) return;

  messages.value.push({
    id: Date.now(),
    content: trimmedMessage,
    sender: 'user'
  });
  store.generateFolder(trimmedMessage);
  newMessage.value = '';
  scrollToBottom();
};

// --- New function to handle copying the generated tree ---
const copyToClipboard = (text: string, id: number) => {
  navigator.clipboard.writeText(text);
  copiedState.value[id] = true;
  setTimeout(() => {
    copiedState.value[id] = false;
  }, 2000); // Reset after 2 seconds
};

// --- Watchers ---

watch(() => store.folderResult, (newResult) => {
  if (newResult) {
    messages.value.push({
      id: Date.now() + 1,
      sender: 'ai',
      content: 'Here is the project structure I generated for you:',
      projectName: newResult.directoryStructure.projectName,
      tree: newResult.directoryStructure.tree,
    });
    scrollToBottom();
  }
});

watch(() => store.errorMessage, (newError) => {
  if (newError) {
    messages.value.push({
      id: Date.now() + 2,
      sender: 'error',
      content: newError,
    });
    scrollToBottom();
  }
});
</script>

<template>
  <div class="flex flex-col h-screen bg-gradient-to-br from-slate-950 to-slate-900 text-white font-sans">
    
    <header class="text-center p-4 sm:p-6 border-b border-slate-800/50">
      <div class="flex items-center justify-center space-x-3">
        <svg class="w-8 h-8 text-cyan-400" fill="none" stroke="currentColor" viewBox="0 0 24 24"
          xmlns="http://www.w3.org/2000/svg">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
            d="M14 10l-2 1m0 0l-2-1m2 1v2.5M20 7l-2 1m2-1l-2-1m2 1v2.5M14 4l-2-1-2 1M4 7l2 1M4 7l2-1M4 7v2.5M12 21.5v-2.5M12 18.5l-2 1m2-1l2 1m-2-1v-2.5m-8-4l2 1m-2-1l2-1m-2 1V14m16-4.5l-2 1m-2-1l-2 1m2-1V14">
          </path>
        </svg>
        <h1 class="text-2xl font-bold text-white">Folder AI</h1>
      </div>
      <p class="text-slate-400 text-sm mt-2">Describe your project to generate a folder structure.</p>
    </header>

    <div class="w-full max-w-4xl mx-auto flex-grow flex flex-col">
      
      <div ref="chatWindow" class="flex-grow p-4 sm:p-6 space-y-6 overflow-y-auto chat-scrollbar">
        <div class="flex items-start gap-3">
          <div class="w-8 h-8 rounded-full bg-slate-700 flex items-center justify-center flex-shrink-0">
             🤖
          </div>
          <div class="bg-slate-800 border border-slate-700 text-slate-200 rounded-lg rounded-tl-none p-3 max-w-[85%]">
            <p>Hello! How can I help you structure your project today?</p>
          </div>
        </div>

        <div v-for="message in messages" :key="message.id" class="flex items-start gap-3"
          :class="{ 'justify-end': message.sender === 'user' }">
          
          <template v-if="message.sender !== 'user'">
            <div class="w-8 h-8 rounded-full bg-slate-700 flex items-center justify-center flex-shrink-0">
               {{ message.sender === 'ai' ? '🤖' : '⚠️' }}
            </div>
          </template>
          
          <div v-if="message.sender === 'user'" class="bg-blue-600 text-white rounded-lg rounded-br-none py-2 px-4 max-w-[85%] order-2">
            <p class="break-words">{{ message.content }}</p>
          </div>

          <div v-else-if="message.sender === 'ai'" class="bg-slate-800 border border-slate-700 text-slate-200 rounded-lg rounded-tl-none p-4 max-w-[85%]">
             <p class="font-bold text-white text-lg mb-2">{{ message.projectName }}</p>
             <div class="relative bg-slate-900/70 p-3 rounded-md">
               <button @click="copyToClipboard(message.tree!, message.id)" class="absolute top-2 right-2 text-xs bg-slate-700 hover:bg-slate-600 text-slate-300 font-semibold py-1 px-2 rounded-md transition-all">
                 {{ copiedState[message.id] ? 'Copied!' : 'Copy' }}
               </button>
               <pre class="font-mono text-sm whitespace-pre-wrap text-slate-300 pr-14">{{ message.tree }}</pre>
             </div>
          </div>
          
          <div v-else-if="message.sender === 'error'" class="bg-red-900/50 border border-red-700 text-red-200 rounded-lg rounded-tl-none p-3 max-w-[85%]">
             <p class="font-bold mb-1">An error occurred:</p>
             <p>{{ message.content }}</p>
          </div>
          
           <template v-if="message.sender === 'user'">
              <div class="w-8 h-8 rounded-full bg-slate-700 flex items-center justify-center flex-shrink-0 order-3">
                 👤
              </div>
          </template>

        </div>

        <div v-if="store.isLoading" class="flex items-start gap-3">
          <div class="w-8 h-8 rounded-full bg-slate-700 flex items-center justify-center flex-shrink-0">
            🤖
          </div>
          <div class="bg-slate-800 border border-slate-700 text-slate-400 rounded-lg rounded-tl-none py-2 px-4 max-w-[85%] italic">
            AI is thinking...
          </div>
        </div>
      </div>

      <div class="p-4 sm:p-6 border-t border-slate-800/50 bg-slate-950/50 backdrop-blur-sm">
        <form @submit.prevent="handleSubmit" class="flex items-center gap-3">
          <input
            v-model="newMessage"
            type="text"
            placeholder="e.g., a simple blog with Vue and Node"
            :disabled="store.isLoading"
            class="flex-grow bg-slate-800 border border-slate-700 rounded-full px-5 py-3 text-white placeholder-slate-500 focus:outline-none focus:ring-2 focus:ring-blue-500 disabled:opacity-50 transition-all"
          />
          <button
            type="submit"
            :disabled="store.isLoading || !newMessage.trim()"
            class="bg-blue-600 text-white font-bold rounded-full p-3 h-12 w-12 flex items-center justify-center hover:bg-blue-500 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 focus:ring-offset-slate-900 disabled:bg-slate-600 disabled:cursor-not-allowed transition-all"
          >
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" class="w-6 h-6">
              <path d="M3.478 2.405a.75.75 0 00-.926.94l2.432 7.905H13.5a.75.75 0 010 1.5H4.984l-2.432 7.905a.75.75 0 00.926.94 60.519 60.519 0 0018.445-8.986.75.75 0 000-1.218A60.517 60.517 0 003.478 2.405z" />
            </svg>
          </button>
        </form>
      </div>

    </div>
  </div>
</template>

<style>
.chat-scrollbar::-webkit-scrollbar {
  width: 8px;
}
.chat-scrollbar::-webkit-scrollbar-track {
  background: transparent;
}
.chat-scrollbar::-webkit-scrollbar-thumb {
  background-color: #475569; /* slate-600 */
  border-radius: 20px;
  border: 3px solid transparent;
  background-clip: content-box;
}
.chat-scrollbar::-webkit-scrollbar-thumb:hover {
  background-color: #64748b; /* slate-500 */
}
</style>