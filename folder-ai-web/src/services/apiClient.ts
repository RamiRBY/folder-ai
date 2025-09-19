/* import axios, { type AxiosInstance } from "axios";

// Get the API base URL from environment variables
//const baseURL = import.meta.env.VITE_API_BASE_URL;

const apiClient: AxiosInstance = axios.create({
  baseURL: '/api/v1/scaffolds', // Your Spring Boot backend URL
  headers: {
    'Content-Type': 'application/json'
  }
})

export default apiClient;
 */

import axios, { type AxiosInstance } from 'axios';

const apiClient: AxiosInstance = axios.create({
  // Use the common root path for all API v1 calls
  baseURL: '/api/v1',
  headers: {
    'Content-Type': 'application/json',
  },
});

export default apiClient;
