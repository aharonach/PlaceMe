import axios from 'axios';
export const BASE_URL = 'http://localhost:8080/';
export const CSV_CONTENT_TYPE = 'text/csv;charset=UTF-8';

export default axios.create({
    baseURL: BASE_URL,
    headers: { 'Content-Type': 'application/json' }
});