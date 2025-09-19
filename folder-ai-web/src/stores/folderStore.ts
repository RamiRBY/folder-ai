import { defineStore } from 'pinia';
import { ref } from 'vue';
import { generateProjectDirectory as apiGenerateFolder } from '@/services/folderService';
import type { FolderdRequest, FolderResponse } from '@/types';

export const useFolderStore = defineStore('folder', () => {
    // --- STATE ---
    const isLoading = ref<boolean>(false);
    const folderResult = ref<FolderResponse | null>(null);
    const errorMessage = ref<string | null>(null);
    const conversationId = ref<string | null>(null);

    // --- ACTIONS ---

    async function generateFolder(userPrompt: string) {
        isLoading.value = true;
        errorMessage.value = null;
        folderResult.value = null;


        try {
            const request: FolderdRequest = {
                prompt: userPrompt,
                conversationId: conversationId.value!,
            };

            // Pass both the structure and description to the API service
            const result = await apiGenerateFolder(request);
            folderResult.value = result;
            conversationId.value= folderResult.value.conversationId!;
        } catch (err) {
            handleError(err, 'An unknown error occurred during project generation.');
        } finally {
            isLoading.value = false;
        }
    }

    // --- NEW ACTION ---
    function resetFolder() {
        folderResult.value = null;
        errorMessage.value = null;
    }


    // --- PRIVATE HELPER ---
    function handleError(err: unknown, defaultMessage: string) {
        if (err instanceof Error) {
            errorMessage.value = err.message;
        } else {
            errorMessage.value = defaultMessage;
        }
        console.error(err);
    }

    function reset() {
        isLoading.value = false;
        errorMessage.value = null;
        folderResult.value = null;
    }


    return {
        // State
        isLoading,
        folderResult,
        errorMessage,
        conversationId,
        // Actions
        generateFolder,
        resetFolder,
        reset,
    };
});
