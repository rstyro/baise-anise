# AI Coding Agent Rules

## 1. 前端开发规范

### 1.1 UI 组件使用优先级

1. **优先使用 uview-pro 组件库**
   - 前端页面开发时，优先使用 `uview-pro` 组件库的组件
   - 组件目录参考：`anise-ui/node_modules/uview-pro`
   - 常用组件包括但不限于：
     - `u-view` - 基础视图组件
     - `u-cell` - 单元格组件
     - `u-icon` - 图标组件
     - `u-image` - 图片组件
     - `u-rate` - 评分组件
     - `u-picker` - 选择器组件
     - `u-modal` - 模态框组件
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

### 1.2 工具类使用规范

#### uview-pro 时间工具类

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

---

## 2. Controller 层职责分离原则

### 2.1 核心原则
Controller 层仅负责：
- 接收 HTTP 请求参数
- 参数校验（基础校验）
- 调用 Service 层方法
- 返回响应结果

**禁止**在 Controller 中编写业务逻辑，包括：
- 数据库查询逻辑
- 数据转换和组装
- 复杂的业务规则判断

### 2.2 优化前问题
```java
// 反例：Controller 中包含大量业务逻辑
@PostMapping("/list")
public R list(@RequestBody ProductListDto dto) {
    // ❌ 数据库查询在 Controller 中
    LambdaQueryWrapper<BizProduct> query = new LambdaQueryWrapper<>();
    query.eq(BizProduct::getStatus, 1);
    // ... 大量查询逻辑
    
    // ❌ 数据组装在 Controller 中
    List<ProductVo> voList = result.getRecords().stream().map(product -> {
        // ... 复杂的数据转换逻辑
    }).collect(Collectors.toList());
    
    return R.ok(pageResult);
}
```

### 2.3 优化后示例
```java
// 正例：Controller 只做请求转发
@PostMapping("/list")
public R list(@RequestBody ProductListDto dto) {
    PageResultVo<ProductVo> result = bizProductService.getAppProductList(
            dto,
            SecurityContextHolder.getPageNo(),
            SecurityContextHolder.getPageSize()
    );
    return R.ok(result);
}
```

---

## 3. 数据库查询优化 - 优先使用关联查询

### 3.1 核心原则
**能通过关联表查询的，就使用关联查询，尽量减少数据库查询次数。**

### 3.2 优化前问题（N+1 查询问题）
```java
// 反例：先查商品，再循环查 SKU、属性等
List<BizProduct> products = bizProductService.list(query);  // 1次查询

// ❌ N次查询：每个商品查一次 SKU
products.forEach(product -> {
    List<BizProductSku> skus = bizProductSkuService.listByProductId(product.getId());  // N次查询
});

// ❌ 额外查询：分类、属性等
List<BizCategory> categories = bizCategoryService.listByIds(categoryIds);  // 额外查询
List<BizAttribute> attributes = bizAttributeService.listByIds(attrIds);    // 额外查询
```

**问题：** 查询 100 个商品可能产生 300+ 次数据库查询，严重影响性能。

### 3.3 优化后方案（关联查询）
```sql
<!-- 正例：使用 JOIN 关联查询，一次查询获取所有数据 -->
SELECT
    p.id,
    p.product_name,
    p.product_title,
    COALESCE(MIN(s.price), 0) AS min_price,
    COALESCE(MAX(s.price), 0) AS max_price,
    COALESCE(SUM(s.sales), 0) AS total_sales,
    c.category_name
FROM biz_product p
LEFT JOIN biz_category c ON p.category_id = c.id
LEFT JOIN biz_product_sku s ON p.id = s.product_id AND s.status = 1
WHERE p.status = 1
GROUP BY p.id, p.product_name, p.product_title, c.category_name
ORDER BY p.sort_order DESC, p.id DESC
```

**优势：**
- 将多次查询合并为一次 SQL JOIN 查询
- 数据库优化器可以更好地优化执行计划
- 减少网络往返开销

### 3.4 查询次数对比
| 场景 | 优化前查询次数 | 优化后查询次数 | 优化效果 |
|------|---------------|---------------|----------|
| 商品列表（含分类、SKU统计） | 4-6次 | 2次 | 减少约60% |
| 商品详情（含SKU、属性） | 8-10次 | 4次 | 减少约50% |

---

## 4. MyBatis XML 查询优化 - 优先使用 resultType

### 4.1 核心原则
**Mapper XML 中的查询返回优先使用 `resultType` 直接指定到具体的实体类或 VO 类，依赖 MyBatis 的下划线转驼峰自动映射，避免冗余的 resultMap 配置。**

### 4.2 优化前问题
```xml
<!-- ❌ 反例：使用冗余的 resultMap 配置 -->
<resultMap id="ProductVoMap" type="com.lrs.core.app.dto.product.ProductVo">
    <id column="id" property="id"/>
    <result column="product_name" property="productName"/>
    <result column="product_title" property="productTitle"/>
    <result column="main_image" property="mainImage"/>
    <result column="min_price" property="minPrice"/>
    <!-- ... 大量重复的映射配置 -->
</resultMap>

<select id="selectAppProductList" resultMap="ProductVoMap">
    SELECT ...
</select>
```

**问题：**
- 需要维护大量重复的 `resultMap` 配置
- SQL 字段名变更时，需要同步修改 `resultMap`
- 增加了代码维护成本

### 4.3 优化后方案
```xml
<!-- ✅ 正例：直接使用 resultType，依赖下划线转驼峰自动映射 -->
<select id="selectAppProductList" resultType="com.lrs.core.app.dto.product.ProductVo">
    SELECT
        p.id,
        p.product_name,
        p.product_title,
        p.main_image,
        COALESCE(MIN(s.price), 0) AS min_price,
        COALESCE(MAX(s.price), 0) AS max_price
    FROM biz_product p
    LEFT JOIN biz_product_sku s ON p.id = s.product_id
    WHERE p.status = 1
    GROUP BY p.id, p.product_name, p.product_title, p.main_image
</select>
```

**优势：**
- 无需维护 `resultMap`，代码更简洁
- SQL 字段名与 Java 属性名通过下划线转驼峰自动映射
- 减少配置错误的风险

### 4.4 使用内部类
```xml
<!-- 直接映射到内部类 -->
<select id="selectProductSpuAttrs" resultType="com.lrs.core.app.dto.product.ProductVo$SpuAttrVo">
    SELECT
        spa.product_id,
        spa.attr_id,
        a.attr_name,
        spa.attr_value_id,
        av.value AS attr_value
    FROM biz_product_spu_attr spa
    LEFT JOIN biz_attribute a ON spa.attr_id = a.id
    LEFT JOIN biz_attribute_value av ON spa.attr_value_id = av.id
    WHERE spa.product_id IN ...
</select>
```

### 4.5 配置要求
确保 MyBatis 配置了下划线转驼峰映射：
```yaml
# application.yml
mybatis-plus:
  configuration:
    map-underscore-to-camel-case: true
```

### 4.6 使用场景对比
| 场景 | 使用 resultMap | 使用 resultType |
|------|---------------|----------------|
| 字段名与属性名一致（下划线转驼峰） | 冗余配置 | ✅ 推荐 |
| 需要自定义类型转换 | ✅ 必要时使用 | 不适用 |
| 关联查询嵌套对象 | ✅ 必要时使用 | 不适用 |
| 简单查询映射 | 冗余配置 | ✅ 推荐 |

---

## 5. 时间格式化规范 - 避免在 SQL 层转换

### 5.1 核心原则
**时间格式化应在 Java 层通过注解完成，避免在 SQL 中使用 `DATE_FORMAT()` 等函数进行格式化转换。**

### 5.2 优化前问题
```xml
<!-- ❌ 反例：在 SQL 中使用 DATE_FORMAT 进行时间格式化 -->
SELECT
    DATE_FORMAT(p.pre_sale_start, '%Y-%m-%d %H:%i:%s') AS pre_sale_start,
    DATE_FORMAT(p.pre_sale_end, '%Y-%m-%d %H:%i:%s') AS pre_sale_end,
    DATE_FORMAT(p.estimated_ship_date, '%Y-%m-%d') AS estimated_ship_date
FROM biz_product p
```

**问题：**
- 返回字符串而非时间类型，丢失类型信息
- 无法利用数据库的时间类型特性（如索引、比较运算）
- 增加数据库计算负担
- 格式化逻辑散落在 SQL 中，难以统一维护

### 5.3 优化后方案
```xml
<!-- ✅ 正例：直接返回时间类型，不在 SQL 中格式化 -->
SELECT
    p.pre_sale_start,
    p.pre_sale_end,
    p.estimated_ship_date
FROM biz_product p
```

在 Java VO/DTO 类中使用注解进行格式化：
```java
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

public class ProductDetailVo {
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime preSaleStart;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime preSaleEnd;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate estimatedShipDate;
}
```

**优势：**
- 返回原生时间类型，保留类型信息
- 便于后续业务逻辑处理（时间比较、计算等）
- 格式化逻辑集中在 Java 层，便于统一维护
- 减轻数据库计算负担

### 5.4 注解说明
| 注解 | 作用 | 使用场景 |
|------|------|----------|
| `@JsonFormat` | 控制 JSON 序列化时的日期格式 | 响应给前端时的格式化 |
| `@DateTimeFormat` | 控制日期字符串反序列化为日期对象 | 接收前端请求参数时的解析 |

### 5.5 类型选择
| Java 类型 | 对应数据库类型 | 使用场景 |
|-----------|---------------|----------|
| `LocalDateTime` | `DATETIME` / `TIMESTAMP` | 包含日期和时间 |
| `LocalDate` | `DATE` | 仅包含日期 |
| `LocalTime` | `TIME` | 仅包含时间 |

---

## 6. Java 代码质量规范

### 6.1 代码规范
- 使用有意义的变量和方法命名
- 添加必要的注释说明业务逻辑
- 使用 Lombok 减少样板代码
- 单表查询使用 MyBatis-Plus 的 LambdaQueryWrapper 进行查询条件构建
- 如果需要多表关联查询，可以在 resources 下的 mapper 中的 .xml 添加相关查询方法

### 6.2 代码复用原则
- **优先复用**：实现新功能前，先搜索项目中是否已存在相同或相关的方法
- **方法命名**：保持方法命名一致性，便于查找和复用
- **服务层方法**：在 Service 层添加方法前，检查同模块或相关模块是否已有类似方法
- **Mapper层方法**：在 Mapper 中添加新查询前，检查是否已有可复用的 SQL 查询
- **后端接口和同业务类型的实体类能复用的尽量复用，避免重复实现**

### 6.2.1 重复方法合并经验

当发现两个方法逻辑基本相同时，应优先合并为一个方法，将差异化参数放到 DTO 中作为可选参数。

**优化前问题**：
```java
// ❌ 反例：两个方法逻辑重复，仅参数不同
public PageResultVo<ProductVo> getAppProductList(ProductListDto dto, int pageNo, int pageSize) { ... }
public PageResultVo<ProductVo> getAppProductListByMerchant(Long merchantId, int pageNo, int pageSize) { ... }
```

**优化后方案**：
1. 在 DTO 中添加可选参数 `merchantId`
2. 在 SQL 中增加条件判断 `WHERE p.status = 1 <if test="dto.merchantId != null and dto.merchantId > 0">AND p.merchant_id = #{dto.merchantId}</if>`
3. 删除重复的方法和 SQL 查询

**优化效果**：
| 指标 | 优化前 | 优化后 |
|------|--------|--------|
| 代码行数 | 80+ 行 | 40+ 行 |
| SQL 查询数 | 2 个 | 1 个 |
| 维护成本 | 需要维护两处逻辑 | 只需维护一处 |
| 参数灵活性 | 固定参数 | 可选参数，null=全部 |

**注意事项**：
- 可选参数应在 DTO 中添加注释说明（如 `/** 商家ID（可选，null=全部商家） */`）
- SQL 中使用 `<if>` 判断确保参数不为空时才加入查询条件
- Controller 层调用时，通过 `productListDto.setMerchantId()` 传递可选参数

### 6.3 接口设计
- RESTful API 设计规范
- 统一的响应格式（R<T>）
- 必要的参数校验
- Controller 接收复杂 JSON 请求体时必须定义 DTO，不要使用 `Map<String, Object>` 或原始 `Map` 接收业务参数；嵌套结构使用内部 DTO 类或独立 DTO 类表达清楚。

### 6.4 数据库操作
- 使用逻辑删除而非物理删除
- 批量操作时注意事务控制
- 敏感数据加密存储
- **SQL优化原则**：能通过一次SQL查询获取的数据，不要写多次查询。复杂统计查询应写入Mapper的XML文件中，使用聚合函数（如SUM、COUNT、CASE WHEN等）一次性获取多个统计结果，减少数据库连接开销
- **减少查询次数**：尽量使用 JOIN 查询、子查询或批量操作减少数据库交互次数；避免在循环中进行数据库查询

---

## 7. 关键代码注释规范

### 7.1 核心原则
**代码关键地方必须添加注释，包括：**
- Controller 类和方法的功能说明
- Service 接口方法的业务含义
- Mapper XML 中复杂 SQL 的逻辑说明
- 复杂业务逻辑的处理流程

### 7.2 注释示例

#### 7.2.1 Controller 层注释
```java
/**
 * 小程序商品控制器
 * 负责接收请求、调用Service、返回响应，不包含业务逻辑
 */
@RestController
@RequestMapping("/app/product")
public class AppProductController extends BaseController {
    
    /**
     * 获取商品分类列表
     */
    @PostMapping("/categoryList")
    public R categoryList() {
        // ...
    }
}
```

#### 7.2.2 Service 层注释
```java
/**
 * 获取小程序商品列表（分页）
 * @param dto 查询条件（分类ID、关键词）
 * @param pageNo 页码
 * @param pageSize 每页大小
 * @return 分页结果
 */
PageResultVo<ProductVo> getAppProductList(ProductListDto dto, int pageNo, int pageSize);
```

#### 7.2.3 Mapper XML 注释
```xml
<!-- 
    查询小程序商品列表，关联分类和SKU表
    通过 LEFT JOIN 一次性获取：
    1. 商品基础信息
    2. 分类名称
    3. SKU价格区间（MIN/MAX）
    4. 总销量（SUM）
-->
<select id="selectAppProductList" resultType="com.lrs.core.app.dto.product.ProductVo">
    SELECT ...
</select>
```

---

## 7. 代码分层架构规范

### 7.1 分层职责
| 层级 | 职责 | 禁止操作 |
|------|------|----------|
| **Controller** | 请求接收、参数校验、响应返回 | 数据库查询、业务逻辑 |
| **Service** | 业务逻辑处理、事务管理 | 直接操作数据库（通过Mapper） |
| **Mapper** | 数据访问、SQL执行 | 业务逻辑、事务管理 |
| **Entity** | 数据库表映射 | 业务方法、复杂逻辑 |
| **DTO/VO** | 数据传输对象 | 业务逻辑、数据库操作 |

### 7.2 调用链路

```
Controller → Service → Mapper → Database
    ↓           ↓
  参数校验    业务处理
  响应封装    数据组装
```

---

## 8. 性能优化最佳实践

### 8.1 批量查询替代循环查询
```java
// ❌ 反例：循环查询
products.forEach(p -> {
    List<BizProductSku> skus = skuService.listByProductId(p.getId());
});

// ✅ 正例：批量查询后分组
List<Long> productIds = products.stream().map(BizProduct::getId).collect(Collectors.toList());
List<BizProductSku> allSkus = skuService.listByProductIds(productIds);
Map<Long, List<BizProductSku>> skuGroupMap = allSkus.stream()
    .collect(Collectors.groupingBy(BizProductSku::getProductId));
```

### 8.2 使用 COALESCE 处理 NULL 值
```sql
-- 避免 NULL 值导致计算异常
COALESCE(MIN(s.price), 0) AS min_price
COALESCE(SUM(s.sales), 0) AS total_sales
```

### 8.3 合理使用 LEFT JOIN
```sql
-- 关联条件放在 ON 中，而非 WHERE 中
LEFT JOIN biz_product_sku s ON p.id = s.product_id AND s.status = 1
```

---

## 9. 常见错误案例

### 9.1 错误：Controller 中直接操作 Mapper
```java
// ❌ 错误示例
@PostMapping("/list")
public R list() {
    // 直接调用 Mapper，跳过 Service 层
    List<BizProduct> list = bizProductMapper.selectList(null);
    return R.ok(list);
}
```

### 9.2 错误：Service 中包含 HTTP 相关逻辑
```java
// ❌ 错误示例
@Service
public class BizProductServiceImpl {
    public void createProduct(HttpServletRequest request) {
        // Service 层不应处理 HTTP 请求对象
    }
}
```

### 9.3 错误：SQL 中使用 SELECT *
```sql
-- ❌ 错误示例
SELECT * FROM biz_product;

-- ✅ 正确示例：只查询需要的字段
SELECT id, product_name, product_title, main_image FROM biz_product;
```

---

## 10. 安全规范

- 用户敏感操作需登录验证
- SQL 注入防护使用参数化查询
- XSS 防护对输出内容进行转义
- 接口权限校验遵循最小权限原则

---

## 11. 总结

| 优化方向 | 优化前 | 优化后 |
|----------|--------|--------|
| Controller 职责 | 包含业务逻辑、数据组装 | 仅转发请求，返回响应 |
| 数据库查询 | N+1 查询问题 | JOIN 关联查询，减少查询次数 |
| 代码可读性 | 逻辑分散，难以维护 | 职责清晰，易于维护 |
| 性能 | 查询次数多，性能差 | 查询次数少，性能好 |
| 可测试性 | 难以单元测试 | Service 层可独立测试 |
| 代码复用 | 重复实现 | 优先复用现有代码 |
