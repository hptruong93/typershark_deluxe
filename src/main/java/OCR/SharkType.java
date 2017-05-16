package OCR;

public enum SharkType {
	REGULAR(0, "regular"),
    HAMMER_HEAD(1, "hammer"),
    TIGER(2, "tiger"),
    GHOST(3, "ghost"),
    PIRANHA(4, "piranha"),
    DOUBLE_PIRANHA(5, "double_piranha"),
    POISON(6, "poison"),
    ;

    public final int value;
    private final String text;

    /**
     * @param value
     */
    private SharkType(final int value, String text) {
        this.value = value;
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }

    public static SharkType identify(int value) {
    	for (SharkType language : SharkType.values()) {
    		if (value == language.value) {
    			return language;
    		}
    	}
    	return null;
    }
}
