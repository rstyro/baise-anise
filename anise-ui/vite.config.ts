import { defineConfig } from "vite";
import uni from "@dcloudio/vite-plugin-uni";

export default async () => {
  // 动态导入 ESM 模块
  const AutoImport = await import("unplugin-auto-import/vite").then(m => m.default || m);
  const Components = await import("unplugin-vue-components/vite").then(m => m.default || m);
  const path = await import("path").then(m => m.default || m);

  return defineConfig({
    plugins: [
      uni(),
      AutoImport({
        imports: [
          "vue",
          {
            "@dcloudio/uni-app": [
              "onLoad",
              "onShow",
              "onReady",
              "onHide",
              "onUnload",
              "onPullDownRefresh",
              "onReachBottom",
              "onShareAppMessage",
              "onPageScroll",
              "onTabItemTap",
            ],
          },
          {
            "@/api": ["userApi", "commonApi"],
          },
          {
            "@/utils/router": ["navigateTo", "redirectTo", "switchTab", "reLaunch", "navigateBack"],
          },
          {
            "uview-pro": ["$u"],
          },
        ],
        dts: "src/auto-imports.d.ts",
        eslintrc: {
          enabled: true,
          filepath: "./.eslintrc-auto-import.json",
        },
      }),
      Components({
        dirs: ["src/components"],
        resolvers: [
          (name: string) => {
            if (name.startsWith("c-")) {
              return {
                name: "default",
                from: path.resolve(__dirname, `src/components/${name}.vue`),
                sideEffects: null,
              };
            }
          },
        ],
        dts: "src/components.d.ts",
        deep: true,
        extensions: ["vue"],
      }),
    ],
    css: {
      preprocessorOptions: {
        scss: {
          additionalData: `@import "uview-pro/theme.scss";@import "@/uni.scss";`,
          api: 'modern-compiler',
           // 静默 import 和 legacy-js-api 弃用警告
          silenceDeprecations: ['import','legacy-js-api'],
        }
      }
    },
    resolve: {
      alias: {
        "@": path.resolve(__dirname, "src"),
      },
    }
  });
};
