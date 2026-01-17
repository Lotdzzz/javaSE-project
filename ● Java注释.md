# ● Java注释

javadoc.exe 是 JDK 自带的工具，用来根据 Java 代码里的规范注释，生成和官方 Java API 风格一致的 HTML 文档（方便其他人查看类、方法的说明）。

## ○ Javadoc的使用

**使用javadoc.exe：**
1：javadoc Demo.java

2：javadoc -d doc -author -version -encoding UTF-8 -charset UTF-8 Demo.java

```bash
-d 目录名：指定文档输出目录（必用，避免当前目录混乱）
-author：显示代码中的@author信息
-version：显示代码中的@version信息
-encoding UTF-8：指定代码文件的编码（解决中文注释乱码）
-charset UTF-8：指定生成文档的编码
*.java：批量处理当前目录下所有 Java 文件
```

## ○ 注释标签

Javadoc 有很多实用标签，不同标签对应不同的说明场景（比如类、方法、字段），下面按「适用场景」分类详细说明，附用法示例：

**通用标签：**

```bash
@author

用途：标注作者信息
适用：类、接口
示例：@author 张三（可写多个，用逗号分隔）
```

```bash
@version

用途：标注版本号
适用：类、接口
示例：@version 1.0.0
```

```bash
@since

用途：标注「该类 / 方法 / 字段从哪个版本开始提供」
适用：类、接口、方法、字段
示例：@since JDK 1.8（说明从 JDK8 开始支持）
```

```bash
@see

用途：添加「相关参考链接」（可以是其他类、方法、外部文档）
适用：所有元素
@see java.lang.String  // 参考String类
@see #add(int, int)    // 参考当前类的add方法（#代表当前类）
@see "https://docs.oracle.com"  // 参考外部文档
```

```bash
@link

用途：在注释文本中嵌入可点击的链接（比 @see 更灵活，可写在句子里）
适用：所有注释文本
格式：{@link 目标}
/**
 * 调用{@link #add(int, int)}方法可以实现求和
 */
```

**类 / 接口专用标签：**

```bash
@deprecated

用途：标记「该类 / 接口已过时」，同时说明替代方案
适用：类、接口、方法、字段
/**
 * @deprecated 该类已过时，请使用{@link NewDemo}替代
 */
```

**方法 / 构造器专用标签：**

```bash
@param

用途：说明方法的参数含义
适用：方法、构造器
格式：@param 参数名 参数说明
示例：@param a 要相加的第一个整数
```

```bash
@return

用途：说明方法的返回值含义
适用：有返回值的方法（void 方法不能用）
示例：@return 两个整数相加的结果
```

```bash
@throws / @exception

用途：说明方法可能抛出的异常（两者功能一致，@exception 是旧写法）
适用：方法、构造器
格式：@throws 异常类型 异常触发场景
/**
 * @throws IllegalArgumentException 当参数a为负数时抛出
 */
```

```bash
@inheritDoc

用途：继承父类 / 接口的 Javadoc 注释（减少重复编写）
适用：子类方法（覆盖父类方法时用）
/**
 * {@inheritDoc}
 */
public int add(int a, int b) { ... }
```

**字段专用标签:**

```bash
@value
用途：显示静态常量的具体值（仅用于static final字段）
适用：静态常量
/**
 * 圆周率常量：{@value}
 */
public static final double PI = 3.1415926;
```

## ○ Idea工具快捷注释模板

**1：创建接口或类时自动生成注释：**
打开idea设置：File-Settings
进入 Editor → File and Code Templates（文件和代码模板）；
选中class（Enum等也可以配置）：
![在这里插入图片描述](https://i-blog.csdnimg.cn/direct/9ab7cdda798f45bb841d0a5072cb6fff.png)
**2：注释快捷键生成标签：**
打开 IDEA 设置，进入 Editor → Live Templates
点击右侧「+」→ 选Template Group... → 输入分组名（比如MyJavadoc）→ 确定；
选中刚创建的分组，点击「+」→ 选Live Template（新建实时模板）；
配置模板：
Abbreviation（缩写）：输入触发快捷键（比如doc，也可以用/**）(默认输入完缩写后按tab快捷生成)；

Description（描述）：填 “生成方法 Javadoc 注释”；

Template text（模板内容）：

```bash
/**
 * 方法描述：
 * @author name
 */
```

设置模板适用范围：
点击模板下方的「Define」→ 勾选「Java」→ 选「Declaration」（声明处，比如方法上方）；