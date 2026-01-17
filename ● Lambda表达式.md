# ● Lambda表达式

**核心作用：**
简化「函数式接口」的匿名内部类写法，极大精简代码、提高可读性

## ○ 函数式接口

**定义：**
一个接口中，有且仅有 1 个抽象方法（可以有默认方法、静态方法，不影响），这样的接口就是「函数式接口」。
例如：
java.lang.Runnable ：抽象方法 void run()
java.util.Comparator ：抽象方法 int compare(T o1, T o2)

Java 中专门提供了注解 **@FunctionalInterface** 标记函数式接口，作用是编译期校验：如果加了这个注解的接口，不符合「只有 1 个抽象方法」的规则，编译器会直接报错。

```java
@FunctionalInterface
public interface Message {
    //不算唯一抽象方法
    default void send() {
        System.out.println("固定发送信息");
    }
    //可以有默认方法和静态方法
    static void send(String msg) {
        System.out.println("动态发送信息" + msg);
    }

    //唯一抽象方法
    String run(String msg);
}
```

## ○ Lambda语法

Lambda 本质是「函数式接口中抽象方法」的代码实现简写，语法完全固定，标准格式如下：

```java
(参数列表) -> { 方法体代码 };
```

**(参数列表)**：和「函数式接口的抽象方法」的参数列表 完全一致（参数个数、类型、顺序都一样）

**->**：Lambda 的「语法分隔符」，固定写法，无实际含义，作用是：把「参数列表」和「方法体」分隔开

**{方法体代码}**：就是原来抽象方法需要编写的业务逻辑代码，等价于匿名内部类中重写抽象方法的方法体

以 Java 内置的Runnable接口（函数式接口）为例，对比「匿名内部类」和「Lambda 标准写法」，效果完全等价：

```java
public class LambdaDemo {
    public static void main(String[] args) {
        // 1. 传统写法：Runnable匿名内部类
        Runnable runnable1 = new Runnable() {
            @Override
            public void run() { // Runnable唯一的抽象方法
                System.out.println("匿名内部类：执行线程任务");
            }
        };
        
        // 2. Lambda标准写法：等价于上面的匿名内部类
        Runnable runnable2 = () -> {
            System.out.println("Lambda标准写法：执行线程任务");
        };
    }
}
```

本质上是：
**new了一个接口的实现类 重写父类的run方法 然后返回赋值给父引用**

可以看到：Lambda 省略了「new 接口 ()」「@Override」「方法名」这些冗余代码，只保留了核心的「参数 + 业务逻辑」。

## ○ Lambda核心语法

标准语法只是基础，实际开发中几乎不用标准写法，都是用「简化写法」，Lambda 提供了 4 条核心简化规则，规则可以叠加使用，简化后代码极致精简，这也是 Lambda 的精髓，所有规则都基于「函数式接口的抽象方法」定义，规则优先级从高到低，全部通用：
**规则 1：参数的「数据类型」可以直接省略**

```java
// 示例：Comparator接口（抽象方法 int compare(Integer o1, Integer o2)）
// 标准写法
Comparator<Integer> com1 = (Integer o1, Integer o2) -> {
    return o1 - o2;
};
// 简化后：省略参数类型
Comparator<Integer> com2 = (o1, o2) -> {
    return o1 - o2;
};
```

**规则 2：如果参数列表只有 1 个参数，参数的「小括号 ()」可以直接省略**

```java
// 示例：自定义函数式接口（抽象方法 void doSomething(String str)）
@FunctionalInterface
interface MyInterface {
    void doSomething(String str);
}
// 标准写法
MyInterface mi1 = (String str) -> {
    System.out.println(str);
};
// 叠加规则1+2：省略类型+省略小括号
MyInterface mi2 = str -> {
    System.out.println(str);
};
```

**规则 3：如果方法体中只有 1 行代码，方法体的「大括号 {}」可以直接省略**

```java
// 示例：Runnable接口（无参数、无返回值，方法体1行代码）
// 标准写法
Runnable r1 = () -> {
    System.out.println("Hello Lambda");
};
// 简化后：省略大括号
Runnable r2 = () -> System.out.println("Hello Lambda");
```

**规则 4：如果方法体中只有 1 行 return 语句，省略大括号的同时，必须一起省略 return 关键字和分号；**

```java
// 示例：Comparator接口（返回int类型，方法体只有1行return）
// 标准写法
Comparator<Integer> com1 = (o1, o2) -> {
    return o1 - o2;
};
// 叠加所有规则：省略类型+省略大括号+省略return+省略分号
Comparator<Integer> com2 = (o1, o2) -> o1 - o2;
```

**规则 5：方法引用式**

```java
public class JavaMainTest {
    public static void main(String[] args) {
        List<User> userList = new ArrayList<>();
        userList.add(new User(1, "张三", 20, "正常"));
        userList.add(new User(2, "李四", 22, "禁用"));
        userList.add(new User(3, "王五", 25, "正常"));
        userList.add(new User(4, "张三", 18, "正常"));

        userList.forEach(System.out::println);
        //等价于
        userList.forEach(user -> {
            System.out.println(user);
        });
    }
}
```

**使用场景：For循环遍历**

```java
public class JavaMainTest {
    public static void main(String[] args) {
        List<User> userList = new ArrayList<>();
        userList.add(new User(1, "张三", 20, "正常"));
        userList.add(new User(2, "李四", 22, "禁用"));
        userList.add(new User(3, "王五", 25, "正常"));
        userList.add(new User(4, "张三", 18, "正常"));

        //使用Lambda表达式进行遍历
        userList.forEach(u -> {
            System.out.println(u);
        });

        //使用Lambda进行集合排序
        Collections.sort(userList, (u1, u2) -> u1.getAge() - u2.getAge());

        userList.forEach(System.out::println);
    }
}
```

## ○ 类字面常量[额外语法]

`new Class[]{Service.class}` 是 Java 中 **「创建【Class 类型的数组】的标准语法」**，是一个**数组字面量初始化写法**，属于 Java 基础语法

**`new Class[]{ ... }` 是什么**

新建一个 `Class` 类型的数组，并且**立即初始化数组中的元素**

这是 Java 中 **「数组的动态初始化语法」**，是创建数组的标准写法之一

**例如：**

```java
// 字符串数组：创建String类型数组，元素是"a","b"
new String[]{"a","b"};

// 整数数组：创建int类型数组，元素是1,2,3
new int[]{1,2,3};

// 对象数组：创建User类型数组，元素是new User()
new User[]{new User(), new User()};

// 你的写法：创建Class类型数组，元素是Service.class
new Class[]{Service.class};
```

**等价于：**

```java
Class<?>[] aclass = new Class[]{Service.class, ServiceDemo.class}

Class<?>[] aclass = {Service.class, ServiceDemo.class}
```

**使用场景：**

```java
// 需求：获取Service类中「参数是 String 和 int 类型」的构造器
Class<Service> clazz = Service.class;
// 传入：参数类型的Class数组 → new Class[]{String.class, int.class}
Constructor<Service> constructor = clazz.getConstructor(new Class[]{String.class, int.class});
// 简化写法（推荐）
Constructor<Service> constructor = clazz.getConstructor(String.class, int.class);
```