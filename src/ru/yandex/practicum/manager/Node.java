package ru.yandex.practicum.manager;

import ru.yandex.practicum.tasks.Task;

public class Node {
    private Node prevNode;
    private Task actualTask;
    private Node nextNode;

    public Node(Node prevNode, Task actualTask, Node nextNode) {
        this.prevNode = prevNode;
        this.actualTask = actualTask;
        this.nextNode = nextNode;
    }

    public Node getPrevNode() {
        return prevNode;
    }

    public void setPrevNode(Node prevNode) {
        this.prevNode = prevNode;
    }

    public Task getActualTask() {
        return actualTask;
    }

    public void setActualTask(Task actualTask) {
        this.actualTask = actualTask;
    }

    public Node getNextNode() {
        return nextNode;
    }

    public void setNextNode(Node nextNode) {
        this.nextNode = nextNode;
    }
}
