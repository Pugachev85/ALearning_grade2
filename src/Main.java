
public class Main {
    public static void main(String[] args) {
    CustomHashMap<Integer,String> map = new CustomHashMap<>();
    map.put(1, "a");
    map.put(2, "b");
    map.put(3, "c");
    map.put(4, "d");
    map.put(5, "e");
    map.put(6, "f");
    map.put(7, "g");
    map.put(8, "h");
    map.put(9, "i");
    map.put(10, "j");
    System.out.println(map);
    System.out.println(map.get(3));
    map.remove(5);
    map.put(10, "end");
    System.out.println(map);
    }
}