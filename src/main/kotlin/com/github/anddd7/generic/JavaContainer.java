package com.github.anddd7.generic;

import java.util.*;

public class JavaContainer {
    private void showcase() {
        // 数组协变
        Number[] array = new Integer[1];
        // 泛型不支持协变
//        List<Number> list = new ArrayList<Integer>(); // 编译错误, required type List<Object>
        // 上界通配支持协变
        List<? extends Number> cons = new ArrayList<Integer>();
        // 下界通配支持逆变
        List<? super Integer> contras = new ArrayList<Number>();
    }
}
