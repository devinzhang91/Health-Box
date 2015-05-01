package com.example.devinzhang.myapplication;

/**
 * Created by DevinZhang on 2015/4/19 0019.
 */
public class MyDataQueue {
    private int maxSize; //队列长度，由构造函数初始化
    private int[] queArray; // 队列
    private int front; //队头
    private int rear; //队尾
    private int nItems;  //元素的个数

    //--------------------------------------------------------------
    public MyDataQueue(int s) {          // 构造函数

        maxSize = s;
        queArray = new int[maxSize];
        front = 0;
        rear = -1;
        nItems = 0;
    }

    //--------------------------------------------------------------
    public void insert(int j) {   // 进队列

        if (rear == maxSize - 1)        // 处理循环
            rear = -1;
        queArray[++rear] = j;          // 队尾指针加1,把值j加入队尾
        if(!isFull())  nItems++;
    }

    //--------------------------------------------------------------
    public int remove() {         // 取得队列的队头元素。
        int temp = queArray[front++]; // 取值和修改队头指针
        if (front == maxSize)            // 处理循环
            front = 0;
        nItems--;
        return temp;
    }

    //--------------------------------------------------------------
    public int peekFront() {      // 取得队列的队头元素。该运算与 remove()不同，后者要修改队头元素指针。

        return queArray[front];
    }

    //--------------------------------------------------------------
    public boolean isEmpty() {    // 判队列是否为空。若为空返回一个真值，否则返回一个假值。

        return (nItems == 0);
    }

    //--------------------------------------------------------------
    public boolean isFull() {     // 判队列是否已满。若已满返回一个真值，否则返回一个假值。
        return (nItems == maxSize);
    }

    //--------------------------------------------------------------
    public int size() {           // 返回队列的长度
        return nItems;
    }

    //--------------------------------------------------------------
    public int getAll(char c) {
        int temp = 0;
        for(int i=rear; i<nItems; i++){
            temp += queArray[i]+c;
        }
        for(int i=0; i<rear; i++){
            temp += queArray[i]+c;
        }
        return temp;
    }

    //--------------------------------------------------------------
    public int getData(int n) {
       if(n+rear > nItems) {
           return queArray[n+rear-nItems];
       } else {
           return queArray[n+rear];
       }
    }
}
