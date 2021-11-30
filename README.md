# jFly
## 基于：MIT License.
----
## 前言
 自己项目上的一些内容翻来覆去写得烦了
 正好被hutool所启发（在此感谢hutool所给予的设计灵感）
 就将几个项目公共代码给抽取了出来，又修改了一下
## 简单说明：
----

1. batis:
    仅仅是包含一个JsonTypeHandler，用于batis的序列/反序列化
2. crypto:
    包含一个MixinCryptor，支持RSA/AES的混合加解密。个人觉得没有hutool好用。
3. date:
    这个就很艹蛋了。日期格式化工具DateHammer，包含了对各种日期格式的深切愤怒。全部锤成yyyy-MM-dd。
4. dynamic:
    包含了一套动态编译的核心。不支持jar包（这块hutool已经写的很好了，造不动轮子），但是可以自定义扩展，从任何地方加载、编译类。
5. function:
    主要是对jdk原生function包的一个补充。支持例如tryOrElse的操作，拒绝在stream里写try块。
6. jwt: 
    只包含了一个用于判空的工具类，个人觉得写得不好。
7. polyfill:
    顾名思义，是一个功能补充，包含了Map和Objects两个工具类。每个方法都写了javadoc。
8. random:
    随机包。内含了一个随机token生成工具。跟hutool的有所区别。
9. reflect:
    反射工具包。目前有：实现js里Object.assign的类似效果，以及对象转map（方法重载支持自定义转换条件）
10. spider:
    爬虫工具包。基于jdk11的http功能做了封装。也包含一个抽象类和基本实现类。可直接使用。
11. tuple:
    泛型元组。