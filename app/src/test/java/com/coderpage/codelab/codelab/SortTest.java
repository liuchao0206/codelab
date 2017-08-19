package com.coderpage.codelab.codelab;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author lc. 2017-08-17
 * @since 0.1.0
 */

public class SortTest {

    @Test
    public void testBubbleSort() {
        Integer[] arr = createArray();
        Sort.bubbleSort(arr);
        Sort.show(arr);
        Assert.assertTrue(Sort.isSorted(arr));
    }

    @Test
    public void testSelectSort() {
        Integer[] arr = createArray();
        Sort.selectSort(arr);
        Sort.show(arr);
        Assert.assertTrue(Sort.isSorted(arr));
    }

    @Test
    public void testInsertionSort() {
        Integer[] arr = createArray();
        Sort.insertionSort(arr);
        Sort.show(arr);
        Assert.assertTrue(Sort.isSorted(arr));
    }

    private Integer[] createArray() {
        Integer[] array = new Integer[]{19, 9, 0, -1, 9, 29, 4, 8, 8, 3};
        return array;
    }

}
