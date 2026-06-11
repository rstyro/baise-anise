import { createSSRApp } from "vue";
import { createPinia } from "pinia";
import piniaPluginPersistedstate from "pinia-plugin-persistedstate";
import uViewPro, { httpPlugin } from 'uview-pro'
import App from "./App.vue";
import globalPlugins from "@/utils/global/index";
import { httpInterceptor, httpRequestConfig } from '@/utils/http.interceptor'

export function createApp() {
  const app = createSSRApp(App);
  const pinia = createPinia();
  pinia.use(piniaPluginPersistedstate);
  app.use(pinia);

  app.use(uViewPro);

  // 注册http插件
  app.use(httpPlugin, {
    interceptor: httpInterceptor,
    requestConfig: httpRequestConfig
  })

  app.use(globalPlugins);


  return {
    app,
    pinia,
  };
}
