package service;

import model.Node;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryHistoryManager implements HistoryManager {

    private HashMap<Integer, Node> historyHashmap;
    private Node head;
    private Node tail;

    public InMemoryHistoryManager() {
        this.historyHashmap = new HashMap<>();
        this.head = null;
        this.tail = null;
    }

    @Override
    public void add(Task task) {
        if (task == null) return;
        if (historyHashmap.get(task.getId()) != null) {
            remove(task.getId());
        }

        linkLast(task);
    }

    @Override
    public ArrayList<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void remove(int id) {
        Node node = historyHashmap.get(id);
        if (node != null) {
            removeNode(node);
            historyHashmap.remove(id);
        } else {
            System.out.println("Task not found.");
        }
    }

    public void linkLast(Task task) {
        Node newNode = new Node(task);

        if (head == null) {
            head = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
        }
        tail = newNode;
        historyHashmap.put(task.getId(), newNode);
    }

    public void removeNode(Node node) {
        if (node == head && node == tail) {
            head = null;
            tail = null;
        } else if (node == head) {
            node.next.prev = null;
            head = node.next;
        } else if (node == tail) {
            node.prev.next = null;
            tail = node.prev;
        } else {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }
        node.next = null;
        node.prev = null;
    }

    public ArrayList<Task> getTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        Node curNode = head;
        while (curNode != null) {
            tasks.add(curNode.task);
            curNode = curNode.next;
        }
        return tasks;
    }
}
