import apiClient from "./apiClient";
import { isAxiosError } from "axios";
import type { FolderdRequest, ErrorResponse, FolderResponse,  } from '@/types'



/**
 * A type guard to check if an object conforms to the ErrorResponse interface.
 * @param data The object to check.
 * @returns True if the object is a valid ErrorResponse, false otherwise.
 */
const isApiError = (data: any): data is ErrorResponse => {
  return data && typeof data.message === 'string' && typeof data.status === 'number';
};

/**
 * Handles API errors in a centralized and type-safe way.
 * It inspects the error to see if it's an Axios error and if the
 * response body matches our backend's ErrorResponse DTO.
 *
 * @param error The error object caught in the catch block (typed as unknown for safety).
 * @param defaultMessage A fallback message if a specific error message can't be parsed.
 * @throws {Error} Throws a new, cleaned-up error with a user-friendly message.
 */
const handleApiError = (error: unknown, defaultMessage: string): never => {
  if (isAxiosError(error) && error.response) {
    // Check if the response data matches our backend's error contract
    if (isApiError(error.response.data)) {
      // Use the specific error message from the backend
      throw new Error(error.response.data.message);
    }
  }
  // For all other errors (network issues, non-axios errors, etc.), throw the default.
  throw new Error(defaultMessage);
};


/**
 * Sends the selected project description to the backend to generate the project directory.
 * @param folderRequst The user's description of the project.
 * @returns A promise that resolves to the ScaffoldResult containing the zip file.
 */
export async function generateProjectDirectory(folderRequst: FolderdRequest): Promise<FolderResponse> {
  try {
    const response = await apiClient.post(
      '/folders/project-directory',
      folderRequst, // Send both as payload
      {
        headers: {
          'Content-Type': 'application/json', // The request body is JSON
        }
      }
    );

    // The data is already parsed JSON, so just return it.
    return response.data;
  } catch (error: unknown) {
    return handleApiError(error, "Failed to generate project. Please check the backend connection.");
  }
};

