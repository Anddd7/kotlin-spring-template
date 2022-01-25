package com.github.anddd7.generic

class KotlinContainer {
    // 生产者, 提供某一个类型的产品
    // <out T> 生产的产品的最小可用类型是 T
    // val p: Provider<Parent> = Provider(Child())
    class Provider<out T>(private val item: T) {
        fun get() = item
    }

    // 消费者, 消费某种类型的产品
    // <in T> 消费的产品的最大可用类型是 T
    // val c: Consumer<Child> = Consumer(Parent())
    class Consumer<in T> {
        fun handle(t: T) {}
    }

    interface Apple {}
    interface Microsoft {}
    interface Laptop {}
    class MacBook : Apple, Laptop
    class Surface : Microsoft, Laptop

    private fun provide() {
        val p: Provider<MacBook> = Provider(MacBook())
        // 协变, A<Child> -> A<Parent>
        val apples: Provider<Apple> = p
        val laptops: Provider<Laptop> = p
        // 后续操作: 获取生产出的苹果设备和笔记本电脑
        val apple: Apple = apples.get()
        val laptop: Laptop = laptops.get()
    }

    private fun consume() {
        val laptops = Consumer<Laptop>()
        // 逆变, A<Parent> -> A<Child>
        val macbook: Consumer<MacBook> = laptops
        val surfaces: Consumer<Surface> = laptops
        // 后续操作: 消费不同品牌的笔记本
        macbook.handle(MacBook())
        surfaces.handle(Surface())
    }
}

