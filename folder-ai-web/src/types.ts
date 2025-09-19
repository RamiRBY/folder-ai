
export interface FolderdRequest {
    prompt: string;
    conversationId: string;
    chatOptions?: FolderModelOptions;
}

export interface FolderModelOptions {
    model: string,
    temperature: number
}

export interface FolderResponse {
    directoryStructure: DirectoryStructure 
    conversationId: string;
    metaData: MetaData;
}
export interface DirectoryStructure {
    projectName: string;
    tree: string;
}

export interface Usage {
    prompt_tokens: number;
    generation_tokens: number;
    total_tokens: number;
}

export interface MetaData {
    model: string;
    usage: Usage;
}


export interface ErrorResponse {
    timestamp: string;
    status: number;
    error: string;
    message: string;
}


