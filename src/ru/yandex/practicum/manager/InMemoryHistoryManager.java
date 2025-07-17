package ru.yandex.practicum.manager;

import ru.yandex.practicum.tasks.HistoryManager;
import ru.yandex.practicum.tasks.Task;
import ru.yandex.practicum.tasks.Node;

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
            Node newNode = new Node (null, task, null);
            head = newNode;
            tail = newNode;
            history.put(id,newNode);
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
            Node nodeForRemove = history.get(id);
            Node prevNode = nodeForRemove.getPrevNode();
            Node nextNode = nodeForRemove.getNextNode();

            if (history.size() == 1) {
               head = null;
               tail = null;
            } else if (prevNode == null) {
                nextNode.setPrevNode(null);
                head = nextNode;
            } else if (nextNode == null) {
                prevNode.setNextNode(null);
                tail = prevNode;
            } else {
                prevNode.setNextNode(nextNode);
                nextNode.setPrevNode(prevNode);
            }

            nodeForRemove.setPrevNode(null);
            nodeForRemove.setNextNode(null);
            history.remove(id);
        }
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