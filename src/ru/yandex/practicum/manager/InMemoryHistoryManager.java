package ru.yandex.practicum.manager;

import ru.yandex.practicum.tasks.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {
<<<<<<< HEAD
    private Node head;
    private Node tail;

    private final Map<Integer, Node> history = new HashMap<>();
=======

    private final ArrayList<Task> history = new ArrayList<>();
    private static final int MAX_SIZE_HISTORY = 10;
>>>>>>> parent of 6cd7235 (feat(history): реализовано хранение истории через двусвязный список, добавлены тесты)

    @Override
    public ArrayList<Task> getHistory() {
        return history;
    }

<<<<<<< HEAD
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
=======
    @Override
    public void updateHistory(Task task) {
        if(MAX_SIZE_HISTORY == history.size()) {
            history.removeFirst();
            history.add(task);
>>>>>>> parent of 6cd7235 (feat(history): реализовано хранение истории через двусвязный список, добавлены тесты)
        } else {
            history.add(task);
        }
    }
<<<<<<< HEAD

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

=======
>>>>>>> parent of 6cd7235 (feat(history): реализовано хранение истории через двусвязный список, добавлены тесты)
}