package structure;

import alat.Alat;

public class Stack<T> {
    public Node<T> top;

    public void push(T data) {
        Node<T> newNode = new Node<>(data);
        newNode.next = top;
        top = newNode;
    }

    public T pop() {
        if (top == null) return null;
        T data = top.data;
        top = top.next;
        return data;
    }

    public boolean isEmpty() {
        return top == null;
    }

    public boolean containsAlatById(int idAlat) {
        Node<T> current = top;
        while (current != null) {
            if (current.data instanceof Alat) {
                Alat alat = (Alat) current.data;
                if (alat.getIdAlat() == idAlat) {
                    return true;
                }
            }
            current = current.next;
        }
        return false;
    }

    public void printAll() {
        Node<T> current = top;
        while (current != null) {
            System.out.println(current.data.toString());
            current = current.next;
        }
    }
}