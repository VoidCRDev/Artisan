package sh.miles;

public class Example {
    private static final String STATIC_FIELD = "this is some string content";
    private static final int NUMBER = 5;

    private String field;

    public Example(String constructor) {
        this.field = constructor;
    }

    protected void doThing() {
        System.out.println(field);
    }
}
