# AI Coding Agent Rules

## 前端开发规范

### UI 组件使用优先级

1. **优先使用 uview-pro 组件库**
   - 前端页面开发时，优先使用 `uview-pro` 组件库的组件
   - 组件目录参考：`anise-ui/node_modules/uview-pro`
   - 常用组件包括但不限于：
     - `u-view` - 基础视图组件
     - `u-cell` - 单元格组件
     - `u-button` - 按钮组件
     - `u-input` - 输入框组件
     - `u-tabs` - 标签页组件
     - `u-list` - 列表组件
     - `u-empty` - 空状态组件
     - `u-loading-icon` - 加载图标组件
     - `u-status-bar` - 状态栏组件
     - `u-safe-bottom` - 安全区域组件

2. **组件使用原则**
   - 优先使用 uview-pro 提供的组件，而非原生组件
   - 使用前先查阅 node_modules/uview-pro 目录下的组件文档
   - 保持组件风格一致性

3. **样式规范**
   - 使用 SCSS/CSS 变量统一主题色
   - 遵循项目的设计规范和色彩体系
   - 响应式设计适配不同屏幕尺寸

4. **主题配色规范**
   - 页面设计必须使用统一的主题配色方案
   - 主题文件独立管理：`anise-ui/src/styles/theme.scss` 作为主题入口，`anise-ui/src/styles/themes/` 存放各主题配置
   - `uni.scss` 通过 `@import "./styles/theme.scss"` 引入主题变量，**禁止直接在 uni.scss 中定义颜色变量**
   - 所有颜色必须通过 SCSS 变量引用，禁止硬编码颜色值
   - 主题色包含：主色、成功色、警告色、错误色、文字色、背景色六大类
   - 主题切换方式：修改 `theme.scss` 中的 `@import` 语句，引入不同主题文件
   - 主题文件命名规范：`theme-{theme-name}.scss`（如 `theme-default.scss`、`theme-orange.scss`）

5. **脚本语言规范**
   - 所有 Vue 页面和组件的 `<script>` 标签必须声明 `lang="ts"`
   - 示例：`<script setup lang="ts">`
   - 确保 TypeScript 语法可以正常使用，包括类型标注、类型断言等

6. **图片路径规范**
   - 所有图片 URL 必须使用 `src/utils/image.ts` 中的 `getImageUrl` 方法进行处理
   - 该方法会自动判断 URL 是否以 `http://` 或 `https://` 开头
   - 如果不是完整 URL，则自动拼接后端图片服务的 BASE_URL
   - 示例：`getImageUrl(user.avatar)`

7. **多端适配规范**
   - 所有页面必须同时适配微信小程序和 H5 页面
   - 使用 uni-app 的条件编译语法处理平台差异：
     - `<!-- #ifdef MP-WEIXIN -->` 微信小程序专属代码
     - `<!-- #ifdef H5 -->` H5 专属代码
     - `<!-- #ifndef MP-WEIXIN -->` 非微信小程序代码
   - 避免使用只在特定平台生效的组件或 API
   - 样式使用 rpx 单位，确保在不同设备上显示一致

## 代码质量规范

### Java 代码
- 使用有意义的变量和方法命名
- 添加必要的注释说明业务逻辑
- 使用 Lombok 减少样板代码
- 单表查询使用 MyBatis-Plus 的 LambdaQueryWrapper 进行查询条件构建
- 如果需要多表关联查询，可以在resources下的mapper中的.xml 添加相关查询方法
- **代码复用原则**：开发新功能前，先检查是否已有相同或相似的方法、类或组件，优先复用现有代码；后端接口和同业务类型的实体类能复用的尽量复用，避免重复实现

### 数据库操作
- 使用逻辑删除而非物理删除
- 批量操作时注意事务控制
- 敏感数据加密存储
- **SQL优化原则**：能通过一次SQL查询获取的数据，不要写多次查询。复杂统计查询应写入Mapper的XML文件中，使用聚合函数（如SUM、COUNT、CASE WHEN等）一次性获取多个统计结果，减少数据库连接开销
- **减少查询次数**：尽量使用 JOIN 查询、子查询或批量操作减少数据库交互次数；避免在循环中进行数据库查询

### 方法复用规范
- **优先复用**：实现新功能前，先搜索项目中是否已存在相同或相关的方法
- **方法命名**：保持方法命名一致性，便于查找和复用
- **服务层方法**：在 Service 层添加方法前，检查同模块或相关模块是否已有类似方法
- **Mapper层方法**：在 Mapper 中添加新查询前，检查是否已有可复用的 SQL 查询

### 接口设计
- RESTful API 设计规范
- 统一的响应格式（R<T>）
- 必要的参数校验

## 工具类使用规范

### uview-pro 时间工具类

1. **优先使用 uview-pro 的时间工具类**
   - 时间格式化应优先使用 `$u.timeFormat()` 方法
   - 文档参考：https://uviewpro.cn/zh/tools/time.html
   - `$u` 已配置自动导入，无需手动引入
   - 自动导入配置位置：`anise-ui/vite.config.ts` 的 AutoImport 插件

2. **时间格式化示例**
   ```javascript
   // 在模板中使用
   <view>{{ $u.timeFormat(timestamp, 'yyyy年mm月dd日') }}</view>
   
   // 在脚本中使用
   import { $u } from 'uview-pro'
   const time = $u.timeFormat(timestamp, 'yyyy-mm-dd hh:MM:ss')
   ```

3. **格式化模板说明**
   | 格式 | 说明 | 示例 |
   |------|------|------|
   | yyyy | 四位年份 | 2024 |
   | mm | 两位月份 | 01-12 |
   | dd | 两位日期 | 01-31 |
   | hh | 两位小时 | 00-23 |
   | MM | 两位分钟 | 00-59 |
   | ss | 两位秒数 | 00-59 |

4. **优势**
   - 自动处理日期对象、时间戳、ISO字符串等多种输入格式
   - 内置补零处理，无需手动编写 pad 函数
   - 统一的时间处理方式，减少代码重复

5. **代码对比**
   - 优化前（手动实现）：
   ```javascript
   const pad = (n) => (n < 10 ? '0' + n : n)
   const startStr = `${date.getMonth() + 1}月${date.getDate()}日 ${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(date.getSeconds())}`
   ```
   - 优化后（使用 uview-pro）：
   ```javascript
   const startStr = $u.timeFormat(date, 'mm月dd日 hh:MM:ss')
   ```

## 安全规范

- 用户敏感操作需登录验证
- SQL 注入防护使用参数化查询
- XSS 防护对输出内容进行转义
- 接口权限校验遵循最小权限原则
