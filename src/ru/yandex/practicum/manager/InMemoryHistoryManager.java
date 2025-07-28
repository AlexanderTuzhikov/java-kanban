package ru.yandex.practicum.manager;

import ru.yandex.practicum.tasks.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private Node head;
    private Node tail;

    private final Map<Integer, Node> history = new HashMap<>();

    @Override
    public void add(Task task) {
        if (task != null) {
            int id = task.getTaskId();

            if (history.containsKey(id)) {
                remove(id);
            }

            linkLast(task);
        }
    }

    private void linkLast(Task task) {
        if (task == null) {
            return;
        }
        Integer id = task.getTaskId();

        if (tail == null) {
            Node newNode = new Node(null, task, null);
            head = newNode;
            tail = newNode;
            history.put(id, newNode);
        } else {
            Node oldLastNode = tail;
            Node newLastNode = new Node(oldLastNode, task, null);
            oldLastNode.setNextNode(newLastNode);
            tail = newLastNode;
            history.put(id, newLastNode);
        }
    }

    @Override
    public void remove(int id) {
        if (history.containsKey(id)) {
            Node node = history.get(id);
            removeNode(node);
            history.remove(id);
        }
    }

    private void removeNode(Node node) {
        if (node == null) {
            return;
        }

        Node prevNode = node.getPrevNode();
        Node nextNode = node.getNextNode();

        if (node == head && node == tail) {
            head = null;
            tail = null;
        } else if (node == head) {
            head = nextNode;
            if (nextNode != null) {
                nextNode.setPrevNode(null);
            }
        } else if (node == tail) {
            tail = prevNode;
            if (prevNode != null) {
                prevNode.setNextNode(null);
            }
        } else {
            prevNode.setNextNode(nextNode);
            nextNode.setPrevNode(prevNode);
        }

        node.setPrevNode(null);
        node.setNextNode(null);

    }

    @Override
    public List<Task> getHistory() {
        List<Task> showHistory = new ArrayList<>();
        Node actualNode = head;

        while (actualNode != null) {
            showHistory.add(actualNode.getActualTask());
            actualNode = actualNode.getNextNode();
        }
        return showHistory;
    }

}