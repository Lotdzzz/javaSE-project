# ● Java Stream流

**Java Stream流是什么：**

Java 8 新增的 Stream 流是「集合 / 数组的终极处理工具」，可以完全替代所有 for/foreach 循环，核心作用是：对 集合（List/Set/Map）、数组 做【批量数据处理】，所有遍历、筛选、加工、统计、转换、聚合的逻辑，都能通过 Stream 一行代码搞定，代码极简、无嵌套、可读性拉满。

**Java Stream流能做什么：**

stream() 不是一个普通方法，是开启流式处理的「入口」，通过 **集合.stream() / Arrays.stream(数组)** 生成流对象后，就能调用各种流式方法，完成所有你能想到的数据批量处理。

**✅ 核心能力 1：替代循环（遍历 / 赋值）**
遍历集合 / 数组的每一个元素，替代 for / foreach 循环
遍历的同时给对象赋值 / 修改属性（就是你之前问的循环赋值）
支持「带条件的遍历赋值」，精准修改符合要求的元素

**✅ 核心能力 2：数据筛选 & 过滤**
按条件筛选集合中的元素（比如：筛选年龄 > 20 的用户、ID 不为空的数据、名称包含指定字符的元素）
多条件组合筛选（且 / 或），无需写嵌套if判断

**✅ 核心能力 3：数据映射 & 转换**
提取集合中对象的单个属性（比如：从List<User>中提取所有用户名，得到List<String>）
对象类型转换（比如：把List<Integer>转List<String>、把实体类转 DTO/VO）
对元素做计算处理（比如：所有数字 + 10、字符串拼接前缀 / 后缀）

**✅ 核心能力 4：数据排序 & 去重 & 截取**
对集合元素自然排序（数字升序、字符串字典序）
对对象按指定属性排序（比如：按年龄升序、按 ID 降序、多字段排序）
对集合去重（基本类型直接去重、对象按属性去重）
截取前 N 个元素、跳过前 N 个元素

**✅ 核心能力 5：数据统计 & 聚合**
统计数量（替代list.size()，支持筛选后统计）
数值类聚合：求最大值、最小值、平均值、总和
对象类聚合：找属性最大 / 最小的对象

**✅ 核心能力 6：数据匹配 & 查找**
判断集合中是否存在符合条件的元素
判断集合中全部元素都符合条件
判断集合中没有符合条件的元素
查找任意一个符合条件的元素、查找第一个符合条件的元素

**✅ 核心能力 7：数据收集 & 归约**
将处理后的流，重新封装为List/Set/Map集合（流式处理的收尾操作）
将所有元素聚合为一个值（比如：所有数字求和、所有字符串拼接成一个长字符串）

## ○ Stream流的操作

**✔️  Stream 的操作[三步走：开流 -> 干活 -> 收尾]**
**开流：**
集合.stream() 就这一句话，意思是「打开数据流，准备处理这个集合里的所有数据」；

**干活：**
调用各种方法[中间操作]（比如挑数据、改数据、排序），可以连着调用多个（比如：先挑数据，再改数据，再排序）；

**收尾：**
必须调用一个「收尾方法」，意思是「处理完了，给我结果 / 结束操作」；

**✅ 关键坑：只开流、只干活，不收尾 → 等于啥都没做！代码白写！**
数据：

```java
@Data
public class User {
    private Integer id;       // 用户ID
    private String name;      // 用户名
    private Integer age;      // 年龄
    private String status;    // 状态
}
```

```java
List<User> userList = new ArrayList<>();
        userList.add(new User(1, "张三", 20, "正常"));
        userList.add(new User(2, "李四", 22, "禁用"));
        userList.add(new User(3, "王五", 25, "正常"));
        userList.add(new User(4, "张三", 18, "正常"));
```

### △ 中间操作

**1：过滤筛选：filter(条件)**
作用：筛选出符合条件的元素，条件是 Lambda 表达式，返回布尔值

```java
// 示例1：筛选年龄>20的用户
        userList.stream().filter(user -> user.getAge() > 20).forEach(System.out::println);
        // 示例2：多条件筛选（年龄>=18 且 状态为正常 且 ID不为空）
        userList.stream().filter(user -> user.getAge()>=18
                && "正常".equals(user.getStatus())
                && user.getId() != null).forEach(System.out::println);
```

**2：映射转换：map(处理逻辑)**
作用：对每个元素做「转换 / 加工」，返回新的元素；核心能力：**提取属性、类型转换、数据计算**

```java
// 示例1：提取属性 → 从User集合中提取所有用户名，得到List<String>
        List<String> nameList = userList.stream().map(User::getName).collect(Collectors.toList());
        nameList.forEach(System.out::println);

        // 示例2：数据计算 → 所有用户年龄+5
        userList.stream().map(user -> user.getAge() +5).forEach(System.out::println);

        // 示例3：类型转换 → Integer转String
        List<Integer> numList = Arrays.asList(1,2,3);
        List<String> strList = numList.stream().map(String::valueOf).collect(Collectors.toList());
        strList.forEach(System.out::println);
```

**3：遍历处理：peek(处理逻辑)**
作用：遍历每个元素，执行处理逻辑（比如赋值、修改属性），返回值还是流，可以继续链式调用
✔️ 和 forEach 的区别：peek 是「中间方法」（可继续链式调用），forEach 是「终止方法」（调用后流关闭），循环赋值优先用 peek！

```java
// 示例：遍历赋值，给所有状态为正常的用户，修改备注（实际开发赋值首选）
        userList.stream().filter(user -> "正常".equals(user.getStatus()))
                .peek(user -> user.setName(user.getName() + "-VIP")) // 赋值
                .peek(user -> user.setAge(user.getAge()+1)).collect(Collectors.toList()) // 继续赋值
                .forEach(System.out::println);
```

**4：去重：distinct()**
作用：去除集合中的重复元素，基本类型直接去重，对象默认按「地址」去重，对象去重配合 filter/map 使用

```java
// 示例1：基本类型去重
List<Integer> numList = Arrays.asList(1,2,2,3,3,3);
numList.stream().distinct(); // 结果：[1,2,3]

// 示例2：对象去重（比如：去重同名的用户）
userList.stream().map(User::getName).distinct();
```

**5：排序：sorted() / sorted(Comparator)**
作用：对元素排序；无参是「自然排序」，有参是「自定义属性排序」，对象排序必用有参！

```java
// 示例1：自然排序（数字升序、字符串字典序）
        List<Integer> numList = Arrays.asList(3,1,2);
        numList.stream().sorted().forEach(System.out::println); // 结果：[1,2,3]
        // 示例2：对象按单个属性排序 → 按年龄升序
        userList.stream().sorted(Comparator.comparing(User::getAge)).forEach(System.out::println);
        // 示例3：对象按单个属性降序 → 按ID降序
        userList.stream().sorted(Comparator.comparing(User::getId).reversed()).forEach(System.out::println);
        // 示例4：对象多字段排序 → 先按状态升序，再按年龄降序
        userList.stream().sorted(Comparator.comparing(User::getStatus)
                .thenComparing(User::getAge).reversed()).forEach(System.out::println);
```

**6：截取 / 跳过：limit(n) / skip(n)**
limit(n)：只保留前 n 个元素
skip(n)：跳过前 n 个元素，保留后面的元素

```java
// 示例1：只取前2个用户
userList.stream().limit(2);
// 示例2：跳过前1个，取后面的所有元素
userList.stream().skip(1);
```

### △ 终止方法

**1：遍历执行：forEach(处理逻辑)**
作用：遍历流中所有元素，执行处理逻辑（无返回值 void），最常用的「循环赋值」方式

```java
// 示例1：纯赋值 → 给所有用户设置状态为正常
userList.stream().forEach(user -> user.setStatus("正常"));
// 示例2：带条件赋值 → 给年龄>20的用户设置备注
userList.stream().filter(user -> user.getAge()>20)
        .forEach(user -> user.setRemark("成年用户"));
// 示例3：打印数据
userList.stream().forEach(System.out::println);
```

**2：收集结果：collect(Collectors.xxx)**
作用：将处理后的流，转换为集合 / 其他数据结构，返回值是集合，是 Stream 中最核心的终止方法

```java
// 示例1：收集为List集合（最常用）
List<User> filterList = userList.stream().filter(user -> user.getAge()>20).collect(Collectors.toList());
// 示例2：收集为Set集合（自动去重）
Set<String> nameSet = userList.stream().map(User::getName).collect(Collectors.toSet());
// 示例3：收集为Map集合（比如：ID为key，User对象为value）
Map<Integer, User> userMap = userList.stream().collect(Collectors.toMap(User::getId, user -> user));

// 拓展：高级收集（开发常用）
// 分组 → 按状态分组，key=状态，value=对应状态的用户集合
Map<String, List<User>> statusGroup = userList.stream().collect(Collectors.groupingBy(User::getStatus));
// 统计数量 → 按状态分组后，统计每组的用户数
Map<String, Long> statusCount = userList.stream().collect(Collectors.groupingBy(User::getStatus, Collectors.counting()));
```

**3：统计数量：count()**
作用：统计流中元素的个数，返回值 long 类型，替代传统的list.size()，支持筛选后统计

```java
// 示例1：统计总用户数
long total = userList.stream().count();
// 示例2：统计年龄>20的用户数（开发高频）
long adultCount = userList.stream().filter(user -> user.getAge()>20).count();
```

**4. 匹配判断：anyMatch / allMatch / noneMatch**
作用：判断流中元素是否符合指定条件，返回值 boolean 类型，业务中做「条件判断」的核心方法

anyMatch(条件)：是否存在至少一个符合条件的元素 → 有一个就返回 true
allMatch(条件)：是否全部元素都符合条件 → 全符合才返回 true
noneMatch(条件)：是否没有任何一个符合条件的元素 → 全不符合才返回 true

```java
// 示例1：判断是否存在年龄>25的用户
boolean hasAdult = userList.stream().anyMatch(user -> user.getAge()>25);
// 示例2：判断所有用户的状态都是正常
boolean allNormal = userList.stream().allMatch(user -> "正常".equals(user.getStatus()));
// 示例3：判断没有ID为0的用户
boolean noZeroId = userList.stream().noneMatch(user -> user.getId() == 0);
```

**5：查找元素：findAny() / findFirst()**
作用：查找流中的元素，返回值 Optional 类型（避免空指针），配合 filter 使用，查找符合条件的元素

findAny()：返回流中任意一个元素（并行流中效率高）
findFirst()：返回流中第一个元素

```java
// 示例1：查找第一个年龄>20的用户
Optional<User> firstUser = userList.stream().filter(user -> user.getAge()>20).findFirst();
// 安全取值（Optional避免空指针）
firstUser.ifPresent(user -> System.out.println(user.getName()));

// 示例2：查找任意一个状态为禁用的用户
Optional<User> disableUser = userList.stream().filter(user -> "禁用".equals(user.getStatus())).findAny();
```

**6：聚合最值：max() / min()**
作用：查找流中的最大值 / 最小值，返回值 Optional 类型，支持数值 / 对象属性的最值

```java
// 示例1：查找年龄最大的用户
Optional<User> maxAgeUser = userList.stream().max(Comparator.comparing(User::getAge));
// 示例2：查找ID最小的用户
Optional<User> minIdUser = userList.stream().min(Comparator.comparing(User::getId));
// 示例3：数值集合的最大值
List<Integer> numList = Arrays.asList(1,2,3);
Optional<Integer> maxNum = numList.stream().max(Integer::compare);
```

**7：归约聚合：reduce()**
作用：将流中所有元素聚合为一个值（比如：求和、求积、拼接字符串），返回 Optional 类型，开发中数值聚合常用

```java
// 示例1：所有用户的年龄求和
Optional<Integer> sumAge = userList.stream().map(User::getAge).reduce(Integer::sum);
// 示例2：数值集合求和，指定初始值0
int sum = Arrays.asList(1,2,3).stream().reduce(0, Integer::sum);
// 示例3：拼接所有用户名，用逗号分隔
String nameStr = userList.stream().map(User::getName).reduce("", (a,b) -> a + "," + b);
```