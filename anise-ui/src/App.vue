<script setup lang="ts">
import { onLaunch, onShow, onHide } from "@dcloudio/uni-app";

onLaunch(() => {
  console.log("App Launch");
});
onShow(() => {
  console.log("App Show");
});
onHide(() => {
  console.log("App Hide");
});
</script>

<script lang="ts">
export default {
  globalData: {
    // 记录需要刷新的页面
    refreshPages: {} as Record<string, boolean>
  }
}
</script>

<style lang="scss" >
//@import "uview-pro/index.scss";
@import "static/css/iconfont.css";

</style>
