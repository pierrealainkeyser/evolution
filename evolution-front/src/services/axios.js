import axios from 'axios';

const url = process.env.VUE_APP_BACKEND;
const axiosConfig = {
  withCredentials: true
}
if (url) {
  axiosConfig.baseURL = url;
  axiosConfig.headers = {
    "X-CSRF-TOKEN": null
  }
}

export default axios.create(axiosConfig);
