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

<!-- <template>
  <view class="app">
    <slot />
    <u-toast global />
    <u-modal global />
  </view>
</template> -->

<style>
	@import "static/css/iconfont.css";
</style>
